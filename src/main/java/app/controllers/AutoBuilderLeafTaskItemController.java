package app.controllers;

import app.data.DataLoader;
import app.data.autobuilder.ItemAutoBuilder;
import app.data.autobuilder.Status;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import ogame.utils.log.AppLog;
import ogame.utils.watch.Calendar;

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

    @FXML
    void delete(MouseEvent event) {
        if(itemAutoBuilder.getStatus() == Status.FINISHED){
            DataLoader.listItemAutoBuilder.getHistoryList().remove(itemAutoBuilder);
            autoBuilderLeafTaskController.updateHistoryList();
            AppLog.print(AutoResearchLeafTaskItemController.class.getName(),2,"Remove from built history: Upgrade "+ itemAutoBuilder.getBuilding().getName() + " to "
                    + itemAutoBuilder.getUpgradeLevel() +" level on " + itemAutoBuilder.getPlanet().getCoordinate().getText() + ".");
        }else{
            DataLoader.listItemAutoBuilder.getQueueList().remove(itemAutoBuilder);
            autoBuilderLeafTaskController.updateQueueList();
            AppLog.print(AutoResearchLeafTaskItemController.class.getName(),2,"Remove from built queue: Upgrade "+ itemAutoBuilder.getBuilding().getName() + " to "
                    + itemAutoBuilder.getUpgradeLevel() +" level on " + itemAutoBuilder.getPlanet().getCoordinate().getText() + ".");
        }
        DataLoader.listItemAutoBuilder.save();
    }

    @FXML
    void down(MouseEvent event) {
        int listIndex = DataLoader.listItemAutoBuilder.getQueueList().indexOf(itemAutoBuilder);
        int listSize = DataLoader.listItemAutoBuilder.getQueueList().size();
    }

    @FXML
    void up(MouseEvent event) {

    }

    public void create(ItemAutoBuilder itemAutoBuilder){
        labelPlanet.setText(itemAutoBuilder.getPlanet().getCoordinate().getText());
        labelBuilding.setText(itemAutoBuilder.getBuilding().getName());
        labelLevel.setText((itemAutoBuilder.getUpgradeLevel())+"");
        labelStartTime.setText(Calendar.getDateTime(itemAutoBuilder.getStartTime()));
        if(itemAutoBuilder.getTimer() != null)
            labelFinishTime.setText(itemAutoBuilder.getTimer().leftTime());
        else
            labelFinishTime.setText(Calendar.getDateTime(itemAutoBuilder.getFinishTime()));
        labelStatus.setText(itemAutoBuilder.getStatus()+"");
        labelStatusTime.setText(Calendar.getDateTime(itemAutoBuilder.getStatusTime()));
    }

    public void createHistoryItem(ItemAutoBuilder itemAutoBuilder){
        labelPlanet.setText(itemAutoBuilder.getPlanet().getCoordinate().getText());
        labelBuilding.setText(itemAutoBuilder.getBuilding().getName());
        labelLevel.setText((itemAutoBuilder.getUpgradeLevel())+"");
        labelStartTime.setText(Calendar.getDateTime(itemAutoBuilder.getStartTime()));
        labelFinishTime.setText(Calendar.getDateTime(itemAutoBuilder.getFinishTime()));
        labelStatus.setText(itemAutoBuilder.getStatus()+"");
        labelStatusTime.setText(Calendar.getDateTime(itemAutoBuilder.getStatusTime()));
        labelDown.setDisable(true);
        labelUp.setDisable(true);
    }

    public void update(){
        if(itemAutoBuilder.getTimer() != null)
            labelFinishTime.setText(itemAutoBuilder.getTimer().leftTime());
        else
            labelFinishTime.setText(Calendar.getDateTime(itemAutoBuilder.getFinishTime()));
        labelStatus.setText(itemAutoBuilder.getStatus()+"");
        labelStatusTime.setText(Calendar.getDateTime(itemAutoBuilder.getStatusTime()));
    }



    public void setAutoBuilderLeafTaskController(AutoBuilderLeafTaskController autoBuilderLeafTaskController) {
        this.autoBuilderLeafTaskController = autoBuilderLeafTaskController;
    }

    public void setItemAutoBuilder(ItemAutoBuilder itemAutoBuilder) {
        this.itemAutoBuilder = itemAutoBuilder;
    }
}
