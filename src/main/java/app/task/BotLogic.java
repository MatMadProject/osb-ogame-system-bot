package app.task;

import app.leaftask.LeafTaskManager;
import ogame.utils.Waiter;
import ogame.utils.log.AppLog;

public class BotLogic extends Task{
    private final LeafTaskManager leafTaskManager;

    public BotLogic(LeafTaskManager leafTaskManager) {
        this.leafTaskManager = leafTaskManager;
        setThread(new Thread(this));
        startThread();
        AppLog.print(CheckInternet.class.getName(),0,"Bot logic started.");
    }

    @Override
    public void run() {
        while(isRun()) {
            tasks();
            Waiter.sleep(10,40);
        }
    }

    public void tasks(){
        if(leafTaskManager.getTasks() != null){
            leafTaskManager.getTasks()[0].execute();
            leafTaskManager.getTasks()[1].execute();
            leafTaskManager.getTasks()[2].execute();
        }
    }
}
