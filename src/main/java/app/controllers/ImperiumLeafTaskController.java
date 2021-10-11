package app.controllers;

import app.controllers_connector.ImperiumPlanetDataConnector;
import app.data.DataLoader;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import ogame.planets.Planet;

public class ImperiumLeafTaskController {

    @FXML
    private HBox hBoxPlanets;

    public void update(){
        if(DataLoader.planets != null && DataLoader.planets.getPlanetList().size() > 0){

            int sizePlanets = hBoxPlanets.getChildren().size();
            if(sizePlanets > 1)
                hBoxPlanets.getChildren().remove(1,sizePlanets);

            for(Planet planet : DataLoader.planets.getPlanetList()){
                ImperiumPlanetDataConnector connector = new ImperiumPlanetDataConnector(planet);
                hBoxPlanets.getChildren().add(connector.content());
            }
        }
    }
}
