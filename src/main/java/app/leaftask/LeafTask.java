package app.leaftask;

import ogame.OgameWeb;
import ogame.planets.Planet;
import ogame.planets.PlanetsList;
import ogame.utils.AntiLooping;
import ogame.utils.Waiter;

public class LeafTask implements Execute{

    private boolean run = false;
    private long lastTimeExecute = 0;
    private long nextTimeExecute = 0;
    private long sleep;
    private final int index;
    private final String name;
    private final AntiLooping antiLooping;

    public LeafTask(int index, long sleepms, String name) {
        this.sleep = sleepms;
        this.name = name;
        this.index = index;
        this.antiLooping = new AntiLooping(5);
    }

    public LeafTask(int index, long sleepms, String name, boolean run) {
        this.sleep = sleepms;
        this.name = name;
        this.index = index;
        this.run = run;
        this.antiLooping = new AntiLooping(5);
    }
  /*
    EXECUTING METHODS
   */
    /**
     * Checks if sleep time has passed since the last task execution.
     * @param currentTime Current time.
     * @return If time has passed returns true.
     */
    protected boolean isSleepTimeOut(long currentTime)
    {
        return  currentTime >= nextTimeExecute;
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
        setLastNextExecute();
    }
    /**
     * Set time of last execute task.
     */
    protected void setLastNextExecute() {
        this.nextTimeExecute = this.lastTimeExecute + sleep;
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
    public long getLastTimeExecute() {
        return lastTimeExecute;
    }
    /**
     * Time of nextxecute task.
     */
    public long getNextTimeExecute() {
        return nextTimeExecute;
    }
    /**
     * Task sleep time.
     */
    public long getSleep() {
        return sleep;
    }

    public AntiLooping getAntiLooping() {
        return antiLooping;
    }

    public boolean clickPlanet(Planet planet){
        boolean planetSelected;
        do {
            if (PlanetsList.clickOnPlanet(OgameWeb.webDriver, planet.getPositionOnList()))
                break;
            Waiter.sleep(200, 200);
            if (getAntiLooping().check()) {
                getAntiLooping().reset();
                return true;
            }
            Planet tmpPlanet = PlanetsList.selectedPlanet(OgameWeb.webDriver);
            planetSelected = tmpPlanet.getId().equals(planet.getId());
        } while (planetSelected);
        return false;
    }
}
