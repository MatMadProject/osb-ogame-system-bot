package app.controllers;

import app.data.DataLoader;
import app.data.shipyard.ShipItem;
import app.data.shipyard.Status;
import javafx.scene.control.Label;
import ogame.utils.log.AppLog;
import ogame.utils.watch.Calendar;

import java.util.List;

public class ShipLeafTaskItemController {
    public Label labelId;
    public Label labelPlanet;
    public Label labelDefence;
    public Label labelValue;
    public Label labelStatus;
    public Label labelStatusTime;
    public Label labelTimer;
    private ShipLeafTaskController shipLeafTaskController;
    private ShipItem shipItem;

    public void setShipLeafTaskController(ShipLeafTaskController shipLeafTaskController) {
        this.shipLeafTaskController = shipLeafTaskController;
    }

    public void setShipItem(ShipItem shipItem) {
        this.shipItem = shipItem;
    }

    public void update() {
        labelId.setText(shipItem.getId());
        labelPlanet.setText(shipItem.getPlanet().getCoordinate().getText());
        labelDefence.setText(shipItem.getShip().getName());
        labelValue.setText(shipItem.getValue()+"");
        labelStatus.setText(shipItem.getStatus().name());
        labelStatusTime.setText(Calendar.getDateTime(shipItem.getStatusTime()));
        if(shipItem.getTimer() != null)
            labelTimer.setText(shipItem.getTimer().leftTimeSecond());
        else
            labelTimer.setText("--:--:--");
    }

    public void delete() {
        if(shipItem.getStatus() == Status.FINISHED){
            List<ShipItem> list = DataLoader.listShipItem.getHistoryList();
            if(!list.isEmpty()){
                list.remove(shipItem);
                shipLeafTaskController.updateHistoryList();
                AppLog.print(ShipLeafTaskItemController.class.getName(),2,"Remove from history queue, id = " + shipItem.getId() + ".");
            }
        }else{
            DataLoader.listShipItem.getQueueList().remove(shipItem);
            shipLeafTaskController.updateQueueList();
            AppLog.print(ShipLeafTaskItemController.class.getName(),2,"Remove from defence queue, id =  " + shipItem.getId() + ".");
        }
        DataLoader.listShipItem.save();
    }
}
