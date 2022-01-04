package app.controllers_connector;

import app.controllers.RequirementsContainerController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import ogame.RequiredTechnology;
import ogame.planets.Planet;

import java.io.IOException;

public class RequirementsContainerConnector implements LeafTaskConnector {

    private RequirementsContainerController controller;
    private HBox content;
    private final RequiredTechnology requiredTechnology;
    private final Planet planet;

    public RequirementsContainerConnector(RequiredTechnology requiredTechnology, Planet planet){
        this.requiredTechnology = requiredTechnology;
        this.planet = planet;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/requirements-container.fxml"));
        try {
            content = fxmlLoader.load();
            controller = fxmlLoader.getController();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public Node content() {
        if(controller != null) {
            controller.update(requiredTechnology, planet);
        }
        return content;
    }
}
