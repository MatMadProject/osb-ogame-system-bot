package app.controllers;

import app.task.Logger;
import javafx.fxml.FXML;
import ogame.utils.log.AppLog;

public class BotWindowController {

    @FXML
    private LoggedContainerController loggedContainerController;

    private Logger logger = null;

    @FXML
    private void initialize(){

        logger = new Logger();
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
}
