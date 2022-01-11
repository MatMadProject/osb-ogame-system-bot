package app.controllers;

import app.data.DataLoader;
import app.data.expedition.Expedition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import ogame.planets.Resources;
import ogame.utils.log.AppLog;
import ogame.utils.watch.Calendar;

public class ExpeditionLeafTaskExpeditionItemController {

    @FXML
    public Label labelTimer;
    @FXML
    private Label labelId;

    @FXML
    private Label labelPlanet;

    @FXML
    private Label labelTarget;

    @FXML
    private Label labelStorage;

    @FXML
    private Label labelFleet;

    @FXML
    private Label labelStatus;

    @FXML
    private Label labelStatusTime;

    private ExpeditionLeafTaskController expeditionLeafTaskController;
    private Expedition expedition;
    @FXML
    void delete() {
        DataLoader.expeditions.getExpeditionList().remove(expedition);
        expeditionLeafTaskController.updateQueue();
        AppLog.print(ExpeditionLeafTaskController.class.getName(),2,"Remove from expedition queue " + expedition.getId() + ".");
        DataLoader.expeditions.save();
    }

    public void update() {
        labelId.setText(expedition.getId());
        labelPlanet.setText(expedition.getPlanet().getCoordinate().getText());
        labelTarget.setText(expedition.getDestinationCoordinate().getText());
        Resources resources = expedition.getLootedResources();
        if(resources == null)
            labelStorage.setText("0");
        else{
            labelStorage.setText(expedition.getLootedResources().getMetal()+"/"+
                    expedition.getLootedResources().getCrystal()+ "/"+
                    expedition.getLootedResources().getDeuterium());
        }
        if(expedition.getTimer()!= null)
            labelTimer.setText(expedition.getTimer().leftTimeSecond());
        else
            labelTimer.setText("--:--:--");

        labelFleet.setText(expedition.getShipsBefore()+"/"+expedition.getShipsAfter());
        labelStatus.setText(expedition.getStatus().name());
        labelStatusTime.setText(Calendar.getDateTime(expedition.getStatusTime()));
    }

    public void setExpeditionLeafTaskController(ExpeditionLeafTaskController expeditionLeafTaskController) {
        this.expeditionLeafTaskController = expeditionLeafTaskController;
    }

    public void setExpedition(Expedition expedition) {
        this.expedition = expedition;
    }
}
