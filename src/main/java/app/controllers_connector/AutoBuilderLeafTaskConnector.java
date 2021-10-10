package app.controllers_connector;

import app.controllers.AutoBuilderLeafTaskController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class AutoBuilderLeafTaskConnector implements LeafTaskConnector{

    private AutoBuilderLeafTaskController controller;
    private AnchorPane content;

    public AutoBuilderLeafTaskConnector(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/auto-builder-leaftask.fxml"));
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
        return content;
    }
}
