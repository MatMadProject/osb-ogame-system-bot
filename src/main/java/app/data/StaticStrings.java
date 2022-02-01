package app.data;

import java.io.File;

public class StaticStrings {
    public static String DISK = ""; // Nazwa dysku przypisywana jest podczas uruchomienia.
    public static final String MAIN_FOLDER = "ogamebot";
    public static final String FILE_EXTEND = ".obt";
    public static final String FILE_EXTEND_TXT = ".txt";
    public static String PLAYER_FOLDER_PATH = StaticStrings.DISK + File.separator +
            StaticStrings.MAIN_FOLDER + File.separator +
            StaticStrings.ACCOUNT_FOLDER + File.separator +
            StaticStrings.PLAYER_FOLDER + File.separator;

    public static void setPlayerFolderPath() {
        PLAYER_FOLDER_PATH = StaticStrings.DISK + File.separator +
                StaticStrings.MAIN_FOLDER + File.separator +
                StaticStrings.ACCOUNT_FOLDER + File.separator +
                StaticStrings.PLAYER_FOLDER + File.separator;
    }

    /*
        FILES
         */
    public static final String FIRST_TIME_LOGIN_FILE = "first-time";
    public static final String START_COUNTER_FILE = "start-counter";
    public static final String ACCOUNTS_LIST_FILE = "accounts-list";
    public static final String LAST_LOGIN_FILE = "last-login";
    //Ekspedycje
    public static final String EXPEDITION_FILE = "expedition";
    public static final String EXPEDITION_LOG_FILE = "expedition-log";
    //Autotransport
    public static final String AUTOTRANSPOSRT_CONFIG_FILE = "autotransport-config";
    public static final String AUTOTRANSPORT_FILE = "autotransport"; //v2
    //Farming
    public static final String FARMING_CONFIG_FILE = "farming-config";
    //Fleetsave
    public static final String FLEETSAVE_CONFIG_FILE = "fleetsave-config";
    //Badania
    public static final String RESEARCH_FILE = "research";
    //Planety
    public static final String PLANETS_FILE = "planets-config";
    public static final String COLONIES_DATA_FILE = "colonies-data";
    //AutoBuilder
    public static final String AUTO_BUILDER_FILE = "autobuilder";
    public static final String AUTO_RESEARCH_FILE = "autoresearch";
    //Player data
    public static final String PLAYER_DATA_FILE = "player-data";
    //Error
    public static final String ERRORS_FILE = "error";
    //Defence
    public static final String DEFENCE_FILE = "defence";
    //Ship
    public static final String SHIP_FILE = "ship";
    /*
    FOLDERS
     */
    public static String ACCOUNT_FOLDER = "";
    public static String PLAYER_FOLDER = "";
    public static final String EKSPEDYCJE_FOLDER = "ekspedycje";
    public static final String EKSPEDYCJE_STAT_FOLDER = "st";
    public static final String PLANETY_FOLDER = "planety";
    public static final String FARMING_FOLDER = "farming";
    public static final String LOG_FOLDER = "log";
    public static final String AUTOBOT_FOLDER = "autobot";
    public static final String AUTOTRANSPORT_FOLDER = "autotransport";
    public static final String FLEETSAVE_FOLDER = "fleetsave";
    public static final String PLAYER_DATA_FOLDER = "playerdata";
    public static final String BADANIA_FOLDER = "research";
    /*
    VERSION
     */
    public static final String VERSION = "Version 0.5 - beta";

    /*
    STRINGS
     */
    public static final String SNIPPEST_OF_TITILE_OF_OGAME_SITE_PL = "OGame - najpopularniejsza gra";
    public static final String SNIPPEST_OF_TITILE_OF_OGAME_SITE_DE = "OGame - Das erfolgreichste Browsergame";
    public static final String [] SNIPPESTS_OF_TITILE_OF_OGAME_SITE ={
            SNIPPEST_OF_TITILE_OF_OGAME_SITE_PL,
            SNIPPEST_OF_TITILE_OF_OGAME_SITE_DE
    };
    public static final String CHROMEDRIVER_FILE = "chromedriver.exe";
    public static final String FIREFOXDRIVER_FILE = "geckodriver.exe";
    public static String APP_INFO_STRING = "Błąd. Uruchom ponownie.";
    public static final String OGAME_SITE_TITLE_WHEN_INTERNET_DISCONNECTED = "gameforge.com";
    public static final String OGAME_SITE_TITLE_WHEN_INTERNET_DISCONNECTED2 = "Nie odnaleziono serwera";
    public static final String OGAME_SITE_TITLE_WHEN_INTERNET_DISCONNECTED3 = "Błąd wczytywania strony";
}
