package app.task;


import ogame.OgameWeb;
import ogame.utils.Waiter;
import ogame.utils.log.AppLog;
import ogame.watch.OgameClock;

public class GameTime extends Task{

    public static String date = "";
    public static String time = "";
    public static String datetime = "";

    public GameTime() {
        setThread(new Thread(this));
        startThread();
        AppLog.print(GameTime.class.getName(),0,"GameTime started.");
    }

    @Override
    public void run() {
        while(true) {
            if(isRun()) {
                date = OgameClock.date(OgameWeb.webDriver);
                time = OgameClock.time(OgameWeb.webDriver);
                datetime = date + " " + time;
            }
            Waiter.sleep(100,250);
        }
    }
}
