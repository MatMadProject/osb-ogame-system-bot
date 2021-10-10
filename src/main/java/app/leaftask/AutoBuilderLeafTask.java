package app.leaftask;

public class AutoBuilderLeafTask extends LeafTask{

    public AutoBuilderLeafTask(int index, long sleepms, String name) {
        super(index, sleepms, name);
    }

    @Override
    public void execute() {
        if(isRun()){
            if(isSleepTimeOut(System.currentTimeMillis())){
                setLastTimeExecute(System.currentTimeMillis());
            }
        }
    }
}
