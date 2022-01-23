package app.leaftask;

import ogame.OgameWeb;
import ogame.Type;
import ogame.eventbox.Event;
import ogame.planets.Coordinate;
import ogame.planets.Planet;
import ogame.planets.PlanetsList;
import ogame.tabs.*;
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
    public void execute() throws Exception {
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
     * @param lastTimeExecute Time in milliseconds.
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
     * @param ms Time in milliseconds.
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
     * Time of next execute task.
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

    /**
     * Clicks on planet
     * @param planet ***
     * @return If fail, return false.
     */
    public boolean clickPlanet(Planet planet){
        boolean planetSelected;
        do {
            if (PlanetsList.clickOnPlanet(OgameWeb.webDriver, planet.getPositionOnList()))
                break;
            Waiter.sleep(200, 200);
            if (getAntiLooping().check()) {
                getAntiLooping().reset();
                return false;
            }
            Planet tmpPlanet = PlanetsList.selectedPlanet(OgameWeb.webDriver);
            planetSelected = tmpPlanet.getId().equals(planet.getId());
        } while (planetSelected);
        getAntiLooping().reset();
        return true;
    }

    public boolean clickOverview(){
        //Klikanie podgląd
        do{
            Overview.click(OgameWeb.webDriver);
            Waiter.sleep(200,300);
            if(getAntiLooping().check()){
                getAntiLooping().reset();
                return false;
            }
        }while(!Overview.visible(OgameWeb.webDriver)); // Jest niewidoczne
        getAntiLooping().reset();
        return true;
    }

    public boolean clickSupplies(){
        //Klikanie podgląd
        do{
            Supplies.click(OgameWeb.webDriver);
            Waiter.sleep(200,300);
            if(getAntiLooping().check()){
                getAntiLooping().reset();
                return false;
            }
        }while(!Supplies.visible(OgameWeb.webDriver)); // Jest niewidoczne
        getAntiLooping().reset();
        return true;
    }

    public boolean clickOnBuilding(Type type, int listIndex){
        boolean checkFlag;
        do{
            if(type == Type.PRODUCTION)
                Supplies.clickOnBuilding(OgameWeb.webDriver,listIndex);
            else
                Facilities.clickOnBuilding(OgameWeb.webDriver,listIndex);

            Waiter.sleep(200,300);
            if(getAntiLooping().check()){
                getAntiLooping().reset();
                return false;
            }

            if(type == Type.PRODUCTION)
                checkFlag = !Supplies.visibleBuildingDetails(OgameWeb.webDriver,listIndex);
            else
                checkFlag = !Facilities.visibleBuildingDetails(OgameWeb.webDriver,listIndex);
        }while(checkFlag);
        getAntiLooping().reset();
        return true;
    }

    public boolean refreshWebPage(){
        boolean refresh = Overview.clickAlways(OgameWeb.webDriver);
        Waiter.sleep(200,300);
        return refresh;
    }

    public boolean clickFleetDispatch(){
        //Klikanie podgląd
        do{
            FleetDispatch.click(OgameWeb.webDriver);
            Waiter.sleep(200,300);
            if(getAntiLooping().check()){
                getAntiLooping().reset();
                return false;
            }
        }while(!FleetDispatch.visible(OgameWeb.webDriver)); // Jest niewidoczne
        getAntiLooping().reset();
        return true;
    }

    public boolean inputCoordinate(Coordinate coordinate){
        boolean flag;
        do{
            if(!FleetDispatch.setGalaxy(OgameWeb.webDriver,coordinate.getGalaxy())){
                flag = false;
                continue;
            }
            if(!FleetDispatch.setSystem(OgameWeb.webDriver,coordinate.getSystem())){
                flag = false;
                continue;
            }
            flag =  FleetDispatch.setPosition(OgameWeb.webDriver, coordinate.getPlanet());

            Waiter.sleep(200,300);
            if(getAntiLooping().check()){
                getAntiLooping().reset();
                return false;
            }
        }while(!flag);
        getAntiLooping().reset();
        return true;
    }

    public boolean inputCoordinateExpedition(Coordinate coordinate){
        boolean flag;
        do{
            if(!FleetDispatch.setGalaxy(OgameWeb.webDriver,coordinate.getGalaxy())){
                flag = false;
                continue;
            }
            if(!FleetDispatch.setSystem(OgameWeb.webDriver,coordinate.getSystem())){
                flag = false;
                continue;
            }
            flag =  FleetDispatch.setPosition(OgameWeb.webDriver, 16);

            Waiter.sleep(200,300);
            if(getAntiLooping().check()){
                getAntiLooping().reset();
                return false;
            }
        }while(!flag);
        getAntiLooping().reset();
        return true;
    }

    public boolean openEventBox(){
        //Otwieranie openEventBox
        do{
            EventBoxContent.open(OgameWeb.webDriver);
            Waiter.sleep(200,300);
            if(getAntiLooping().check()){
                getAntiLooping().reset();
                return false;
            }
        }while(!EventBoxContent.visible(OgameWeb.webDriver)); // Jest niewidoczne
        getAntiLooping().reset();
        return true;
    }

    public Event getEvent(String idEvent){
        Event event;
        do{
            event = EventBoxContent.eventFromId(OgameWeb.webDriver,idEvent);
            Waiter.sleep(100,100);
            if(getAntiLooping().check()){
                getAntiLooping().reset();
                return null;
            }
        }while(event == null); // Nie przypisano eventu
        getAntiLooping().reset();
        return event;
    }
}
