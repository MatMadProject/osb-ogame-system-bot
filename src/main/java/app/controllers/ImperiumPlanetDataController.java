package app.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import ogame.DataTechnology;
import ogame.planets.Planet;
import ogame.utils.watch.Calendar;

public class ImperiumPlanetDataController {

    @FXML
    public Label labelRoboticsFactory;
    @FXML
    public Label labelShipyard;
    @FXML
    public Label labelResearchLaboratory;
    @FXML
    public Label labelAllianceDepot;
    @FXML
    public Label labelMissileSilo;
    @FXML
    public Label labelNaniteFactory;
    @FXML
    public Label labelTerraformer;
    @FXML
    public Label labelRepairDock;
    @FXML
    public Label labelEnergy;
    @FXML
    public Label labelMetalResource;
    @FXML
    public Label labelCrystalResource;
    @FXML
    public Label labelDeuteriumResource;
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
        labelMetalResource.setText(planet.getResources().getMetal()+"");
        labelCrystalResource.setText(planet.getResources().getCrystal()+"");
        labelDeuteriumResource.setText(planet.getResources().getDeuterium()+"");
        labelEnergy.setText(planet.getResources().getEnergy()+"");
        labelMetalMine.setText(planet.getBuilding(DataTechnology.METAL_MINE).getLevel()+"");
        labelCrystalMine.setText(planet.getBuilding(DataTechnology.CRYSTAL_MINE).getLevel()+"");
        labelDeuteriumSynth.setText(planet.getBuilding(DataTechnology.DEUTERIUM_SYNTHESIZER).getLevel()+"");
        labelSolarPlant.setText(planet.getBuilding(DataTechnology.SOLAR_PLANT).getLevel()+"");
        labelFusionPlant.setText(planet.getBuilding(DataTechnology.FUSION_PLANT).getLevel()+"");
        labelSolarSatelite.setText(planet.getBuilding(DataTechnology.SOLAR_SATELITE).getLevel()+"");
        labelResbuggy.setText(planet.getBuilding(DataTechnology.RESBUGGY).getLevel()+"");
        labelMetalStorage.setText(planet.getBuilding(DataTechnology.METAL_STORAGE).getLevel()+"");
        labelCrystalStorage.setText(planet.getBuilding(DataTechnology.CRYSTAL_STORAGE).getLevel()+"");
        labelDeuteriumStorage.setText(planet.getBuilding(DataTechnology.DEUTERIUM_STORAGE).getLevel()+"");
        labelRoboticsFactory.setText(planet.getBuilding(DataTechnology.ROBOTICS_FACTORY).getLevel()+"");
        labelShipyard.setText(planet.getBuilding(DataTechnology.SHIPYARD).getLevel()+"");
        labelResearchLaboratory.setText(planet.getBuilding(DataTechnology.RESEARCH_LABORATORY).getLevel()+"");
        labelAllianceDepot.setText(planet.getBuilding(DataTechnology.ALLIANCE_DEPOT).getLevel()+"");
        labelMissileSilo.setText(planet.getBuilding(DataTechnology.MISSILE_SILO).getLevel()+"");
        labelNaniteFactory.setText(planet.getBuilding(DataTechnology.NANITE_FACTORY).getLevel()+"");
        labelTerraformer.setText(planet.getBuilding(DataTechnology.TERRAFORMER).getLevel()+"");
        labelRepairDock.setText(planet.getBuilding(DataTechnology.REPAIR_DOCK).getLevel()+"");
    }
}
