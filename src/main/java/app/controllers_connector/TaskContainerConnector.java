package app.controllers_connector;

import app.controllers.BotWindowController;
import app.controllers.TaskContainerControllers;
import app.leaftask.LeafTask;
import app.task.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class TaskContainerConnector {

    private TaskContainerControllers controller;
    private AnchorPane container;

    public TaskContainerConnector(LeafTask task, BotWindowController botWindowController) {
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
            controller.setBotWindowController(botWindowController);
        }
    }

    public AnchorPane getContainer() {
        return container;
    }

    public TaskContainerControllers getController() {
        return controller;
    }
}
