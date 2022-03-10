package app.data.expedition;

import app.data.DataLoader;
import app.leaftask.Status;
import ogame.eventbox.Event;
import ogame.eventbox.FleetDetails;
import ogame.eventbox.FleetDetailsShip;
import ogame.planets.Coordinate;
import ogame.planets.Planet;
import ogame.planets.Resources;
import ogame.ships.Ship;
import ogame.utils.watch.Calendar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class Expedition implements Serializable {

    private static final long serialVersionUID = 1992L;
    private final Object planetListObject;
    private final Coordinate destinationCoordinate;
    private ArrayList<Ship> declaredShips;
    private ArrayList<FleetDetailsShip> returningFleet = new ArrayList<>();
    private String id;
    private long shipsBefore;
    private long shipsAfter;
    private long storage;
    private Resources lootedResources = new Resources(0L,0L,0L,0);
    private Status status;
    private long flyDuration;
    private Event flyToExpeditionEvent;
    private Event flyOnExpeditionEvent;
    private Event flyFromExpeditionEvent;
    private long endTimeInSeconds;
    private long statusTimeInMilliseconds;

    @Deprecated
    public Expedition(Planet startObject, Coordinate targetCoordinate, ArrayList<Ship> declaredShips) {
        this.planetListObject = startObject;
        this.destinationCoordinate = targetCoordinate;
        this.declaredShips = declaredShips;
        status = Status.SENDING;
        statusTimeInMilliseconds = System.currentTimeMillis();
        id = String.valueOf(DataLoader.expeditions.getId());
        setShipsBefore();
    }

    public Expedition(Object startObject, Coordinate targetCoordinate, ArrayList<Ship> declaredShips) {
        this.planetListObject = startObject;
        this.destinationCoordinate = targetCoordinate;
        this.declaredShips = declaredShips;
        status = Status.SENDING;
        statusTimeInMilliseconds = System.currentTimeMillis();
        id = String.valueOf(DataLoader.expeditions.getId());
        setShipsBefore();
    }

    public Object getPlanetListObject() {
        return planetListObject;
    }

    public Coordinate getDestinationCoordinate() {
        return destinationCoordinate;
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

    public long getShipsBefore() {
        return shipsBefore;
    }

    public void setShipsBefore() {
        for(Ship ship : declaredShips)
            shipsBefore += ship.getValue();
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

    public ArrayList<FleetDetailsShip> getReturningFleet() {
        return returningFleet;
    }

    public void setReturningFleet(ArrayList<FleetDetailsShip> ships) {
        this.returningFleet = ships;
    }

    public void setReturningFleet(Event event) {
        FleetDetails fleetDetails = new FleetDetails(event.getFleetDetails());
        ArrayList<FleetDetailsShip> ships = fleetDetails.ships();
        returningFleet.addAll(ships);

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

    public void resetFields(){
        setStorage(0);
        setShipsBefore(0);
        setShipsAfter(0);
        setLootedResources(new Resources(0,0,0,0));
        setFlyDuration(0);
        setEndTimeInSeconds(0);
        returningFleet.clear();
    }

    public Expedition copy(){
        Expedition expedition = new Expedition(this.planetListObject,this.destinationCoordinate,this.declaredShips);
        expedition.setLootedResources(this.lootedResources);
        expedition.setStorage(this.storage);
        expedition.setShipsBefore(this.shipsBefore);
        expedition.setShipsAfter(this.shipsAfter);
        expedition.setStatus(this.status);
        expedition.setStatusTimeInMilliseconds(this.statusTimeInMilliseconds);
        expedition.setFlyFromExpeditionEvent(this.flyFromExpeditionEvent);
        return expedition;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public long getEndTimeInSeconds() {
        return endTimeInSeconds;
    }

    public void setEndTimeInSeconds(long endTimeInSeconds) {
        this.endTimeInSeconds = endTimeInSeconds;
    }

    public long getStatusTimeInMilliseconds() {
        return statusTimeInMilliseconds;
    }

    public void setStatusTimeInMilliseconds(long statusTimeInMilliseconds) {
        this.statusTimeInMilliseconds = statusTimeInMilliseconds;
    }
    public void setStatusTimeInMilliseconds() {
        this.statusTimeInMilliseconds = System.currentTimeMillis();
    }

    public ArrayList<Ship> getDeclaredShips() {
        return declaredShips;
    }

    public void setDeclaredShips(ArrayList<Ship> declaredShips) {
        this.declaredShips = declaredShips;
    }

    public String log(){
        final String SEPARATOR =";";

        return id + SEPARATOR + Calendar.getDateTime(System.currentTimeMillis()) + SEPARATOR + "\n" +
                (flyFromExpeditionEvent != null ?  flyFromExpeditionEvent.getArrivalTime() + SEPARATOR
                        + flyFromExpeditionEvent.getID() : "null") + SEPARATOR +
                lootedResources + SEPARATOR +
                shipsBefore + SEPARATOR +
                shipsAfter + SEPARATOR +
                (returningFleet.isEmpty() ? "null" : returningFleet.toString()) + SEPARATOR +
                flyToExpeditionEvent.getArrivalTime() + SEPARATOR +
                flyToExpeditionEvent.getID() + SEPARATOR +
                flyOnExpeditionEvent.getArrivalTime() + SEPARATOR +
                flyOnExpeditionEvent.getID() + SEPARATOR;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Expedition that = (Expedition) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public boolean isMaxExpeditionReached(int currentExpeditions, int maxExpeditions){
        return currentExpeditions == maxExpeditions;
    }
    public boolean isMaxFleetReached(int currentFleet, int maxFleet){
        return currentFleet == maxFleet;
    }

    public Ship getShipFromList(ArrayList<Ship> ships, Ship wantedShip){
        Ship ship = null;
        if(!ships.isEmpty())
           ship = ships.stream()
                    .filter(item -> item.equals(wantedShip))
                    .findFirst()
                    .orElse(null);

        return ship;
    }
}
