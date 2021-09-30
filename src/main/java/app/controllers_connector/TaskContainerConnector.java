package app.controllers_connector;

import app.controllers.TaskContainerControllers;
import app.task.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class TaskContainerConnector {

    private final Task task;
    private TaskContainerControllers controller;
    private AnchorPane container;

    public TaskContainerConnector(Task task) {
        this.task = task;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/task-container.fxml"));
        try {
            container = fxmlLoader.load();
            controller = fxmlLoader.getController();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        if(controller != null) {
            controller.setTask(task);
        }
    }

    public AnchorPane getContainer() {
        return container;
    }

    public TaskContainerControllers getController() {
        return controller;
    }
}
