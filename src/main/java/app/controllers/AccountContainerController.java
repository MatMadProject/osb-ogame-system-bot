package app.controllers;

import app.data.accounts.Account;
import app.data.accounts.Accounts;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;


public class AccountContainerController {

    @FXML
    private HBox hBoxAccount;
    @FXML
    private Label labelName;
    @FXML
    private Label labelServer;
    @FXML
    private Label labelWebAddress;

    private boolean selected = false;
    private LoginWindowController loginWindowController;
    private Account account;

    /** PL
     * Wybieranie użytkownika.
     * EN
     * User selects.
     */
    @FXML
    void select() {
        if(selected){
            hBoxAccount.getStyleClass().removeAll("selected");
            selected = false;
            Accounts.setSelected(null);
            loginWindowController.disableButtonStart();
            loginWindowController.disableRemoveButton();
        }
        else{
            if(Accounts.getSelected() == null){
                hBoxAccount.getStyleClass().add("selected");
                selected = true;
                Accounts.setSelected(account);
                loginWindowController.disableButtonStart();
                loginWindowController.disableRemoveButton();
            }
        }
    }

    public void setLoginWindowController(LoginWindowController loginWindowController) {
        this.loginWindowController = loginWindowController;
    }

    public void setAccount(Account account) {
        this.account = account;
        labelName.setText(account.getName());
        labelServer.setText(account.getSerwer());
        labelWebAddress.setText(account.getWeb());
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
