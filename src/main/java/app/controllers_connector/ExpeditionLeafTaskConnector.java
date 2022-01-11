package app.controllers_connector;

import app.controllers.BotWindowController;
import app.controllers.ExpeditionLeafTaskController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class ExpeditionLeafTaskConnector implements LeafTaskConnector{

    private ExpeditionLeafTaskController controller;
    private AnchorPane content;

    public ExpeditionLeafTaskConnector(BotWindowController botWindowController){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/expedition-leaftask.fxml"));
        try {
            content = fxmlLoader.load();
            controller = fxmlLoader.getController();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        if(controller != null)
            controller.setBotWindowController(botWindowController);

    }

    @Override
    public Node content() {
        if(controller != null) {
            controller.update();
        }
        return content;
    }

    public ExpeditionLeafTaskController getController() {
        return controller;
    }
}
