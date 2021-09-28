package app.task;

import app.controllers.BotWindowController;
import app.data.DataLoader;
import ogame.utils.log.AppLog;

public class BotClient extends Task{

    private static DataLoader dataLoader = null;
    private CheckInternet checkInternet;
    private GUIUpdater guiUpdater;
    private GameTime gameTime;
    public BotClient(BotWindowController botWindowController){
        dataLoader = new DataLoader();
        //Sprawdzanie połączenia z internetem
        checkInternet = new CheckInternet();
        guiUpdater = new GUIUpdater(botWindowController);
        gameTime = new GameTime();
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

    }
}
