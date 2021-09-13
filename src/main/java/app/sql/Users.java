package app.sql;

import ogame.utils.log.AppLog;
import ogame.utils.watch.Calendar;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Users
{
    private final Connection connection;

    public Users() {
        this.connection = Connector.getConnection();
    }

//    /**
//     * Pobieranie danych z tabeli.
//     */
//    public ArrayList<User> get(){
//        try{
//            if(connection != null){
//                PreparedStatement preparedStatement = connection.prepareStatement(
//                        "SELECT * FROM users");
//                ResultSet resultSet = preparedStatement.executeQuery();
//                ArrayList<User> users = new ArrayList<>();
//                //Wykonuję pętle do ostatniego rekordu w tabeli.
//                while(resultSet.next()){
//                    String id = resultSet.getString(User.ID_DB_COL_NAME);
//                    String user = resultSet.getString(User.USER_DB_COL_NAME);
//                    String pass = resultSet.getString(User.PASS_DB_COL_NAME);
//                    String email = resultSet.getString(User.EMAIL_DB_COL_NAME);
//                    String registration = resultSet.getString(User.REGISTRATION_DB_COL_NAME);
//                    String statusApp = resultSet.getString(User.STATUS_APP_DB_COL_NAME);
//                    String statusWebApp = resultSet.getString(User.STATUS_WEB_APP_DB_COL_NAME);
//                    String statusWeb = resultSet.getString(User.STATUS_WEB_DB_COL_NAME);
//                    String dateTimeApp = resultSet.getString(User.APP_DT_DB_COL_NAME);
//                    String dateTimeWeb = resultSet.getString(User.WEB_DT_COL_NAME);
//                    users.add(new User(id,user,pass,email,statusApp, statusWebApp, statusWeb,
//                            registration,dateTimeApp,dateTimeWeb));
//                }
//                AppLog.print(Users.class.getName(),0,"Downloading users data from database success.");
//                return users;
//            }
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
//        AppLog.print(Users.class.getName(),1,"Downloading users data from database fails.");
//        return new ArrayList<>();
//    }
    /**
     * Pobieranie danych z tabeli.
     */
    public User getUser(String userName){
        User user = null;
        try{
            if(connection != null){
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT * FROM users WHERE name='"+userName+"'");
                ResultSet resultSet = preparedStatement.executeQuery();

                //Wykonuję pętle do ostatniego rekordu w tabeli.
                while(resultSet.next()){
                    String id = resultSet.getString(User.ID_DB_COL_NAME);
                    String username = resultSet.getString(User.USER_DB_COL_NAME);
                    String pass = resultSet.getString(User.PASS_DB_COL_NAME);
                    String email = resultSet.getString(User.EMAIL_DB_COL_NAME);
                    String registration = resultSet.getString(User.REGISTRATION_DB_COL_NAME);
                    String statusApp = resultSet.getString(User.STATUS_APP_DB_COL_NAME);
                    String statusWebApp = resultSet.getString(User.STATUS_WEB_APP_DB_COL_NAME);
                    String statusWeb = resultSet.getString(User.STATUS_WEB_DB_COL_NAME);
                    String dateTimeApp = resultSet.getString(User.APP_DT_DB_COL_NAME);
                    String dateTimeWeb = resultSet.getString(User.WEB_DT_COL_NAME);
                    user = new User(id,username,pass,email,statusApp, statusWebApp, statusWeb,
                            registration,dateTimeApp,dateTimeWeb);
                }
                AppLog.print(Users.class.getName(),0,"Downloading users data from database success.");
                return user;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        AppLog.print(Users.class.getName(),1,"Downloading users data from database fails.");
        return user;
    }
    /** PL
     * Status bota w web aplikacji.
     * EN
     * Bot status on web app.
     * @param user ***
     * @return If is stopped - false.
     */
    public boolean getWebAppStatus(User user){
        try{
            if(connection != null){
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT " + User.STATUS_WEB_APP_DB_COL_NAME + " FROM users WHERE id="+user.getId());
                ResultSet resultSet = preparedStatement.executeQuery();

                //Wykonuję pętle do ostatniego rekordu w tabeli.

                String statusWebApp = resultSet.getString(User.STATUS_WEB_APP_DB_COL_NAME);
                return statusWebApp.equals("0");
            }
        }
        catch (Exception e){
            e.printStackTrace();
            AppLog.print(Users.class.getName(),1,"Failed when try download web app status.");
        }
        return false;
    }
//    /** PL
//     * Logowanie do systemu. Sprawdza poprawność danych.
//     * EN
//     * Login to system. Validates data.
//     * @param user ***
//     * @param pass Zhashowane hasło. Hashed password.
//     * @return Null if login failed.
//     */
//    public User logIn(String user, String pass) {
//        for(User user1 : list){
//            if(user1.checkDataUser(user,pass)){
//                AppLog.print(Users.class.getName(),0,"User: " + user + " login successed.");
//                return user1;
//            }
//        }
//
//        AppLog.print(Users.class.getName(),0,"User: " + user + " login failed.");
//        return null;
//    }

    /** PL
     * Wylogowywanie z systemu.
     * EN
     * Logging out of the system.
     * @param user ***
     */
    public void setLoggedOut(User user) {
        try{
            if(connection != null){
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "UPDATE users SET "+User.STATUS_APP_DB_COL_NAME+"=0 WHERE id="+user.getId());
                preparedStatement.executeUpdate();
                user.setAppLogged(false);
                AppLog.print(Users.class.getName(),0,"User was logged out.");
            }
        }
        catch (Exception e){
            e.printStackTrace();
            AppLog.print(Users.class.getName(),1,"User wasn't logged out.");
        }
    }

    /** PL
     * Update danych o logowaniu do systemu.
     * EN
     * Data updating about login to system.
     * @param user ***
     */
    public void setLoggedInData(User user)
    {
        try{
            if(connection != null){
                String s = Calendar.getDateTimeSQL();
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "UPDATE users SET " +
                                User.STATUS_APP_DB_COL_NAME+"=1, " +User.APP_DT_DB_COL_NAME + "='"+s+
                                "' WHERE id="+user.getId());
                preparedStatement.executeUpdate();
                user.setAppLogged(true);
                user.setAppLoginDateTime(s);
                AppLog.print(Users.class.getName(),0,"User was logged in.");
            }
        }
        catch (Exception e){
            e.printStackTrace();
            AppLog.print(Users.class.getName(),1,"User wasn't logged in.");
        }
    }
}
