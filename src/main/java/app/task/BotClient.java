package app.task;

import app.controllers.BotWindowController;
import app.data.DataLoader;
import app.leaftask.LeafTaskManager;
import ogame.utils.log.AppLog;

public class BotClient{

    private static DataLoader dataLoader = null;
    private CheckInternet checkInternet;
    private GUIUpdater guiUpdater;
    private GameTime gameTime;
    private LeafTaskManager leafTaskManager;
    private BotLogic botLogic;

    public BotClient(BotWindowController botWindowController){
        leafTaskManager = new LeafTaskManager();
        dataLoader = new DataLoader();
        //Sprawdzanie połączenia z internetem
        checkInternet = new CheckInternet();
        guiUpdater = new GUIUpdater(botWindowController);
        gameTime = new GameTime();
        botWindowController.setLeafTaskManager(leafTaskManager);
        botLogic = new BotLogic(leafTaskManager);

        AppLog.print(BotClient.class.getName(),0,"Bot client started.");
    }
    public void off(){
        checkInternet.stop();
        checkInternet = null;

        gameTime.stop();
        gameTime = null;

        guiUpdater.stop();
        guiUpdater = null;

        dataLoader = null;

        botLogic.stop();
        botLogic = null;
    }
}
