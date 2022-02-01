package app.task;

import app.controllers.BotWindowController;
import app.controllers_connector.*;
import app.data.DataLoader;
import javafx.application.Platform;
import ogame.utils.Waiter;

public class GUIUpdater extends Task {

    private BotWindowController botWindowController;

    public GUIUpdater(BotWindowController botWindowController) {
        this.botWindowController = botWindowController;
        setThread(new Thread(this));
        startThread();
    }

    @Override
    public void run() {
        while(true) {
            if(isRun()) {
                Runnable updater = () -> {
                    startTime();
                    internetStatus();
                    actualGameTime();
                    initializeTasksList();
                    doneProgressBar();
                    updateAutoBuilderGUI();
                    updateAutoResearchGUI();
                    updateExpeditionGUI();
                    updateDefenceGUI();
                    updateShipGUI();
                };
                Platform.runLater(updater);
            }
            Waiter.sleep(100,250);
        }
    }

    private void internetStatus(){
        if(botWindowController != null){
            botWindowController.setInternetStatus();
        }
    }

    private void startTime(){
        if(botWindowController != null){
            botWindowController.setStartTime();
        }
    }

    private void actualGameTime(){
        if(botWindowController != null){
            botWindowController.setActualGameTime();
        }
    }

    private void initializeTasksList(){
        if(botWindowController != null){
            if(botWindowController.isFillTaskList())
                botWindowController.fillTaskList();
        }
    }

    private void doneProgressBar(){
        if(botWindowController != null){
//            if(((PlanetsLeafTask)botWindowController.getLeafTaskManager().getTasks()[0]).isInited())
            if(!DataLoader.planets.isInitPlanetList())
               botWindowController.getPlanetsLeafTaskController().doneProgressBar();
        }
    }

    private void updateAutoBuilderGUI(){
        if(botWindowController != null){
            if(botWindowController.isSelectedLeafTask()){
                LeafTaskConnector  leafTaskConnector = botWindowController.getSelectedLeafTask().getLeafTaskConnector();
                if(leafTaskConnector instanceof AutoBuilderLeafTaskConnector){
                    ((AutoBuilderLeafTaskConnector) leafTaskConnector).getController().updateHistoryList();
                    ((AutoBuilderLeafTaskConnector) leafTaskConnector).getController().updateQueueList();
                }
            }
        }
    }

    private void updateAutoResearchGUI(){
        if(botWindowController != null){
            if(botWindowController.isSelectedLeafTask()){
                LeafTaskConnector  leafTaskConnector = botWindowController.getSelectedLeafTask().getLeafTaskConnector();
                if(leafTaskConnector instanceof AutoResearchLeafTaskConnector){
                    ((AutoResearchLeafTaskConnector) leafTaskConnector).getController().updateHistoryList();
                    ((AutoResearchLeafTaskConnector) leafTaskConnector).getController().updateQueueList();
                }
            }
        }
    }

    private void updateExpeditionGUI(){
        if(botWindowController != null){
            if(botWindowController.isSelectedLeafTask()){
                LeafTaskConnector  leafTaskConnector = botWindowController.getSelectedLeafTask().getLeafTaskConnector();
                if(leafTaskConnector instanceof ExpeditionLeafTaskConnector){
                    ((ExpeditionLeafTaskConnector) leafTaskConnector).getController().updateQueue();
                }
            }
        }
    }

    private void updateDefenceGUI() {
        if(botWindowController != null){
            if(botWindowController.isSelectedLeafTask()){
                LeafTaskConnector  leafTaskConnector = botWindowController.getSelectedLeafTask().getLeafTaskConnector();
                if(leafTaskConnector instanceof DefenceLeafTaskConnector){
                    ((DefenceLeafTaskConnector) leafTaskConnector).getController().updateQueueList();
                    ((DefenceLeafTaskConnector) leafTaskConnector).getController().updateHistoryList();
                    ((DefenceLeafTaskConnector) leafTaskConnector).getController().updateDisplayError();
                }
            }
        }
    }

    private void updateShipGUI() {
        if(botWindowController != null){
            if(botWindowController.isSelectedLeafTask()){
                LeafTaskConnector  leafTaskConnector = botWindowController.getSelectedLeafTask().getLeafTaskConnector();
                if(leafTaskConnector instanceof ShipLeafTaskConnector){
                    ((ShipLeafTaskConnector) leafTaskConnector).getController().updateQueueList();
                    ((ShipLeafTaskConnector) leafTaskConnector).getController().updateHistoryList();
                    ((ShipLeafTaskConnector) leafTaskConnector).getController().updateDisplayError();
                }
            }
        }
    }

}
