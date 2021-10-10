package app.controllers_connector;

import app.controllers.PlanetLeafTaskController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class PlanetsLeafTaskConnector implements LeafTaskConnector{

    private PlanetLeafTaskController controller;
    private AnchorPane content;

    public PlanetsLeafTaskConnector(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/planets-leaftask.fxml"));
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
}
