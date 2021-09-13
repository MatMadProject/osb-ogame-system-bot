package app.sql;

import java.util.Objects;

public class User {
    private final int id;
    private final String name;
    private final String pass;
    private final String email;
    private boolean isAppLogged;
    private boolean isWebAppLogged;
    private boolean isWebLogged;
    private final String registraionDateTime;
    private String appLoginDateTime;
    private String webLoginDateTime;

    public static final String ID_DB_COL_NAME ="id";
    public static String USER_DB_COL_NAME ="name";
    public static String PASS_DB_COL_NAME ="pass";
    public static String EMAIL_DB_COL_NAME ="email";
    public static String REGISTRATION_DB_COL_NAME = "registrationDateTime";
    public static String STATUS_APP_DB_COL_NAME ="isAppLogged";
    public static String STATUS_WEB_APP_DB_COL_NAME ="isWebAppLogged";
    public static String STATUS_WEB_DB_COL_NAME ="isWebLogged";
    public static String APP_DT_DB_COL_NAME ="appLoginDateTime";
    public static String WEB_DT_COL_NAME ="webLoginDateTime";

    public User(String id, String name, String pass, String email, String isAppLogged, String isWebAppLogged,
                String isWebLogged, String registraionDateTime, String appLoginDateTime, String webLoginDateTime) {
        this.id = Integer.parseInt(id);
        this.name = name;
        this.pass = pass;
        this.email = email;
        this.isAppLogged = Integer.parseInt(isAppLogged) != 0;
        this.isWebAppLogged = Integer.parseInt(isWebAppLogged) != 0;
        this.isWebLogged = Integer.parseInt(isWebLogged) != 0;
        this.registraionDateTime = registraionDateTime;
        this.appLoginDateTime = appLoginDateTime;
        this.webLoginDateTime = webLoginDateTime;
    }

    /*
    EXECUTE
     */
    /** PL
     * Sprawdza czy wprowadzono poprawne dane logowania.
     * EN
     * Checks if correct data has been entered.
     * @return Returns true if data are correct.
     */
    public boolean checkDataUser(String user, String pass){
        return this.name.equals(user) && this.pass.equals(pass);
    }

    /*
    GETTERS
     */
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPass() {
        return pass;
    }

    public String getEmail() {
        return email;
    }

    public boolean isAppLogged() {
        return isAppLogged;
    }

    public boolean isWebAppLogged() {
        return isWebAppLogged;
    }

    public boolean isWebLogged() {
        return isWebLogged;
    }

    public String getRegistraionDateTime() {
        return registraionDateTime;
    }

    public String getAppLoginDateTime() {
        return appLoginDateTime;
    }

    public String getWebLoginDateTime() {
        return webLoginDateTime;
    }
    /*
    SETTERS
     */
    public void setAppLogged(boolean appLogged) {
        isAppLogged = appLogged;
    }

    public void setAppLoginDateTime(String appLoginDateTime) {
        this.appLoginDateTime = appLoginDateTime;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", pass='" + pass + '\'' +
                ", email='" + email + '\'' +
                ", isAppLogged=" + isAppLogged +
                ", isWebAppLogged=" + isWebAppLogged +
                ", isWebLogged=" + isWebLogged +
                ", registraionDateTime='" + registraionDateTime + '\'' +
                ", appLoginDateTime='" + appLoginDateTime + '\'' +
                ", webLoginDateTime='" + webLoginDateTime + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
