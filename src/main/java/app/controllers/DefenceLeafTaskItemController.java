package app.controllers;

import app.data.DataLoader;
import app.data.shipyard.DefenceItem;
import javafx.scene.control.Label;
import ogame.utils.log.AppLog;
import ogame.utils.watch.Calendar;
import ogame.utils.watch.Timer;

public class DefenceLeafTaskItemController {
    public Label labelId;
    public Label labelPlanet;
    public Label labelDefence;
    public Label labelValue;
    public Label labelStatus;
    public Label labelStatusTime;
    public Label labelTimer;
    private DefenceLeafTaskController defenceLeafTaskController;
    private DefenceItem defence;
    private boolean historyItem = false;

    public void setDefenceLeafTaskController(DefenceLeafTaskController defenceLeafTaskController) {
        this.defenceLeafTaskController = defenceLeafTaskController;
    }

    public void setDefence(DefenceItem defence) {
        this.defence = defence;
    }

    public void update() {
        labelId.setText(defence.getId());
        labelPlanet.setText(defence.getPlanet().getCoordinate().getText());
        labelDefence.setText(defence.getDefence().getName());
        labelValue.setText(defence.getValue()+"");
        labelStatus.setText(defence.getStatus().name());
        labelStatusTime.setText(Calendar.getDateTime(defence.getStatusTimeInMilliseconds()));
        labelTimer.setText(defence.getEndTimeInSeconds() == 0 ? "--:--:--" : Timer.leftTimeSecond(defence.getEndTimeInSeconds()));
    }

    public void delete() {
        if(historyItem){
            DataLoader.listDefenceItem.getHistoryList().remove(defence);
            defenceLeafTaskController.updateHistoryList();
            AppLog.print(DefenceLeafTaskItemController.class.getName(),2,"Remove from history queue, id = " + defence.getId() + ".");
        }else{
            DataLoader.listDefenceItem.getQueueList().remove(defence);
            defenceLeafTaskController.updateQueueList();
            AppLog.print(DefenceLeafTaskItemController.class.getName(),2,"Remove from defence queue, id =  " + defence.getId() + ".");
        }
        DataLoader.listDefenceItem.save();
    }
    public void setHistoryItem() {
        this.historyItem = true;
    }
}
