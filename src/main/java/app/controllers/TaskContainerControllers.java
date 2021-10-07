package app.controllers;

import app.leaftask.LeafTask;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;


public class TaskContainerControllers {

    private LeafTask leafTask;
    private BotWindowController botWindowController;

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
    }

    public void setBotWindowController(BotWindowController botWindowController) {
        this.botWindowController = botWindowController;
    }

    public void select() {
        if(botWindowController.isSelectedLeafTask()){
            botWindowController.unselectLeafTask();
            botWindowController.selectedLeafTask(this);
            hBox.getStyleClass().add("hbox-task-selected");
        }
        else{
            botWindowController.selectedLeafTask(this);
            hBox.getStyleClass().add("hbox-task-selected");
        }
    }
    public void unselect(){
        hBox.getStyleClass().removeAll("hbox-task-selected");
    }
}
