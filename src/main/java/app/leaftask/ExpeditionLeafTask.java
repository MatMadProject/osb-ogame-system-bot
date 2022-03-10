package app.leaftask;

import app.data.DataLoader;
import app.data.expedition.Expedition;
import app.data.expedition.Expeditions;
import ogame.OgameWeb;
import ogame.ResourcesBar;
import ogame.eventbox.Event;
import ogame.eventbox.FleetDetails;
import ogame.eventbox.FleetDetailsShip;
import ogame.planets.Coordinate;
import ogame.planets.Resources;
import ogame.ships.Mission;
import ogame.ships.Ship;
import ogame.tabs.EventBoxContent;
import ogame.tabs.FleetDispatch;
import ogame.utils.Waiter;
import ogame.utils.log.AppLog;
import ogame.utils.watch.Timer;

import java.util.ArrayList;
import java.util.List;

public class ExpeditionLeafTask extends LeafTask{

    public ExpeditionLeafTask(int index, long sleepMs, String name) {
        super(index, sleepMs, name);
    }

    @Override
    public void execute() {
        if(isRun()){
            if(isSleepTimeOut(System.currentTimeMillis())){
                ArrayList<Expedition> expeditions = DataLoader.expeditions.getExpeditionList();
                if(!expeditions.isEmpty())
                    for(Expedition expedition : expeditions){
                        selectTask(expedition);
                        DataLoader.expeditions.save();
                    }
                setLastTimeExecute(System.currentTimeMillis());
            }
        }
    }

    private void selectTask(Expedition expedition){
        Status status = expedition.getStatus();
        switch(status){
            case SENDING:
                sending(expedition);
                break;
            case DATA_DOWNLOADING:
                dataDownloading(expedition);
                break;
            case SENT:
                sent(expedition);
                break;
            case EXPEDITION:
                expedition(expedition);
                break;
            case RETURN:
            case RETURN_CHANGED:
                returns(expedition);
                break;
            case FINISHED:
                finished(expedition);
                break;
            case NO_FUEL:
            case NO_FLEET:
            case MAX_EXPEDITION:
            case MAX_FLEET_SLOT:
            case DATA_ERROR:
                wait(expedition);
                break;
        }
    }

    private void wait(Expedition expedition) {
        long timeToFinish = Timer.timeSeconds(expedition.getEndTimeInSeconds(),System.currentTimeMillis()/1000);
        if(timeToFinish < 0){
            expedition.setStatus(Status.SENDING);
            expedition.setStatusTimeInMilliseconds();
        }
    }

    private void finished(Expedition expedition) {
        DataLoader.expeditions.addToHistory(expedition.copy());
        Expeditions.saveLog(expedition.log());
        expedition.resetFields();
        expedition.setStatus(Status.SENDING);
        expedition.setStatusTimeInMilliseconds();
    }

    private void returns(Expedition expedition) {
        long timeToFinish = Timer.timeSeconds(expedition.getEndTimeInSeconds(),System.currentTimeMillis()/1000);
        if(timeToFinish < 0){
            expedition.setStatus(Status.FINISHED);
            expedition.setStatusTimeInMilliseconds();
        }
    }

    private void expedition(Expedition expedition) {
        long timeToFinish = Timer.timeSeconds(expedition.getEndTimeInSeconds(),System.currentTimeMillis()/1000);
        if(timeToFinish < 0){
            refreshWebPage();
            String idReturnEvent = expedition.getFlyFromExpeditionEvent().getID();
            Event currentEvent = expedition.getFlyFromExpeditionEvent();
            //The bot starts up after a few hours of inactivity. The expedition event may have ended.
            List<Event> expeditionEvents = EventBoxContent.events(OgameWeb.webDriver,Mission.EXPEDITION);
            if(expeditionEvents.isEmpty()){
                expedition.setStatus(Status.FINISHED);
                expedition.setStatusTimeInMilliseconds();
                return;
            }
            if(!openEventBox())
                return;
            Event returnEvent = getEvent(idReturnEvent);
            //Fleet lost
            if(returnEvent == null) {
                expedition.setStatus(Status.FINISHED);
                expedition.setStatusTimeInMilliseconds();
                expedition.setFlyFromExpeditionEvent(returnEvent);
                AppLog.print(ExpeditionLeafTask.class.getName(),0,"Expedition " + expedition.getId() + " is lost.");
                return;
            }

            //Fleet not delayed
            if(returnEvent.getArrivalTime() == currentEvent.getArrivalTime())
                expedition.setStatus(Status.RETURN);
            else//Fleet delayed or accelerated
                expedition.setStatus(Status.RETURN_CHANGED);

            expedition.setStatusTimeInMilliseconds();

            FleetDetails fleetDetails = new FleetDetails(returnEvent.getFleetDetails());
            Resources resources = fleetDetails.getResources();
            ArrayList<FleetDetailsShip> returnedShips = fleetDetails.ships();
            long shipsAfter = sumOfShips(returnedShips);
            expedition.setLootedResources(resources);
            expedition.setShipsAfter(shipsAfter);
            expedition.setFlyFromExpeditionEvent(returnEvent);
            expedition.setReturningFleet(returnedShips);
            expedition.setEndTimeInSeconds(expedition.getFlyFromExpeditionEvent().getArrivalTime());
        }
    }

    private void sent(Expedition expedition) {
        long timeToFinish = Timer.timeSeconds(expedition.getEndTimeInSeconds(),System.currentTimeMillis()/1000);
        if(timeToFinish < 0){
            expedition.setStatus(Status.EXPEDITION);
            expedition.setStatusTimeInMilliseconds();
            expedition.setEndTimeInSeconds(expedition.getFlyOnExpeditionEvent().getArrivalTime());
        }
    }

    private void dataDownloading(Expedition expedition) {
        final long TIME_SHIFT_SECONDS = 3L;
        final long EXPEDITION_TIME_IN_SECONDS = 3600L;
        long expeditionStatusTime = expedition.getStatusTimeInMilliseconds()/1000;
        long expeditionFleetFlyDuration = expedition.getFlyDuration()*2;
        long returnTime = expeditionStatusTime + expeditionFleetFlyDuration + EXPEDITION_TIME_IN_SECONDS;
        Event returnEvent;
        if(!openEventBox())
            return;
        ArrayList<Event> eventList = EventBoxContent.events(OgameWeb.webDriver, Mission.EXPEDITION);
        //Downloads correct event of expedition
        returnEvent = getCorrectEvent(eventList,expedition.getDestinationCoordinate(),TIME_SHIFT_SECONDS,returnTime);
        //Event doesn't exist.
        if(returnEvent == null){
            getAntiLooping().reset();
            expedition.setStatus(Status.SENDING);
            expedition.setStatusTimeInMilliseconds();
            expedition.setEndTimeInSeconds(0);
            return;
        }
        expedition.setFlyFromExpeditionEvent(returnEvent);
        //Downloads data of other events of expedition
        Event expeditionEvent = EventBoxContent.eventFromId(OgameWeb.webDriver, previousEventID(returnEvent.getID()));
        //Download incorrect data or another event ID shift occurred
        if(expeditionEvent == null){
            expedition.setStatus(Status.DATA_ERROR);
            expedition.setStatusTimeInMilliseconds();
            expedition.setEndTimeInSeconds(returnEvent.getArrivalTime());
        }
        Event flyToExpeditionEvent = EventBoxContent.eventFromId(OgameWeb.webDriver, previousEventID(expeditionEvent.getID()));

        //Downloads incorrect event data
        if(flyToExpeditionEvent == null)
            return;

        expedition.setFlyOnExpeditionEvent(expeditionEvent);
        expedition.setFlyToExpeditionEvent(flyToExpeditionEvent);
        expedition.setStatus(Status.SENT);
        expedition.setStatusTimeInMilliseconds();
        expedition.setEndTimeInSeconds(flyToExpeditionEvent.getArrivalTime());
    }

    private void sending(Expedition expedition){
        Object planetListObject = expedition.getPlanetListObject();
        ArrayList<Ship> declaredShips = expedition.getDeclaredShips();
//        ArrayList<ItemShipList> itemShipLists = expedition.getItemShipLists();
        long ships = 0;

        if(!clickOnPlanetListObject(planetListObject))
            return;
        if(!clickFleetDispatch())
            return;

        //Expeditions limit data
        int maxExpeditions = FleetDispatch.maxExpeditionSlots(OgameWeb.webDriver);
        int currentExpeditions = FleetDispatch.currentExpeditionSlots(OgameWeb.webDriver);
        DataLoader.expeditions.setMaxExpeditions(maxExpeditions);
        long WAIT_TIME = 10 * 60L;
        if(expedition.isMaxExpeditionReached(currentExpeditions,maxExpeditions)){
            expedition.setStatus(Status.MAX_EXPEDITION);
            expedition.setStatusTimeInMilliseconds();
            expedition.setEndTimeInSeconds(System.currentTimeMillis()/1000 + WAIT_TIME);
            return;
        }

        if(!FleetDispatch.isAnyFleetOnPlanet(OgameWeb.webDriver)){
            expedition.setStatus(Status.NO_FLEET);
            expedition.setStatusTimeInMilliseconds();
            expedition.setEndTimeInSeconds(System.currentTimeMillis()/1000 + WAIT_TIME);
            return;
        }

        int maxFleetSlots = FleetDispatch.maxFleetSlots(OgameWeb.webDriver);
        int currentSlots = FleetDispatch.currentFleetSlots(OgameWeb.webDriver);
        if(expedition.isMaxFleetReached(currentSlots,maxFleetSlots)){
            expedition.setStatus(Status.MAX_FLEET_SLOT);
            expedition.setStatusTimeInMilliseconds();
            expedition.setEndTimeInSeconds(System.currentTimeMillis()/1000 + WAIT_TIME);
            return;
        }

        //Current ships on planet data
        ArrayList<Ship> currentShipOnPlanet = new ArrayList<>();
        for(Ship ship : declaredShips){
            Ship currentShip = new Ship(ship.getDataTechnology());
            int value = FleetDispatch.getValueShips(OgameWeb.webDriver,ship.getDataTechnology());
            currentShip.setValue(value);
            currentShipOnPlanet.add(currentShip);
        }
        //Selecting ships
        for(Ship ship : declaredShips){
            int value = ship.getValue();
            int currentValue = expedition.getShipFromList(currentShipOnPlanet,ship).getValue();

            //Sent all
            if(value == -1){
                FleetDispatch.setAllShips(OgameWeb.webDriver,ship.getDataTechnology());
                ships += currentValue;
                continue;
            }
            //There are more ships on the planet
            if(currentValue >= value){
                FleetDispatch.setValueShips(OgameWeb.webDriver,ship.getDataTechnology(),value);
                ships += value;
            }
            else{
                FleetDispatch.setValueShips(OgameWeb.webDriver,ship.getDataTechnology(),currentValue);
                ships += currentValue;
            }
        }
        //When on planet are less than 50% of declared ships.
        double shipsAvailable = (ships * 1.0f)/expedition.getShipsBefore();
        if(shipsAvailable <= 0.5){
            expedition.setStatus(Status.NO_FLEET);
            expedition.setStatusTimeInMilliseconds();
            expedition.setEndTimeInSeconds(System.currentTimeMillis()/1000 + WAIT_TIME);
            return;
        }
//        if(!FleetDispatch.isClickContinueToFleet2ON(OgameWeb.webDriver))
//            return;
        FleetDispatch.clickContinueToFleet2(OgameWeb.webDriver);
        //Input coordinate
        if(!inputCoordinateExpedition(expedition.getDestinationCoordinate()))
            return;

        long deuteriumConsumption = FleetDispatch.deuteriumConsumption(OgameWeb.webDriver);
        long deuteriumOnPlanet = ResourcesBar.deuterium(OgameWeb.webDriver);
        if(deuteriumConsumption > deuteriumOnPlanet){
            expedition.setStatus(Status.NO_FUEL);
            expedition.setStatusTimeInMilliseconds();
            expedition.setEndTimeInSeconds(System.currentTimeMillis()/1000 + WAIT_TIME);
            return;
        }
        Waiter.sleep(200,250);
        long flyDuration = FleetDispatch.flyDurationSeconds(OgameWeb.webDriver);
        long storage = FleetDispatch.maxStorage(OgameWeb.webDriver);

        if(flyDuration == 0 || storage == 0)
            return;

        expedition.setFlyDuration(flyDuration);
        expedition.setStorage(storage);
        expedition.setShipsBefore(ships);

        expedition.setStatusTimeInMilliseconds();
        if(!FleetDispatch.sendFleet(OgameWeb.webDriver))
            return;

        expedition.setStatus(Status.DATA_DOWNLOADING);
        Waiter.sleep(1000,2000);
    }

    private long sumOfShips(ArrayList<FleetDetailsShip> returnedShips){
        long sum = 0;
        for(FleetDetailsShip ship : returnedShips){
            sum += Integer.parseInt(ship.getValue());
        }
        return sum;
    }

    /**
     *
     * @param eventList ***
     * @param expeditionCoordinate Expedition sent at the moment.
     * @param timeShiftSeconds Maximum difference in seconds between time from sending and time downloaded from event
     * @param returnTimeSecond Calculated return time of the expedition. (Fly time *2 + Expedition time)
     * @return Return null if expedition not found.
     */
    public Event getCorrectEvent(ArrayList<Event> eventList, Coordinate expeditionCoordinate, long timeShiftSeconds, long returnTimeSecond){
        Event event = null;
        do{
            for(Event tmpEvent : eventList)
                if(tmpEvent.getDestinationCoordinate().equals(expeditionCoordinate)) {
                    long timeDifference = tmpEvent.getArrivalTime() - returnTimeSecond;
                    //timeDifference between <-x;x>
                    if(timeDifference <= timeShiftSeconds && timeDifference >= timeShiftSeconds*-1){
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
}
