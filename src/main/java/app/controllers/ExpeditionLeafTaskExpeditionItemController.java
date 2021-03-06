package app.controllers;

import app.data.DataLoader;
import app.data.expedition.Expedition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import ogame.eventbox.FleetDetailsShip;
import ogame.planets.Moon;
import ogame.planets.Planet;
import ogame.planets.Resources;
import ogame.ships.Ship;
import ogame.utils.log.AppLog;
import ogame.utils.watch.Calendar;
import ogame.utils.watch.Timer;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ExpeditionLeafTaskExpeditionItemController {

    @FXML
    public Label labelTimer;
    public HBox hBoxRoot;
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
    private boolean historyItem = false;

    Tooltip tooltipStorage = new Tooltip("Metal: 0 \n" +
            "Crystal: 0 \n" +
            "Deuterium: 0");
    Tooltip tooltipFleet = new Tooltip("Before: 0 \n" +
            "After: 0" );
    @FXML
    void initialize(){
        labelStorage.setTooltip(tooltipStorage);
        labelFleet.setTooltip(tooltipFleet);
    }
    @FXML
    void delete() {
        if(historyItem){
            DataLoader.expeditions.getHistoryList().remove(expedition);
            expeditionLeafTaskController.updateHistoryList();
            AppLog.print(ExpeditionLeafTaskController.class.getName(),2,"Remove from  history expedition, id = " + expedition.getId() + ".");
        }else{
            DataLoader.expeditions.getExpeditionList().remove(expedition);
            expeditionLeafTaskController.updateQueue();
            AppLog.print(ExpeditionLeafTaskController.class.getName(),2,"Remove from expedition queue, id = " + expedition.getId() + ".");
            expeditionLeafTaskController.clearShipsList();
            expeditionLeafTaskController.disableEditButton();
        }
        DataLoader.expeditions.save();
    }

    public void update() {
        labelId.setText(expedition.getId());
        labelPlanet.setText(expedition.getPlanetListObject() instanceof Planet ?
                ((Planet)expedition.getPlanetListObject()).getCoordinate().getText()
                : ((Moon)expedition.getPlanetListObject()).getCoordinate().getText()+" [M]");
        labelTarget.setText(expedition.getDestinationCoordinate().getText());
        Resources resources = expedition.getLootedResources();
        if(historyItem){
            if(resources.sum() == 0)
                labelStorage.setText("0");
            else{
                double sum = resources.sum();
                NumberFormat numberFormat = NumberFormat.getPercentInstance(new Locale("pl", "PL"));
                labelStorage.setText(sum+"/"
                        + numberFormat.format(sum/expedition.getStorage()));
            }
        }else{
            if(resources.sum() == 0)
                labelStorage.setText("0");
            else{
                labelStorage.setText(resources.getMetal()+"/"+
                        resources.getCrystal()+ "/"+
                        resources.getDeuterium());
            }
        }

        labelTimer.setText(expedition.getEndTimeInSeconds() == 0 ? "--:--:--" : Timer.leftTimeSecond(expedition.getEndTimeInSeconds()));

        labelFleet.setText(expedition.getShipsBefore()+"/"+expedition.getShipsAfter());
        labelStatus.setText(expedition.getStatus().name());
        labelStatusTime.setText(Calendar.getDateTime(expedition.getStatusTimeInMilliseconds()));
        tooltipStorage.setText("Metal: " +  resources.getMetal() +"\n" +
                "Crystal: " +  resources.getCrystal() +"\n" +
                "Deuterium: " +  resources.getDeuterium());
        tooltipFleet.setText(fleetToolTip(expedition.getDeclaredShips(),expedition.getReturningFleet()));
        if(!expeditionLeafTaskController.isExpeditionContainerSelected(expedition))
            hBoxRoot.getStyleClass().remove("expediton-item-selected");
    }

    public void setExpeditionLeafTaskController(ExpeditionLeafTaskController expeditionLeafTaskController) {
        this.expeditionLeafTaskController = expeditionLeafTaskController;
    }

    public void setExpedition(Expedition expedition) {
        this.expedition = expedition;
    }

    public void select() {
        if(expeditionLeafTaskController.isExpeditionContainerSelected(expedition)){
            hBoxRoot.getStyleClass().remove("expediton-item-selected");
            expeditionLeafTaskController.disableEditButton();
            expeditionLeafTaskController.clearShipsList();
            expeditionLeafTaskController.updateVBoxAddedShips();
            expeditionLeafTaskController.setSelectedExpedition(null);
        }
        else
            if(!expeditionLeafTaskController.isExpeditionContainerSelected()){
                expeditionLeafTaskController.setSelectedExpedition(expedition);
                hBoxRoot.getStyleClass().add("expediton-item-selected");
                expeditionLeafTaskController.disableEditButton();
                expeditionLeafTaskController.setDeclaredShips(new ArrayList<>(expedition.getDeclaredShips()));
                expeditionLeafTaskController.updateVBoxAddedShips();
            }
    }

    public void setHistoryItem() {
        this.historyItem = true;
    }

    private String fleetToolTip(ArrayList<Ship> declaredShips, ArrayList<FleetDetailsShip> returningFleet){
        StringBuilder tooltip = new StringBuilder("Before: 0 \n");

        for(Ship ship : declaredShips)
            tooltip.append(ship.getLocalName()).append(" ").append(ship.getValue()).append("\n");

        tooltip.append("After: 0 \n");
        if(!returningFleet.isEmpty())
            for(FleetDetailsShip ship : returningFleet)
                tooltip.append(ship.getName()).append(" ").append(ship.getValue()).append("\n");
        return tooltip.toString();
    }
}
