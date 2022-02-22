package app.controllers;

import app.controllers_connector.TransportLeafTaskItemConnector;
import app.data.DataLoader;
import app.data.planets.ComboBoxPlanet;
import app.data.transport.TransportItem;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import ogame.planets.Planet;
import ogame.planets.Resources;
import ogame.utils.log.AppLog;

import java.util.ArrayList;
import java.util.List;

public class TransportLeafTaskController {
    @FXML
    private CheckBox checkBoxAutrotransport;

    @FXML
    private CheckBox checkBoxExplorer;

    @FXML
    private CheckBox checkBoxLargeTransporter;

    @FXML
    private CheckBox checkBoxSingle;

    @FXML
    private CheckBox checkBoxSmallTransporter;

    @FXML
    private ComboBox<ComboBoxPlanet> comboBoxPlanetEnd;

    @FXML
    private ComboBox<ComboBoxPlanet> comboBoxPlanetStart;

    @FXML
    private Label labelError;

    @FXML
    private TextField textFieldCrystal;

    @FXML
    private TextField textFieldDeuter;

    @FXML
    private TextField textFieldHour;

    @FXML
    private TextField textFieldMetal;

    @FXML
    private TextField textFieldMinute;

    @FXML
    private TextField textFieldSeconds;

    @FXML
    private VBox vBoxHistory;

    @FXML
    private VBox vBoxQueue;

    private long errorTimeStamp = 0;

    private BotWindowController botWindowController;
    private boolean selectedSmallTransporter;
    private boolean selectedLargeTransporter;
    private boolean selectedExplorer;
    private boolean selectedSingle;
    private boolean selectedAutotransport;
    @FXML
    public void initialize(){
        checkBoxSingle.selectedProperty().addListener((observable, oldValue, newValue) -> {
            textFieldHour.setDisable(newValue);
            textFieldMinute.setDisable(newValue);
            textFieldSeconds.setDisable(newValue);
            selectedSingle = newValue;
        });
        checkBoxAutrotransport.selectedProperty().addListener((observable, oldValue, newValue) -> {
            textFieldMetal.setDisable(newValue);
            textFieldCrystal.setDisable(newValue);
            textFieldDeuter.setDisable(newValue);
            selectedAutotransport = newValue;
        });
        checkBoxSmallTransporter.selectedProperty().addListener((observable, oldValue, newValue) -> selectedSmallTransporter = newValue);
        checkBoxLargeTransporter.selectedProperty().addListener((observable, oldValue, newValue) -> selectedLargeTransporter = newValue);
        checkBoxExplorer.selectedProperty().addListener((observable, oldValue, newValue) -> selectedExplorer = newValue);
    }
    @FXML
    void add() {
        Planet planetStart = comboBoxPlanetStart.getValue().getPlanet();
        Planet planetEnd = comboBoxPlanetEnd.getValue().getPlanet();
        int hour = Integer.parseInt(textFieldHour.getText().equals("") ? "0":textFieldHour.getText());
        int minute = Integer.parseInt(textFieldMinute.getText().equals("") ? "0":textFieldMinute.getText());
        int seconds = Integer.parseInt(textFieldSeconds.getText().equals("") ? "0":textFieldSeconds.getText());
        long time = seconds + minute*60L + hour*3600L;
        long metal = Long.parseLong(textFieldMetal.getText().equals("") ? "0" : textFieldMetal.getText());
        long crystal = Long.parseLong(textFieldCrystal.getText().equals("") ? "0" : textFieldCrystal.getText());
        long deuterium = Long.parseLong(textFieldDeuter.getText().equals("") ? "0" : textFieldDeuter.getText());

        TransportItem transportItem = new TransportItem(planetStart,planetEnd, time);
        transportItem.setAutotransport(selectedAutotransport);
        transportItem.setSingleExecute(selectedSingle);
        transportItem.setSelectedSmallTransporter(selectedSmallTransporter);
        transportItem.setSelectedLargeTransporter(selectedLargeTransporter);
        transportItem.setSelectedExplorer(selectedExplorer);
        if(!selectedAutotransport)
            transportItem.setDeclaredResources(new Resources(metal,crystal,deuterium,0));

        if(planetEnd.equals(planetStart)){
            setError("Selected the same planet for start and end flight.");
            return;
        }
        if(DataLoader.listTransportItem.add(transportItem)){
            TransportLeafTaskItemConnector connector = new TransportLeafTaskItemConnector(transportItem,this);
            vBoxQueue.getChildren().add(connector.content());
            connectors.add(connector);
            AppLog.print(TransportLeafTaskController.class.getName(),2,"Add to queue transport from " + planetStart.getCoordinate().getText()
                    + " to " + planetEnd.getCoordinate().getText());
        }else
            setError("On queue exists transport from " + planetStart.getCoordinate().getText() + " to "
                    + planetEnd.getCoordinate().getText());
    }
    public void update() {
        ArrayList<ComboBoxPlanet> comboBoxPlanetArrayList = ComboBoxPlanet.list(DataLoader.planets.getPlanetList());
        if(!comboBoxPlanetArrayList.isEmpty()) {
            comboBoxPlanetStart.setItems(FXCollections.observableArrayList(comboBoxPlanetArrayList));
            comboBoxPlanetStart.setValue(comboBoxPlanetArrayList.get(0));
            comboBoxPlanetEnd.setItems(FXCollections.observableArrayList(comboBoxPlanetArrayList));
            comboBoxPlanetEnd.setValue(comboBoxPlanetArrayList.get(1));
        }
        updateQueueList();
        updateHistoryList();
    }
    public void updateHistoryList() {
        List<TransportItem> historyList = DataLoader.listTransportItem.get50LatestItemOfHistoryList();
        if(historyList.size() != vBoxHistory.getChildren().size()){
            vBoxHistory.getChildren().clear();
            for(TransportItem transportItem : historyList){
                TransportLeafTaskItemConnector connector = new TransportLeafTaskItemConnector(transportItem,this);
                connector.getController().setHistoryItem();
                vBoxHistory.getChildren().add(connector.content());
            }
        }
    }
    private final ArrayList<TransportLeafTaskItemConnector> connectors = new ArrayList<>();
    public void updateQueueList() {
        List<TransportItem> queueList = DataLoader.listTransportItem.getQueueList();
        if(queueList.size() != vBoxQueue.getChildren().size()) {
            vBoxQueue.getChildren().clear();
            connectors.clear();
            for(TransportItem transportItem : queueList){
                TransportLeafTaskItemConnector connector = new TransportLeafTaskItemConnector(transportItem,this);
                vBoxQueue.getChildren().add(connector.content());
                connectors.add(connector);
            }
        }else
            for(TransportLeafTaskItemConnector connector : connectors)
                connector.getController().update();
    }
    public BotWindowController getBotWindowController() {
        return botWindowController;
    }

    public void setBotWindowController(BotWindowController botWindowController) {
        this.botWindowController = botWindowController;
    }
    
    public void updateDisplayError(){
        if(!displayError()){
            labelError.getStyleClass().remove("error-label");
            labelError.setText("");
        }
    }
    private void setError(String errorText){
        labelError.setText(errorText);
        labelError.getStyleClass().add("error-label");
        errorTimeStamp = System.currentTimeMillis();
    }
    private boolean displayError(){
        long ERROR_DISPLAY_TIME = 3 * 1000L;
        return System.currentTimeMillis() - errorTimeStamp < ERROR_DISPLAY_TIME;
    }

}
