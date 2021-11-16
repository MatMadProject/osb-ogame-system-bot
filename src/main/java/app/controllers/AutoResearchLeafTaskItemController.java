package app.controllers;

import app.data.autoresearch.ItemAutoResearch;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import ogame.utils.watch.Calendar;

public class AutoResearchLeafTaskItemController {
    @FXML
    private Label labelPlanet;

    @FXML
    private Label labelResearch;

    @FXML
    private Label labelLevel;

    @FXML
    private Label labelStartTime;

    @FXML
    private Label labelFinishTime;

    @FXML
    private Label labelStatus;

    @FXML
    private Label labelStatusTime;

    @FXML
    void delete(MouseEvent event) {

    }

    @FXML
    void down(MouseEvent event) {

    }

    @FXML
    void up(MouseEvent event) {

    }

    public void update(ItemAutoResearch itemAutoResearch){
        labelPlanet.setText(itemAutoResearch.getPlanet().getCoordinate().getText());
        labelResearch.setText(itemAutoResearch.getResearch().getName());
        labelLevel.setText((itemAutoResearch.getUpgradeLevel())+"");
        labelStartTime.setText(Calendar.getDateTime(itemAutoResearch.getStartTime()));
        if(itemAutoResearch.getTimer() != null)
            labelFinishTime.setText(itemAutoResearch.getTimer().leftTime());
        else
            labelFinishTime.setText(Calendar.getDateTime(itemAutoResearch.getFinishTime()));
        labelStatus.setText(itemAutoResearch.getStatus()+"");
        labelStatusTime.setText(Calendar.getDateTime(itemAutoResearch.getStatusTime()));
    }
}
