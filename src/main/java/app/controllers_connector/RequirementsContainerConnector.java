package app.controllers_connector;

import app.controllers.RequirementsContainerController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import ogame.RequiredTechnology;

import java.io.IOException;

public class RequirementsContainerConnector implements LeafTaskConnector {

    private RequirementsContainerController controller;
    private HBox content;
    private final RequiredTechnology requiredTechnology;
    private final Object planetListObject;

    public RequirementsContainerConnector(RequiredTechnology requiredTechnology, Object planetListObject){
        this.requiredTechnology = requiredTechnology;
        this.planetListObject = planetListObject;
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
            controller.update(requiredTechnology, planetListObject);
        }
        return content;
    }
}
