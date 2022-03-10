package app.controllers;

import app.data.DataLoader;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import ogame.DataTechnology;
import ogame.RequiredTechnology;
import ogame.Type;
import ogame.planets.Planet;

public class RequirementsContainerController {

    @FXML
    private Label labelDataTechnology;
    @FXML
    private Label labelRequiredLevel;

    private RequiredTechnology requiredTechnology;

    public void update(RequiredTechnology requiredTechnology, Object planetListObject) {
        this.requiredTechnology = requiredTechnology;
        DataTechnology dataTechnology = requiredTechnology.getRequiredTechnology();
        int currentLevel = -1;
        int requiredLevel = requiredTechnology.getRequiredLevel();
        Type type = dataTechnology.getType();
        switch(type){
            case BASIC:
            case DRIVE:
            case ADVANCED:
            case COMBAT:
                currentLevel = DataLoader.researches.getResearch(dataTechnology).getLevel();
                break;
            case PRODUCTION:
            case TECHNOLOGIES:
                if(planetListObject instanceof Planet)
                    currentLevel = ((Planet)planetListObject).getBuilding(dataTechnology).getLevel();
                else{
                    //todo Moon
                }
                break;
            case BATTLE:
            case CIVIL:
            case DEFENCE:
        }

        labelDataTechnology.setText(dataTechnology+"");
        if(requiredLevel > currentLevel)
            labelRequiredLevel.getStyleClass().add("disable-requirements");
        labelRequiredLevel.setText(requiredTechnology.getRequiredLevel()+"");
    }

    public RequiredTechnology getRequiredTechnology() {
        return requiredTechnology;
    }
}
