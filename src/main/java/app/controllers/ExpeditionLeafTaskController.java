package app.controllers;

import app.controllers_connector.ExpeditionLeafTaskExpeditionItemConnector;
import app.controllers_connector.ExpeditionLeafTaskShipItemConnector;
import app.data.DataLoader;
import app.data.expedition.*;
import app.data.planets.ComboBoxPlanet;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import ogame.planets.Coordinate;
import ogame.planets.Planet;
import ogame.ships.Ship;

import java.util.ArrayList;

public class ExpeditionLeafTaskController {
    @FXML
    public TextField textFieldGalaxy;
    @FXML
    public TextField textFieldSystem;
    @FXML
    public TextField textFieldPlanet;
    public Button editButton;
    @FXML
    private ComboBox<ComboBoxPlanet> comboBoxPlanet;

    @FXML
    private ComboBox<ComboBoxShip> comboBoxShip;

    @FXML
    private TextField textFieldValue;

    @FXML
    private CheckBox checkBoxAll;

    @FXML
    private VBox vBoxAddedShips;

    @FXML
    private VBox vBoxQueue;

    private Expedition selectedExpedition;

    @FXML
    void initialize(){
        checkBoxAll.selectedProperty().addListener((observable, oldValue, newValue) -> textFieldValue.setDisable(newValue));
    }

    private ArrayList<ItemShipList> itemShipLists = new ArrayList<>();
    @FXML
    void addShip() {
        Ship ship = comboBoxShip.getValue().getShip();
        int value;
        if(checkBoxAll.isSelected())
            value = -1;
        else{
            if(textFieldValue.getText().isEmpty())
                return;

            value = Integer.parseInt(textFieldValue.getText());
        }

        ItemShipList itemShipList = new ItemShipList(ship,value);
        if(!itemShipLists.contains(itemShipList)){
            ExpeditionLeafTaskShipItemConnector connector = new ExpeditionLeafTaskShipItemConnector(itemShipList);
            itemShipLists.add(itemShipList);
            vBoxAddedShips.getChildren().add(connector.content());
        }
        else{
            int index = itemShipLists.indexOf(itemShipList);
            itemShipLists.set(index,itemShipList);
            updateVBoxAddedShips();
        }
        DataLoader.expeditions.save();
    }

    @FXML
    void addExpedition() {
        if(textFieldGalaxy.getText().isEmpty() || textFieldSystem.getText().isEmpty())
            return;
        int galaxy = Integer.parseInt(textFieldGalaxy.getText());
        int system = Integer.parseInt(textFieldSystem.getText());
        Coordinate coordinate = new Coordinate(galaxy,system,16);
        Planet planet = comboBoxPlanet.getValue().getPlanet();

        Expedition expedition = new Expedition(planet,coordinate,new ArrayList<>(itemShipLists));
        DataLoader.expeditions.add(expedition);
        updateQueue();
        DataLoader.expeditions.save();
    }

    public void updateVBoxAddedShips(){
        vBoxAddedShips.getChildren().clear();
        for(ItemShipList itemShipList : itemShipLists){
            ExpeditionLeafTaskShipItemConnector connector = new ExpeditionLeafTaskShipItemConnector(itemShipList);
            vBoxAddedShips.getChildren().add(connector.content());
        }
    }
    private final ArrayList<ExpeditionLeafTaskExpeditionItemConnector> connectorArrayList = new ArrayList<>();
    public void updateQueue(){
        ArrayList<Expedition> queueList = DataLoader.expeditions.getExpeditionList();
        if(queueList.size() != vBoxQueue.getChildren().size()) {
            vBoxQueue.getChildren().clear();
            connectorArrayList.clear();
            for(Expedition expedition : queueList){
                ExpeditionLeafTaskExpeditionItemConnector expeditionItemConnector = new ExpeditionLeafTaskExpeditionItemConnector(expedition, this);
                vBoxQueue.getChildren().add(expeditionItemConnector.content());
                connectorArrayList.add(expeditionItemConnector);
            }
        }else
            for(ExpeditionLeafTaskExpeditionItemConnector connector : connectorArrayList)
                connector.getController().update();
    }

    public void update() {
        ArrayList<ComboBoxPlanet> comboBoxPlanetArrayList = ComboBoxPlanet.list(DataLoader.planets.getPlanetList());
        if(!comboBoxPlanetArrayList.isEmpty()) {
            comboBoxPlanet.setItems(FXCollections.observableArrayList(comboBoxPlanetArrayList));
            comboBoxPlanet.setValue(comboBoxPlanetArrayList.get(0));
        }
        ArrayList<ComboBoxShip> comboBoxShipsArrayList = ComboBoxShip.list();

        if(!comboBoxShipsArrayList.isEmpty()){
            comboBoxShip.setItems(FXCollections.observableArrayList(comboBoxShipsArrayList));
            comboBoxShip.setValue(comboBoxShipsArrayList.get(0));
        }
        comboBoxShip.valueProperty().addListener((observable, oldValue, newValue) -> {
            Planet planet = comboBoxPlanet.getValue().getPlanet();
            if(newValue != null){
                Ship ship = comboBoxShip.getValue().getShip();
                botWindowController.setRequirementsTechnology(ship.getDataTechnology().getRequiredTechnologies(),planet);
            }
        });
        itemShipLists.clear();
        updateVBoxAddedShips();
    }

    private BotWindowController botWindowController;
    public void setBotWindowController(BotWindowController botWindowController) {
        this.botWindowController = botWindowController;
    }
    public void saveShipsList() {
        selectedExpedition.setItemShipLists(new ArrayList<>(itemShipLists));
        selectedExpedition = null;
        disableEditButton();
        clearShipsList();
        updateVBoxAddedShips();
        DataLoader.expeditions.save();
    }

    public void setSelectedExpedition(Expedition selectedExpedition) {
        this.selectedExpedition = selectedExpedition;
    }

    public void setItemShipLists(ArrayList<ItemShipList> itemShipLists) {
        this.itemShipLists = itemShipLists;
    }

    /**
     * @param expedition ***
     * @return If an expedition is selected returns true.
     */
    public boolean isExpeditionContainerSelected(Expedition expedition){
        return expedition.equals(selectedExpedition);
    }

    /**
     * @return If any expedition is selected returns true.
     */
    public boolean isExpeditionContainerSelected(){
        return selectedExpedition != null;
    }

    public void disableEditButton(){
        editButton.setDisable(!editButton.isDisable());
    }
    public void clearShipsList(){
        itemShipLists.clear();
    }



}
