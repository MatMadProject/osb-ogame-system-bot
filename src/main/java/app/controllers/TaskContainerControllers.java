package app.controllers;

import app.task.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;


public class TaskContainerControllers {

    private Task task;
    private boolean on = false;

    @FXML
    private Label labelName;

    @FXML
    private Button buttonOnOff;

    @FXML
    void onOff() {
        if(on) {
            buttonOnOff.setText("OFF");
            buttonOnOff.getStyleClass().removeAll("on");
            buttonOnOff.getStyleClass().add("off");
            task.stop();
            on = false;
        }
        else{
            buttonOnOff.setText("ON");
            buttonOnOff.getStyleClass().removeAll("off");
            buttonOnOff.getStyleClass().add("on");
            task.start();
            on = true;
        }
    }

    public void setTask(Task task) {
        this.task = task;
        labelName.setText(task.getName());
    }
}
