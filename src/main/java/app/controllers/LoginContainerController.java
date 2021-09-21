package app.controllers;

import app.data.Session;
import app.data.StaticStrings;
import app.server.Connector;
import app.sql.User;
import app.sql.Users;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import ogame.utils.StringFactory;

import java.io.IOException;

public class LoginContainerController
{
    private LoginWindowController loginWindowController;

    @FXML
    private TextField editTextUsername;

    @FXML
    private TextField editTextPassword;

    @FXML
    private Label labelError;

    /** PL
     * Logowanie użytkownika.
     * EN
     * Logs user.
     */
    @FXML
    void login() {
        String userName = editTextUsername.getText();
        String password = editTextPassword.getText();

        boolean emptyFields = userName.isEmpty() || password.isEmpty();
        boolean alphanumeric = StringFactory.isAlphanumeric(userName);
        if(emptyFields){
            labelError.setText("Complete the text fields.");
            return;
        }
        if(!alphanumeric){
            labelError.setText("Username can contains only alphanumeric.");
            return;
        }
        Users users = new Users();
        User user = users.getUser(userName);
        if(user == null){
            labelError.setText("Username deosn't exists.");
            return;
        }
        if(user.isAppLogged()){
            labelError.setText("User is logged.");
            return;
        }
        boolean validatePass = Connector.validatePassword(password,user.getPass());

        if(validatePass) {
            users.setLoggedInData(user);
            Session.users = users;
            Session.user = user;
            StaticStrings.ACCOUNT_FOLDER = Session.user.getName();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/logged-container.fxml"));
            VBox content = null;
            try {
                content = fxmlLoader.load();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            LoggedContainerController loggedContainerController = fxmlLoader.getController();
            loggedContainerController.setLoginWindowController(loginWindowController);
            loginWindowController.getvBoxLoginContainer().getChildren().clear();
            loginWindowController.getvBoxLoginContainer().getChildren().add(content);
            loginWindowController.disableAddButton();
            loginWindowController.setAccounts();
        }
        else
            labelError.setText("Wrong password.");
    }

    @FXML
    void onMouseEntered(MouseEvent event) {
        ((Node)event.getSource()).getStyleClass().add("hoover");
    }
    @FXML
    void onMouseExited(MouseEvent event) {
        ((Node)event.getSource()).getStyleClass().removeAll("hoover");
    }

    public void setLoginWindowController(LoginWindowController loginWindowController) {
        this.loginWindowController = loginWindowController;
    }
}
