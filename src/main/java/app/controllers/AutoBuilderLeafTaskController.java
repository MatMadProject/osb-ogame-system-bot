package app.controllers;

import app.controllers_connector.AutoBuilderLeafTaskItemConnector;
import app.data.DataLoader;
import app.data.autobuilder.ItemAutoBuilder;
import app.data.planets.ComboBoxPlanet;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import ogame.Type;
import ogame.buildings.Building;
import ogame.DataTechnology;
import ogame.planets.Planet;
import ogame.utils.log.AppLog;

import java.util.ArrayList;

public class AutoBuilderLeafTaskController {
    @FXML
    private ComboBox<ComboBoxPlanet> comboBoxPlanet;

    @FXML
    private ComboBox<ComboBoxBuilding> comboBoxBuilding;

    @FXML
    private Label labelUpgradeLevel;

    @FXML
    private VBox vBoxQueue;

    @FXML
    private VBox vBoxHistory;

    Planet comboBoxPlanetValue;
    private BotWindowController botWindowController;
    public void update(){
        ArrayList<ComboBoxPlanet> comboBoxPlanetArrayList = ComboBoxPlanet.list(DataLoader.planets.getPlanetList());
        if(!comboBoxPlanetArrayList.isEmpty()){
            comboBoxPlanet.setItems(FXCollections.observableArrayList(comboBoxPlanetArrayList));
            comboBoxPlanet.setValue(comboBoxPlanetArrayList.get(0));

            comboBoxPlanet.valueProperty().addListener((observable, oldValue, newValue) -> {

                if(newValue != null){
                    comboBoxPlanetValue = newValue.getPlanet();
                    ArrayList<ComboBoxBuilding> comboBoxBuildingArrayList = ComboBoxBuilding.list(comboBoxPlanetValue);
                    comboBoxBuilding.setItems(FXCollections.observableArrayList(comboBoxBuildingArrayList));
                    comboBoxBuilding.setValue(comboBoxBuildingArrayList.get(0));
                }
            });

            comboBoxBuilding.valueProperty().addListener((observable, oldValue, newValue) -> {
                ArrayList<ItemAutoBuilder> planetQueue = DataLoader.listItemAutoBuilder.getQueueListFromPlanet(comboBoxPlanetValue);
                Building building;
                if(newValue != null){
                    building = newValue.getBuilding();
                    int upgradeLevel = DataLoader.listItemAutoBuilder.getHighestLevelOfBuildingOnQueue(building,planetQueue)+1;
                    labelUpgradeLevel.setText(upgradeLevel+"");
                }
                else{
                    building = oldValue.getBuilding();
                    int upgradeLevel = DataLoader.listItemAutoBuilder.getHighestLevelOfBuildingOnQueue(building,planetQueue)+1;
                    labelUpgradeLevel.setText(upgradeLevel+"");
                }
                botWindowController.setRequirementsTechnology(building.getDataTechnology().getRequiredTechnologies(),comboBoxPlanetValue);
            });
        }
        updateQueueList();
        updateHistoryList();
    }

    public void add() {
        Planet planet = comboBoxPlanet.getValue().getPlanet();
        Building building = comboBoxBuilding.getValue().getBuilding();
        building.setRequiredResources(null);
        building.setProductionTime(null);
        int upgradeLevel = Integer.parseInt(labelUpgradeLevel.getText());

        ItemAutoBuilder itemAutoBuilder = new ItemAutoBuilder(planet,building, upgradeLevel, System.currentTimeMillis());
        if(DataLoader.listItemAutoBuilder.addToQueue(itemAutoBuilder)){
            AutoBuilderLeafTaskItemConnector autoBuilderLeafTaskItemConnector = new AutoBuilderLeafTaskItemConnector(itemAutoBuilder, this);

            vBoxQueue.getChildren().add(autoBuilderLeafTaskItemConnector.content());
            connectorArrayList.add(autoBuilderLeafTaskItemConnector);
            AppLog.print(AutoBuilderLeafTaskController.class.getName(),2,"Add to built queue: Upgrade "+ building.getName() + " to "
                    + upgradeLevel +" level on " + planet.getCoordinate().getText() + ".");
        }
    }

    public void updateHistoryList(){
        ArrayList<ItemAutoBuilder> historyList = DataLoader.listItemAutoBuilder.get50LatestItemOfHistoryList();
        if(historyList.size() != vBoxHistory.getChildren().size()){
            vBoxHistory.getChildren().clear();
            for(ItemAutoBuilder itemAutoBuilder : historyList){
                AutoBuilderLeafTaskItemConnector autoBuilderLeafTaskItemConnector = new AutoBuilderLeafTaskItemConnector(itemAutoBuilder, this);
                vBoxHistory.getChildren().add(autoBuilderLeafTaskItemConnector.contentHistoryItem());
            }
        }
    }

    private final ArrayList<AutoBuilderLeafTaskItemConnector> connectorArrayList = new ArrayList<>();
    public void updateQueueList(){
        ArrayList<ItemAutoBuilder> queueList = DataLoader.listItemAutoBuilder.getQueueList();
        if(queueList.size() != vBoxQueue.getChildren().size()) {
            vBoxQueue.getChildren().clear();
            connectorArrayList.clear();
            for(ItemAutoBuilder itemAutoBuilder : queueList){
                AutoBuilderLeafTaskItemConnector autoBuilderLeafTaskItemConnector = new AutoBuilderLeafTaskItemConnector(itemAutoBuilder, this);
                vBoxQueue.getChildren().add(autoBuilderLeafTaskItemConnector.content());
                connectorArrayList.add(autoBuilderLeafTaskItemConnector);
            }
        }else
            for(AutoBuilderLeafTaskItemConnector connector : connectorArrayList)
                connector.getController().update();
    }

    public void setBotWindowController(BotWindowController botWindowController) {
        this.botWindowController = botWindowController;
    }

    static class  ComboBoxBuilding{
        final Building BUILDING;
        final String VALUE;

        public ComboBoxBuilding(Building building) {
            this.BUILDING = building;
            this.VALUE = building.getName();
        }

        static ArrayList<ComboBoxBuilding> list (Planet planet){
            ArrayList<ComboBoxBuilding> list = new ArrayList<>();

            ArrayList<DataTechnology> dataTechnologyArrayList = new ArrayList<>(DataTechnology.dataTechnologyList(Type.PRODUCTION));
            dataTechnologyArrayList.addAll(DataTechnology.dataTechnologyList(Type.TECHNOLOGIES));

            for(DataTechnology dataTechnology : dataTechnologyArrayList){
                if(dataTechnology.equals(DataTechnology.RESBUGGY_B) || dataTechnology.equals(DataTechnology.SOLAR_SATELITE_B)
                || dataTechnology.equals(DataTechnology.UNDEFINED))
                    continue;

                Building tmp = planet.getBuilding(dataTechnology);
                list.add(new ComboBoxBuilding(tmp));
            }
            return list;
        }

        public Building getBuilding() {
            return BUILDING;
        }

        @Override
        public String toString() {
            return VALUE;
        }
    }
}
