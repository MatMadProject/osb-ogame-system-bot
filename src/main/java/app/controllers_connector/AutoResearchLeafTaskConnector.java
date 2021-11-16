package app.controllers_connector;

import app.controllers.AutoResearchLeafTaskController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class AutoResearchLeafTaskConnector implements LeafTaskConnector {
    private AutoResearchLeafTaskController controller;
    private AnchorPane content;

    public AutoResearchLeafTaskConnector(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/auto-research-leaftask.fxml"));
        try {
            content = fxmlLoader.load();
            controller = fxmlLoader.getController();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        if(controller != null) {

        }
    }

    @Override
    public Node content() {
        if(controller != null) {
            controller.update();
        }
        return content;
    }

    public AutoResearchLeafTaskController getController() {
        return controller;
    }
}
