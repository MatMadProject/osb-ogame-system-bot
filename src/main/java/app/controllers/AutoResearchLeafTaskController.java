package app.controllers;

import app.controllers_connector.AutoResearchLeafTaskItemConnector;
import app.data.DataLoader;
import app.data.autobuilder.ItemAutoResearch;
import app.data.planets.ComboBoxPlanet;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import ogame.planets.Planet;
import ogame.researches.Research;
import ogame.utils.log.AppLog;

import java.util.ArrayList;

public class AutoResearchLeafTaskController {

    @FXML
    private ComboBox<ComboBoxPlanet> comboBoxPlanet;

    @FXML
    private ComboBox<ComboBoxResearch> comboBoxResearch;

    @FXML
    private Label labelUpgradeLevel;

    @FXML
    private VBox vBoxQueue;

    @FXML
    private VBox vBoxHistory;
    private BotWindowController botWindowController;

    public void add() {
        Planet planet = (Planet) comboBoxPlanet.getValue().getObject();
        Research research = comboBoxResearch.getValue().getResearch();
        research.setRequiredResources(null);
        research.setProductionTime(null);
        int upgradeLevel = Integer.parseInt(labelUpgradeLevel.getText());

        ItemAutoResearch itemAutoResearch = new ItemAutoResearch(planet,research, upgradeLevel, System.currentTimeMillis());
        if(DataLoader.listItemAutoResearch.addToQueue(itemAutoResearch)){
            AutoResearchLeafTaskItemConnector autoResearchLeafTaskItemConnector = new AutoResearchLeafTaskItemConnector(itemAutoResearch,this);
            vBoxQueue.getChildren().add(autoResearchLeafTaskItemConnector.content());
            connectorArrayList.add(autoResearchLeafTaskItemConnector);
            AppLog.print(AutoResearchLeafTaskController.class.getName(),2,"Add to research queue: Upgrade "+ research.getName() + " to "
                    + upgradeLevel +" level on " + planet.getCoordinate().getText() + ".");
        }
    }

    public void update() {
        ArrayList<ComboBoxPlanet> comboBoxPlanetArrayList = ComboBoxPlanet.planetList(DataLoader.planets.getPlanetList());
        if(!comboBoxPlanetArrayList.isEmpty()) {
            comboBoxPlanet.setItems(FXCollections.observableArrayList(comboBoxPlanetArrayList));
            comboBoxPlanet.setValue(comboBoxPlanetArrayList.get(0));
        }
        ArrayList<AutoResearchLeafTaskController.ComboBoxResearch> comboBoxResearchArrayList = AutoResearchLeafTaskController.ComboBoxResearch.list();
        if(!comboBoxResearchArrayList.isEmpty()){
            comboBoxResearch.setItems(FXCollections.observableArrayList(comboBoxResearchArrayList));
            comboBoxResearch.setValue(comboBoxResearchArrayList.get(0));
        }
        comboBoxResearch.valueProperty().addListener((observable, oldValue, newValue) -> {
            Planet planet = (Planet) comboBoxPlanet.getValue().getObject();
            if(newValue != null){
                    Research research = newValue.getResearch();
                    int upgradeLevel = DataLoader.listItemAutoResearch.getHighestLevelOfResearchOnQueue(research)+1;
                    labelUpgradeLevel.setText(upgradeLevel+"");

                    botWindowController.setRequirementsTechnology(research.getDataTechnology().getRequiredTechnologies(),planet);
                }
            });
        updateQueueList();
        updateHistoryList();
    }

    public void updateHistoryList() {
        ArrayList<ItemAutoResearch> historyList = DataLoader.listItemAutoResearch.get50LatestItemOfHistoryList();
        if(historyList.size() != vBoxHistory.getChildren().size()){
            vBoxHistory.getChildren().clear();
            for(ItemAutoResearch itemAutoResearch : historyList){
                AutoResearchLeafTaskItemConnector autoResearchLeafTaskItemConnector = new AutoResearchLeafTaskItemConnector(itemAutoResearch, this);
                vBoxHistory.getChildren().add(autoResearchLeafTaskItemConnector.contentHistoryItem());
            }
        }
    }

    private final ArrayList<AutoResearchLeafTaskItemConnector> connectorArrayList = new ArrayList<>();
    public void updateQueueList() {
        ArrayList<ItemAutoResearch> queueList = DataLoader.listItemAutoResearch.getQueueList();
        if(queueList.size() != vBoxQueue.getChildren().size()) {
            vBoxQueue.getChildren().clear();
            connectorArrayList.clear();
            for (ItemAutoResearch itemAutoResearch : queueList) {
                AutoResearchLeafTaskItemConnector autoResearchLeafTaskItemConnector = new AutoResearchLeafTaskItemConnector(itemAutoResearch, this);
                vBoxQueue.getChildren().add(autoResearchLeafTaskItemConnector.content());
                connectorArrayList.add(autoResearchLeafTaskItemConnector);
            }
        }else
            for(AutoResearchLeafTaskItemConnector connector : connectorArrayList)
                connector.getController().update();
    }

    public void setBotWindowController(BotWindowController botWindowController) {
        this.botWindowController = botWindowController;
    }

    static class ComboBoxResearch {
        final Research RESEARCH;
        final String VALUE;

        public ComboBoxResearch(Research research) {
            this.RESEARCH = research;
            this.VALUE = research.getName();
        }

        static ArrayList<AutoResearchLeafTaskController.ComboBoxResearch> list (){
            ArrayList<AutoResearchLeafTaskController.ComboBoxResearch> list = new ArrayList<>();

            for(Research research : DataLoader.researches.getResearchList())
                list.add(new AutoResearchLeafTaskController.ComboBoxResearch(research));

            return list;
        }

        public Research getResearch() {
            return RESEARCH;
        }

        @Override
        public String toString() {
            return VALUE;
        }
    }
}
