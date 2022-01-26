package app.controllers_connector;

import app.controllers.BotWindowController;
import app.controllers.DefenceLeafTaskController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class DefenceLeafTaskConnector implements LeafTaskConnector{

    private DefenceLeafTaskController controller;
    private AnchorPane content;

    public DefenceLeafTaskConnector(BotWindowController botWindowController){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/defence-leaftask.fxml"));
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

    public DefenceLeafTaskController getController() {
        return controller;
    }
}
