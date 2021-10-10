package app.controllers;

import app.controllers_connector.LeafTaskConnector;
import app.leaftask.LeafTask;
import app.leaftask.PlanetsLeafTask;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;


public class TaskContainerController {

    private LeafTask leafTask;
    private BotWindowController botWindowController;
    private LeafTaskConnector leafTaskConnector;

    private boolean on = false;

    @FXML
    private Label labelName;
    @FXML
    private HBox hBox;
    @FXML
    private Button buttonOnOff;

    @FXML
    void onOff() {
        if(on) {
            buttonOnOff.setText("OFF");
            buttonOnOff.getStyleClass().removeAll("on");
            buttonOnOff.getStyleClass().add("off");
            leafTask.stop();
            on = false;
        }
        else{
            buttonOnOff.setText("ON");
            buttonOnOff.getStyleClass().removeAll("off");
            buttonOnOff.getStyleClass().add("on");
            leafTask.start();
            on = true;
        }
    }

    public void setTask(LeafTask task) {
        this.leafTask = task;
        labelName.setText(task.getName());
        if(this.leafTask.isRun()){
            on = true;
            buttonOnOff.setText("ON");
            buttonOnOff.getStyleClass().removeAll("off");
            buttonOnOff.getStyleClass().add("on");
        }
        if(leafTask instanceof PlanetsLeafTask)
            buttonOnOff.setDisable(true);
    }

    public void setBotWindowController(BotWindowController botWindowController) {
        this.botWindowController = botWindowController;
    }

    public void setLeafTaskConnector(LeafTaskConnector leafTaskConnector) {
        this.leafTaskConnector = leafTaskConnector;
    }

    public void select() {
        //Kolejne zaznaczenie
        if(botWindowController.isSelectedLeafTask()){
            botWindowController.unselectLeafTask();
            botWindowController.selectedLeafTask(this);
            hBox.getStyleClass().add("hbox-task-selected");
            botWindowController.setTaskInformation(leafTask);
        }
        //Zaznaczenie po raz pierwszy
        else{
            botWindowController.selectedLeafTask(this);
            hBox.getStyleClass().add("hbox-task-selected");
            botWindowController.setTaskInformation(leafTask);
        }
        botWindowController.setAnchorPaneTaskContent(leafTaskConnector.content());
    }
    public void unselect(){
        hBox.getStyleClass().removeAll("hbox-task-selected");
    }
}
