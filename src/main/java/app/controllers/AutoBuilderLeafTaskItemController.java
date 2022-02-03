package app.controllers;

import app.data.DataLoader;
import app.data.autobuilder.ItemAutoBuilder;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import ogame.utils.log.AppLog;
import ogame.utils.watch.Calendar;
import ogame.utils.watch.Timer;

public class AutoBuilderLeafTaskItemController {

    public Label labelDown;
    public Label labelUp;
    @FXML
    private Label labelPlanet;

    @FXML
    private Label labelBuilding;

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

    private AutoBuilderLeafTaskController autoBuilderLeafTaskController;
    private ItemAutoBuilder itemAutoBuilder;
    private boolean historyItem = false;

    @FXML
    void delete() {
        if(historyItem){
            DataLoader.listItemAutoBuilder.getHistoryList().remove(itemAutoBuilder);
            autoBuilderLeafTaskController.updateHistoryList();
            AppLog.print(AutoResearchLeafTaskItemController.class.getName(),2,"Remove from built history: Upgrade "+ itemAutoBuilder.getBuilding().getName() + " to "
                    + itemAutoBuilder.getUpgradeLevel() +" level on " + itemAutoBuilder.getPlanet().getCoordinate().getText() + ".");
        }else{
            DataLoader.listItemAutoBuilder.getQueueList().remove(itemAutoBuilder);
            autoBuilderLeafTaskController.updateQueueList();
            AppLog.print(AutoResearchLeafTaskItemController.class.getName(),2,"Remove from built queue: Upgrade "+ itemAutoBuilder.getBuilding().getName() + " to "
                    + itemAutoBuilder.getUpgradeLevel() +" level on " + itemAutoBuilder.getPlanet().getCoordinate().getText() + ".");
            if(itemAutoBuilder.isFirstOnQueue())
                DataLoader.listItemAutoBuilder.startNextBuildingOnPlanet(itemAutoBuilder.getPlanet());
        }
        DataLoader.listItemAutoBuilder.save();
    }

    @FXML
    void down() {
    }

    @FXML
    void up() {
    }

    public void create(ItemAutoBuilder itemAutoBuilder){
        labelPlanet.setText(itemAutoBuilder.getPlanet().getCoordinate().getText());
        labelBuilding.setText(itemAutoBuilder.getBuilding().getName());
        labelLevel.setText((itemAutoBuilder.getUpgradeLevel())+"");
        labelStartTime.setText(Calendar.getDateTime(itemAutoBuilder.getAddTimeToQueueInMilliseconds()));
        labelFinishTime.setText(itemAutoBuilder.getEndTimeInSeconds() == 0 ? "--:--:--" : Timer.leftTimeSecond(itemAutoBuilder.getEndTimeInSeconds()));
        labelStatus.setText(itemAutoBuilder.getStatus()+"");
        labelStatusTime.setText(Calendar.getDateTime(itemAutoBuilder.getStatusTimeInMilliseconds()));
    }

    public void createHistoryItem(ItemAutoBuilder itemAutoBuilder){
        labelPlanet.setText(itemAutoBuilder.getPlanet().getCoordinate().getText());
        labelBuilding.setText(itemAutoBuilder.getBuilding().getName());
        labelLevel.setText((itemAutoBuilder.getUpgradeLevel())+"");
        labelStartTime.setText(Calendar.getDateTime(itemAutoBuilder.getAddTimeToQueueInMilliseconds()));
        labelFinishTime.setText(Calendar.getDateTime(itemAutoBuilder.getFinishTimeInMilliseconds()));
        labelStatus.setText(itemAutoBuilder.getStatus()+"");
        labelStatusTime.setText(Calendar.getDateTime(itemAutoBuilder.getStatusTimeInMilliseconds()));
        labelDown.setDisable(true);
        labelUp.setDisable(true);
    }

    public void update(){
        labelFinishTime.setText(itemAutoBuilder.getEndTimeInSeconds() == 0 ? "--:--:--" : Timer.leftTimeSecond(itemAutoBuilder.getEndTimeInSeconds()));
        labelStatus.setText(itemAutoBuilder.getStatus()+"");
        labelStatusTime.setText(Calendar.getDateTime(itemAutoBuilder.getStatusTimeInMilliseconds()));
    }



    public void setAutoBuilderLeafTaskController(AutoBuilderLeafTaskController autoBuilderLeafTaskController) {
        this.autoBuilderLeafTaskController = autoBuilderLeafTaskController;
    }

    public void setItemAutoBuilder(ItemAutoBuilder itemAutoBuilder) {
        this.itemAutoBuilder = itemAutoBuilder;
    }

    public void setHistoryItem() {
        this.historyItem = true;
    }
}
