package app.controllers;

import app.controllers_connector.ShipLeafTaskItemConnector;
import app.data.DataLoader;
import app.data.expedition.ComboBoxShip;
import app.data.planets.ComboBoxPlanet;
import app.data.shipyard.ShipItem;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import ogame.planets.Planet;
import ogame.ships.Ship;
import ogame.utils.log.AppLog;

import java.util.ArrayList;
import java.util.List;

public class ShipLeafTaskController {
    public ComboBox<ComboBoxPlanet> comboBoxPlanet;
    public ComboBox<ComboBoxShip> comboBoxShip;
    public TextField textFieldValue;
    public TextField textFieldHour;
    public TextField textFieldMinute;
    public TextField textFieldSeconds;
    public VBox vBoxHistory;
    public Label labelError;
    public VBox vBoxQueue;
    public CheckBox checkBoxSingle;
    private long errorTimeStamp = 0;

    private BotWindowController botWindowController;

    public BotWindowController getBotWindowController() {
        return botWindowController;
    }

    public void setBotWindowController(BotWindowController botWindowController) {
        this.botWindowController = botWindowController;
    }

    @FXML
    public void initialize(){
        checkBoxSingle.selectedProperty().addListener((observable, oldValue, newValue) -> {
            textFieldHour.setDisable(newValue);
            textFieldMinute.setDisable(newValue);
            textFieldSeconds.setDisable(newValue);
        });
    }

    public void update() {
        ArrayList<ComboBoxPlanet> comboBoxPlanetArrayList = ComboBoxPlanet.list(DataLoader.planets.getPlanetList());
        if(!comboBoxPlanetArrayList.isEmpty()) {
            comboBoxPlanet.setItems(FXCollections.observableArrayList(comboBoxPlanetArrayList));
            comboBoxPlanet.setValue(comboBoxPlanetArrayList.get(0));
        }
        ArrayList<ComboBoxShip> comboBoxDefenceList = ComboBoxShip.allShips();

        if(!comboBoxDefenceList.isEmpty()){
            comboBoxShip.setItems(FXCollections.observableArrayList(comboBoxDefenceList));
            comboBoxShip.setValue(comboBoxDefenceList.get(0));
        }

        comboBoxShip.valueProperty().addListener((observable, oldValue, newValue) -> {
            Planet planet = comboBoxPlanet.getValue().getPlanet();
            if(newValue != null){
                Ship ship = comboBoxShip.getValue().getShip();
                botWindowController.setRequirementsTechnology(ship.getDataTechnology().getRequiredTechnologies(),planet);
            }
        });
        updateQueueList();
        updateHistoryList();
    }
    private final ArrayList<ShipLeafTaskItemConnector> connectors = new ArrayList<>();
    @FXML
    public void add() {
        Planet planet = comboBoxPlanet.getValue().getPlanet();
        Ship ship = comboBoxShip.getValue().getShip();
        int value = Integer.parseInt(textFieldValue.getText().equals("") ? "0":textFieldValue.getText());
        int hour = Integer.parseInt(textFieldHour.getText().equals("") ? "0":textFieldHour.getText());
        int minute = Integer.parseInt(textFieldMinute.getText().equals("") ? "0":textFieldMinute.getText());
        int seconds = Integer.parseInt(textFieldSeconds.getText().equals("") ? "0":textFieldSeconds.getText());
        long time = seconds + minute*60L + hour*3600L;

        ShipItem shipItem = new ShipItem(planet,ship,value,time);
        shipItem.setSingleExecute(checkBoxSingle.isSelected());
        if(DataLoader.listShipItem.add(shipItem)){
            ShipLeafTaskItemConnector connector = new ShipLeafTaskItemConnector(shipItem,this);
            vBoxQueue.getChildren().add(connector.content());
            connectors.add(connector);
            AppLog.print(ShipLeafTaskController.class.getName(),2,"Add to built queue: "+ ship.getName() + " value " + value +
                    " on " + planet.getCoordinate().getText() + ".");
        }else
            setError(ship.getName() + " on " + planet.getCoordinate().getText() + " exist on list.");
    }

    public void updateQueueList(){
        List<ShipItem> queueList = DataLoader.listShipItem.getQueueList();
        if(queueList.size() != vBoxQueue.getChildren().size()) {
            vBoxQueue.getChildren().clear();
            connectors.clear();
            for(ShipItem shipItem : queueList){
                ShipLeafTaskItemConnector connector = new ShipLeafTaskItemConnector(shipItem,this);
                vBoxQueue.getChildren().add(connector.content());
                connectors.add(connector);
            }
        }else
            for(ShipLeafTaskItemConnector connector : connectors)
                connector.getController().update();
    }

    public void updateHistoryList(){
        List<ShipItem> historyList = DataLoader.listShipItem.get50LatestItemOfHistoryList();
        if(historyList.size() != vBoxHistory.getChildren().size()){
            vBoxHistory.getChildren().clear();
            for(ShipItem shipItem : historyList){
                ShipLeafTaskItemConnector connector = new ShipLeafTaskItemConnector(shipItem,this);
                connector.getController().setHistoryItem();
                vBoxHistory.getChildren().add(connector.content());
            }
        }
    }

    public void updateDisplayError(){
        if(!displayError())
            labelError.setText("");
    }

    private void setError(String errorText){
        labelError.setText(errorText);
        errorTimeStamp = System.currentTimeMillis();
    }

    private boolean displayError(){
        long ERROR_DISPLAY_TIME = 3 * 1000L;
        return System.currentTimeMillis() - errorTimeStamp < ERROR_DISPLAY_TIME;
    }
}