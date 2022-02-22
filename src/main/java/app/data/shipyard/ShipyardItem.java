package app.data.shipyard;

import app.leaftask.Status;
import ogame.planets.Planet;
import ogame.utils.watch.Timer;

import java.io.Serializable;
import java.util.Objects;

public class ShipyardItem implements Serializable {
    private static final long serialVersionUID = 1992L;
    private final Planet planet;
    private int value;
    private String id;
    private Status status;
    private long statusTimeInMilliseconds;
    private long timePeriodInSeconds;
    private long endTimeInSeconds;
    private boolean singleExecute;
    private Timer timer;

    public ShipyardItem(Planet planet, int value, long timePeriod) {
        this.planet = planet;
        this.value = value;
        this.timePeriodInSeconds = timePeriod;
        status = Status.ADDED;
    }

    public Planet getPlanet() {
        return planet;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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

    public void setTimePeriodInSeconds(long timePeriodInSeconds) {
        this.timePeriodInSeconds = timePeriodInSeconds;
    }

    public boolean isSingleExecute() {
        return singleExecute;
    }

    public void setSingleExecute(boolean singleExecute) {
        this.singleExecute = singleExecute;
    }

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public long getEndTimeInSeconds() {
        return endTimeInSeconds;
    }

    public void setEndTimeInSeconds(long endTimeInSeconds) {
        this.endTimeInSeconds = endTimeInSeconds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShipyardItem that = (ShipyardItem) o;
        return Objects.equals(planet, that.planet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(planet);
    }

    @Override
    public String toString() {
        return "ShipyardItem{" +
                "planet=" + planet +
                ", value=" + value +
                ", id='" + id + '\'' +
                ", status=" + status +
                ", statusTime=" + statusTimeInMilliseconds +
                ", timePeriod=" + timePeriodInSeconds +
                ", singleExecute=" + singleExecute +
                ", timer=" + timer +
                '}';
    }
}
