package app.controllers;

import app.data.Session;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class LoggedContainerController {

    @FXML
    private Label labelUser;

    private LoginWindowController loginWindowController;
    private boolean isOnBotWindow = false;
    private BotWindowController botWindowController;

    @FXML
    void initialize(){
        labelUser.setText(Session.user.getName());
    }

    /** PL
     * Konczy sesję i wylogowuję użytkownika.
     * EN
     * Ends session and log out user.
     */
    @FXML
    void logout(ActionEvent event) {
        try {
            if(isOnBotWindow){
                botWindowController.getLogger().getOgameWeb().close();
                botWindowController.stopLogger();
                Session.logout();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/login-window.fxml"));
                Parent root = fxmlLoader.load();
                LoginWindowController loginWindowController = fxmlLoader.getController();
                setLoginWindowController(loginWindowController);
                Stage primaryStage = new Stage();
                primaryStage.setTitle("OGame System Bot - Login");
                Scene scene = new Scene(root);
                scene.getStylesheets().add("/css/login-style.css");
                primaryStage.setScene(scene);
                primaryStage.setResizable(false);
                primaryStage.sizeToScene();
                primaryStage.show();
                primaryStage.setOnCloseRequest(e ->
                {
                    if(Session.user != null)
                        Session.logout();
                    Platform.exit();
                    System.exit(0);
                });
                //Zamykanie okna logowania
                Node  source = (Node)  event.getSource();
                Stage stage  = (Stage) source.getScene().getWindow();
                stage.close();
                isOnBotWindow = false;
                loginWindowController.setContent();
                loginWindowController.removeAccounts();
            }
            else{
                Session.logout();
                loginWindowController.setContent();
                loginWindowController.removeAccounts();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setLoginWindowController(LoginWindowController loginWindowController) {
        this.loginWindowController = loginWindowController;
    }

    @FXML
    void onMouseEntered(MouseEvent event) {
        ((Node)event.getSource()).getStyleClass().add("hoover");
    }
    @FXML
    void onMouseExited(MouseEvent event) {
        ((Node)event.getSource()).getStyleClass().removeAll("hoover");
    }

    public void setOnBotWindow(boolean onBotWindow) {
        isOnBotWindow = onBotWindow;
    }

    public void setBotWindowController(BotWindowController botWindowController) {
        this.botWindowController = botWindowController;
    }
}
