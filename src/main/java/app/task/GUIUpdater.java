package app.task;

import app.controllers.BotWindowController;
import app.leaftask.PlanetsLeafTask;
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
            if(!((PlanetsLeafTask)botWindowController.getLeafTaskManager().getTasks()[0]).init)
               botWindowController.getPlanetsLeafTaskController().doneProgressBar();
        }
    }
}
