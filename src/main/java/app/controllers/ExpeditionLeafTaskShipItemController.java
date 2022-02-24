package app.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import ogame.ships.Ship;

public class ExpeditionLeafTaskShipItemController {
    @FXML
    private Label labelShip;

    @FXML
    private Label labelValue;

    public void update(Ship ship) {
        labelShip.setText(ship.getDataTechnology().name());
        if(ship.getValue() == -1)
            labelValue.setText("ALL");
        else
            labelValue.setText(ship.getValue()+"");
    }
}
