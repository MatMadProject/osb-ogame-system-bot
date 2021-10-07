package app.task;

import app.controllers.BotWindowController;
import app.data.DataLoader;
import app.leaftask.LeafTaskManager;
import ogame.utils.Waiter;
import ogame.utils.log.AppLog;

public class BotClient extends Task{

    private static DataLoader dataLoader = null;
    private CheckInternet checkInternet;
    private GUIUpdater guiUpdater;
    private GameTime gameTime;
    private LeafTaskManager leafTaskManager;

    public BotClient(BotWindowController botWindowController){
        leafTaskManager = new LeafTaskManager();
        dataLoader = new DataLoader();
        //Sprawdzanie połączenia z internetem
        checkInternet = new CheckInternet();
        guiUpdater = new GUIUpdater(botWindowController);
        gameTime = new GameTime();
        botWindowController.setLeafTaskManager(leafTaskManager);
        AppLog.print(BotClient.class.getName(),0,"Bot client started.");
    }

    @Override
    public void run() {
        while(isRun()) {
            tasks();
            Waiter.sleep(10,40);
        }
    }

    public void off(){
        checkInternet.stop();
        checkInternet = null;
        gameTime.stop();
        gameTime = null;
        guiUpdater.stop();
        guiUpdater = null;
        dataLoader = null;

    }

    public void tasks(){
        if(leafTaskManager.getTasks() != null){
            leafTaskManager.getTasks()[0].execute();
        }
    }
}
