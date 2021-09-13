package app.data;

import ogame.utils.log.AppLog;

import java.io.*;
import java.net.URISyntaxException;

public class Configuration
{
    public static boolean firstConfiguration = load();
    private static int startCounter = loadCounter();

    /** PL
     * Wczytuje z pliku inforamcję czy wykonano konfigurację pierszego uruchomienia.
     * EN
     * Loads information from file: the first running comfiguration was executed.
     */
    private static boolean load() {
        URLFactory urlFactory = null;
        try {
            urlFactory = new URLFactory(Configuration.runPath());
            String pathName = urlFactory.disk()+ StaticStrings.MAIN_FOLDER + File.separator + StaticStrings.FIRST_TIME_LOGIN_FILE+
                    StaticStrings.FILE_EXTEND;

            File file = new File(pathName);
            if(file.exists()) {
                try {
                    FileInputStream f = new FileInputStream(pathName);
                    ObjectInputStream o = new ObjectInputStream(f);

                    boolean b = (boolean) o.readObject();

                    o.close();
                    f.close();
                    StaticStrings.DISK = urlFactory.disk();
                    return b;

                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            else {
                AppLog.printOnConsole(Configuration.class.getName(),"Sciezka "+pathName +" nie istnieje.");
            }

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        StaticStrings.DISK = urlFactory != null ? urlFactory.disk():"";
        return true;
    }
    /** PL
     * Zapisuje informację do pliku, że dokonano kofiguracji pierwszego uruchomienia.
     * EN
     * Saves information about the first running configuration to a file.
     */
    public static void save() {
        URLFactory urlFactory;
        try {
            urlFactory = new URLFactory(Configuration.runPath());
            String pathName = urlFactory.disk() + StaticStrings.MAIN_FOLDER + File.separator + StaticStrings.FIRST_TIME_LOGIN_FILE +
                    StaticStrings.FILE_EXTEND;

            FileOutputStream f = new FileOutputStream(pathName);
            ObjectOutputStream o = new ObjectOutputStream(f);
            o.writeObject(false);
            o.close();
            f.close();
            AppLog.printOnConsole(Configuration.class.getName(),"Zapisano wskaźnik pod ścieżką "+pathName);
        }

        catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
    /** PL
     * Wczytuje z pliku inforamcję o ilości uruchomień.
     * EN
     * Saves information about the first running configuration to a file.
     */
    private static int loadCounter() {
        URLFactory urlFactory;
        try {
            urlFactory = new URLFactory(Configuration.runPath());
            String pathName = urlFactory.disk()+ StaticStrings.MAIN_FOLDER + File.separator + StaticStrings.START_COUNTER_FILE+
                    StaticStrings.FILE_EXTEND;

            File file = new File(pathName);
            if(file.exists()) {
                try {
                    FileInputStream f = new FileInputStream(pathName);
                    ObjectInputStream o = new ObjectInputStream(f);

                    int a = (int) o.readObject();

                    o.close();
                    f.close();
                    return a;

                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            else {
                AppLog.printOnConsole(Configuration.class.getName(),"Scieżka "+pathName +" nie istnieje.");
            }

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return 0;
    }
    /** PL
     * Zapisuje informację do pliku o ilości uruchomień.
     * EN
     * Saves information about the number of runs to a file.
     */
    public static void saveCounter() {
        URLFactory urlFactory;
        try {
            urlFactory = new URLFactory(Configuration.runPath());
            String pathName = urlFactory.disk()+ StaticStrings.MAIN_FOLDER + File.separator + StaticStrings.START_COUNTER_FILE+
                    StaticStrings.FILE_EXTEND;

            FileOutputStream f = new FileOutputStream(pathName);
            ObjectOutputStream o = new ObjectOutputStream(f);
            o.writeObject(++startCounter);
            o.close();
            f.close();
            AppLog.print(Configuration.class.getName(),0,"Zapisano licznik pod ścieżką "+pathName);

        }

        catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
    /**
     * PL
     * Pobiera bezwzględną ścieżke z systemu z której uruchomiona jest ta klasa.
     * EN
     * Gets the absolute path from which the class is executed in the system.
     * @return ***
     * @throws URISyntaxException ***
     */
    public static String runPath() throws URISyntaxException {
        return new File(Configuration.class.getProtectionDomain().getCodeSource().getLocation()
                .toURI()).getPath();
    }

    /**
     * PL
     *
     * Ilość uruchomień aplikacji.
     * EN
     * The number of runs.
     * @return ***
     */
    public static String getStartCounter() {
        return String.valueOf(startCounter);
    }
}
