package app.leaftask;

import app.data.DataLoader;
import app.data.autobuilder.ItemAutoBuilder;
import app.data.autoresearch.ItemAutoResearch;
import ogame.OgameWeb;
import ogame.ResourcesBar;
import ogame.buildings.RequiredResources;
import ogame.planets.Planet;
import ogame.planets.ResourcesProduction;
import ogame.researches.Research;
import ogame.Type;
import ogame.tabs.Overview;
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

    @Override
    public void execute() {
        if (isRun()) {
            if (isSleepTimeOut(System.currentTimeMillis())) {
                ArrayList<ItemAutoResearch> queueList = DataLoader.listItemAutoResearch.getQueueList();
                if (!queueList.isEmpty()) {
                    for (ItemAutoResearch itemAutoResearch : queueList) {
                        selectTask(itemAutoResearch);
                    }
                    if (itemAutoResearchToRemove != null) {
                        queueList.remove(itemAutoResearchToRemove);
                        itemAutoResearchToRemove = null;
                    }
                }
                DataLoader.listItemAutoResearch.save();
                setLastTimeExecute(System.currentTimeMillis());
            }
        }
    }

    private void selectTask(ItemAutoResearch itemAutoResearch) {
        Status status = itemAutoResearch.getStatus();
        switch (status){
            case ADDED:
            case DATA_DOWNLOADING:
                dataDownloading(itemAutoResearch);
                break;
            case DATA_DOWNLOADED:
                dataDownloaded(itemAutoResearch);
                break;
            case STARTING:
                starting(itemAutoResearch);
                break;
            case UPGRADING:
                upgrading(itemAutoResearch);
                break;
            case FINISHED:
                finished(itemAutoResearch);
                break;
            case LABORATORY_BUILD:
            case WAIT:
            case NOT_ENOUGH_RESOURCES:
            case NOT_ENOUGH_ENERGY:
            case OFF:
                wait(itemAutoResearch);
                break;
        }
    }


    private void finished(ItemAutoResearch itemAutoResearch) {
        Research research = itemAutoResearch.getResearch();
        research.setProductionTime(null);
        research.setRequiredResources(null);
        itemAutoResearchToRemove = itemAutoResearch;
        DataLoader.listItemAutoResearch.getHistoryList().add(itemAutoResearch);
        DataLoader.researches.setUpdateData(true);

        //Starting buliding next object on queue list.
        DataLoader.listItemAutoResearch.startNextResearchOnQueue();
    }

    private void upgrading(ItemAutoResearch itemAutoResearch) {
        long timeToFinish = Timer.timeSeconds(itemAutoResearch.getEndTimeInSeconds(),System.currentTimeMillis()/1000);
        if(timeToFinish < 0){
            itemAutoResearch.setStatus(Status.FINISHED);
            itemAutoResearch.getResearch().setStatus(ogame.Status.DISABLED);
            long currentTime = System.currentTimeMillis();
            itemAutoResearch.setStatusTimeInMilliseconds(currentTime);
            itemAutoResearch.setFinishTimeInMilliseconds(currentTime);
        }
    }

    private void starting(ItemAutoResearch itemAutoResearch) {
        int indexOfList = DataLoader.listItemAutoResearch.getQueueList().indexOf(itemAutoResearch);
        //Is on first position on queueList
        if(indexOfList == 0){
            Research research = itemAutoResearch.getResearch();
            Type type = itemAutoResearch.getResearch().getDataTechnology().getType();
            Planet planet = itemAutoResearch.getPlanet();
            int listIndex = itemAutoResearch.getResearch().getDataTechnology().getListIndex();
            //Clicking on planet
            if(!clickPlanet(planet))
                return;
            //Clicking on research tabs
            if(!clickResearch())
                return;
            //Upgrade building
            do{
                ogame.tabs.Research.upgrade(OgameWeb.webDriver,listIndex,type);
                Waiter.sleep(200,300);
                if(getAntiLooping().check()){
                    getAntiLooping().reset();
                    return;
                }
            }while(ogame.tabs.Research.statusOfResearch(OgameWeb.webDriver,listIndex,type) != ogame.Status.ACTIVE);
            long endTimeInSeconds = ogame.tabs.Research.endDateOfUpgradeResearch(OgameWeb.webDriver,itemAutoResearch.getResearch().getDataTechnology());
            research.setStatus(ogame.Status.ACTIVE);
            itemAutoResearch.setEndTimeInSeconds(endTimeInSeconds);
            AppLog.print(AutoResearchLeafTask.class.getName(),2,"Start upgrade " + research.getName() +
                    " to level " + itemAutoResearch.getUpgradeLevel() + ".");
            itemAutoResearch.setStatus(Status.UPGRADING);
            itemAutoResearch.setStatusTimeInMilliseconds();
        }else
            if(DataLoader.listItemAutoResearch.isAnyResearchUprading()) {
                itemAutoResearch.setStatus(Status.WAIT);
                itemAutoResearch.setStatusTimeInMilliseconds();
                ItemAutoResearch itemAutoResearchUpgrading = DataLoader.listItemAutoResearch.getUpgradingResearch();
                itemAutoResearch.setEndTimeInSeconds(itemAutoResearchUpgrading.getEndTimeInSeconds());
            }
    }

    private void wait(ItemAutoResearch itemAutoResearch) {
        long timeToFinish = Timer.timeSeconds(itemAutoResearch.getEndTimeInSeconds(),System.currentTimeMillis()/1000);
        if(timeToFinish < 0){
            itemAutoResearch.setStatus(Status.DATA_DOWNLOADING);
            itemAutoResearch.setStatusTimeInMilliseconds();
            itemAutoResearch.setEndTimeInSeconds(0);
            Overview.clickAlways(OgameWeb.webDriver);
        }
    }



    private void dataDownloaded(ItemAutoResearch itemAutoResearch) {
        Planet planet = itemAutoResearch.getPlanet();
        long SECONDS_WAIT_WHEN_OFF_STATUS = 3600;
        if(itemAutoResearch.isResearchOgameStatusOff()){
            itemAutoResearch.setStatus(Status.OFF);
            itemAutoResearch.setStatusTimeInMilliseconds();
            itemAutoResearch.setEndTimeInSeconds(System.currentTimeMillis()/1000 + SECONDS_WAIT_WHEN_OFF_STATUS);
            return;
        }
        if(itemAutoResearch.isResearchOgameStatusOn()){
            if(DataLoader.listItemAutoResearch.getQueueList().indexOf(itemAutoResearch) == 0){
                itemAutoResearch.setStatus(Status.STARTING);
                itemAutoResearch.setStatusTimeInMilliseconds();
            }
            else {
                itemAutoResearch.setStatus(Status.WAIT);
                itemAutoResearch.setStatusTimeInMilliseconds();
                long SECONDS_WAIT_WHEN_ON_STATUS = 60;
                if(DataLoader.listItemAutoResearch.isAnyResearchUprading()) {
                    ItemAutoResearch itemAutoResearchUpgrading = DataLoader.listItemAutoResearch.getUpgradingResearch();
                    itemAutoResearch.setEndTimeInSeconds(itemAutoResearchUpgrading.getEndTimeInSeconds());
                }
                else
                    itemAutoResearch.setEndTimeInSeconds(System.currentTimeMillis()/1000 + SECONDS_WAIT_WHEN_ON_STATUS);
            }
            return;
        }
        if(itemAutoResearch.isResearchOgameStatusActive()){
            if(DataLoader.listItemAutoResearch.isAnyResearchUprading()){
                itemAutoResearch.setStatus(Status.WAIT);
                itemAutoResearch.setStatusTimeInMilliseconds();
                ItemAutoResearch itemAutoResearchUpgrading = DataLoader.listItemAutoResearch.getUpgradingResearch();
                itemAutoResearch.setEndTimeInSeconds(itemAutoResearchUpgrading.getEndTimeInSeconds());
                return;
            }
        }
        if(itemAutoResearch.isResearchOgameStatusDisabled()){
            if(DataLoader.listItemAutoResearch.isAnyResearchUprading()){
                itemAutoResearch.setStatus(Status.WAIT);
                itemAutoResearch.setStatusTimeInMilliseconds();
                ItemAutoResearch itemAutoResearchUpgrading = DataLoader.listItemAutoResearch.getUpgradingResearch();
                itemAutoResearch.setEndTimeInSeconds(itemAutoResearchUpgrading.getEndTimeInSeconds());
                return;
            }
            if(DataLoader.listItemAutoBuilder.isResearchLaboratorydUpradingOnPlanet(planet)){
                ItemAutoBuilder researchUpgrading = DataLoader.listItemAutoBuilder.researchLaboratorydUpgradingOnPlanet(planet);
                itemAutoResearch.setStatus(Status.LABORATORY_BUILD);
                itemAutoResearch.setStatusTimeInMilliseconds();
                itemAutoResearch.setEndTimeInSeconds(researchUpgrading.getEndTimeInSeconds());
            }
            RequiredResources requiredResources = itemAutoResearch.getResearch().getRequiredResources();
            ResourcesProduction resourcesProduction = itemAutoResearch.getPlanet().getResourcesProduction();
            long metal = ResourcesBar.metal(OgameWeb.webDriver);
            long crystal = ResourcesBar.crystal(OgameWeb.webDriver);
            long deuterium = ResourcesBar.deuterium(OgameWeb.webDriver);
            long energy = ResourcesBar.energyBalanace(OgameWeb.webDriver);

            planet.getResources().setMetal(metal);
            planet.getResources().setCrystal(crystal);
            planet.getResources().setDeuterium(deuterium);

            long metalBalance = requiredResources.getMetal() - metal;
            long crystalBalance = requiredResources.getCrystal() - crystal;
            long deuteriumBalance = requiredResources.getDeuterium() - deuterium;
            long energyBalance = requiredResources.getEnergy() - energy;

            long timeToResourceProduction = 0;
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
                itemAutoResearch.setStatusTimeInMilliseconds();
                itemAutoResearch.setEndTimeInSeconds(System.currentTimeMillis()/1000 + SECONDS_WAIT_WHEN_OFF_STATUS);
            }
            itemAutoResearch.setEndTimeInSeconds(System.currentTimeMillis()/1000 + timeToResourceProduction/1000);
            itemAutoResearch.setStatus(Status.NOT_ENOUGH_RESOURCES);
            itemAutoResearch.setStatusTimeInMilliseconds();
        }
    }

    private void dataDownloading(ItemAutoResearch itemAutoResearch) {
        Research research = itemAutoResearch.getResearch();
        Type type = research.getDataTechnology().getType();
        Planet planet = itemAutoResearch.getPlanet();
        int listIndex = research.getDataTechnology().getListIndex();
        //Clicking on planet
        if(!clickPlanet(planet))
            return;
        //Clickinh on research tabs
        if(!clickResearch())
            return;
        //Data was download on first time execute.
        if(itemAutoResearch.isDataDownloaded()) {
            ogame.Status status = ogame.tabs.Research.statusOfResearch(OgameWeb.webDriver,listIndex,type);
            research.setStatus(status);
            itemAutoResearch.setStatus(Status.DATA_DOWNLOADED);
            itemAutoResearch.setStatusTimeInMilliseconds();
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
        int curentLevel = ogame.tabs.Research.levelOfResearch(OgameWeb.webDriver,listIndex,type);
        ogame.Status status = ogame.tabs.Research.statusOfResearch(OgameWeb.webDriver,listIndex,type);
        if(itemAutoResearch.isResearchAchievedUpgradeLevel(curentLevel)){
            itemAutoResearch.setStatus(Status.FINISHED);
            itemAutoResearch.setStatusTimeInMilliseconds();
            return;
        }
        //Update research data
        research.setProductionTime(productionTime);
        research.setRequiredResources(requiredResources);
        research.setLevel(curentLevel);
        research.setStatus(status);


        AppLog.print(AutoResearchLeafTask.class.getName(),2,"Downloads data of " + research.getName() + ". " +
                "Current level : " + curentLevel + " ("+itemAutoResearch.getUpgradeLevel()+"^), Required resources: " + requiredResources +
                ", Production time: " + productionTime + ".");
        itemAutoResearch.setStatus(Status.DATA_DOWNLOADED);
        itemAutoResearch.setStatusTimeInMilliseconds(System.currentTimeMillis());
    }
}
