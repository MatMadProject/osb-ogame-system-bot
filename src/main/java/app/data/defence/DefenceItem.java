package app.data.defence;

import app.data.DataLoader;
import ogame.defence.Defence;
import ogame.planets.Planet;
import ogame.utils.watch.Timer;

import java.io.Serializable;
import java.util.Objects;

public class DefenceItem implements Serializable {
    private static final long serialVersionUID = 1992L;
    private final Planet planet;
    private Defence defence;
    private int value;
    private String id;
    private Status status;
    private long statusTime;
    private long timePeriod;
    private Timer timer;

    public DefenceItem(Planet planet, Defence defence, int value, long timePeriod) {
        this.planet = planet;
        this.defence = defence;
        this.value = value;
        this.timePeriod = timePeriod;
        status = Status.ADDED;
        id = DataLoader.listDefenceItem.getId()+"";
    }

    public Planet getPlanet() {
        return planet;
    }

    public Defence getDefence() {
        return defence;
    }

    public void setDefence(Defence defence) {
        this.defence = defence;
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

    public Timer getTimer() {
        return timer;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public DefenceItem copy(){
        DefenceItem defenceItem = new DefenceItem(planet,defence,value,timePeriod);
        defenceItem.setId(id);
        defenceItem.setStatus(status);
        defenceItem.setStatusTime(statusTime);
        return defenceItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefenceItem that = (DefenceItem) o;
        return Objects.equals(planet, that.planet) && Objects.equals(defence, that.defence);
    }

    @Override
    public int hashCode() {
        return Objects.hash(planet, defence);
    }

    @Override
    public String toString() {
        return "DefenceItem{" +
                "planet=" + planet +
                ", defence=" + defence +
                ", value=" + value +
                ", id='" + id + '\'' +
                ", status=" + status +
                ", statusTime=" + statusTime +
                ", timePeriod=" + timePeriod +
                ", timer=" + timer +
                '}';
    }
}
