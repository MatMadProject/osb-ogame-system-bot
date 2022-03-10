package app.controllers;

import app.data.DataLoader;
import app.data.transport.TransportItem;
import javafx.scene.control.Label;
import ogame.planets.Moon;
import ogame.planets.Planet;
import ogame.utils.log.AppLog;
import ogame.utils.watch.Calendar;
import ogame.utils.watch.Timer;

import java.util.List;

public class TransportLeafTaskItemController {
    public Label labelId;
    public Label labelPlanetStart;
    public Label labelPlanetEnd;
    public Label labelValue;
    public Label labelStatus;
    public Label labelStatusTime;
    public Label labelTimer;
    private TransportItem transportItem;
    private boolean historyItem = false;
    private TransportLeafTaskController transportLeafTaskController;
    public void delete() {
        if(historyItem){
            List<TransportItem> list = DataLoader.listTransportItem.getHistoryList();
            if(!list.isEmpty()){
                list.remove(transportItem);
                transportLeafTaskController.updateHistoryList();
                AppLog.print(TransportLeafTaskItemController.class.getName(),2,"Remove from history queue, id = " + transportItem.getId() + ".");
            }
        }else{
            DataLoader.listTransportItem.getQueueList().remove(transportItem);
            transportLeafTaskController.updateQueueList();
            AppLog.print(TransportLeafTaskItemController.class.getName(),2,"Remove from transport queue, id =  " + transportItem.getId() + ".");
        }
        DataLoader.listTransportItem.save();
    }
    public void update(){
        labelId.setText(transportItem.getId());
        if(transportItem.getObjectStart() instanceof Planet)
            labelPlanetStart.setText(((Planet)transportItem.getObjectStart()).getCoordinate().getText());
        else
            labelPlanetStart.setText(((Moon)transportItem.getObjectStart()).getCoordinate().getText()+" [M]");
        if(transportItem.getObjectEnd() instanceof Planet)
            labelPlanetEnd.setText(((Planet)transportItem.getObjectEnd()).getCoordinate().getText());
        else
            labelPlanetEnd.setText(((Moon)transportItem.getObjectEnd()).getCoordinate().getText()+" [M]");
        labelValue.setText(transportItem.resources());
        labelStatus.setText(transportItem.getStatus().name());
        labelStatusTime.setText(Calendar.getDateTime(transportItem.getStatusTimeInMilliseconds()));
        labelTimer.setText(transportItem.getEndTimeInSeconds() == 0 ? "--:--:--" : Timer.leftTimeSecond(transportItem.getEndTimeInSeconds()));
    }
    public void setTransportLeafTaskController(TransportLeafTaskController transportLeafTaskController) {
        this.transportLeafTaskController = transportLeafTaskController;
    }

    public void setTransportItem(TransportItem transportItem) {
        this.transportItem = transportItem;
    }

    public void setHistoryItem() {
        this.historyItem = true;
    }
}
