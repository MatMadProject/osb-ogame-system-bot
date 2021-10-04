package app.leaftask;

import app.task.BotClient;
import ogame.utils.log.AppLog;

public class LeafTaskManager {

    private LeafTask[] leafTasks;

    public LeafTaskManager() {

    }

    private  void initLeafTask(){

        leafTasks = new LeafTask[]{

        };
    }

    public  void startTask(int taskIndex) {
        if(taskIndex <= leafTasks.length-1)
            leafTasks[taskIndex].start();
        else
            AppLog.print(LeafTaskManager.class.getName(),1,"Task index is out of range. Maximal range: " + (leafTasks.length-1));
    }

    public void stopTask(int taskIndex) {
        if(taskIndex <= leafTasks.length-1) {
            leafTasks[taskIndex].stop();
            AppLog.print(LeafTaskManager.class.getName(),0,"Stopped leaftask " + taskIndex);
        }
        else
            AppLog.print(LeafTaskManager.class.getName(),1,"Task index is out of range. Maximal range: " + (leafTasks.length-1));
    }

    public LeafTask[] getTasks() {
        return leafTasks;
    }

    public void stopTasks(int [] taskIndex) {
        for(int a : taskIndex) {
            for(LeafTask task : leafTasks)
                if(a == task.getIndex())
                    task.stop();
        }
    }
}