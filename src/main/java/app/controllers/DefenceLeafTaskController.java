package app.controllers;

import app.controllers_connector.DefenceLeafTaskItemConnector;
import app.data.DataLoader;
import app.data.defence.ComboBoxDefence;
import app.data.defence.DefenceItem;
import app.data.planets.ComboBoxPlanet;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import ogame.DataTechnology;
import ogame.defence.Defence;
import ogame.planets.Planet;
import ogame.utils.log.AppLog;

import java.util.ArrayList;
import java.util.List;

public class DefenceLeafTaskController {
    public ComboBox<ComboBoxPlanet> comboBoxPlanet;
    public ComboBox<ComboBoxDefence> comboBoxDefence;
    public TextField textFieldValue;
    public TextField textFieldHour;
    public TextField textFieldMinute;
    public TextField textFieldSeconds;
    public VBox vBoxHistory;
    public Label labelError;
    public VBox vBoxQueue;
    private long errorTimeStamp = 0;

    private BotWindowController botWindowController;

    public BotWindowController getBotWindowController() {
        return botWindowController;
    }

    public void setBotWindowController(BotWindowController botWindowController) {
        this.botWindowController = botWindowController;
    }

    public void update() {
        ArrayList<ComboBoxPlanet> comboBoxPlanetArrayList = ComboBoxPlanet.list(DataLoader.planets.getPlanetList());
        if(!comboBoxPlanetArrayList.isEmpty()) {
            comboBoxPlanet.setItems(FXCollections.observableArrayList(comboBoxPlanetArrayList));
            comboBoxPlanet.setValue(comboBoxPlanetArrayList.get(0));
        }
        ArrayList<ComboBoxDefence> comboBoxDefenceList = ComboBoxDefence.list();

        if(!comboBoxDefenceList.isEmpty()){
            comboBoxDefence.setItems(FXCollections.observableArrayList(comboBoxDefenceList));
            comboBoxDefence.setValue(comboBoxDefenceList.get(0));
        }

        comboBoxDefence.valueProperty().addListener((observable, oldValue, newValue) -> {
            Planet planet = comboBoxPlanet.getValue().getPlanet();
            if(newValue != null){
                Defence defence = comboBoxDefence.getValue().getDefence();
                botWindowController.setRequirementsTechnology(defence.getDataTechnology().getRequiredTechnologies(),planet);
            }
        });
        updateQueueList();
        updateHistoryList();
    }

    private final ArrayList<DefenceLeafTaskItemConnector> connectors = new ArrayList<>();
    @FXML
    public void add() {
        Planet planet = comboBoxPlanet.getValue().getPlanet();
        Defence defence = comboBoxDefence.getValue().getDefence();
        int value = Integer.parseInt(textFieldValue.getText().equals("") ? "0":textFieldValue.getText());
        int hour = Integer.parseInt(textFieldHour.getText().equals("") ? "0":textFieldHour.getText());
        int minute = Integer.parseInt(textFieldMinute.getText().equals("") ? "0":textFieldMinute.getText());
        int seconds = Integer.parseInt(textFieldSeconds.getText().equals("") ? "0":textFieldSeconds.getText());
        long time = seconds + minute*60L + hour*3600L;
        if(defence.getDataTechnology() == DataTechnology.SHIELD_DOME_SMALL ||
                defence.getDataTechnology() == DataTechnology.SHIELD_DOME_SMALL)
            value = 1;

        DefenceItem defenceItem = new DefenceItem(planet,defence,value,time);
        if(DataLoader.listDefenceItem.add(defenceItem)){
            DefenceLeafTaskItemConnector connector = new DefenceLeafTaskItemConnector(defenceItem,this);
            vBoxQueue.getChildren().add(connector.content());
            connectors.add(connector);
            AppLog.print(DefenceLeafTaskController.class.getName(),2,"Add to built queue: "+ defence.getName() + " value " + value +
                    " on " + planet.getCoordinate().getText() + ".");
        }else
            setError(defence.getName() + " on " + planet.getCoordinate().getText() + " exist on list.");
    }

    public void updateQueueList(){
        List<DefenceItem> queueList = DataLoader.listDefenceItem.getQueueList();
        if(queueList.size() != vBoxQueue.getChildren().size()) {
            vBoxQueue.getChildren().clear();
            connectors.clear();
            for(DefenceItem defenceItem : queueList){
                DefenceLeafTaskItemConnector connector = new DefenceLeafTaskItemConnector(defenceItem,this);
                vBoxQueue.getChildren().add(connector.content());
                connectors.add(connector);
            }
        }else
            for(DefenceLeafTaskItemConnector connector : connectors)
                connector.getController().update();
    }

    public void updateHistoryList(){
        List<DefenceItem> historyList = DataLoader.listDefenceItem.get50LatestItemOfHistoryList();
        if(historyList.size() != vBoxHistory.getChildren().size()){
            vBoxHistory.getChildren().clear();
            for(DefenceItem defenceItem : historyList){
                DefenceLeafTaskItemConnector connector = new DefenceLeafTaskItemConnector(defenceItem,this);
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
