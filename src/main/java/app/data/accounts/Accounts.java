package app.data.accounts;

import app.data.Configuration;
import app.data.StaticStrings;
import app.data.URLFactory;
import ogame.utils.log.AppLog;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class Accounts
{
    private static Account selected = null;
    public static List<Account> list = load();

    public static void setSelected(Account selected) {
        Accounts.selected = selected;
    }

    public static Account getSelected() {
        return selected;
    }

    /** PL
     * Dodaje nowego bota, jeżeli nie istnieje.
     * EN
     * Adds new bot, if not exists.
     * @param a Konto; Account.
     */
    public static void addAccount(Account a) {
        StaticStrings.PLAYER_FOLDER = a.getSerwer() + "-" + a.getName();
        if(!isDuplicated(a)) {
            list.add(a);
            createFolder();
        }
    }
    /** PL
     * Sprawdza czy wprowadzone konto istnieje.
     * EN
     * Checks is account exists.
     * @param a Konto; Account.
     * @return true - if exists, false - if doesn't exist.
     */
    private static boolean isDuplicated(Account a) {
        if(list.size() > 0) {
            for(Account tmp : list)
                if(tmp.equals(a))
                    return true;
        }
        else
            return false;
        // Gdy w liście nie ma wskazanego konta.
        return false;
    }
    /** PL
     * Sprawdza czy wybrano bota..
     * EN
     * Checks is bot selected.
     * @return true - if is selected.
     */
    public static boolean isAccountSelected(){
        return selected != null;
    }
    /** PL
     * Zapisuje dane o botach.
     * EN
     * Saves data about the bots.
     */
    public static void save() {
        String pathName = StaticStrings.DISK + File.separator + StaticStrings.MAIN_FOLDER + File.separator +
                StaticStrings.ACCOUNT_FOLDER + File.separator + StaticStrings.ACCOUNTS_LIST_FILE +
                StaticStrings.FILE_EXTEND;
        try {
            FileOutputStream f = new FileOutputStream(pathName);
            ObjectOutputStream o = new ObjectOutputStream(f);
            o.writeObject(list);
            o.close();
            f.close();
            AppLog.print(Accounts.class.getName(),0,"The bots were save on " + pathName );
        } catch (IOException e) {
            e.printStackTrace();
            AppLog.print(Accounts.class.getName(),1,"The bots were not saved on " + pathName );
        }
    }
    /** PL
     * Wczytujeane o botach.
     * EN
     * Loads data about the bots.
     */
    private static List<Account> load() {
        String pathName = StaticStrings.DISK + File.separator + StaticStrings.MAIN_FOLDER + File.separator +
                StaticStrings.ACCOUNT_FOLDER + File.separator + StaticStrings.ACCOUNTS_LIST_FILE +
                StaticStrings.FILE_EXTEND;

        File file = new File(pathName);
        if(file.exists()) {
            try {
                FileInputStream f = new FileInputStream(pathName);
                ObjectInputStream o = new ObjectInputStream(f);

                List<Account> list = (List<Account>) o.readObject();
                AppLog.print(Accounts.class.getName(),0,"Success loading of the bots list.");
                o.close();
                f.close();
                return list;

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        else {
            AppLog.print(Accounts.class.getName(),1,"Path: " + pathName + " doesn't exist. The bots weren't load.");
        }
        return new ArrayList<>();
    }
    /** PL
     * Tworzy foldery dla bota, podczas pierwszego uruchomienia.
     * EN
     * Creates folders for bots on the first time.
     */
    private static void createFolder() {
        URLFactory urlFactory;
        try {
            urlFactory = new URLFactory(Configuration.runPath());
            String path = urlFactory.disk() + StaticStrings.MAIN_FOLDER + File.separator + StaticStrings.ACCOUNT_FOLDER + File.separator +
                    StaticStrings.PLAYER_FOLDER + File.separator;
            // planety.
            String pathName = path +
                    StaticStrings.PLANETY_FOLDER;
            File file = new File(pathName);
            file.mkdirs();
            // ekspedycje.
            pathName = path +
                    StaticStrings.EKSPEDYCJE_FOLDER;
            file = new File(pathName);
            file.mkdirs();
            // ekspedycje statystyki
            pathName = path +
                    StaticStrings.EKSPEDYCJE_FOLDER + File.separator + StaticStrings.EKSPEDYCJE_STAT_FOLDER;
            file = new File(pathName);
            file.mkdirs();
            // farming.
            pathName = path +
                    StaticStrings.FARMING_FOLDER;
            file = new File(pathName);
            file.mkdirs();
            // log.
            pathName = path +
                    StaticStrings.LOG_FOLDER;
            file = new File(pathName);
            file.mkdirs();
            // autobot.
            pathName = path +
                    StaticStrings.AUTOBOT_FOLDER;
            file = new File(pathName);
            file.mkdirs();
            // autotransport.
            pathName = path +
                    StaticStrings.AUTOTRANSPORT_FOLDER;
            file = new File(pathName);
            file.mkdirs();
            // fleetsave
            pathName = path +
                    StaticStrings.FLEETSAVE_FOLDER;
            file = new File(pathName);
            file.mkdirs();
            // player data
            pathName = path +
                    StaticStrings.PLAYER_DATA_FOLDER;
            file = new File(pathName);
            file.mkdirs();
            // badania
            pathName = path +
                    StaticStrings.BADANIA_FOLDER;
            file = new File(pathName);
            file.mkdirs();

            AppLog.print(Accounts.class.getName(),0,"Success on create bot folders.");
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
            AppLog.print(Accounts.class.getName(),1,"Fails on create bot folders.");
        }
    }
}
