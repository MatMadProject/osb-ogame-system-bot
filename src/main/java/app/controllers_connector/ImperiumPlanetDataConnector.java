package app.controllers_connector;

import app.controllers.ImperiumPlanetDataController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import ogame.planets.Planet;

import java.io.IOException;

public class ImperiumPlanetDataConnector implements LeafTaskConnector{

    private ImperiumPlanetDataController controller;
    private VBox content;
    private Planet planet;

    public ImperiumPlanetDataConnector(Planet planet){
        this.planet = planet;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/imperium-planet-data.fxml"));
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
            controller.update(planet);
        }
        return content;
    }
}
