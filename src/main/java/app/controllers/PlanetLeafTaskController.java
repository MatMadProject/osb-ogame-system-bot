package app.controllers;

import app.data.DataLoader;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class PlanetLeafTaskController {

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label labelProgressBar;

    @FXML
    void initialize(){
        if(DataLoader.planets != null && !DataLoader.planets.isInitPlanetList())
            doneProgressBar();
    }

    public void doneProgressBar(){
        progressBar.setProgress(1.0);
        labelProgressBar.setText("Planets loaded");
    }

    public void update(){
        if(DataLoader.planets != null && !DataLoader.planets.isInitPlanetList())
            doneProgressBar();
    }
}
