package app.leaftask;

import app.data.DataLoader;
import app.data.transport.TransportItem;
import ogame.DataTechnology;
import ogame.OgameWeb;
import ogame.eventbox.Event;
import ogame.eventbox.FleetDetails;
import ogame.planets.Planet;
import ogame.planets.Resources;
import ogame.ships.Mission;
import ogame.ships.Ship;
import ogame.tabs.EventBoxContent;
import ogame.tabs.FleetDispatch;
import ogame.utils.Waiter;
import ogame.utils.watch.Timer;

import java.util.ArrayList;
import java.util.List;

public class TransportLeafTask extends LeafTask{

    public TransportLeafTask(int index, long sleepms, String name) {
        super(index, sleepms, name);
    }
    private TransportItem transportItemToRemove;

    @Override
    public void execute() {
        if(isRun()) {
            if (isSleepTimeOut(System.currentTimeMillis())) {
                List<TransportItem> transports = DataLoader.listTransportItem.getQueueList();
                if(!transports.isEmpty())
                    for(TransportItem transportItem : transports){
                        selectTask(transportItem);
                        DataLoader.listTransportItem.save();
                    }
                if(transportItemToRemove != null){
                    DataLoader.listTransportItem.getQueueList().remove(transportItemToRemove);
                    transportItemToRemove = null;
                }
                setLastTimeExecute(System.currentTimeMillis());
            }
        }
    }

    private void selectTask(TransportItem transportItem) {
        Status status = transportItem.getStatus();
        switch(status){
            case ADDED:
            case SENDING:
                sending(transportItem);
                break;
            case DATA_DOWNLOADING:
                dataDownloading(transportItem);
                break;
            case SENT:
                sent(transportItem);
                break;
            case RETURN:
                returns(transportItem);
                break;
            case FINISHED:
                finished(transportItem);
                break;
            case NO_FUEL:
            case NO_FLEET:
            case NEXT:
            case MAX_FLEET_SLOT:
            case NOT_ENOUGH_RESOURCES:
            case DATA_ERROR:
                wait(transportItem);
                break;
        }
    }

    private void wait(TransportItem transportItem) {
        long timeToFinish = Timer.timeSeconds(transportItem.getEndTimeInSeconds(),System.currentTimeMillis()/1000);
        if(timeToFinish < 0){
            transportItem.setStatus(Status.SENDING);
            transportItem.setStatusTimeInMilliseconds();
            transportItem.resetData();
        }
    }

    private void finished(TransportItem transportItem) {
        if(transportItemToRemove == null){
            DataLoader.listTransportItem.addToHistory(transportItem.copy());
            if(transportItem.isRequiredNextFlight()){
                transportItem.setStatus(Status.SENDING);
                transportItem.setStatusTimeInMilliseconds();
                transportItem.setEndTimeInSeconds(0);
                transportItem.resetData();
            }else{
                if(transportItem.isSingleExecute()){
                    transportItemToRemove = transportItem;
                }else{
                    transportItem.setStatus(Status.NEXT);
                    transportItem.setStatusTimeInMilliseconds();
                    transportItem.setEndTimeInSeconds(System.currentTimeMillis()/1000 + transportItem.getTimePeriodInSeconds());
                    transportItem.resetData();
                }
            }
        }
    }

    private void returns(TransportItem transportItem) {
        long timeToFinish = Timer.timeSeconds(transportItem.getEndTimeInSeconds(),System.currentTimeMillis()/1000);
        if(timeToFinish < 0){
            transportItem.setStatus(Status.FINISHED);
            transportItem.setStatusTimeInMilliseconds();
            transportItem.setEndTimeInSeconds(transportItem.getReturnFlight().getArrivalTime());
        }
    }

    private void sent(TransportItem transportItem) {
        long timeToFinish = Timer.timeSeconds(transportItem.getEndTimeInSeconds(),System.currentTimeMillis()/1000);
        if(timeToFinish < 0){
            transportItem.setStatus(Status.RETURN);
            transportItem.setStatusTimeInMilliseconds();
            transportItem.setEndTimeInSeconds(transportItem.getReturnFlight().getArrivalTime());
        }
    }

    private void dataDownloading(TransportItem transportItem) {
        final long TIME_SHIFT_SECONDS = 4L;
        long itemStatusTime = transportItem.getStatusTimeInMilliseconds()/1000;
        long flightDuration = transportItem.getFlyDurationInSeconds() * 2;
        long estimatedReturnTime = itemStatusTime + flightDuration;
        
        Event returnEvent;
        if(!openEventBox())
            return;
        Waiter.sleep(200,200);
        ArrayList<Event> eventList = EventBoxContent.events(OgameWeb.webDriver, Mission.TRANSPORT);
        //Downloads correct event of expedition
        returnEvent = getCorrectEventFromEventBox(eventList,transportItem.getPlanetEnd().getCoordinate(),TIME_SHIFT_SECONDS,estimatedReturnTime);
        if(returnEvent == null){
            getAntiLooping().reset();
            transportItem.setStatus(Status.SENDING);
            transportItem.setStatusTimeInMilliseconds();
            return;
        }
        transportItem.setReturnFlight(returnEvent);
        Event flightToDestinationEvent = EventBoxContent.eventFromId(OgameWeb.webDriver,previousEventID(returnEvent.getID()));
        if(flightToDestinationEvent == null){
            transportItem.setStatus(Status.DATA_ERROR);
            transportItem.setStatusTimeInMilliseconds();
            transportItem.setEndTimeInSeconds(System.currentTimeMillis()/1000 + estimatedReturnTime);
            return;
        }

        String fleetDetailsHTML = flightToDestinationEvent.getFleetDetails();
        FleetDetails fleetDetails = new FleetDetails(fleetDetailsHTML);
        Resources sentResources = fleetDetails.getResources();

        if(transportItem.isRequiredNextFlight()){

            Resources declaredResources = transportItem.getDeclaredResources();
            long metal = declaredResources.getMetal() - sentResources.getMetal();
            long crystal = declaredResources.getCrystal() - sentResources.getCrystal();
            long deuterium = declaredResources.getDeuterium() - sentResources.getDeuterium();

            transportItem.setDeclaredResources(new Resources(
                    metal > 0 ? metal:0,
                    crystal > 0 ? crystal:0,
                    deuterium > 0 ? deuterium:0,
                    0));
        }
        transportItem.setFlightToDestination(flightToDestinationEvent);
        transportItem.setSentResources(sentResources);
        transportItem.setStatus(Status.SENT);
        transportItem.setStatusTimeInMilliseconds();
        transportItem.setEndTimeInSeconds(flightToDestinationEvent.getArrivalTime());
    }

    private void sending(TransportItem transportItem) {
        Planet plantStart  = transportItem.getPlanetStart();
        Planet planetEnd = transportItem.getPlanetEnd();
        final long ONE_HOUR = 3600L;

        if(!clickPlanet(plantStart))
            return;
        if(!clickFleetDispatch())
            return;

        int maxFleetSlots = FleetDispatch.maxFleetSlots(OgameWeb.webDriver);
        int currentFleetSlots = FleetDispatch.currentFleetSlots(OgameWeb.webDriver);
        final long WAIT_SECONDS = 10 * 60L;

        if(maxFleetSlots == currentFleetSlots){
            transportItem.setStatus(Status.MAX_FLEET_SLOT);
            transportItem.setStatusTimeInMilliseconds();
            transportItem.setEndTimeInSeconds(System.currentTimeMillis()/1000 + WAIT_SECONDS);
            return;
        }
        if(!FleetDispatch.isAnyFleetOnPlanet(OgameWeb.webDriver)){
            transportItem.setStatus(Status.NO_FLEET);
            transportItem.setStatusTimeInMilliseconds();
            transportItem.setEndTimeInSeconds(System.currentTimeMillis()/1000 + WAIT_SECONDS);
            return;
        }
        //Resources on planet
        Resources resourcesOnPlanet = resourcesOnPlanet();
        long leftResourcesToTransport = 0;
        if(transportItem.isAutotransport()){
            setRequiredShipToTransportResources(transportItem,resourcesOnPlanet.sum());
            selectShips(transportItem.getFleet());
            leftResourcesToTransport = resourcesOnPlanet.sum() - transportItem.fleetCapacity();
        }else{
            if(isEnoughResourcesOnPlanet(transportItem.getDeclaredResources(),resourcesOnPlanet)){
                setRequiredShipToTransportResources(transportItem,transportItem.getDeclaredResources().sum());
                selectShips(transportItem.getFleet());
                leftResourcesToTransport = transportItem.getDeclaredResources().sum() - transportItem.fleetCapacity();
            }else{
                transportItem.setStatus(Status.NOT_ENOUGH_RESOURCES);
                transportItem.setStatusTimeInMilliseconds();
                transportItem.setEndTimeInSeconds(System.currentTimeMillis()/1000 + ONE_HOUR);
            }
        }

        if(leftResourcesToTransport > 0)
            transportItem.setRequiredNextFlight(true);

        FleetDispatch.clickContinueToFleet2(OgameWeb.webDriver);
        if(!inputCoordinate(planetEnd.getCoordinate()))
            return;

        if(!selectFleetMission(Mission.TRANSPORT))
            return;

        long deuteriumConsumption = FleetDispatch.deuteriumConsumption(OgameWeb.webDriver);
        if(deuteriumConsumption > resourcesOnPlanet.getDeuterium()){
            transportItem.setStatus(Status.NO_FUEL);
            transportItem.setStatusTimeInMilliseconds();
            transportItem.setEndTimeInSeconds(System.currentTimeMillis()/1000 + WAIT_SECONDS);
            return;
        }

        if(transportItem.isAutotransport())
            loadAllResourcesOnFleet();
        else
            loadDeclaredResourcesOnFleet(transportItem.getDeclaredResources());

        Waiter.sleep(200,250);

        long maxStorage = FleetDispatch.maxStorage(OgameWeb.webDriver);
        long freeStorage = FleetDispatch.freeStorage(OgameWeb.webDriver);
        long flyDuration = FleetDispatch.flyDurationSeconds(OgameWeb.webDriver);
        if(flyDuration == 0 || maxStorage == 0)
            return;

        transportItem.setFlyDurationInSeconds(flyDuration);
        transportItem.setMaxStorage(maxStorage);
        transportItem.setFreeStorage(freeStorage);

        transportItem.setStatus(Status.DATA_DOWNLOADING);
        transportItem.setStatusTimeInMilliseconds();

        FleetDispatch.sendFleet(OgameWeb.webDriver);
    }

    private void setRequiredShipToTransportResources(TransportItem transportItem, long resourcesToTransport){
        long leftResourcesToTransport = resourcesToTransport;
        Ship largeTransporter = new Ship(DataTechnology.TRANSPORTER_LARGE);
        Ship smallTransporter = new Ship(DataTechnology.TRANSPORTER_SMALL);
        Ship explorer = new Ship(DataTechnology.EXPLORER);
        //Ships on planet
        int largeTransportersOnPlanet = FleetDispatch.getValueShips(OgameWeb.webDriver,largeTransporter.getDataTechnology());
        int smallTransportersOnPlanet = FleetDispatch.getValueShips(OgameWeb.webDriver,smallTransporter.getDataTechnology());
        int explorersOnPlanet = FleetDispatch.getValueShips(OgameWeb.webDriver,explorer.getDataTechnology());
        //Calculating ship for transport all resources
        int requiredLargeTransporters = transportItem.requiredShipsForTransport(largeTransporter,leftResourcesToTransport);
        if(transportItem.isEnoughShipsOnPlanet(largeTransportersOnPlanet,requiredLargeTransporters))
            transportItem.addShipToFleet(largeTransporter,requiredLargeTransporters);
        else
            transportItem.addShipToFleet(largeTransporter,largeTransportersOnPlanet);

        leftResourcesToTransport -= transportItem.capacityOfShips(largeTransporter,largeTransportersOnPlanet);

        int requiredSmallTransporters = transportItem.requiredShipsForTransport(smallTransporter,leftResourcesToTransport);
        if(transportItem.isEnoughShipsOnPlanet(smallTransportersOnPlanet,requiredSmallTransporters))
            transportItem.addShipToFleet(smallTransporter,requiredSmallTransporters);
        else
            transportItem.addShipToFleet(smallTransporter,smallTransportersOnPlanet);

        leftResourcesToTransport -= transportItem.capacityOfShips(smallTransporter,smallTransportersOnPlanet);

        int requiredExplorees = transportItem.requiredShipsForTransport(explorer,leftResourcesToTransport);
        if(transportItem.isEnoughShipsOnPlanet(explorersOnPlanet,requiredExplorees))
            transportItem.addShipToFleet(explorer,requiredExplorees);
        else
            transportItem.addShipToFleet(explorer,explorersOnPlanet);
    }
    private void loadDeclaredResourcesOnFleet(Resources declaredResources){

        if(declaredResources.getMetal() > 0)
            FleetDispatch.setMetalValue(OgameWeb.webDriver,declaredResources.getMetal());

        if(declaredResources.getCrystal() > 0)
            FleetDispatch.setCrystalValue(OgameWeb.webDriver,declaredResources.getCrystal());

        if(declaredResources.getDeuterium() > 0)
            FleetDispatch.setDeueriumValue(OgameWeb.webDriver,declaredResources.getDeuterium());
    }
    private void loadAllResourcesOnFleet(){
        FleetDispatch.maxMetal(OgameWeb.webDriver);
        FleetDispatch.maxCrystal(OgameWeb.webDriver);
        FleetDispatch.maxDeuterium(OgameWeb.webDriver);
    }
    private void selectShips(List<Ship> ships){
        if(!ships.isEmpty())
            for(Ship ship : ships)
                FleetDispatch.setValueShips(OgameWeb.webDriver,ship.getDataTechnology(),ship.getValue());
    }

    private boolean isEnoughResourcesOnPlanet(Resources declaredResources, Resources resourcesOnPlanet){
        return resourcesOnPlanet.getMetal() >= declaredResources.getMetal()
                && resourcesOnPlanet.getCrystal() >= declaredResources.getCrystal()
                && resourcesOnPlanet.getDeuterium() >= declaredResources.getDeuterium();
    }
}
