package app.leaftask;

import ogame.DataTechnology;
import ogame.OgameWeb;
import ogame.ResourcesBar;
import ogame.Type;
import ogame.eventbox.Event;
import ogame.planets.*;
import ogame.ships.Mission;
import ogame.tabs.*;
import ogame.utils.AntiLooping;
import ogame.utils.Waiter;

import java.util.ArrayList;

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

    /**
     * Clicks on planet list object [Planet, Moon]
     * @return If fail, return false.
     */
    public boolean clickOnPlanetListObject(Object planetListObject){
        boolean planetSelected;
        do {
            if(planetListObject instanceof Planet){
                Planet planet = (Planet) planetListObject;
                if (PlanetsList.clickOnPlanet(OgameWeb.webDriver, planet.getPositionOnList()))
                    break;
                Waiter.sleep(200, 200);
                if (getAntiLooping().check()) {
                    getAntiLooping().reset();
                    return false;
                }
                Planet tmpPlanet = PlanetsList.selectedPlanet(OgameWeb.webDriver);
                planetSelected = tmpPlanet.getId().equals(planet.getId());
            }else{
                Moon moon = (Moon) planetListObject;
                if (PlanetsList.clickOnMoon(OgameWeb.webDriver, moon.getPositionOnList()))
                    break;
                Waiter.sleep(200, 200);
                if (getAntiLooping().check()) {
                    getAntiLooping().reset();
                    return false;
                }
                Planet tmpPlanet = PlanetsList.selectedMoon(OgameWeb.webDriver);
                planetSelected = tmpPlanet.getPositionOnList() == moon.getPositionOnList();
            }

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
    public boolean clickFacilities(){
        //Klikanie podgląd
        do{
            Facilities.click(OgameWeb.webDriver);
            Waiter.sleep(200,300);
            if(getAntiLooping().check()){
                getAntiLooping().reset();
                return false;
            }
        }while(!Facilities.visible(OgameWeb.webDriver)); // Jest niewidoczne
        getAntiLooping().reset();
        return true;
    }

    public boolean clickResearch(){
        do{
            Research.click(OgameWeb.webDriver);
            Waiter.sleep(200,300);
            if(getAntiLooping().check()){
                getAntiLooping().reset();
                return false;
            }
        }while(!Research.visible(OgameWeb.webDriver)); // Jest niewidoczne
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
    public boolean clickDefence(){
        //Klikanie podgląd
        do{
            Defence.click(OgameWeb.webDriver);
            Waiter.sleep(200,300);
            if(getAntiLooping().check()){
                getAntiLooping().reset();
                return false;
            }
        }while(!Defence.visible(OgameWeb.webDriver)); // Jest niewidoczne
        getAntiLooping().reset();
        return true;
    }

    public boolean sendFleet(){
        boolean isSent = false;
        do{
            isSent = FleetDispatch.sendFleet(OgameWeb.webDriver);
            Waiter.sleep(200,300);
            if(getAntiLooping().check()){
                getAntiLooping().reset();
                return false;
            }
        }while(isSent); // Jest niewidoczne
        getAntiLooping().reset();
        return true;
    }
    public boolean clickShipyard(){
        do{
            Shipyard.click(OgameWeb.webDriver);
            Waiter.sleep(200,300);
            if(getAntiLooping().check()){
                getAntiLooping().reset();
                return false;
            }
        }while(!Shipyard.visible(OgameWeb.webDriver)); // Jest niewidoczne
        getAntiLooping().reset();
        return true;
    }
    public boolean clickOnShipItem(DataTechnology dataTechnology){
        do{
            Shipyard.clickOnShip(OgameWeb.webDriver,dataTechnology);
            Waiter.sleep(200,300);
            if(getAntiLooping().check()){
                getAntiLooping().reset();
                return false;
            }
        }while(!Shipyard.visibleShipDetails(OgameWeb.webDriver,dataTechnology));
        getAntiLooping().reset();
        return true;
    }
    public boolean clickOnDefenceItem(DataTechnology dataTechnology){
        do{
            Defence.clickOnDefence(OgameWeb.webDriver,dataTechnology);
            Waiter.sleep(200,300);
            if(getAntiLooping().check()){
                getAntiLooping().reset();
                return false;
            }
        }while(!Defence.visibleDefenceDetails(OgameWeb.webDriver,dataTechnology));
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
    public boolean selectMoonDestinationObject(){
        //Otwieranie openEventBox
        do{
            FleetDispatch.clickTargetMoon(OgameWeb.webDriver);
            Waiter.sleep(200,300);
            if(getAntiLooping().check()){
                getAntiLooping().reset();
                return false;
            }
        }while(!FleetDispatch.isTargetMoonSelected(OgameWeb.webDriver)); // Jest niewidoczne
        getAntiLooping().reset();
        return true;
    }

    public boolean selectFleetMission(Mission mission){
        //Otwieranie openEventBox
        do{
            FleetDispatch.selectMission(OgameWeb.webDriver,mission);
            Waiter.sleep(200,300);
            if(getAntiLooping().check()){
                getAntiLooping().reset();
                return false;
            }
        }while(!FleetDispatch.isMissionSelected(OgameWeb.webDriver,mission)); // Jest niewidoczne
        getAntiLooping().reset();
        return true;
    }

    public Resources resourcesOnPlanet(){
        long metal = ResourcesBar.metal(OgameWeb.webDriver);
        long crystal = ResourcesBar.crystal(OgameWeb.webDriver);
        long deuterium = ResourcesBar.deuterium(OgameWeb.webDriver);
        return new Resources(metal,crystal,deuterium,0);
    }

    /**
     *
     * @param eventList ***
     * @param eventTargetCoordinate Expedition sent at the moment.
     * @param timeShiftInSeconds Maximum difference in seconds between time from sending and time downloaded from event
     * @param returnTimeSecond Calculated return time of the expedition. (Fly time *2 + Expedition time)
     * @return Return null if expedition not found.
     */
    public Event getCorrectEventFromEventBox(ArrayList<Event> eventList, Coordinate eventTargetCoordinate, long timeShiftInSeconds, long returnTimeSecond){
        Event event = null;
        do{
            for(Event tmpEvent : eventList)
                if(tmpEvent.getDestinationCoordinate().equals(eventTargetCoordinate)) {
                    long timeDifference = tmpEvent.getArrivalTime() - returnTimeSecond;
                    //timeDifference between <-x;x>
                    if(timeDifference <= timeShiftInSeconds && timeDifference >= timeShiftInSeconds*-1){
                        event = tmpEvent;
                        break;
                    }
                }
            Waiter.sleep(200,300);
            if(getAntiLooping().check()){
                getAntiLooping().reset();
                return null;
            }

        }while(event == null); // Nie przypisano eventu
        getAntiLooping().reset();
        return event;
    }
    /**
     *
     * @param eventList ***
     * @param timeShiftInSeconds Maximum difference in seconds between time from sending and time downloaded from event
     * @param returnTimeSecond Calculated return time of the expedition. (Fly time *2 + Expedition time)
     * @return Return null if expedition not found.
     */
    public Event getCorrectEventFromEventBox(ArrayList<Event> eventList, long timeShiftInSeconds, long returnTimeSecond){
        Event event = null;
        do{
            for(Event tmpEvent : eventList){
                long timeDifference = tmpEvent.getArrivalTime() - returnTimeSecond;
                //timeDifference between <-x;x>
                if(timeDifference <= timeShiftInSeconds && timeDifference >= timeShiftInSeconds*-1){
                    event = tmpEvent;
                    break;
                }
            }

            Waiter.sleep(200,300);
            if(getAntiLooping().check()){
                getAntiLooping().reset();
                return null;
            }

        }while(event == null); // Nie przypisano eventu
        getAntiLooping().reset();
        return event;
    }
    /**
     * Gets previous Event ID.
     * @param id Current event id
     * @return previous Event ID.
     */
    public String previousEventID(String id){
        String[] tab = id.split("-");
        StringBuilder stringBuilder = new StringBuilder();
        int previousId = Integer.parseInt(tab[1])-1;
        stringBuilder.append(tab[0]).append("-").append(previousId);
        return stringBuilder.toString();
    }
}
