package app.controllers;

import app.data.expedition.ItemShipList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ExpeditionLeafTaskShipItemController {
    @FXML
    private Label labelShip;

    @FXML
    private Label labelValue;

    public void update(ItemShipList itemShipList) {
        labelShip.setText(itemShipList.getShip().getDataTechnology().name());
        if(itemShipList.getValue() == -1)
            labelValue.setText("ALL");
        else
            labelValue.setText(itemShipList.getValue()+"");
    }
}
