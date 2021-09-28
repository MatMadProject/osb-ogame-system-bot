package app.task;

import ogame.utils.log.AppLog;

public class Task implements Runnable{

    private boolean run = true;
    private Thread thread;
    private long lastTimeExecute = 0;
    private long sleep = 0;
    private final int index = -1;
    private String name = "Empty task";

    public Task(String name) {
        this.name = name;
    }

    public Task() { }
    /*
    EXECUTE
     */
    /**PL
     * Główna metoda w której wykonywane są zadania.
     * EN
     * The main method within which to perform tasks.
     */
    @Override
    public void run() { }
    /**PL
     * Uruchamia zadania.
     * EN
     * Starts task.
     */
    public void start(){ run = true; }
    /**PL
     * Zatrzymuje zadanie.
     * EN
     * Stops task.
     */
    public void stop(){ run = false; }
    /**PL
     * Uruchamia wątek.
     * EN
     * Starts thread.
     */
    protected void startThread() {
        if(thread != null)
            thread.start();
        else
            AppLog.print(Task.class.getName(),1,"Thread ["+name+";"+index+"] is null." );
    }
    /**PL
     * Czy minął czas przerwy.
     * EN
     * Is pause time out.
     * @return Return true if time out.
     */
    protected boolean isSleepTimeOut() { return  System.currentTimeMillis() - lastTimeExecute > sleep; }
    /*
    SETTERS
    */
    protected void setThread(Thread thread) { this.thread = thread; }
    /**PL
     * Ustawia czas ostatniego wykonania - czas systemowy [ms].
     * EN
     * Sets last time execute - current system time [ms].
     */
    protected void setLastTimeExecute() { lastTimeExecute = System.currentTimeMillis(); }
    /**PL
     * Ustawia czas co ile wykonać zadanie.
     * EN
     * Sets stop time period, beetween  execute task.
     * @param sleep Czas [ms]; Time [ms].
     */
    public void setSleep(long sleep) { this.sleep = sleep; }

    public void terminate(){
        if(thread != null){
            stop();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    /*
    GETTERS
    */
    public int getIndex() { return index; }
    public boolean isRun() { return run; }
    public Thread getThread() { return thread; }
    public long getLastTimeExecute() { return lastTimeExecute; }
    public long getSleep() { return sleep; }
    public String getName() { return name; }

    @Override
    public String toString() {
        return "Task{" +
                "run=" + run +
                ", thread=" + thread +
                ", lastTimeExecute=" + lastTimeExecute +
                ", sleep=" + sleep +
                ", index=" + index +
                ", name='" + name + '\'' +
                '}';
    }
}
