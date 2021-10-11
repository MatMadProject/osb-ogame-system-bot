package app.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import ogame.planets.Planet;
import ogame.utils.watch.Calendar;

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
//        labelMetal.setText();
//        labelCrystal.setText();
//        labelDeuterium.setText();
//        labelMetalMine.setText();
//        labelCrystalMine.setText();
//        labelDeuteriumSynth.setText();
//        labelSolarPlant.setText();
//        labelFusionPlant.setText();
//        labelSolarSatelite.setText();
//        labelResbuggy.setText();
//        labelMetalStorage.setText();
//        labelCrystalStorage.setText();
//        labelDeuteriumStorage.setText();
    }
}
