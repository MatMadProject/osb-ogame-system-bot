package app.task;

import app.data.StaticStrings;
import ogame.OgameWeb;
import ogame.utils.Waiter;
import ogame.utils.log.AppLog;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class CheckInternet extends Task{
    public static boolean connected = true;
    private int count = 0;
    public static String log ="";

    public CheckInternet() {
        setThread(new Thread(this));
        startThread();
        AppLog.print(CheckInternet.class.getName(),0,"Check internet started.");
    }

    @Override
    public void run() {
        while(isRun()) {
                state();
            Waiter.sleep(1000,30*1000);
        }
    }

    /**
     * Sprawdza czy jest połączenie z internetem.
     */
    private void state() {
        try {

            URL url = new URL("http://www.google.com");
            URLConnection connection = url.openConnection();
            connection.connect();
            connected = true;
            //Odświeża stronę gdy wykryto brak połączenia, pomiędzy 1 a 4 próbą.
            if(count > 0) {
                OgameWeb.webDriver.navigate().refresh();
                AppLog.print(CheckInternet.class.getName(),0,"Page refreshed after reconnect.");
            }
//            log = ogame.zegar.CzasGry.string();
            count = 0;
        }
        catch (IOException e) {
            connected = false;
            count++;
            log = "Disconnect ["+count+"]";
            AppLog.print(CheckInternet.class.getName(),1,log);
        }
        finally {
            //Odświeża stronę po 5 kolejnym braku połączenia
            if(count != 0 && count % 5 == 0) {
                OgameWeb.webDriver.navigate().refresh();
                AppLog.print(CheckInternet.class.getName(),0,"Page refreshed after disconnect.");
            }
        }

        if(!OgameWeb.closed &&
                (OgameWeb.webDriver.getTitle().contains(StaticStrings.OGAME_SITE_TITLE_WHEN_INTERNET_DISCONNECTED)
                || OgameWeb.webDriver.getTitle().contains(StaticStrings.OGAME_SITE_TITLE_WHEN_INTERNET_DISCONNECTED2)
                || OgameWeb.webDriver.getTitle().contains(StaticStrings.OGAME_SITE_TITLE_WHEN_INTERNET_DISCONNECTED3))) {
            OgameWeb.webDriver.navigate().refresh();

            AppLog.print(CheckInternet.class.getName(),0,"Page refreshed when detects the disconnect page.");
        }
    }
}
