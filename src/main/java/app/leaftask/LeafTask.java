package app.leaftask;

import ogame.utils.Waiter;

public class LeafTask implements Execute{

    private boolean run = true;
    private long lastTimeExecute = 0;
    private long sleep;
    private final int index;
    private final String name;

    public LeafTask(int index, long sleep, String name) {
        this.sleep = sleep;
        this.name = name;
        this.index = index;
    }

    public LeafTask(int index, long sleep, String name, boolean run) {
        this.sleep = sleep;
        this.name = name;
        this.index = index;
        this.run = run;
    }
  /*
    EXECUTING METHODS
   */
    /**
     * Checks if sleep time has passed since the last task execution
     * @param currentTime Current time.
     * @return If time has passed returns true.
     */
    protected boolean isSleepTimeOut(long currentTime)
    {
        return  (currentTime - lastTimeExecute) > sleep;
    }
    /**
     * Body of executing task.
     */
    @Override
    public void execute() {
        Waiter.sleep(15,15);
    }
    /*
    SETTERS
     */
    public void stop(){
        run = false;
    }
    public void start(){
        run = true;
    }
    /**
     * Set time of last execute task.
     * @param lastTimeExecute Time in miliseconds.
     */
    protected void setLastTimeExecute(long lastTimeExecute) {
        this.lastTimeExecute = lastTimeExecute;
    }
    /**
     * Set task sleep time.
     * @param ms Time in miliseconds.
     */
    public void setSleep(long ms) {
        this.sleep = ms;
    }
    /*
    GETTERS
     */
    /**
     * Returns whether the task is running.
     */
    public boolean isRun() {
        return run;
    }
    /**
     * Task index. Should be unique.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Task name.
     */
    public String getName() {
        return name;
    }
    /**
     * Time of last execute task.
     */
    protected long getLastTimeExecute() {
        return lastTimeExecute;
    }
    /**
     * Task sleep time.
     */
    public long getSleep() {
        return sleep;
    }
}