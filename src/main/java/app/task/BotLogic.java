package app.task;

import app.data.ErrorLog;
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
            try {
                tasks();
            } catch (Exception e) {
                ErrorLog.save(e.getLocalizedMessage());
                e.printStackTrace();
            }
            Waiter.sleep(10,40);
        }
    }

    public void tasks() throws Exception {
        if(leafTaskManager.getTasks() != null){
            leafTaskManager.getTasks()[0].execute();
            leafTaskManager.getTasks()[1].execute();
            leafTaskManager.getTasks()[2].execute();
            leafTaskManager.getTasks()[3].execute();
            leafTaskManager.getTasks()[4].execute();
        }
    }
}
