package app.task;

import app.data.Session;
import app.data.StaticStrings;
import app.data.accounts.Accounts;
import ogame.OgameWeb;
import ogame.utils.Waiter;
import ogame.utils.log.AppLog;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.util.Set;

public class Logger extends Task{

    private boolean firtsTimeExecute = true;
    private final String SERVER;
    private final String PATH = StaticStrings.DISK + StaticStrings.MAIN_FOLDER + File.separator +
            StaticStrings.CHROMEDRIVER_FILE;
    public static boolean stoppedOnWebApp = false;
    private final OgameWeb ogameWeb = new OgameWeb(Accounts.getSelected().getWeb());
    private  BotClient botClient = null;

    public Logger(){
        SERVER = Accounts.getSelected().getSerwer();
        setThread(new Thread(this));
        startThread();
        OgameWeb.setPath(PATH);
        OgameWeb.open();
        AppLog.print(Logger.class.getName(),0,"Logger started.");
    }

    private int webAppCount = 1;
    @Override
    public void run() {
        while(isRun()) {
            if(OgameWeb.webDriver != null){
                stoppedOnWebApp = Session.isStoppedOnWebApp();
                if(stoppedOnWebApp){
                    //todo - odpowiedź do serwera, że odebrano zatrzymanie bota
                    AppLog.print(Logger.class.getName(),0,"Detected logout on web app - "+webAppCount+".");
                    webAppCount++;
                }
                else{
                    if(webAppCount > 0){
                        AppLog.print(Logger.class.getName(),0,"Detected login on web app - "+webAppCount+".");
                        webAppCount = 0;
                    }
                    //Pierwsze uruchomienie lub wylogowało z gry.
                    if(OgameWeb.webDriver.getTitle().contains(StaticStrings.SNIPPEST_OF_TITILE_OF_OGAME_SITE)) {
                        if(firtsTimeExecute)
                            firstLogin();
                        else {
                            StaticStrings.APP_INFO_STRING = "Detect log out from OGame. Starts relogin.";
                            relogin();
                            AppLog.print(Logger.class.getName(),0,"Logs again to OGame on server " + OgameWeb.webDriver.getTitle());
                            StaticStrings.APP_INFO_STRING = "BotClient is running.";
                        }
                    }
                    // Sprawdza czy gracz zalogował się do gry
                    else if(OgameWeb.webDriver.getTitle().contains(SERVER) && firtsTimeExecute) {
                        StaticStrings.APP_INFO_STRING = "BotClient is running.";
                        //Uruchamianie bota.
                        botClient = new BotClient();
                        //Odznaczam, że było to pierwsze logowanie.
                        firtsTimeExecute = false;
                    }
                }
            }
            Waiter.sleep(100,400);
        }
    }
    private int dotCount = 0;
    /** PL
     * Pierwsze logowanie do Ogame.
     * EN
     * First time login to Ogame.
     */
    private void firstLogin() {
        String parent = OgameWeb.webDriver.getWindowHandle();
        //Pętla oczekuję na otwatcie nowej zakładki, po otwarciu przełacza na nowa zakładkę.
        StaticStrings.APP_INFO_STRING = "Runs web browser. Waits for login to OGame"+
                (dotCount%3 == 0 ? "...":  dotCount%3 == 1 ? ".":"..");
        dotCount++;
        Set<String> s = OgameWeb.webDriver.getWindowHandles();

        if(s.size()>1)
            for(String child : s)
                if(!child.equals(parent))
                    OgameWeb.webDriver.switchTo().window(child);
    }
    /** PL
     * Zalogowanie po wylogowaniu.
     * EN
     * First time login to Ogame.
     */
    private void relogin() {
        Waiter.sleep(250,250);
        AppLog.print(Logger.class.getName(),0,"Detected logout. Starts relogin.");
        pressPlay();
        pressSecondPlay();
        changeTab();
    }

    private void pressPlay() {
        try {
            Waiter.sleep(250,1050);
            WebElement element = OgameWeb.webDriver.findElement(By.xpath("//*[@id=\"joinGame\"]/a/button"));
            OgameWeb.scrollToElement(element);
            element.click();
            Waiter.sleep(250,250);
        }
        catch(Exception ex) {
            AppLog.print(Logger.class.getName(),1,"Try click to play button.");
        }
    }
    //todo - poprawić klikanie w przycisk.
    private void pressSecondPlay() {
        while (true) {
            WebElement playButton = OgameWeb.webDriver.findElement(By.id("myAccounts"));
            if (playButton.isDisplayed()){
                playButton.findElement(By.tagName("button")).click();
                break;
            }
        }
    }

    private  void changeTab() {
        boolean b = OgameWeb.webDriver.getTitle().contains(StaticStrings.SNIPPEST_OF_TITILE_OF_OGAME_SITE);
        while(b) {
            Set<String> s = OgameWeb.webDriver.getWindowHandles();

            for(String child : s) {
                OgameWeb.webDriver.switchTo().window(child);
                if(OgameWeb.webDriver.getTitle().contains(SERVER))
                    b = OgameWeb.webDriver.getTitle().equals("OGame");
            }
        }
    }

    public BotClient getBotClient() {
        return botClient;
    }

    public OgameWeb getOgameWeb() {
        return ogameWeb;
    }
}
