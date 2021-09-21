package app.controllers;

import app.data.accounts.Account;
import app.data.accounts.Accounts;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class AddAccountContainerController {

    @FXML
    private TextField textFieldPlayerName;
    @FXML
    private TextField textFieldServer;
    @FXML
    private TextField textFieldWebAddress;
    @FXML
    private Label labelError;

    private LoginWindowController loginWindowController;

    /** PL
     * Dodaje nowego bota.
     * EN
     * Adds new bot.
     */
    @FXML
    void save(ActionEvent event) {
        String playerName = textFieldPlayerName.getText();
        String server = textFieldServer.getText();
        String webAddress = textFieldWebAddress.getText();

        if(playerName.isEmpty() && server.isEmpty() && webAddress.isEmpty()){
            labelError.setText("Complete the text fields.");
        }
        else{
            Account account = new Account(playerName,server,webAddress);
            Accounts.addAccount(account);
            Accounts.save();
            loginWindowController.removeAccounts();
            loginWindowController.setAccounts();
            Node  source = (Node)  event.getSource();
            Stage stage  = (Stage) source.getScene().getWindow();
            stage.close();
        }
    }

    /** PL
     * Zamyka okno dodawania nowego bota.
     * EN
     * Closes the add new bot window.
     */
    @FXML
    void close(ActionEvent event) {
        Node  source = (Node)  event.getSource();
        Stage stage  = (Stage) source.getScene().getWindow();
        stage.close();
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
}
