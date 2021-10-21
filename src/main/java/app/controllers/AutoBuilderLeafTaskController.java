package app.controllers;

import app.data.DataLoader;
import app.data.planets.ComboBoxPlanet;
import app.data.planets.Planets;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import ogame.buildings.Building;
import ogame.buildings.DataTechnology;
import ogame.planets.Planet;

import java.util.ArrayList;


public class AutoBuilderLeafTaskController {
    @FXML
    private ComboBox<ComboBoxPlanet> comboBoxPlanet;

    @FXML
    private ComboBox<ComboBoxBuilding> comboBoxBuilding;

    @FXML
    private Label labelCurrentLevel;

    @FXML
    private Button buttonAdd;

    @FXML
    private VBox vBoxQueue;

    @FXML
    private VBox vBoxHistory;

    public void update(){
        ArrayList<ComboBoxPlanet> comboBoxPlanetArrayList = ComboBoxPlanet.list(DataLoader.planets.getPlanetList());
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
                labelCurrentLevel.setText(newValue.getBuilding().getLevel()+"");
            }
        });
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
