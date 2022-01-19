package app.data.expedition;

import app.data.DataLoader;
import ogame.eventbox.Event;
import ogame.eventbox.FleetDetails;
import ogame.eventbox.FleetDetailsShip;
import ogame.planets.Coordinate;
import ogame.planets.Planet;
import ogame.planets.Resources;
import ogame.utils.watch.Calendar;
import ogame.utils.watch.Timer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Expedition implements Serializable {

    private static final long serialVersionUID = 1992L;
    private final Planet planet;
    private final Coordinate destinationCoordinate;
    private final ArrayList<ItemShipList> itemShipLists;
    private HashMap<String,Integer> returningFleet = new HashMap<>();
    private String id;
    private long shipsBefore;
    private long shipsAfter;
    private long storage;
    private Resources lootedResources;
    private Status status;
    private long statusTime;
    private long flyDuration;
    private Event flyToExpeditionEvent;
    private Event flyOnExpeditionEvent;
    private Event flyFromExpeditionEvent;
    private Timer timer;

    public Expedition(Planet startObject, Coordinate targetCoordinate, ArrayList<ItemShipList> itemShipLists) {
        this.planet = startObject;
        this.destinationCoordinate = targetCoordinate;
        this.itemShipLists = itemShipLists;
        lootedResources = new Resources(0,0,0,0);
        status = Status.SENDING;
        statusTime = System.currentTimeMillis();
        id = String.valueOf(DataLoader.expeditions.getId());
        setShipsBefore();
    }

    public Planet getPlanet() {
        return planet;
    }

    public Coordinate getDestinationCoordinate() {
        return destinationCoordinate;
    }

    public ArrayList<ItemShipList> getItemShipLists() {
        return itemShipLists;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getStorage() {
        return storage;
    }

    public void setStorage(long storage) {
        this.storage = storage;
    }

    public Resources getLootedResources() {
        return lootedResources;
    }

    public void setLootedResources(Resources lootedResources) {
        this.lootedResources = lootedResources;
    }

    public long getFlyDuration() {
        return flyDuration;
    }

    public void setFlyDuration(long flyDuration) {
        this.flyDuration = flyDuration;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public long getStatusTime() {
        return statusTime;
    }

    public void setStatusTime(long statusTime) {
        this.statusTime = statusTime;
    }

    public long getShipsBefore() {
        return shipsBefore;
    }

    public void setShipsBefore() {
        for(ItemShipList itemShipList : itemShipLists)
            shipsBefore += itemShipList.getValue();
    }

    public void setShipsBefore(long shipsBefore) {
        this.shipsBefore = shipsBefore;
    }

    public long getShipsAfter() {
        return shipsAfter;
    }

    public void setShipsAfter(long shipsAfter) {
        this.shipsAfter = shipsAfter;
    }

    public HashMap<String, Integer> getReturningFleet() {
        return returningFleet;
    }

    public void setReturningFleet(HashMap<String, Integer> returningFleet) {
        this.returningFleet = returningFleet;
    }

    public void setReturningFleet(Event event) {
        FleetDetails fleetDetails = new FleetDetails(event.getFleetDetails());
        ArrayList<FleetDetailsShip> ships = fleetDetails.ships();
        for(FleetDetailsShip ship : ships)
            returningFleet.put(ship.getName(),Integer.parseInt(ship.getValue()));

    }

    public Event getFlyToExpeditionEvent() {
        return flyToExpeditionEvent;
    }

    public void setFlyToExpeditionEvent(Event flyToExpeditionEvent) {
        this.flyToExpeditionEvent = flyToExpeditionEvent;
    }

    public Event getFlyOnExpeditionEvent() {
        return flyOnExpeditionEvent;
    }

    public void setFlyOnExpeditionEvent(Event flyOnExpeditionEvent) {
        this.flyOnExpeditionEvent = flyOnExpeditionEvent;
    }

    public Event getFlyFromExpeditionEvent() {
        return flyFromExpeditionEvent;
    }

    public void setFlyFromExpeditionEvent(Event flyFromExpeditionEvent) {
        this.flyFromExpeditionEvent = flyFromExpeditionEvent;
    }

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public void resetFields(){
        setStorage(0);
        setShipsBefore(0);
        setShipsAfter(0);
        setLootedResources(new Resources(0,0,0,0));
        setFlyDuration(0);
        setTimer(null);
    }

    public String log(){
        final String SEPARATOR =";";

        return id + SEPARATOR + Calendar.getDateTime(System.currentTimeMillis()) + SEPARATOR + "\n" +
                (flyFromExpeditionEvent != null ?  flyFromExpeditionEvent.getArrivalTime() + SEPARATOR
                        + flyFromExpeditionEvent.getID() : "null") + SEPARATOR +
                lootedResources + SEPARATOR +
                (returningFleet.isEmpty() ? "null" : returningFleet.toString()) + SEPARATOR +
                flyToExpeditionEvent.getArrivalTime() + SEPARATOR +
                flyToExpeditionEvent.getID() + SEPARATOR +
                flyOnExpeditionEvent.getArrivalTime() + SEPARATOR +
                flyOnExpeditionEvent.getID() + SEPARATOR;
    }

    public String logOld(){
        final String SEPARATOR =";";

        return id + SEPARATOR + Calendar.getDateTime(System.currentTimeMillis()) + SEPARATOR + "\n" +
                (flyFromExpeditionEvent != null ? flyFromExpeditionEvent.toString() : "null") + SEPARATOR +
                lootedResources + SEPARATOR +
                (itemShipLists.isEmpty() ? "null" : itemShipLists.toString()) + SEPARATOR +
                (returningFleet.isEmpty() ? "null" : returningFleet.toString()) + SEPARATOR +
                flyToExpeditionEvent.getArrivalTime() + SEPARATOR +
                flyToExpeditionEvent.getID() + SEPARATOR +
                flyOnExpeditionEvent.getArrivalTime() + SEPARATOR +
                flyOnExpeditionEvent.getID() + SEPARATOR;
    }
}
