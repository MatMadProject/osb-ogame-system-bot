package app.data.autobuilder;

import ogame.buildings.Building;
import ogame.planets.Planet;

import java.io.Serializable;
import java.util.Objects;

public class ItemAutoBuilder implements Serializable {
    private static final long serialVersionUID = 1992L;
    private final Planet planet;
    private final Building building;
    private final int upgradeLevel;
    private final long startTime;
    private long finishTime;
    private Status status;
    private long statusTime;

    public ItemAutoBuilder(Planet planet, Building building, int upgradeLevel, long startTime) {
        this.planet = planet;
        this.building = building;
        this.upgradeLevel = upgradeLevel;
        this.startTime = startTime;
        this.status = Status.ADDED;
    }

    public Planet getPlanet() {
        return planet;
    }

    public Building getBuilding() {
        return building;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(long finishTime) {
        this.finishTime = finishTime;
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

    public int getUpgradeLevel() {
        return upgradeLevel;
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
}
