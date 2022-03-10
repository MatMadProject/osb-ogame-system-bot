package app.controllers;

import app.controllers_connector.RequirementsContainerConnector;
import app.controllers_connector.TaskContainerConnector;
import app.leaftask.LeafTask;
import app.leaftask.LeafTaskManager;
import app.task.CheckInternet;
import app.task.GameTime;
import app.task.Logger;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import ogame.RequiredTechnology;
import ogame.utils.log.AppLog;
import ogame.utils.watch.Calendar;

import java.util.ArrayList;

public class BotWindowController {

    @FXML
    public VBox vBoxTaskList;
    @FXML
    public AnchorPane anchorPaneTaskContent;
    @FXML
    public VBox vBoxRequirements;
    @FXML
    private Label labelInternetStatus;
    @FXML
    private Label labelCurrentTime;
    @FXML
    private Label labelStartTime;

    private boolean startTimeFlag = true;
    private TaskContainerController selectedLeafTask = null;

    private Logger logger = null;
    private LeafTaskManager leafTaskManager;

    //Controllers
    @FXML
    private LoggedContainerController loggedContainerController;
    @FXML
    public PlanetLeafTaskController planetsLeafTaskController;

    //Getters - Controllers
    public PlanetLeafTaskController getPlanetsLeafTaskController() {
        return planetsLeafTaskController;
    }

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
        else{
            labelLastExecute.setText("");
            labelNextExecute.setText("");
        }
    }
    //Display reuirements for object.
    public void setRequirementsTechnology(ArrayList<RequiredTechnology> requiredTechnologyArrayList, Object planetListObject){
        vBoxRequirements.getChildren().clear();
        //When object has required technolgies
        if(requiredTechnologyArrayList != null)
            for(RequiredTechnology requiredTechnology : requiredTechnologyArrayList){
                RequirementsContainerConnector requirementsContainerConnector = new RequirementsContainerConnector(requiredTechnology, planetListObject);
                vBoxRequirements.getChildren().add(requirementsContainerConnector.content());
            }
    }

    public void setAnchorPaneTaskContent(Node content){
        anchorPaneTaskContent.getChildren().clear();
        anchorPaneTaskContent.getChildren().add(content);
    }

    public boolean isSelectedLeafTask() {
        return selectedLeafTask != null;
    }

    public void selectedLeafTask(TaskContainerController controllers) {
        this.selectedLeafTask = controllers;
    }

    public void unselectLeafTask() {
        this.selectedLeafTask.unselect();
    }

    public TaskContainerController getSelectedLeafTask() {
        return selectedLeafTask;
    }

    public void setLeafTaskManager(LeafTaskManager leafTaskManager) {
        this.leafTaskManager = leafTaskManager;
    }

    public LeafTaskManager getLeafTaskManager() {
        return leafTaskManager;
    }
}
