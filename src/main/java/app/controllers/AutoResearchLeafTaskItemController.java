package app.controllers;

import app.data.DataLoader;
import app.data.autoresearch.ItemAutoResearch;
import app.data.autoresearch.Status;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import ogame.utils.log.AppLog;
import ogame.utils.watch.Calendar;

public class AutoResearchLeafTaskItemController {
    public Label labelDown;
    public Label labelUp;

    @FXML
    private Label labelPlanet;

    @FXML
    private Label labelResearch;

    @FXML
    private Label labelLevel;

    @FXML
    private Label labelStartTime;

    @FXML
    private Label labelFinishTime;

    @FXML
    private Label labelStatus;

    @FXML
    private Label labelStatusTime;

    private AutoResearchLeafTaskController autoResearchLeafTaskController;
    private ItemAutoResearch itemAutoResearch;

    @FXML
    void delete() {
        if(itemAutoResearch.getStatus() == Status.FINISHED){
            DataLoader.listItemAutoResearch.getHistoryList().remove(itemAutoResearch);
            autoResearchLeafTaskController.updateHistoryList();
            AppLog.print(AutoResearchLeafTaskItemController.class.getName(),2,"Remove research from history: Upgrade "+ itemAutoResearch.getResearch().getName() + " to "
                    + itemAutoResearch.getUpgradeLevel() +" level on " + itemAutoResearch.getPlanet().getCoordinate().getText() + ".");
        }else{
            DataLoader.listItemAutoResearch.getQueueList().remove(itemAutoResearch);
            autoResearchLeafTaskController.updateQueueList();
            AppLog.print(AutoResearchLeafTaskItemController.class.getName(),2,"Remove research from queue: Upgrade "+ itemAutoResearch.getResearch().getName() + " to "
                    + itemAutoResearch.getUpgradeLevel() +" level on " + itemAutoResearch.getPlanet().getCoordinate().getText() + ".");
        }
    }

    @FXML
    void down(MouseEvent event) {

    }

    @FXML
    void up(MouseEvent event) {

    }

    public void create(ItemAutoResearch itemAutoResearch){
        labelPlanet.setText(itemAutoResearch.getPlanet().getCoordinate().getText());
        labelResearch.setText(itemAutoResearch.getResearch().getName());
        labelLevel.setText((itemAutoResearch.getUpgradeLevel())+"");
        labelStartTime.setText(Calendar.getDateTime(itemAutoResearch.getStartTime()));
        if(itemAutoResearch.getTimer() != null)
            labelFinishTime.setText(itemAutoResearch.getTimer().leftTime());
        else
            labelFinishTime.setText(Calendar.getDateTime(itemAutoResearch.getFinishTime()));
        labelStatus.setText(itemAutoResearch.getStatus()+"");
        labelStatusTime.setText(Calendar.getDateTime(itemAutoResearch.getStatusTime()));
    }

    public void createHistoryItem(ItemAutoResearch itemAutoResearch){
        labelPlanet.setText(itemAutoResearch.getPlanet().getCoordinate().getText());
        labelResearch.setText(itemAutoResearch.getResearch().getName());
        labelLevel.setText((itemAutoResearch.getUpgradeLevel())+"");
        labelStartTime.setText(Calendar.getDateTime(itemAutoResearch.getStartTime()));
        labelFinishTime.setText(Calendar.getDateTime(itemAutoResearch.getFinishTime()));
        labelStatus.setText(itemAutoResearch.getStatus()+"");
        labelStatusTime.setText(Calendar.getDateTime(itemAutoResearch.getStatusTime()));
        labelDown.setDisable(true);
        labelUp.setDisable(true);
    }

    public void update(){
        if(itemAutoResearch.getTimer() != null)
            labelFinishTime.setText(itemAutoResearch.getTimer().leftTime());
        else
            labelFinishTime.setText(Calendar.getDateTime(itemAutoResearch.getFinishTime()));
        labelStatus.setText(itemAutoResearch.getStatus()+"");
        labelStatusTime.setText(Calendar.getDateTime(itemAutoResearch.getStatusTime()));
    }

    public void setAutoBuilderLeafTaskController(AutoResearchLeafTaskController autoResearchLeafTaskController) {
        this.autoResearchLeafTaskController = autoResearchLeafTaskController;
    }

    public void setItemAutoResearch(ItemAutoResearch itemAutoResearch) {
        this.itemAutoResearch = itemAutoResearch;
    }
}
