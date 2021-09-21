package app.controllers;

import app.data.Configuration;
import app.data.StaticStrings;
import app.data.accounts.Account;
import app.data.accounts.Accounts;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class LoginWindowController {

    @FXML
    private Label labelFooter;
    @FXML
    private VBox vBoxLoginContainer;
    @FXML
    private Button buttonStart;
    @FXML
    private VBox vBoxAccounts;
    @FXML
    private Pane paneRemove;
    @FXML
    private Pane paneAdd;

    @FXML
    private void initialize() {
        labelFooter.setText(StaticStrings.VERSION + " : " + Configuration.getStartCounter() + " | ©Copyright 2021 MatMadProject");

        try {
            setContent();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void onMouseEntered(MouseEvent event) {
        ((Node)event.getSource()).getStyleClass().add("hoover");
    }
    @FXML
    void onMouseExited(MouseEvent event) {
        ((Node)event.getSource()).getStyleClass().removeAll("hoover");
    }


    /** PL
     * Ustawia w lewnym oknie formularz logowania.
     * EN
     * Sets login form in left window.
     */
    void setContent() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/login-container.fxml"));
        VBox contentLogin = fxmlLoader.load();
        LoginContainerController loginContainerController = fxmlLoader.getController();
        vBoxLoginContainer.getChildren().clear();
        vBoxLoginContainer.getChildren().add(contentLogin);
        loginContainerController.setLoginWindowController(this);
    }

    /** PL
     * Ustawia kontery założonych kont.
     * EN
     * Sets created accounts containers.
     */
    void setAccounts() {
        if(!Accounts.list.isEmpty()){
            vBoxAccounts.getChildren().clear();

            for(Account account : Accounts.list){
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/account-container.fxml"));
                HBox contentAccount = null;
                try {
                    contentAccount = fxmlLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                AccountContainerController acccountContainerController = fxmlLoader.getController();
                acccountContainerController.setLoginWindowController(this);
                acccountContainerController.setAccount(account);
                vBoxAccounts.getChildren().add(contentAccount);
            }
        }
    }

    /** PL
     * Usuwa kontery założonych kont.
     * EN
     * Deletes created accounts containers.
     */
    void removeAccounts() {
        vBoxAccounts.getChildren().clear();
        Accounts.setSelected(null);
        buttonStart.setDisable(true);
        paneRemove.setDisable(true);
        paneAdd.setDisable(true);
    }

    /** PL
     * Wyłączanie / Włączanie button start.
     * EN
     * Disabling / Enabling button start.
     */
    void disableButtonStart(){
        buttonStart.setDisable(!buttonStart.isDisable());
    }
    /** PL
     * Wyłączanie / Włączanie button remove.
     * EN
     * Disabling / Enabling button remove.
     */
    void disableRemoveButton(){
        paneRemove.setDisable(!paneRemove.isDisable());
    }

    /** PL
     * Wyłączanie / Włączanie button add.
     * EN
     * Disabling / Enabling button add.
     */
    void disableAddButton(){
        paneAdd.setDisable(!paneAdd.isDisable());
    }

    @FXML
    void add() {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/add-account-container.fxml"));
            Parent root = fxmlLoader.load();
            AddAccountContainerController controller = fxmlLoader.getController();
            controller.setLoginWindowController(this);
            Stage primaryStage = new Stage();
            primaryStage.setTitle("OGame System Bot - Add new bot");
            Scene scene = new Scene(root);
            scene.getStylesheets().add("/css/add-bot.css");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.sizeToScene();
            primaryStage.initStyle(StageStyle.UNDECORATED);
            primaryStage.show();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    @FXML
    void remove() {
        if(Accounts.isAccountSelected()){
            Accounts.list.remove(Accounts.getSelected());
            Accounts.save();
            Accounts.setSelected(null);
            buttonStart.setDisable(true);
            paneRemove.setDisable(true);
            setAccounts();
        }
    }
    @FXML
    void start(ActionEvent event) {
        Stage primaryStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/bot-window.fxml"));
        try {
            Parent root = fxmlLoader.load();
            primaryStage.setTitle("OGame System Bot");
            Scene scene = new Scene(root);
            scene.getStylesheets().add("/css/bot-window.css");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.sizeToScene();
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Zamykanie okna logowania
        Node  source = (Node)  event.getSource();
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
    }

    /*GETTERS*/
    public VBox getvBoxLoginContainer() {
        return vBoxLoginContainer;
    }
}
