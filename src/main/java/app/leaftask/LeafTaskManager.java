package app.leaftask;

import ogame.utils.log.AppLog;

public class LeafTaskManager {

    private LeafTask[] leafTasks;
    private int listSize;

    public LeafTaskManager() {
        initLeafTask();
    }

    private void initLeafTask(){
        leafTasks = new LeafTask[]{
            new PlanetsLeafTask(0,1000*30,"Planets",true),
            new AutoBuilderLeafTask(1,1500,"Auto builder"),
            new ImperiumLeafTask(2,5*1000,"Imperium"),
            new AutoResearchLeafTask(3,1000,"Auto research"),
            new ExpeditionLeafTask(4,1000,"Expedition"),
            new DefenceLeafTask(5,1000,"Defence"),
            new ShipLeafTask(6,1050,"Ship"),
            new TransportLeafTask(7,1050,"Transport")
        };
        listSize = leafTasks.length;
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

    public void stopAllTask(){
        for(LeafTask task : leafTasks)
            task.stop();
    }

    public int getListSize() {
        return listSize;
    }
}
