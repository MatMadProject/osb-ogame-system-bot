package app.controllers;

import app.data.DataLoader;
import app.data.shipyard.DefenceItem;
import app.data.shipyard.Status;
import javafx.scene.control.Label;
import ogame.utils.log.AppLog;
import ogame.utils.watch.Calendar;

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
        labelStatusTime.setText(Calendar.getDateTime(defence.getStatusTime()));
        if(defence.getTimer() != null)
            labelTimer.setText(defence.getTimer().leftTimeSecond());
        else
            labelTimer.setText("--:--:--");
    }

    public void delete() {
        if(defence.getStatus() == Status.FINISHED){
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
}
