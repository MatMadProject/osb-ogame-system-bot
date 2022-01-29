package app.leaftask;

import app.data.DataLoader;
import app.data.defence.DefenceItem;
import app.data.defence.Status;
import ogame.DataTechnology;
import ogame.OgameWeb;
import ogame.ResourcesBar;
import ogame.planets.Planet;
import ogame.planets.ResourcesProduction;
import ogame.tabs.Defence;
import ogame.utils.Waiter;
import ogame.utils.watch.Timer;
import ogame.watch.ProductionTime;

import java.util.List;

public class DefenceLeafTask extends LeafTask{

    public DefenceLeafTask(int index, long sleepms, String name) {
        super(index, sleepms, name);
    }

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
        DataLoader.listDefenceItem.addToHistory(defenceItem.copy());
        defenceItem.setStatus(Status.NEXT);
        long currentTimeMilliseconds = System.currentTimeMillis();
        defenceItem.setStatusTime(currentTimeMilliseconds);
        defenceItem.setTimer(new Timer(0,currentTimeMilliseconds/1000 + defenceItem.getTimePeriod()));
    }

    private void wait(DefenceItem defenceItem) {
        long timeToFinish = Timer.timeSeconds(defenceItem.getTimer().getFinishDate(),System.currentTimeMillis()/1000);
        if(timeToFinish < 0){
            if(defenceItem.getStatus() == Status.BUILDING)
                defenceItem.setStatus(Status.FINISHED);
            else if(defenceItem.getStatus() == Status.WAIT_FOR_STATUS)
                defenceItem.setStatus(Status.DATA_DOWNLOADED);
            else
                defenceItem.setStatus(Status.DATA_DOWNLOADING);

            defenceItem.setStatusTime(System.currentTimeMillis());
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
            defenceItem.setStatusTime(System.currentTimeMillis());
            defenceItem.setTimer(new Timer(0,endDateOfUpgrade));
        }else{
            defenceItem.setStatus(Status.DATA_DOWNLOADING);
            defenceItem.setStatusTime(System.currentTimeMillis());
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
        Defence.inputDefenceAmount(OgameWeb.webDriver,defenceItem.getValue());
        Waiter.sleep(200,200);
        Defence.clickBuiltDefence(OgameWeb.webDriver);
        defenceItem.setStatus(Status.CHECK);
        defenceItem.setStatusTime(System.currentTimeMillis());
    }

    private void dataDownloaded(DefenceItem defenceItem) {
        ogame.defence.Defence defence = defenceItem.getDefence();
        ResourcesProduction resourcesProduction = defenceItem.getPlanet().getResourcesProduction();
        Planet planet = defenceItem.getPlanet();
        if(defence.getStatus() == ogame.Status.OFF){
            defenceItem.setStatus(Status.OFF);
            long currentTimeMillis = System.currentTimeMillis();
            defenceItem.setStatusTime(currentTimeMillis);
            defenceItem.setTimer(new Timer(0,currentTimeMillis/1000 + defenceItem.getTimePeriod()));
            return;
        }
        if(DataLoader.listDefenceItem.isDefenceBuildingOnPlanet(planet)){
            DefenceItem firstOnQueue = DataLoader.listDefenceItem.getDefenceBuildingOnPlanet(planet);
            long currentTimeMillis = System.currentTimeMillis();
            long WAIT_SECONDS_FOR_CHANGE_STATUS = 10L;
            if(firstOnQueue == null)//Status.STARTING
                defenceItem.setTimer(new Timer(0,currentTimeMillis/1000 + WAIT_SECONDS_FOR_CHANGE_STATUS));
            else
                defenceItem.setTimer(new Timer(0,firstOnQueue.getTimer().getFinishDate()));
            defenceItem.setStatus(Status.WAIT_FOR_STATUS);
            defenceItem.setStatusTime(System.currentTimeMillis());

            return;
        }
        if(defence.getStatus() == ogame.Status.DISABLED){
            if(DataLoader.listItemAutoBuilder.isNaniteFactoryUpradingOnPlanet(planet)){
                long naniteFactoryFinishTimeSeconds = DataLoader.listItemAutoBuilder.naniteFactoryUpradingOnPlanet(planet).getFinishTime()/1000;
                defenceItem.setTimer(new Timer(0,naniteFactoryFinishTimeSeconds));
            } else if(DataLoader.listItemAutoBuilder.isShipyardUpradingOnPlanet(planet)){
                long shipyardFinishTimeSeconds = DataLoader.listItemAutoBuilder.shipyardUpradingOnPlanet(planet).getFinishTime()/1000;
                defenceItem.setTimer(new Timer(0,shipyardFinishTimeSeconds));
            }else
                defenceItem.setTimer(new Timer(0,System.currentTimeMillis()/1000 + defenceItem.getTimePeriod()));

            defenceItem.setStatus(Status.WAIT);
            defenceItem.setStatusTime(System.currentTimeMillis());
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

        long timeToProductionResources = 0;

        if(requiredMetal > metal){
            long tmp = resourcesProduction.timeMilisecondsToMetalProduction(metalBalance);
            if(tmp > timeToProductionResources)
                timeToProductionResources = tmp;
        }
        if(requiredCrystal > crystal){
            long tmp = resourcesProduction.timeMilisecondsToCrystalProduction(crystalBalance);
            if(tmp > timeToProductionResources)
                timeToProductionResources = tmp;
        }
        if(requiredDeuterium > deuterium){
            long tmp = resourcesProduction.timeMilisecondsToDeuteriumProduction(deuteriumBalance);
            if(tmp > timeToProductionResources)
                timeToProductionResources = tmp;
        }

        if(timeToProductionResources > 0){
            defenceItem.setStatus(Status.NOT_ENOUGH_RESOURCES);
            long currentTimeMillis = System.currentTimeMillis();
            defenceItem.setStatusTime(currentTimeMillis);
            defenceItem.setTimer(new Timer(0,currentTimeMillis/1000 + timeToProductionResources/1000));
            return;
        }

        defenceItem.setStatus(Status.STARTING);
        defenceItem.setStatusTime(System.currentTimeMillis());
        defenceItem.setTimer(null);
    }

    private void dataDownloading(DefenceItem defenceItem) {
        Planet planet = defenceItem.getPlanet();
        ogame.defence.Defence defence = defenceItem.getDefence();
        DataTechnology dataTechnology = defenceItem.getDefence().getDataTechnology();
        //Clicking on planet
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
        defenceItem.setStatusTime(System.currentTimeMillis());
    }

    private void added(DefenceItem defenceItem) {
        defenceItem.setStatus(Status.DATA_DOWNLOADING);
        defenceItem.setStatusTime(System.currentTimeMillis());
    }
}
