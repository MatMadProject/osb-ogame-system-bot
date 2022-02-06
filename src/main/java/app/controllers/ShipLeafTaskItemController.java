package app.controllers;

import app.data.DataLoader;
import app.data.shipyard.ShipItem;
import javafx.scene.control.Label;
import ogame.utils.log.AppLog;
import ogame.utils.watch.Calendar;
import ogame.utils.watch.Timer;

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
    private boolean historyItem = false;

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
        labelStatusTime.setText(Calendar.getDateTime(shipItem.getStatusTimeInMilliseconds()));
        labelTimer.setText(shipItem.getEndTimeInSeconds() == 0 ? "--:--:--" : Timer.leftTimeSecond(shipItem.getEndTimeInSeconds()));
    }

    public void delete() {
        if(historyItem){
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
    public void setHistoryItem() {
        this.historyItem = true;
    }
}
