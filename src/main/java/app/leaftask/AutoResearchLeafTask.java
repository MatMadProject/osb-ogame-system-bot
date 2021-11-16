package app.leaftask;

import app.data.DataLoader;
import app.data.autoresearch.ItemAutoResearch;
import app.data.autoresearch.Status;
import ogame.OgameWeb;
import ogame.ResourcesBar;
import ogame.buildings.RequiredResources;
import ogame.planets.Planet;
import ogame.planets.PlanetsList;
import ogame.planets.ResourcesProduction;
import ogame.researches.Research;
import ogame.researches.Type;
import ogame.utils.Waiter;
import ogame.utils.log.AppLog;
import ogame.utils.watch.Timer;
import ogame.watch.ProductionTime;

import java.util.ArrayList;

public class AutoResearchLeafTask extends LeafTask{
    public AutoResearchLeafTask(int index, long sleepms, String name) {
        super(index, sleepms, name);
    }
    private ItemAutoResearch itemAutoResearchToRemove;
    private final long TIME_WAIT_WHEN_OFF_STATUS = 3600*1000L;
    private final long TIME_WAIT_WHEN_ON_STATUS = 60*1000L;
    @Override
    public void execute() {
        if (isRun()) {
            if (isSleepTimeOut(System.currentTimeMillis())) {
                ArrayList<ItemAutoResearch> queueList = DataLoader.listItemAutoResearch.getQueueList();
                if(!queueList.isEmpty()){
                    try{
                        for(ItemAutoResearch itemAutoResearch : queueList){
                            final Status STATUS = itemAutoResearch.getStatus();
                            if(STATUS == Status.ADDED)
                                downloadData(itemAutoResearch);
                            if(STATUS == Status.DATA_DOWNLOADING)
                                downloadData(itemAutoResearch);
                            if(STATUS == Status.DATA_DOWNLOADED)
                                checksData(itemAutoResearch);
                            if(STATUS == Status.WAIT || STATUS == Status.NOT_ENOUGH_RESOURCES || STATUS == Status.NOT_ENOUGH_ENERGY ||
                            STATUS == Status.OFF)
                                wait(itemAutoResearch);
                            if(STATUS == Status.STARTING)
                                upgrade(itemAutoResearch);
                            if(STATUS == Status.UPGRADING)
                                isFinished(itemAutoResearch);
                            if(STATUS == Status.FINISHED)
                                finish(itemAutoResearch);
                        }
                    }catch (Exception e){
                        AppLog.print(AutoResearchLeafTask.class.getName(),1,"When iterates queueList on" +
                                " first step.");
                    }
                    if(itemAutoResearchToRemove != null){
                        queueList.remove(itemAutoResearchToRemove);
                        itemAutoResearchToRemove = null;
                    }
                }
                setLastTimeExecute(System.currentTimeMillis());
            }
        }
    }

    private void finish(ItemAutoResearch itemAutoResearch) {
        Research research = itemAutoResearch.getResearch();
        research.setProductionTime(null);
        research.setRequiredResources(null);
        itemAutoResearchToRemove = itemAutoResearch;
        DataLoader.listItemAutoResearch.getHistoryList().add(itemAutoResearch);
        DataLoader.researches.setUpdateData(true);
    }

    private void isFinished(ItemAutoResearch itemAutoResearch) {
        long timeToFinish = itemAutoResearch.timeToFinish();
        if(timeToFinish < 0){
            itemAutoResearch.setStatus(Status.FINISHED);
            itemAutoResearch.getResearch().setStatus(ogame.Status.DISABLED);
            long currentTime = System.currentTimeMillis();
            itemAutoResearch.setStatusTime(currentTime);
            itemAutoResearch.setFinishTime(currentTime);
        }
    }

    private void upgrade(ItemAutoResearch itemAutoResearch) {
        int indexOfList = DataLoader.listItemAutoResearch.getQueueList().indexOf(itemAutoResearch);
        //Is on first position on queueList
        if(indexOfList == 0){
            Research research = itemAutoResearch.getResearch();
            Type type = itemAutoResearch.getResearch().getDataTechnology().getType();
            Planet planet = itemAutoResearch.getPlanet();
            int listIndex = itemAutoResearch.getResearch().getDataTechnology().getListIndex();
            //Clicking on planet
            Planet tmpPlanet;
            do {
                if (PlanetsList.clickOnPlanet(OgameWeb.webDriver, planet.getPositionOnList()))
                    break;
                Waiter.sleep(200, 200);
                if (getAntiLooping().check()) {
                    getAntiLooping().reset();
                    return;
                }
                tmpPlanet = PlanetsList.selectedPlanet(OgameWeb.webDriver);
            } while (tmpPlanet == null || tmpPlanet.getId().equals(planet.getId()));
            //Clicking on research tabs
            do{
                ogame.tabs.Research.click(OgameWeb.webDriver);
                Waiter.sleep(200,300);
                if(getAntiLooping().check()){
                    getAntiLooping().reset();
                    return;
                }
            }while(!ogame.tabs.Research.visible(OgameWeb.webDriver));
            //Upgrade building
            do{
                ogame.tabs.Research.upgrade(OgameWeb.webDriver,listIndex,type);
                Waiter.sleep(200,300);
                if(getAntiLooping().check()){
                    getAntiLooping().reset();
                    return;
                }
            }while(ogame.tabs.Research.statusOfResearch(OgameWeb.webDriver,listIndex,type) != ogame.Status.ACTIVE);

            research.setStatus(ogame.Status.ACTIVE);
            AppLog.print(AutoResearchLeafTask.class.getName(),2,"Start upgrade " + research.getName() +
                    " to level " + itemAutoResearch.getUpgradeLevel() + ".");
            itemAutoResearch.setStatus(Status.UPGRADING);
            long currentTime = System.currentTimeMillis();
            itemAutoResearch.setStatusTime(currentTime);
            itemAutoResearch.updateFinishTime(currentTime);
        }else{
            if(DataLoader.listItemAutoResearch.isAnyResearchUprading()){
                itemAutoResearch.setStatus(Status.WAIT);
                long currentTime = System.currentTimeMillis();
                itemAutoResearch.setStatusTime(currentTime);
                ItemAutoResearch itemAutoResearchUpgrading = DataLoader.listItemAutoResearch.getUpgradingResearch();
                itemAutoResearch.setTimer(new Timer(currentTime, itemAutoResearchUpgrading.getFinishTime()));
            }
        }
    }

    private void wait(ItemAutoResearch itemAutoResearch) {
        if(itemAutoResearch.getTimer().isTimeLeft(System.currentTimeMillis())){
            itemAutoResearch.setStatus(Status.DATA_DOWNLOADING);
            itemAutoResearch.setStatusTime(System.currentTimeMillis());
            itemAutoResearch.setTimer(null);
        }
    }



    private void checksData(ItemAutoResearch itemAutoResearch) {
        Research research = itemAutoResearch.getResearch();
        if(research.getStatus() == ogame.Status.OFF){
            itemAutoResearch.setStatus(Status.OFF);
            long currentTime = System.currentTimeMillis();
            itemAutoResearch.setStatusTime(currentTime);
            itemAutoResearch.setTimer(new Timer(currentTime,currentTime+TIME_WAIT_WHEN_OFF_STATUS));
            return;
        }
        if(research.getStatus() == ogame.Status.ON){
            if(DataLoader.listItemAutoResearch.getQueueList().indexOf(itemAutoResearch) == 0){
                itemAutoResearch.setStatus(Status.STARTING);
                itemAutoResearch.setStatusTime();
                return;
            }
            else {
                itemAutoResearch.setStatus(Status.WAIT);
                long currentTime = System.currentTimeMillis();
                itemAutoResearch.setStatusTime(currentTime);
                if(DataLoader.listItemAutoResearch.isAnyResearchUprading()) {
                    ItemAutoResearch itemAutoResearchUpgrading = DataLoader.listItemAutoResearch.getUpgradingResearch();
                    itemAutoResearch.setTimer(new Timer(currentTime, itemAutoResearchUpgrading.getFinishTime()));
                }
                else
                    itemAutoResearch.setTimer(new Timer(currentTime,currentTime + TIME_WAIT_WHEN_ON_STATUS));
                return;
            }
        }
        if(research.getStatus() == ogame.Status.ACTIVE){
            if(DataLoader.listItemAutoResearch.isAnyResearchUprading()){
                itemAutoResearch.setStatus(Status.WAIT);
                long currentTime = System.currentTimeMillis();
                itemAutoResearch.setStatusTime(currentTime);
                ItemAutoResearch itemAutoResearchUpgrading = DataLoader.listItemAutoResearch.getUpgradingResearch();
                itemAutoResearch.setTimer(new Timer(currentTime, itemAutoResearchUpgrading.getFinishTime()));
                return;
            }
        }
        if(research.getStatus() == ogame.Status.DISABLED){
            if(DataLoader.listItemAutoResearch.isAnyResearchUprading()){
                itemAutoResearch.setStatus(Status.WAIT);
                long currentTime = System.currentTimeMillis();
                itemAutoResearch.setStatusTime(currentTime);
                ItemAutoResearch itemAutoResearchUpgrading = DataLoader.listItemAutoResearch.getUpgradingResearch();
                itemAutoResearch.setTimer(new Timer(currentTime, itemAutoResearchUpgrading.getFinishTime()));
                return;
            }
            RequiredResources requiredResources = itemAutoResearch.getResearch().getRequiredResources();
            ResourcesProduction resourcesProduction = itemAutoResearch.getPlanet().getResourcesProduction();
            int metal = ResourcesBar.metal(OgameWeb.webDriver);
            int crystal = ResourcesBar.crystal(OgameWeb.webDriver);
            int deuterium = ResourcesBar.deuterium(OgameWeb.webDriver);
            int energy = ResourcesBar.energyBalanace(OgameWeb.webDriver);

            int metalBalance = requiredResources.getMetal() - metal;
            int crystalBalance = requiredResources.getCrystal() - crystal;
            int deuteriumBalance = requiredResources.getDeuterium() - deuterium;
            int energyBalance = requiredResources.getEnergy() - energy;

            long timeToResourceProduction = 0;
            long currentTime = System.currentTimeMillis();
            if(metalBalance > 0)
                timeToResourceProduction = resourcesProduction.timeMilisecondsToMetalProduction(metalBalance);
            if(crystalBalance > 0){
                long tmp = resourcesProduction.timeMilisecondsToCrystalProduction(crystalBalance);
                if(tmp > timeToResourceProduction)
                    timeToResourceProduction = tmp;
            }
            if(deuteriumBalance > 0){
                long tmp = resourcesProduction.timeMilisecondsToDeuteriumProduction(deuteriumBalance);
                if(tmp > timeToResourceProduction)
                    timeToResourceProduction = tmp;
            }
            if(energyBalance > 0){
                itemAutoResearch.setStatus(Status.NOT_ENOUGH_ENERGY);
                itemAutoResearch.setStatusTime(currentTime);
                itemAutoResearch.setTimer(new Timer(currentTime,currentTime+TIME_WAIT_WHEN_OFF_STATUS));
            }
//            if(timeToResourceProduction == 0){
//                itemAutoResearch.setTimer(new Timer(currentTime,currentTime+TIME_WAIT_WHEN_OFF_STATUS));
//                itemAutoResearch.setStatus(Status.WAIT);
//                itemAutoResearch.setStatusTime(currentTime);
//                return;
//            }

            itemAutoResearch.setTimer(new Timer(currentTime,currentTime+timeToResourceProduction));
            itemAutoResearch.setStatus(Status.NOT_ENOUGH_RESOURCES);
            itemAutoResearch.setStatusTime(currentTime);
        }
    }

    private void downloadData(ItemAutoResearch itemAutoResearch) {
        itemAutoResearch.setStatus(Status.DATA_DOWNLOADING);
        itemAutoResearch.setStatusTime(System.currentTimeMillis());
        Research research = itemAutoResearch.getResearch();
        Type type = research.getDataTechnology().getType();
        Planet planet = itemAutoResearch.getPlanet();
        int listIndex = research.getDataTechnology().getListIndex();
        //Clicking on planet
        Planet tmpPlanet;
        do {
            if (PlanetsList.clickOnPlanet(OgameWeb.webDriver, planet.getPositionOnList()))
                break;
            Waiter.sleep(200, 200);
            if (getAntiLooping().check()) {
                getAntiLooping().reset();
                return;
            }
            tmpPlanet = PlanetsList.selectedPlanet(OgameWeb.webDriver);
        } while (tmpPlanet == null || tmpPlanet.getId().equals(planet.getId()));
        //Clickinh on research tabs
        do{
            ogame.tabs.Research.click(OgameWeb.webDriver);
            Waiter.sleep(200,300);
            if(getAntiLooping().check()){
                getAntiLooping().reset();
                return;
            }
        }while(!ogame.tabs.Research.visible(OgameWeb.webDriver));
        //Data was download on first time execute.
        if(research.getProductionTime() != null && research.getRequiredResources() != null) {
            ogame.Status status = ogame.tabs.Research.statusOfResearch(OgameWeb.webDriver,listIndex,type);
            research.setStatus(status);
            itemAutoResearch.setStatus(Status.DATA_DOWNLOADED);
            itemAutoResearch.setStatusTime(System.currentTimeMillis());
            return;
        }
        //Shows research details
        do{
            ogame.tabs.Research.clickOnResearch(OgameWeb.webDriver,listIndex,type);
            Waiter.sleep(200,300);
            if(getAntiLooping().check()){
                getAntiLooping().reset();
                return;
            }
        }while(!ogame.tabs.Research.visibleResearchDetails(OgameWeb.webDriver,listIndex,type));
        //Dowloads data
        ProductionTime productionTime = ogame.tabs.Research.productionTimeOfResearch(OgameWeb.webDriver);
        RequiredResources requiredResources = ogame.tabs.Research.getRequiredResources(OgameWeb.webDriver,listIndex,type);
        int level = ogame.tabs.Research.levelOfResearch(OgameWeb.webDriver,listIndex,type);
        ogame.Status status = ogame.tabs.Research.statusOfResearch(OgameWeb.webDriver,listIndex,type);
        //Update research data
        research.setProductionTime(productionTime);
        research.setRequiredResources(requiredResources);
        research.setLevel(level);
        research.setStatus(status);

        AppLog.print(AutoBuilderLeafTask.class.getName(),2,"Downloads data of " + research.getName() + ". " +
                "Upgrade level : " + (level + 1) + ", " + requiredResources + ".");

        itemAutoResearch.setStatus(Status.DATA_DOWNLOADED);
        itemAutoResearch.setStatusTime(System.currentTimeMillis());
    }
}
