package app.data.transport;

import app.data.DataLoader;
import app.leaftask.Status;
import com.sun.org.apache.bcel.internal.generic.INEG;
import ogame.eventbox.Event;
import ogame.planets.Planet;
import ogame.planets.Resources;
import ogame.ships.Ship;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TransportItem implements Serializable {
    private static final long serialVersionUID = 1992L;
    private final Planet planetStart;
    private final Planet planetEnd;
    private Resources declaredResources = new Resources(0L,0L,0L,0);
    private Resources sentResources = new Resources(0L,0L,0L,0);
    private String id;
    private Status status;
    private Event flightToDestination;
    private Event returnFlight;
    private List<Ship> fleet = new ArrayList<>();
    private long endTimeInSeconds;
    private long statusTimeInMilliseconds;
    private final long timePeriodInSeconds;
    private long maxStorage;
    private long freeStorage;
    private long flyDurationInSeconds;
    private boolean singleExecute;
    private boolean autotransport;
    private boolean selectedSmallTransporter;
    private boolean selectedLargeTransporter;
    private boolean selectedExplorer;
    private boolean requiredNextFlight;

    public TransportItem(Planet planetStart, Planet planetEnd, long timePeriodInSeconds) {
        this.planetStart = planetStart;
        this.planetEnd = planetEnd;
        this.timePeriodInSeconds = timePeriodInSeconds;
        status = Status.ADDED;
        setStatusTimeInMilliseconds();
        id = DataLoader.listTransportItem.getId()+"";
    }

    public void resetData(){
        sentResources = new Resources(0L,0L,0L,0);
        flightToDestination = null;
        returnFlight = null;
        fleet.clear();
        maxStorage = 0;
        freeStorage = 0;
        flyDurationInSeconds = 0;
        requiredNextFlight = false;
    }
    public Planet getPlanetStart() {
        return planetStart;
    }

    public Planet getPlanetEnd() {
        return planetEnd;
    }

    public Resources getDeclaredResources() {
        return declaredResources;
    }

    public void setDeclaredResources(Resources declaredResources) {
        this.declaredResources = declaredResources;
    }

    public String resources(){
        return this.sentResources.getMetal() + "/" +this.sentResources.getCrystal() + "/" + this.sentResources.getDeuterium();
    }

    public Resources getSentResources() {
        return sentResources;
    }

    public void setSentResources(Resources sentResources) {
        this.sentResources = sentResources;
    }

    public String getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id+"";
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Event getFlightToDestination() {
        return flightToDestination;
    }

    public void setFlightToDestination(Event flightToDestination) {
        this.flightToDestination = flightToDestination;
    }

    public Event getReturnFlight() {
        return returnFlight;
    }

    public void setReturnFlight(Event returnFlight) {
        this.returnFlight = returnFlight;
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

    public long getTimePeriodInSeconds() {
        return timePeriodInSeconds;
    }

    public long getMaxStorage() {
        return maxStorage;
    }

    public void setMaxStorage(long maxStorage) {
        this.maxStorage = maxStorage;
    }

    public long getFreeStorage() {
        return freeStorage;
    }

    public void setFreeStorage(long freeStorage) {
        this.freeStorage = freeStorage;
    }

    public long getFlyDurationInSeconds() {
        return flyDurationInSeconds;
    }

    public void setFlyDurationInSeconds(long flyDurationInSeconds) {
        this.flyDurationInSeconds = flyDurationInSeconds;
    }

    public boolean isSingleExecute() {
        return singleExecute;
    }

    public void setSingleExecute(boolean singleExecute) {
        this.singleExecute = singleExecute;
    }

    public boolean isAutotransport() {
        return autotransport;
    }

    public void setAutotransport(boolean autotransport) {
        this.autotransport = autotransport;
    }

    public boolean isSelectedSmallTransporter() {
        return selectedSmallTransporter;
    }

    public void setSelectedSmallTransporter(boolean selectedSmallTransporter) {
        this.selectedSmallTransporter = selectedSmallTransporter;
    }


    public int requiredShipsForTransport(Ship ship, long resourcesToTransport){
        if(resourcesToTransport < 0 )
            return 0;
        return (int)(resourcesToTransport/ship.currentCapacity(DataLoader.researches.hyperspaceTechnologyLevel())) + 1;
    }
    public long capacityOfShips(Ship ship, int value){
        return ship.currentCapacity(DataLoader.researches.hyperspaceTechnologyLevel()) * value;
    }

    public boolean isSelectedLargeTransporter() {
        return selectedLargeTransporter;
    }

    public void setSelectedLargeTransporter(boolean selectedLargeTransporter) {
        this.selectedLargeTransporter = selectedLargeTransporter;
    }

    public boolean isSelectedExplorer() {
        return selectedExplorer;
    }

    public void setSelectedExplorer(boolean selectedExplorer) {
        this.selectedExplorer = selectedExplorer;
    }

    public List<Ship> getFleet() {
        return fleet;
    }

    public void setFleet(List<Ship> fleet) {
        this.fleet = fleet;
    }

    public void addShipToFleet (Ship ship, int value){
        if(value > 0 && !fleet.contains(ship)){
            ship.setValue(value);
            fleet.add(ship);
        }
    }
    public long fleetCapacity(){
        long capacity = 0;
        if(!fleet.isEmpty())
            for(Ship ship : fleet)
                capacity += ship.currentCapacity(DataLoader.researches.hyperspaceTechnologyLevel()) * ship.getValue();


        return capacity;
    }

    public boolean isEnoughShipsOnPlanet(int currentShip, int requiredShip){
        return currentShip >= requiredShip;
    }

    public boolean isRequiredNextFlight() {
        return requiredNextFlight;
    }

    public void setRequiredNextFlight(boolean requiredNextFlight) {
        this.requiredNextFlight = requiredNextFlight;
    }

    public TransportItem copy(){
        TransportItem transportItem = new TransportItem(this.planetStart, this.planetEnd, this.timePeriodInSeconds);
        transportItem.setId(Integer.parseInt(this.id));
        transportItem.setSentResources(this.getSentResources());
        transportItem.setStatus(this.status);
        transportItem.setStatusTimeInMilliseconds(this.statusTimeInMilliseconds);
        transportItem.setFleet(this.fleet);

        return transportItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransportItem)) return false;
        TransportItem that = (TransportItem) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
