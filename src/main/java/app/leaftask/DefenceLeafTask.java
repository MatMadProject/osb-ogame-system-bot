package app.leaftask;

import app.data.DataLoader;
import app.data.shipyard.DefenceItem;
import app.data.shipyard.ShipItem;
import ogame.DataTechnology;
import ogame.OgameWeb;
import ogame.ResourcesBar;
import ogame.planets.Planet;
import ogame.planets.ResourcesProduction;
import ogame.tabs.Defence;
import ogame.utils.Waiter;
import ogame.utils.log.AppLog;
import ogame.utils.watch.Timer;
import ogame.watch.ProductionTime;

import java.util.List;

public class DefenceLeafTask extends LeafTask{

    public DefenceLeafTask(int index, long sleepms, String name) {
        super(index, sleepms, name);
    }
    private DefenceItem defenceItemToRemove;
    @Override
    public void execute() {
            if(isRun()){
                if(isSleepTimeOut(System.currentTimeMillis())){
                    List<DefenceItem> defenceItems = DataLoader.listDefenceItem.getQueueList();
                    if(!defenceItems.isEmpty())
                        for(DefenceItem defenceItem : defenceItems){
                            selectTask(defenceItem);
                            DataLoader.listDefenceItem.save();
                        }
                    if(defenceItemToRemove != null){
                        DataLoader.listDefenceItem.getQueueList().remove(defenceItemToRemove);
                        defenceItemToRemove = null;
                    }
                    setLastTimeExecute(System.currentTimeMillis());
                }
            }
    }

    private void selectTask(DefenceItem defenceItem) {
        Status status = defenceItem.getStatus();
        switch (status){
            case ADDED:
                added(defenceItem);
                break;
            case DATA_DOWNLOADING:
                dataDownloading(defenceItem);
                break;
            case DATA_DOWNLOADED:
                dataDownloaded(defenceItem);
                break;
            case STARTING:
                starting(defenceItem);
                break;
            case CHECK:
                check(defenceItem);
                break;
            case FINISHED:
                finished(defenceItem);
                break;
            case BUILDING:
            case OFF:
            case WAIT:
            case WAIT_FOR_STATUS:
            case NEXT:
            case NOT_ENOUGH_RESOURCES:
                wait(defenceItem);
        }
    }

    private void finished(DefenceItem defenceItem) {
        if(defenceItemToRemove == null){
            DataLoader.listDefenceItem.addToHistory(defenceItem.copy());
            if(defenceItem.isSingleExecute())
                defenceItemToRemove = defenceItem;
            else{
                defenceItem.setStatus(Status.NEXT);
                defenceItem.setStatusTimeInMilliseconds();
                defenceItem.setEndTimeInSeconds(System.currentTimeMillis()/1000 + defenceItem.getTimePeriodInSeconds());
            }
        }
    }

    private void wait(DefenceItem defenceItem) {
        long timeToFinish = Timer.timeSeconds(defenceItem.getEndTimeInSeconds(),System.currentTimeMillis()/1000);
        if(timeToFinish < 0){
            if(defenceItem.getStatus() == Status.BUILDING)
                defenceItem.setStatus(Status.FINISHED);
            else if(defenceItem.getStatus() == Status.WAIT_FOR_STATUS)
                defenceItem.setStatus(Status.DATA_DOWNLOADED);
            else
                defenceItem.setStatus(Status.DATA_DOWNLOADING);

            defenceItem.setStatusTimeInMilliseconds();
        }
    }

    private void check(DefenceItem defenceItem) {
        Planet planet = defenceItem.getPlanet();
        DataTechnology dataTechnology = defenceItem.getDefence().getDataTechnology();
        if(!clickPlanet(planet))
            return;
        if(!clickDefence())
            return;
        ogame.Status status = Defence.statusOfDefence(OgameWeb.webDriver,dataTechnology);
        if(status == ogame.Status.ACTIVE){
            long endDateOfUpgrade = Defence.endDateOfUpgrade(OgameWeb.webDriver,dataTechnology);
            defenceItem.setStatus(Status.BUILDING);
            defenceItem.setStatusTimeInMilliseconds(System.currentTimeMillis());
            defenceItem.setTimer(new Timer(0,endDateOfUpgrade));
        }else{
            defenceItem.setStatus(Status.DATA_DOWNLOADING);
            defenceItem.setStatusTimeInMilliseconds(System.currentTimeMillis());
        }
    }

    private void starting(DefenceItem defenceItem) {
        Planet planet = defenceItem.getPlanet();
        DataTechnology dataTechnology = defenceItem.getDefence().getDataTechnology();
        //Clicking on planet
        if(!clickPlanet(planet))
            return;
        if(!clickDefence())
            return;
        if(!clickOnDefenceItem(dataTechnology))
            return;
        do{
            if(defenceItem.isShield())
                Defence.clickBuiltDefenceShield(OgameWeb.webDriver);
            else{
                Defence.inputDefenceAmount(OgameWeb.webDriver, defenceItem.getValue());
                Waiter.sleep(200,200);
                Defence.clickBuiltDefence(OgameWeb.webDriver);
            }
            Waiter.sleep(400,400);
        }while (Defence.statusOfDefence(OgameWeb.webDriver, dataTechnology) != ogame.Status.ACTIVE);
        long endInSeconds = Defence.endDateOfUpgrade(OgameWeb.webDriver, dataTechnology);
        AppLog.print(DefenceLeafTask.class.getName(),2,"Start building " + defenceItem.getValue() + " " +
                defenceItem.getDefence().getName() + " on "+ defenceItem.getPlanet().getCoordinate().getText() +".");
        defenceItem.setEndTimeInSeconds(endInSeconds);
        defenceItem.setStatus(Status.BUILDING);
        defenceItem.setStatusTimeInMilliseconds();
    }

    private void dataDownloaded(DefenceItem defenceItem) {
        ogame.defence.Defence defence = defenceItem.getDefence();
        ResourcesProduction resourcesProduction = defenceItem.getPlanet().getResourcesProduction();
        Planet planet = defenceItem.getPlanet();
        if(defence.getStatus() == ogame.Status.OFF){
            defenceItem.setStatus(Status.OFF);
            defenceItem.setStatusTimeInMilliseconds();
            defenceItem.setEndTimeInSeconds(System.currentTimeMillis()/1000 + defenceItem.getTimePeriodInSeconds());
            return;
        }
        if(DataLoader.listDefenceItem.isDefenceBuildingOnPlanet(planet)){
            DefenceItem firstOnQueue = DataLoader.listDefenceItem.getDefenceBuildingOnPlanet(planet);
            final long WAIT_SECONDS_FOR_CHANGE_STATUS = 10L;
            if(firstOnQueue == null) {
                defenceItem.setEndTimeInSeconds(System.currentTimeMillis()/1000 + WAIT_SECONDS_FOR_CHANGE_STATUS);
                defenceItem.setStatus(Status.WAIT_FOR_STATUS);
            }
            else{
                defenceItem.setEndTimeInSeconds(firstOnQueue.getEndTimeInSeconds());
                defenceItem.setStatus(Status.WAIT);
            }

            defenceItem.setStatusTimeInMilliseconds();
            return;
        }
        if(DataLoader.listShipItem.isShipBuildingOnPlanet(planet)){
            ShipItem firstOnQueue = DataLoader.listShipItem.getShipBuildingOnPlanet(planet);
            final long WAIT_SECONDS_FOR_CHANGE_STATUS = 10L;
            if(firstOnQueue == null) {
                defenceItem.setEndTimeInSeconds(System.currentTimeMillis()/1000 + WAIT_SECONDS_FOR_CHANGE_STATUS);
                defenceItem.setStatus(Status.WAIT_FOR_STATUS);
            }
            else{
                defenceItem.setEndTimeInSeconds(firstOnQueue.getEndTimeInSeconds());
                defenceItem.setStatus(Status.WAIT);
            }

            defenceItem.setStatusTimeInMilliseconds();
            return;
        }
        if(defence.getStatus() == ogame.Status.DISABLED){
            if(DataLoader.listItemAutoBuilder.isNaniteFactoryUpradingOnPlanet(planet)){
                long naniteFactoryFinishTimeSeconds = DataLoader.listItemAutoBuilder.naniteFactoryUpgradingOnPlanet(planet).getEndTimeInSeconds();
                defenceItem.setEndTimeInSeconds(naniteFactoryFinishTimeSeconds);
            } else if(DataLoader.listItemAutoBuilder.isShipyardUpradingOnPlanet(planet)){
                long shipyardFinishTimeSeconds = DataLoader.listItemAutoBuilder.shipyardUpgradingOnPlanet(planet).getEndTimeInSeconds();
                defenceItem.setEndTimeInSeconds(shipyardFinishTimeSeconds);
            }else
                defenceItem.setEndTimeInSeconds(System.currentTimeMillis()/1000 + defenceItem.getTimePeriodInSeconds());

            defenceItem.setStatus(Status.WAIT);
            defenceItem.setStatusTimeInMilliseconds();
            return;
        }

        long metal = ResourcesBar.metal(OgameWeb.webDriver);
        long crystal = ResourcesBar.crystal(OgameWeb.webDriver);
        long deuterium = ResourcesBar.deuterium(OgameWeb.webDriver);

        long requiredMetal = defence.getRequiredResources().getMetal() * defenceItem.getValue();
        long requiredCrystal = defence.getRequiredResources().getCrystal() * defenceItem.getValue();
        long requiredDeuterium = defence.getRequiredResources().getDeuterium() * defenceItem.getValue();

        long metalBalance = requiredMetal - metal;
        long crystalBalance = requiredCrystal - crystal;
        long deuteriumBalance = requiredDeuterium - deuterium;

        long millisecondsToProductionResources = 0;

        if(requiredMetal > metal){
            long tmp = resourcesProduction.timeMilisecondsToMetalProduction(metalBalance);
            if(tmp > millisecondsToProductionResources)
                millisecondsToProductionResources = tmp;
        }
        if(requiredCrystal > crystal){
            long tmp = resourcesProduction.timeMilisecondsToCrystalProduction(crystalBalance);
            if(tmp > millisecondsToProductionResources)
                millisecondsToProductionResources = tmp;
        }
        if(requiredDeuterium > deuterium){
            long tmp = resourcesProduction.timeMilisecondsToDeuteriumProduction(deuteriumBalance);
            if(tmp > millisecondsToProductionResources)
                millisecondsToProductionResources = tmp;
        }

        if(millisecondsToProductionResources > 0){
            defenceItem.setStatus(Status.NOT_ENOUGH_RESOURCES);
            defenceItem.setStatusTimeInMilliseconds();
            defenceItem.setEndTimeInSeconds(System.currentTimeMillis()/1000 + millisecondsToProductionResources/1000);
            return;
        }

        defenceItem.setStatus(Status.STARTING);
        defenceItem.setStatusTimeInMilliseconds();
        defenceItem.setEndTimeInSeconds(0);
    }

    private void dataDownloading(DefenceItem defenceItem) {
        Planet planet = defenceItem.getPlanet();
        ogame.defence.Defence defence = defenceItem.getDefence();
        DataTechnology dataTechnology = defenceItem.getDefence().getDataTechnology();

        if(!clickPlanet(planet))
            return;
        if(!clickDefence())
            return;
        if(!clickOnDefenceItem(dataTechnology))
            return;

        ogame.Status status = Defence.statusOfDefence(OgameWeb.webDriver,dataTechnology);
        ProductionTime productionTime = Defence.productionTimeOfDefence(OgameWeb.webDriver);
        defence.setStatus(status);
        defence.setProductionTime(productionTime);

        defenceItem.setStatus(Status.DATA_DOWNLOADED);
        defenceItem.setStatusTimeInMilliseconds(System.currentTimeMillis());
    }

    private void added(DefenceItem defenceItem) {
        defenceItem.setStatus(Status.DATA_DOWNLOADING);
        defenceItem.setStatusTimeInMilliseconds(System.currentTimeMillis());
    }
}
