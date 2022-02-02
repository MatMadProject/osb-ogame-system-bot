package app.data.autobuilder;

import app.leaftask.Status;
import ogame.DataTechnology;
import ogame.Type;
import ogame.buildings.Building;
import ogame.planets.Planet;
import ogame.utils.watch.Timer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class ItemAutoBuilder implements Serializable {
    private static final long serialVersionUID = 1992L;
    private final Planet planet;
    private final Building building;
    private final int upgradeLevel;
    private final long addTimeToQueueInMilliseconds;
    private long endTimeInSeconds;
    private long finishTimeInMilliseconds;
    private Status status;
    private long statusTimeInMillisconds;
    private int energyConsumption;
    @Deprecated
    private Timer timer;

    public ItemAutoBuilder(Planet planet, Building building, int upgradeLevel, long addTimeToQueueInMilliseconds) {
        this.planet = planet;
        this.building = building;
        this.upgradeLevel = upgradeLevel;
        this.addTimeToQueueInMilliseconds = addTimeToQueueInMilliseconds;
        this.status = Status.ADDED;
    }
    @Deprecated
    public long timeToFinish(){
        if(this.status == Status.UPGRADING){
            if(this.getBuilding().getProductionTime() == null)
                return Timer.timeSeconds(endTimeInSeconds,System.currentTimeMillis()/1000);

            long finishTime = this.building.getProductionTime().timeInSeconds() * 1000 + statusTimeInMillisconds;
            return finishTime - System.currentTimeMillis();
        }
        return 0;
    }
    public Planet getPlanet() {
        return planet;
    }

    public Building getBuilding() {
        return building;
    }

    public long getAddTimeToQueueInMilliseconds() {
        return addTimeToQueueInMilliseconds;
    }

    public long getFinishTimeInMilliseconds() {
        return finishTimeInMilliseconds;
    }

    public void setFinishTimeInMilliseconds(long finishTimeInMilliseconds) {
        this.finishTimeInMilliseconds = finishTimeInMilliseconds;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public long getStatusTimeInMilliseconds() {
        return statusTimeInMillisconds;
    }

    public void setStatusTimeInMilliseconds(long statusTimeInMillisconds) {
        this.statusTimeInMillisconds = statusTimeInMillisconds;
    }
    public void setStatusTimeInMilliseconds() {
        this.statusTimeInMillisconds = System.currentTimeMillis();
    }
    public int getUpgradeLevel() {
        return upgradeLevel;
    }

    public int getEnergyConsumption() {
        return energyConsumption;
    }

    public void setEnergyConsumption(int energyConsumption) {
        this.energyConsumption = energyConsumption;
    }
    @Deprecated
    public Timer getTimer() {
        return timer;
    }
    @Deprecated
    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public long getEndTimeInSeconds() {
        return endTimeInSeconds;
    }

    public void setEndTimeInSeconds(long endTimeInSeconds) {
        this.endTimeInSeconds = endTimeInSeconds;
    }

    public boolean isProductionBuilding(){
        return building.getDataTechnology().getType() == Type.PRODUCTION;
    }
    public boolean isTechnologyBuilding(){
        return building.getDataTechnology().getType() == Type.TECHNOLOGIES;
    }
    public boolean isResearchLaboratory(){ return building.getDataTechnology() == DataTechnology.RESEARCH_LABORATORY;
    }
    public boolean isShipyard(){ return building.getDataTechnology() == DataTechnology.SHIPYARD;
    }
    public boolean isNaniteFactory(){ return building.getDataTechnology() == DataTechnology.NANITE_FACTORY;
    }
    public boolean isRoboticsFactory(){ return building.getDataTechnology() == DataTechnology.ROBOTICS_FACTORY;
    }
    public boolean isDataDownloaded(){
        return building.getProductionTime() != null && building.getRequiredResources() != null;
    }
    public boolean isBuildingOgameStatusOff(){
        return building.getStatus() == ogame.Status.OFF;
    }
    public boolean isBuildingOgameStatusOn(){
        return building.getStatus() == ogame.Status.OFF;
    }
    public boolean isBuildingOgameStatusActive(){
        return building.getStatus() == ogame.Status.ACTIVE;
    }
    public boolean isBuildingOgameStatusUndefined(){
        return building.getStatus() == ogame.Status.UNDEFINED;
    }
    public boolean isBuildingOgameStatusDisabled(){
        return building.getStatus() == ogame.Status.DISABLED;
    }
    public boolean isFirstOnQueue(ArrayList<ItemAutoBuilder> planetQueue){
        return  planetQueue.indexOf(this) == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemAutoBuilder that = (ItemAutoBuilder) o;
        return upgradeLevel == that.upgradeLevel && Objects.equals(planet, that.planet) && Objects.equals(building, that.building);
    }

    @Override
    public int hashCode() {
        return Objects.hash(planet, building, upgradeLevel);
    }

    @Deprecated
    public void updateFinishTime(long currentTime) {
        this.finishTimeInMilliseconds = currentTime + this.building.getProductionTime().timeInSeconds() * 1000;
    }

    public boolean isBuildingAchievedUpgradeLevel(int currentBuildingLevel){
        return currentBuildingLevel >= upgradeLevel;
    }

    public boolean isBuildingUpgrading(ogame.Status status){
        return status == ogame.Status.ACTIVE;
    }

    public boolean isBuildingUpgrading(){
        return this.status == Status.UPGRADING;
    }
}
