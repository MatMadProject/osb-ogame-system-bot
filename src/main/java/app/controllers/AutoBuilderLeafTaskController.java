package app.controllers;

import app.controllers_connector.AutoBuilderLeafTaskItemConnector;
import app.data.DataLoader;
import app.data.autobuilder.ItemAutoBuilder;
import app.data.planets.ComboBoxPlanet;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import ogame.buildings.Building;
import ogame.buildings.DataTechnology;
import ogame.planets.Planet;
import ogame.utils.log.AppLog;

import java.util.ArrayList;
import java.util.List;


public class AutoBuilderLeafTaskController {
    @FXML
    private ComboBox<ComboBoxPlanet> comboBoxPlanet;

    @FXML
    private ComboBox<ComboBoxBuilding> comboBoxBuilding;

    @FXML
    private Label labelUpgradeLevel;

    @FXML
    private Button buttonAdd;

    @FXML
    private VBox vBoxQueue;

    @FXML
    private VBox vBoxHistory;

    public void update(){
        ArrayList<ComboBoxPlanet> comboBoxPlanetArrayList = ComboBoxPlanet.list(DataLoader.planets.getPlanetList());
        if(!comboBoxPlanetArrayList.isEmpty()){
            comboBoxPlanet.setItems(FXCollections.observableArrayList(comboBoxPlanetArrayList));
            comboBoxPlanet.setValue(comboBoxPlanetArrayList.get(0));

            comboBoxPlanet.valueProperty().addListener((observable, oldValue, newValue) -> {

                if(newValue != null){
                    Planet planet = newValue.getPlanet();
                    ArrayList<ComboBoxBuilding> comboBoxBuildingArrayList = ComboBoxBuilding.list(planet);
                    comboBoxBuilding.setItems(FXCollections.observableArrayList(comboBoxBuildingArrayList));
                    comboBoxBuilding.setValue(comboBoxBuildingArrayList.get(0));
                }
            });

            comboBoxBuilding.valueProperty().addListener((observable, oldValue, newValue) -> {
                if(newValue != null){
                    Building building = newValue.getBuilding();
                    int upgradeLevel = DataLoader.listItemAutoBuilder.getHighestLevelOfBuildingOnQueue(building)+1;
                    labelUpgradeLevel.setText(upgradeLevel+"");
                }
                else{
                    Building building = oldValue.getBuilding();
                    int upgradeLevel = DataLoader.listItemAutoBuilder.getHighestLevelOfBuildingOnQueue(building)+1;
                    labelUpgradeLevel.setText(upgradeLevel+"");
                }
            });
        }
    }

    public void add() {
        Planet planet = comboBoxPlanet.getValue().getPlanet();
        Building building = comboBoxBuilding.getValue().getBuilding();
        int upgradeLevel = Integer.parseInt(labelUpgradeLevel.getText());

        if(building != null){
            ItemAutoBuilder itemAutoBuilder = new ItemAutoBuilder(planet,building, upgradeLevel, System.currentTimeMillis());
            if(DataLoader.listItemAutoBuilder.addToQueue(itemAutoBuilder)){
                AutoBuilderLeafTaskItemConnector autoBuilderLeafTaskItemConnector = new AutoBuilderLeafTaskItemConnector(itemAutoBuilder, this);

                vBoxQueue.getChildren().add(autoBuilderLeafTaskItemConnector.content());
                AppLog.print(AutoBuilderLeafTaskController.class.getName(),2,"Add to built queue: Upgrade "+ building.getName() + " to "
                        + upgradeLevel +" level on " + planet.getCoordinate().getText() + ".");
            }
        }
        else
            AppLog.print(AutoBuilderLeafTaskController.class.getName(),2,"Add to built queue fails because building is "+ building.getName() + ".");
    }

    public void updateHistoryList(){
       ArrayList<ItemAutoBuilder> historyList = DataLoader.listItemAutoBuilder.getHistoryList();
       vBoxHistory.getChildren().clear();
       for(ItemAutoBuilder itemAutoBuilder : historyList){
           AutoBuilderLeafTaskItemConnector autoBuilderLeafTaskItemConnector = new AutoBuilderLeafTaskItemConnector(itemAutoBuilder, this);
           vBoxHistory.getChildren().add(autoBuilderLeafTaskItemConnector.content());
       }
    }

    public void updateQueueList(){
        ArrayList<ItemAutoBuilder> queueList = DataLoader.listItemAutoBuilder.getQueueList();
//        if(queueList.size() != vBoxQueue.getChildren().size()) {
            vBoxQueue.getChildren().clear();
            for(ItemAutoBuilder itemAutoBuilder : queueList){
                AutoBuilderLeafTaskItemConnector autoBuilderLeafTaskItemConnector = new AutoBuilderLeafTaskItemConnector(itemAutoBuilder, this);
                vBoxQueue.getChildren().add(autoBuilderLeafTaskItemConnector.content());
            }
//        }
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

            for(DataTechnology dataTechnology: DataTechnology.values()){
                if(dataTechnology.equals(DataTechnology.RESBUGGY) || dataTechnology.equals(DataTechnology.SOLAR_SATELITE)
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
