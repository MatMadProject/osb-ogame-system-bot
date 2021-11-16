package app.controllers;

import app.data.autobuilder.ItemAutoBuilder;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import ogame.utils.watch.Calendar;
import ogame.utils.watch.Time;


public class AutoBuilderLeafTaskItemController {

    @FXML
    private Label labelPlanet;

    @FXML
    private Label labelBuilding;

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

    public void update(ItemAutoBuilder itemAutoBuilder){
        labelPlanet.setText(itemAutoBuilder.getPlanet().getCoordinate().getText());
        labelBuilding.setText(itemAutoBuilder.getBuilding().getName());
        labelLevel.setText((itemAutoBuilder.getUpgradeLevel())+"");
        labelStartTime.setText(Calendar.getDateTime(itemAutoBuilder.getStartTime()));
        if(itemAutoBuilder.getTimer() != null)
            labelFinishTime.setText(itemAutoBuilder.getTimer().leftTime());
        else
            labelFinishTime.setText(Calendar.getDateTime(itemAutoBuilder.getFinishTime()));
        labelStatus.setText(itemAutoBuilder.getStatus()+"");
        labelStatusTime.setText(Calendar.getDateTime(itemAutoBuilder.getStatusTime()));
    }
}
