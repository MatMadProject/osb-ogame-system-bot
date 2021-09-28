package app.controllers;

import app.task.CheckInternet;
import app.task.GameTime;
import app.task.Logger;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import ogame.utils.log.AppLog;
import ogame.utils.watch.Calendar;

public class BotWindowController {

    @FXML
    private Label labelInternetStatus;
    @FXML
    private Label labelCurrentTime;
    @FXML
    private Label labelStartTime;

    private boolean startTimeFlag = true;

    @FXML
    private LoggedContainerController loggedContainerController;

    private Logger logger = null;

    @FXML
    private void initialize(){

        logger = new Logger(this);
        loggedContainerController.setOnBotWindow(true);
        loggedContainerController.setBotWindowController(this);
    }

    public void stopLogger(){
        if(logger != null){
            logger.stop();
            if(logger.getBotClient() != null)
                logger.getBotClient().off();
            logger = null;
            AppLog.print(BotWindowController.class.getName(),0,"Logger stopped.");
        }
    }

    public Logger getLogger() {
        return logger;
    }

    public void setStartTime(){
        if(startTimeFlag){
            labelStartTime.setText(Calendar.getDateTime());
            startTimeFlag = false;
        }
    }

    public void setInternetStatus(){
        if(CheckInternet.connected){
            labelInternetStatus.getStyleClass().removeAll("label-disconnected");
            labelInternetStatus.getStyleClass().add("label-connected");
            labelInternetStatus.setText("Connected");
        }
        else{
            labelInternetStatus.getStyleClass().removeAll("label-connected");
            labelInternetStatus.getStyleClass().add("label-disconnected");
            labelInternetStatus.setText("Disconnected");
        }
    }

    public void setActualGameTime(){
        labelCurrentTime.setText(GameTime.datetime);
    }
}
