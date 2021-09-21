package app.task;

import app.data.DataLoader;
import ogame.utils.log.AppLog;

public class BotClient extends Task{

    private static DataLoader dataLoader = null;
    private CheckInternet checkInternet;
    public BotClient(){
        dataLoader = new DataLoader();
        //Sprawdzanie połączenia z internetem
        checkInternet = new CheckInternet();
        AppLog.print(BotClient.class.getName(),0,"Bot client started.");
    }

    public void off(){
        dataLoader = null;
        checkInternet.stop();
        checkInternet = null;
    }
}
