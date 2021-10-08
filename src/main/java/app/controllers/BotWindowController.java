package app.controllers;

import app.controllers_connector.TaskContainerConnector;
import app.leaftask.LeafTask;
import app.leaftask.LeafTaskManager;
import app.task.CheckInternet;
import app.task.GameTime;
import app.task.Logger;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import ogame.utils.log.AppLog;
import ogame.utils.watch.Calendar;

public class BotWindowController {

    @FXML
    public VBox vBoxTaskList;
    @FXML
    public AnchorPane anchorPaneTaskContent;
    @FXML
    private Label labelInternetStatus;
    @FXML
    private Label labelCurrentTime;
    @FXML
    private Label labelStartTime;

    private boolean startTimeFlag = true;
    private TaskContainerControllers selectedLeafTask = null;

    @FXML
    private LoggedContainerController loggedContainerController;

    private Logger logger = null;
    private LeafTaskManager leafTaskManager;

    @FXML
    private void initialize(){
        logger = new Logger(this);
        loggedContainerController.setOnBotWindow(true);
        loggedContainerController.setBotWindowController(this);
    }

    public void stopLogger(){
        if(logger != null){
            logger.stop();
            if(logger.getBotClient() != null) {
                logger.getBotClient().off();
                leafTaskManager.stopAllTask();
            }
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

    private boolean fillTaskList = true;
    public void fillTaskList(){
        for(int i = 0; i < leafTaskManager.getListSize(); i++){
            TaskContainerConnector connector = new TaskContainerConnector(leafTaskManager.getTasks()[i], this);
            vBoxTaskList.getChildren().add(connector.getContainer());
        }
        for(int i = 1; i < 5; i++){
            TaskContainerConnector connector = new TaskContainerConnector(new LeafTask(1,1000,"Example task no "+i), this);
            vBoxTaskList.getChildren().add(connector.getContainer());
        }
        fillTaskList = false;
    }


    public boolean isFillTaskList() {
        return fillTaskList;
    }
    @FXML
    private Label labelStatus;
    @FXML
    private Label labelSleep;
    @FXML
    private Label labelLastExecute;
    @FXML
    private Label labelNextExecute;

    public void setTaskInformation(LeafTask leafTask){
        labelStatus.setText(leafTask.isRun() ? "ON":"OFF");
        labelSleep.setText(leafTask.getSleep()+" ms");
        if(leafTask.getLastTimeExecute() != 0){
            labelLastExecute.setText(Calendar.getDateTime(leafTask.getLastTimeExecute()));
            labelNextExecute.setText(Calendar.getDateTime(leafTask.getNextTimeExecute()));
        }
    }

    public boolean isSelectedLeafTask() {
        return selectedLeafTask != null;
    }

    public void selectedLeafTask(TaskContainerControllers controllers) {
        this.selectedLeafTask = controllers;
    }

    public void unselectLeafTask() {
        this.selectedLeafTask.unselect();
    }

    public void setLeafTaskManager(LeafTaskManager leafTaskManager) {
        this.leafTaskManager = leafTaskManager;
    }
}
