package app.data.autoresearch;

import app.data.DataLoader;
import app.leaftask.Status;
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
    private final long addTimeToQueueInMilliseconds;
    private long finishTimeInMilliseconds;
    private long endTimeInSeconds;
    private Status status;
    private long statusTimeInMilliseconds;
    @Deprecated
    private Timer timer;

    public ItemAutoResearch(Planet planet, Research research, int upgradeLevel, long addTimeToQueueInMilliseconds) {
        this.planet = planet;
        this.research = research;
        this.upgradeLevel = upgradeLevel;
        this.addTimeToQueueInMilliseconds = addTimeToQueueInMilliseconds;
        this.status = Status.ADDED;
    }

    @Deprecated
    public long timeToFinish(){
        if(this.status == Status.UPGRADING){
            long finishTime = this.research.getProductionTime().timeInSeconds() * 1000 + statusTimeInMilliseconds;
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

    public long getAddTimeToQueueInMilliseconds() {
        return addTimeToQueueInMilliseconds;
    }

    public long getFinishTimeInMilliseconds() {
        return finishTimeInMilliseconds;
    }

    public void setFinishTimeInMilliseconds(long finishTimeInMilliseconds) {
        this.finishTimeInMilliseconds = finishTimeInMilliseconds;
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
    public void setStatusTime() {
        this.statusTimeInMilliseconds = System.currentTimeMillis();
    }

    public int getUpgradeLevel() {
        return upgradeLevel;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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

    @Deprecated
    public void updateFinishTime(long currentTime) {
        this.finishTimeInMilliseconds = currentTime + this.research.getProductionTime().timeInSeconds() * 1000;
    }

    public boolean isDataDownloaded(){
        return research.getProductionTime() != null && research.getRequiredResources() != null;
    }
    public boolean isResearchAchievedUpgradeLevel(int currentResearchLevel){
        return currentResearchLevel >= upgradeLevel;
    }
    public boolean isResearchOgameStatusOff(){
        return research.getStatus() == ogame.Status.OFF;
    }
    public boolean isResearchOgameStatusOn(){
        return research.getStatus() == ogame.Status.ON;
    }
    public boolean isResearchOgameStatusActive(){
        return research.getStatus() == ogame.Status.ACTIVE;
    }
    public boolean isResearchOgameStatusUndefined(){
        return research.getStatus() == ogame.Status.UNDEFINED;
    }
    public boolean isResearchOgameStatusDisabled(){
        return research.getStatus() == ogame.Status.DISABLED;
    }
    public boolean isFirstOnQueue(){
        return  DataLoader.listItemAutoResearch.getQueueList().indexOf(this) == 0;
    }
}
