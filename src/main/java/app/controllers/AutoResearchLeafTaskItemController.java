package app.controllers;

import app.data.DataLoader;
import app.data.autoresearch.ItemAutoResearch;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import ogame.utils.log.AppLog;
import ogame.utils.watch.Calendar;
import ogame.utils.watch.Timer;

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
    private boolean historyItem = false;

    @FXML
    void delete() {
        if(historyItem){
            DataLoader.listItemAutoResearch.getHistoryList().remove(itemAutoResearch);
            autoResearchLeafTaskController.updateHistoryList();
            AppLog.print(AutoResearchLeafTaskItemController.class.getName(),2,"Remove research from history: Upgrade "+ itemAutoResearch.getResearch().getName() + " to "
                    + itemAutoResearch.getUpgradeLevel() +" level on " + itemAutoResearch.getPlanet().getCoordinate().getText() + ".");
        }else{
            DataLoader.listItemAutoResearch.getQueueList().remove(itemAutoResearch);
            autoResearchLeafTaskController.updateQueueList();
            AppLog.print(AutoResearchLeafTaskItemController.class.getName(),2,"Remove research from queue: Upgrade "+ itemAutoResearch.getResearch().getName() + " to "
                    + itemAutoResearch.getUpgradeLevel() +" level on " + itemAutoResearch.getPlanet().getCoordinate().getText() + ".");
            if(itemAutoResearch.isFirstOnQueue())
                DataLoader.listItemAutoResearch.startNextResearchOnQueue();
        }
        DataLoader.listItemAutoResearch.save();
    }

    @FXML
    void down() {

    }

    @FXML
    void up() {

    }

    public void create(ItemAutoResearch itemAutoResearch){
        labelPlanet.setText(itemAutoResearch.getPlanet().getCoordinate().getText());
        labelResearch.setText(itemAutoResearch.getResearch().getName());
        labelLevel.setText((itemAutoResearch.getUpgradeLevel())+"");
        labelStartTime.setText(Calendar.getDateTime(itemAutoResearch.getAddTimeToQueueInMilliseconds()));
        labelFinishTime.setText(itemAutoResearch.getEndTimeInSeconds() == 0 ? "--:--:--" : Timer.leftTimeSecond(itemAutoResearch.getEndTimeInSeconds()));
        labelStatus.setText(itemAutoResearch.getStatus()+"");
        labelStatusTime.setText(Calendar.getDateTime(itemAutoResearch.getStatusTimeInMilliseconds()));
    }

    public void createHistoryItem(ItemAutoResearch itemAutoResearch){
        labelPlanet.setText(itemAutoResearch.getPlanet().getCoordinate().getText());
        labelResearch.setText(itemAutoResearch.getResearch().getName());
        labelLevel.setText((itemAutoResearch.getUpgradeLevel())+"");
        labelStartTime.setText(Calendar.getDateTime(itemAutoResearch.getAddTimeToQueueInMilliseconds()));
        labelFinishTime.setText(Calendar.getDateTime(itemAutoResearch.getFinishTimeInMilliseconds()));
        labelStatus.setText(itemAutoResearch.getStatus()+"");
        labelStatusTime.setText(Calendar.getDateTime(itemAutoResearch.getStatusTimeInMilliseconds()));
        labelDown.setDisable(true);
        labelUp.setDisable(true);
    }

    public void update(){
        labelFinishTime.setText(itemAutoResearch.getEndTimeInSeconds() == 0 ? "--:--:--" : Timer.leftTimeSecond(itemAutoResearch.getEndTimeInSeconds()));
        labelStatus.setText(itemAutoResearch.getStatus()+"");
        labelStatusTime.setText(Calendar.getDateTime(itemAutoResearch.getStatusTimeInMilliseconds()));
    }

    public void setAutoBuilderLeafTaskController(AutoResearchLeafTaskController autoResearchLeafTaskController) {
        this.autoResearchLeafTaskController = autoResearchLeafTaskController;
    }

    public void setItemAutoResearch(ItemAutoResearch itemAutoResearch) {
        this.itemAutoResearch = itemAutoResearch;
    }
    public void setHistoryItem() {
        this.historyItem = true;
    }
}
