package app.data.autoresearch;

import ogame.planets.Planet;
import ogame.researches.Research;
import ogame.utils.watch.Timer;

import java.io.Serializable;
import java.util.Objects;

public class ItemAutoResearch implements Serializable {

    private static final long serialVersionUID = 1992L;
    private final Planet planet;
    private final Research research;
    private final int upgradeLevel;
    private final long startTime;
    private long finishTime;
    private long endTimeInSeconds;
    private Status status;
    private long statusTime;
    private Timer timer;

    public ItemAutoResearch(Planet planet, Research research, int upgradeLevel, long startTime) {
        this.planet = planet;
        this.research = research;
        this.upgradeLevel = upgradeLevel;
        this.startTime = startTime;
        this.status = Status.ADDED;
    }

    public long timeToFinish(){
        if(this.status == Status.UPGRADING){
            long finishTime = this.research.getProductionTime().timeInSeconds() * 1000 + statusTime;
            return finishTime - System.currentTimeMillis();
        }
        return 0;
    }
    public Planet getPlanet() {
        return planet;
    }

    public Research getResearch() {
        return research;
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
    public void setStatusTime() {
        this.statusTime = System.currentTimeMillis();
    }

    public int getUpgradeLevel() {
        return upgradeLevel;
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
        ItemAutoResearch that = (ItemAutoResearch) o;
        return upgradeLevel == that.upgradeLevel && Objects.equals(research, that.research);
    }

    @Override
    public int hashCode() {
        return Objects.hash(research, upgradeLevel);
    }

    public void updateFinishTime(long currentTime) {
        this.finishTime = currentTime + this.research.getProductionTime().timeInSeconds() * 1000;
    }
}
