package app.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import ogame.buildings.DataTechnology;
import ogame.planets.Planet;
import ogame.utils.watch.Calendar;

import java.util.Locale;

public class ImperiumPlanetDataController {

    @FXML
    private Label labelLastUpdate;

    @FXML
    private Label labelId;

    @FXML
    private Label labelCoordinate;

    @FXML
    private Label labelName;

    @FXML
    private Label labelDiameter;

    @FXML
    private Label labelFields;

    @FXML
    private Label labelTemperature;

    @FXML
    private Label labelMetal;

    @FXML
    private Label labelCrystal;

    @FXML
    private Label labelDeuterium;

    @FXML
    private Label labelMetalMine;

    @FXML
    private Label labelCrystalMine;

    @FXML
    private Label labelDeuteriumSynth;

    @FXML
    private Label labelSolarPlant;

    @FXML
    private Label labelFusionPlant;

    @FXML
    private Label labelSolarSatelite;

    @FXML
    private Label labelResbuggy;

    @FXML
    private Label labelMetalStorage;

    @FXML
    private Label labelCrystalStorage;

    @FXML
    private Label labelDeuteriumStorage;

    public void update(Planet planet){
        labelLastUpdate.setText(Calendar.getDateTime(planet.getUpdateTime()));
        labelId.setText(planet.getId());
        labelCoordinate.setText(planet.getCoordinate().getText());
        labelName.setText(planet.getName());
        labelDiameter.setText(planet.getDiameter()+"");
        labelFields.setText(planet.getFields() != null ? planet.getFields().getBuiltUp()+" / "+planet.getFields().getMax():"");
        labelTemperature.setText(planet.getTemperature() != null ?planet.getTemperature().getMin() + " - " + planet.getTemperature().getMax():"");
        labelMetal.setText(String.format("%.0f",planet.getResourcesProduction().getMetalPerHour()));
        labelCrystal.setText(String.format("%.0f",planet.getResourcesProduction().getCrystalPerHour()));
        labelDeuterium.setText(String.format("%.0f",planet.getResourcesProduction().getDeuteriumPerHour()));
        labelMetalMine.setText(planet.getBuilding(DataTechnology.METAL_MINE).getLevel()+"");
        labelCrystalMine.setText(planet.getBuilding(DataTechnology.CRYSTAL_MINE).getLevel()+"");
        labelDeuteriumSynth.setText(planet.getBuilding(DataTechnology.DEUTERIUM_STORAGE).getLevel()+"");
        labelSolarPlant.setText(planet.getBuilding(DataTechnology.SOLAR_PLANT).getLevel()+"");
        labelFusionPlant.setText(planet.getBuilding(DataTechnology.FUSION_PLANT).getLevel()+"");
        labelSolarSatelite.setText(planet.getBuilding(DataTechnology.SOLAR_SATELITE).getLevel()+"");
        labelResbuggy.setText(planet.getBuilding(DataTechnology.RESBUGGY).getLevel()+"");
        labelMetalStorage.setText(planet.getBuilding(DataTechnology.METAL_STORAGE).getLevel()+"");
        labelCrystalStorage.setText(planet.getBuilding(DataTechnology.CRYSTAL_STORAGE).getLevel()+"");
        labelDeuteriumStorage.setText(planet.getBuilding(DataTechnology.DEUTERIUM_STORAGE).getLevel()+"");
    }
}
