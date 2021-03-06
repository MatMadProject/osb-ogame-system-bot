package app.controllers_connector;

import app.controllers.AutoBuilderLeafTaskController;
import app.controllers.BotWindowController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class AutoBuilderLeafTaskConnector implements LeafTaskConnector{

    private AutoBuilderLeafTaskController controller;
    private AnchorPane content;

    public AutoBuilderLeafTaskConnector(BotWindowController botWindowController){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/auto-builder-leaftask.fxml"));
        try {
            content = fxmlLoader.load();
            controller = fxmlLoader.getController();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        if(controller != null) {
            controller.setBotWindowController(botWindowController);
        }
    }

    @Override
    public Node content() {
        if(controller != null) {
            controller.update();
        }
        return content;
    }

    public AutoBuilderLeafTaskController getController() {
        return controller;
    }
}
