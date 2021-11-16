package app.controllers_connector;

import app.controllers.BotWindowController;
import app.controllers.TaskContainerController;
import app.leaftask.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class TaskContainerConnector {

    private TaskContainerController controller;
    private AnchorPane container;
    private final LeafTaskConnector leafTaskConnector;

    public TaskContainerConnector(LeafTask task, BotWindowController botWindowController) {
        leafTaskConnector = setLeafTaskConnector(task);
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
            controller.setLeafTaskConnector(leafTaskConnector);
        }
    }

    public AnchorPane getContainer() {
        return container;
    }

    public TaskContainerController getController() {
        return controller;
    }

    //Update when added new leaftask to app.
    private LeafTaskConnector setLeafTaskConnector(LeafTask leafTask){
        if(leafTask instanceof PlanetsLeafTask)
            return new PlanetsLeafTaskConnector();
        if(leafTask instanceof ImperiumLeafTask)
            return new ImperiumLeafTaskConnector();
        if(leafTask instanceof AutoBuilderLeafTask)
            return new AutoBuilderLeafTaskConnector();
        if(leafTask instanceof AutoResearchLeafTask)
            return new AutoResearchLeafTaskConnector();
        return null;
    }
}
