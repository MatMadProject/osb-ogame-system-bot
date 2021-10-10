package app.controllers_connector;

import app.controllers.ImperiumLeafTaskController;
import app.controllers.PlanetLeafTaskController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class ImperiumLeafTaskConnector implements LeafTaskConnector{

    private ImperiumLeafTaskController controller;
    private AnchorPane content;

    public ImperiumLeafTaskConnector(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/imperium-leaftask.fxml"));
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
