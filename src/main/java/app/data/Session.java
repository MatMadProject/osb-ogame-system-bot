package app.data;

import app.sql.User;
import app.sql.Users;

/** PL
 * Przechowuje dane sesji logowania.
 * EN
 * Stores login session data.
 */
public class Session {
    public static Users users = null;
    public static User user = null;

    public static void logout(){
        users.setLoggedOut(user);
        users = null;
        user = null;
    }

    public static boolean isStoppedOnWebApp(){
        return users.getWebAppStatus(user);
    }
}
