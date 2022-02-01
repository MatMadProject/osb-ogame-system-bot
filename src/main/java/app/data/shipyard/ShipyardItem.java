package app.data.shipyard;

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
    private long statusTime;
    private long timePeriod;
    private boolean singleExecute;
    private Timer timer;

    public ShipyardItem(Planet planet, int value, long timePeriod) {
        this.planet = planet;
        this.value = value;
        this.timePeriod = timePeriod;
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

    public long getStatusTime() {
        return statusTime;
    }

    public void setStatusTime(long statusTime) {
        this.statusTime = statusTime;
    }

    public long getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(long timePeriod) {
        this.timePeriod = timePeriod;
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
                ", statusTime=" + statusTime +
                ", timePeriod=" + timePeriod +
                ", singleExecute=" + singleExecute +
                ", timer=" + timer +
                '}';
    }
}
