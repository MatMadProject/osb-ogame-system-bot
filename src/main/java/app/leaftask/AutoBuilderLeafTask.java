package app.leaftask;

import app.data.DataLoader;
import app.data.autobuilder.ItemAutoBuilder;
import app.data.autoresearch.ItemAutoResearch;
import app.data.shipyard.DefenceItem;
import app.data.shipyard.ShipItem;
import ogame.OgameWeb;
import ogame.ResourcesBar;
import ogame.buildings.Building;
import ogame.buildings.RequiredResources;
import ogame.planets.Planet;
import ogame.planets.ResourcesProduction;
import ogame.Type;
import ogame.tabs.Facilities;
import ogame.tabs.Overview;
import ogame.tabs.Supplies;
import ogame.utils.Waiter;
import ogame.utils.log.AppLog;
import ogame.utils.watch.Timer;
import ogame.watch.ProductionTime;

import java.util.ArrayList;

public class AutoBuilderLeafTask extends LeafTask{

    public AutoBuilderLeafTask(int index, long sleepms, String name) {
        super(index, sleepms, name);
    }
    private ItemAutoBuilder itemAutoBuilderToRemove;

    @Override
    public void execute() {
        if(isRun()){
            if(isSleepTimeOut(System.currentTimeMillis())){
                ArrayList<ItemAutoBuilder> queueList = DataLoader.listItemAutoBuilder.getQueueList();
                if(!queueList.isEmpty()){
                    for(ItemAutoBuilder itemAutoBuilder : queueList){
                        selectTask(itemAutoBuilder);
                        DataLoader.listItemAutoBuilder.save();
                    }
                    try{
                        for(ItemAutoBuilder itemAutoBuilder : queueList){
                            final Status status = itemAutoBuilder.getStatus();
                            switch(status){
                                case ADDED:
                                case DATA_DOWNLOADING:
                                    dataDownloading(itemAutoBuilder);
                                    break;
                                case DATA_DOWNLOADED:
                                    dataDownloaded(itemAutoBuilder);
                                    break;
                                case STARTING:
                                    starting(itemAutoBuilder);
                                    break;
                                case UPGRADING:
                                    upgrading(itemAutoBuilder);
                                    break;
                                case FINISHED:
                                    finished(itemAutoBuilder, DataLoader.listItemAutoBuilder.getQueueListFromPlanet(itemAutoBuilder.getPlanet()));
                                    break;
                                case WAIT:
                                case NOT_ENOUGH_RESOURCES:
                                case NOT_ENOUGH_ENERGY:
                                case OFF:
                                    wait(itemAutoBuilder);
                                    break;
                            }
                        }
                    }catch(Exception ex){
                        AppLog.print(AutoBuilderLeafTask.class.getName(),1,"When iterates queueList on" +
                                " first step.");
                    }
                    if(itemAutoBuilderToRemove != null){
                        queueList.remove(itemAutoBuilderToRemove);
                        itemAutoBuilderToRemove = null;
                    }
                }
                DataLoader.listItemAutoBuilder.save();
                setLastTimeExecute(System.currentTimeMillis());
            }
        }
    }

    private void selectTask(ItemAutoBuilder itemAutoBuilder) {
        Status status = itemAutoBuilder.getStatus();
        switch (status){
            case ADDED:
            case DATA_DOWNLOADING:
                dataDownloading(itemAutoBuilder);
                break;
            case DATA_DOWNLOADED:
                dataDownloaded(itemAutoBuilder);
                break;
            case STARTING:
                starting(itemAutoBuilder);
                break;
            case UPGRADING:
                upgrading(itemAutoBuilder);
                break;
            case FINISHED:
                finished(itemAutoBuilder, DataLoader.listItemAutoBuilder.getQueueListFromPlanet(itemAutoBuilder.getPlanet()));
            case SHIP_BUILD:
            case DEFENCE_BUILD:
            case RESEARCH_UPGRADE:
            case WAIT:
            case NOT_ENOUGH_RESOURCES:
            case NOT_ENOUGH_ENERGY:
            case OFF:
                wait(itemAutoBuilder);
                break;
        }
    }

    private void finished(ItemAutoBuilder itemAutoBuilder, ArrayList<ItemAutoBuilder> queueList) {
        Building building = itemAutoBuilder.getBuilding();
        Planet planet = itemAutoBuilder.getPlanet();
        building.setProductionTime(null);
        building.setRequiredResources(null);
        itemAutoBuilderToRemove = itemAutoBuilder;
        DataLoader.listItemAutoBuilder.getHistoryList().add(itemAutoBuilder);
        DataLoader.planets.setUpdateData(true);
        if(itemAutoBuilder.isProductionBuilding()){
            planet.setUpdateResourceBuilding(true);
            planet.setUpdateResourcesProduction(true);
        }else
            planet.setUpdateTechnologyBuilding(true);
        planet.setUpdatePlanetInformation(true);
        //Starting buliding next object on queue list.
        ItemAutoBuilder index1 = queueList.get(1);
        index1.setStatus(Status.DATA_DOWNLOADING);
        index1.setFinishTimeInMilliseconds(0);
        if(itemAutoBuilder.isNaniteFactory() || itemAutoBuilder.isRoboticsFactory())
            DataLoader.listItemAutoBuilder.setStatusOnAllItems(planet,Status.DATA_DOWNLOADING);
    }

    private void upgrading(ItemAutoBuilder itemAutoBuilder) {
        long timeToFinish = Timer.timeSeconds(System.currentTimeMillis()/1000,itemAutoBuilder.getEndTimeInSeconds());
        if(timeToFinish < 0){
            itemAutoBuilder.setStatus(Status.FINISHED);
            itemAutoBuilder.getBuilding().setStatus(ogame.Status.DISABLED);
            long currentTime = System.currentTimeMillis();
            itemAutoBuilder.setStatusTimeInMilliseconds(currentTime);
            itemAutoBuilder.setFinishTimeInMilliseconds(currentTime);
        }
    }

    private void starting(ItemAutoBuilder itemAutoBuilder) {
        ArrayList<ItemAutoBuilder> planetQueue = DataLoader.listItemAutoBuilder.getQueueListFromPlanet(itemAutoBuilder.getPlanet());
        Planet planet = itemAutoBuilder.getPlanet();
        int indexOfList = planetQueue.indexOf(itemAutoBuilder);
        //Is on first position on queueList
        if(indexOfList == 0){
            Building building = itemAutoBuilder.getBuilding();
            long endTimeInSeconds;
            int listIndex = building.getDataTechnology().getListIndex();
            //Clicking on planet
            if(!clickPlanet(planet))
                return;
            if(itemAutoBuilder.isProductionBuilding()){
                //Suppliec clicking
                if(!clickSupplies())
                    return;
                do{
                    Supplies.upgradeBuilding(OgameWeb.webDriver,listIndex);
                    Waiter.sleep(200,300);
                    if(getAntiLooping().check()){
                        getAntiLooping().reset();
                        return;
                    }
                }while(Supplies.statusOfBuilding(OgameWeb.webDriver,listIndex) != ogame.Status.ACTIVE);
                endTimeInSeconds = Supplies.endDateOfUpgradeBuilding(OgameWeb.webDriver, listIndex);
            }
            else{
                //Facilities clicking
                if(!clickFacilities())
                    return;
                //Upgrade building
                do{
                    Facilities.upgradeBuilding(OgameWeb.webDriver,listIndex);
                    Waiter.sleep(200,300);
                    if(getAntiLooping().check()){
                        getAntiLooping().reset();
                        return;
                    }
                }while(Facilities.statusOfBuilding(OgameWeb.webDriver,listIndex) != ogame.Status.ACTIVE);
                endTimeInSeconds = Facilities.endDateOfUpgradeBuilding(OgameWeb.webDriver, listIndex);
            }
            building.setStatus(ogame.Status.ACTIVE);
            itemAutoBuilder.setEndTimeInSeconds(endTimeInSeconds);
            AppLog.print(AutoBuilderLeafTask.class.getName(),2,"Start upgrade " + building.getName() +
                    " to level " + itemAutoBuilder.getUpgradeLevel() + ".");
            itemAutoBuilder.setStatus(Status.UPGRADING);
            itemAutoBuilder.setStatusTimeInMilliseconds();
        }else{
            //Checks is any building is upgrading
            if(DataLoader.listItemAutoBuilder.isAnyBuildingUpradingOnPlanet(planet)){
                itemAutoBuilder.setStatus(Status.WAIT);
                itemAutoBuilder.setStatusTimeInMilliseconds();
                ItemAutoBuilder itemAutoBuilderUpgrading = DataLoader.listItemAutoBuilder.getUpgradingBuildingOnPlanet(planet);
                itemAutoBuilder.setEndTimeInSeconds(itemAutoBuilderUpgrading.getEndTimeInSeconds());
            }
        }
    }

    private void wait(ItemAutoBuilder itemAutoBuilder) {
        long timeToFinish = Timer.timeSeconds(System.currentTimeMillis()/1000,itemAutoBuilder.getEndTimeInSeconds());
        if(timeToFinish < 0){
            itemAutoBuilder.setStatus(Status.DATA_DOWNLOADING);
            itemAutoBuilder.setStatusTimeInMilliseconds();
            itemAutoBuilder.setEndTimeInSeconds(0);
            Overview.clickAlways(OgameWeb.webDriver);
        }
    }

    private void dataDownloaded(ItemAutoBuilder itemAutoBuilder) {
        Building building = itemAutoBuilder.getBuilding();
        Planet planet = itemAutoBuilder.getPlanet();
        long SECONDS_WAIT_WHEN_OFF_STATUS = 3600 ;
        long SECONDS_WAIT_WHEN_ON_STATUS = 60;
        ArrayList<ItemAutoBuilder> planetQueue = DataLoader.listItemAutoBuilder.getQueueListFromPlanet(itemAutoBuilder.getPlanet());
        if(itemAutoBuilder.isBuildingOgameStatusOff()){
            itemAutoBuilder.setStatus(Status.OFF);
            itemAutoBuilder.setStatusTimeInMilliseconds();
            itemAutoBuilder.setEndTimeInSeconds(System.currentTimeMillis()/1000 + SECONDS_WAIT_WHEN_OFF_STATUS);
            return;
        }
        if(itemAutoBuilder.isBuildingOgameStatusOn()){
            if(itemAutoBuilder.isFirstOnQueue(planetQueue)){
                itemAutoBuilder.setStatus(Status.STARTING);
                itemAutoBuilder.setStatusTimeInMilliseconds();

            }else{
                itemAutoBuilder.setStatus(Status.WAIT);
                itemAutoBuilder.setStatusTimeInMilliseconds();
                if(DataLoader.listItemAutoBuilder.isAnyBuildingUpradingOnPlanet(planet)){
                    ItemAutoBuilder itemAutoBuilderUpgrading = DataLoader.listItemAutoBuilder.getUpgradingBuildingOnPlanet(planet);
                    itemAutoBuilder.setEndTimeInSeconds(itemAutoBuilderUpgrading.getEndTimeInSeconds());
                }else
                    itemAutoBuilder.setEndTimeInSeconds(System.currentTimeMillis()/1000 + SECONDS_WAIT_WHEN_ON_STATUS);

                ItemAutoBuilder first = planetQueue.get(0);
                if(first.getStatus() == Status.NOT_ENOUGH_RESOURCES)
                    itemAutoBuilder.setEndTimeInSeconds(first.getEndTimeInSeconds());

            }
            return;
        }
        if(itemAutoBuilder.isBuildingOgameStatusActive()){
            if(DataLoader.listItemAutoBuilder.isAnyBuildingUpradingOnPlanet(planet)){
                itemAutoBuilder.setStatus(Status.WAIT);
                itemAutoBuilder.setStatusTimeInMilliseconds();
                ItemAutoBuilder itemAutoBuilderUpgrading = DataLoader.listItemAutoBuilder.getUpgradingBuildingOnPlanet(planet);
                itemAutoBuilder.setEndTimeInSeconds(itemAutoBuilderUpgrading.getEndTimeInSeconds());
                return;
            }
        }
        if(itemAutoBuilder.isBuildingOgameStatusDisabled()){
            if(itemAutoBuilder.isResearchLaboratory()){
                if(DataLoader.listItemAutoResearch.isAnyResearchUprading()){
                    ItemAutoResearch research = DataLoader.listItemAutoResearch.getUpgradingResearch();
                    itemAutoBuilder.setStatus(Status.RESEARCH_UPGRADE);
                    itemAutoBuilder.setStatusTimeInMilliseconds();
                    itemAutoBuilder.setEndTimeInSeconds(research.getEndTimeInSeconds());
                    return;
                }
            }
            if(itemAutoBuilder.isShipyard() || itemAutoBuilder.isNaniteFactory()){
                if(DataLoader.listShipItem.isShipBuildingOnPlanet(planet)){
                    ShipItem shipItem = DataLoader.listShipItem.getShipBuildingOnPlanet(planet);
                    DefenceItem defenceItem = DataLoader.listDefenceItem.getDefenceBuildingOnPlanet(planet);
                    if(shipItem != null){
                        itemAutoBuilder.setStatus(Status.SHIP_BUILD);
                        itemAutoBuilder.setStatusTimeInMilliseconds();
                        itemAutoBuilder.setEndTimeInSeconds(shipItem.getEndTimeInSeconds());
                    }
                    if(defenceItem != null){
                        itemAutoBuilder.setStatus(Status.DEFENCE_BUILD);
                        itemAutoBuilder.setStatusTimeInMilliseconds();
                        itemAutoBuilder.setEndTimeInSeconds(defenceItem.getEndTimeInSeconds());
                    }
                }
            }
            if(DataLoader.listItemAutoBuilder.isAnyBuildingUpradingOnPlanet(planet)){
                itemAutoBuilder.setStatus(Status.WAIT);
                itemAutoBuilder.setStatusTimeInMilliseconds();
                ItemAutoBuilder itemAutoBuilderUpgrading = DataLoader.listItemAutoBuilder.getUpgradingBuildingOnPlanet(planet);
                itemAutoBuilder.setEndTimeInSeconds(itemAutoBuilderUpgrading.getEndTimeInSeconds());
                return;
            }
            RequiredResources requiredResources = itemAutoBuilder.getBuilding().getRequiredResources();
            ResourcesProduction resourcesProduction = itemAutoBuilder.getPlanet().getResourcesProduction();
            long metal = ResourcesBar.metal(OgameWeb.webDriver);
            long crystal = ResourcesBar.crystal(OgameWeb.webDriver);
            long deuterium = ResourcesBar.deuterium(OgameWeb.webDriver);
            long energy = ResourcesBar.energyBalanace(OgameWeb.webDriver);

            long metalBalance = requiredResources.getMetal() - metal;
            long crystalBalance = requiredResources.getCrystal() - crystal;
            long deuteriumBalance = requiredResources.getDeuterium() - deuterium;
            long energyBalance = requiredResources.getEnergy() - energy;

            planet.getResources().setMetal(metal);
            planet.getResources().setCrystal(crystal);
            planet.getResources().setDeuterium(deuterium);

            long timeToProductionResources = 0;
            long currentTime = System.currentTimeMillis();
            if(metalBalance > 0)
                timeToProductionResources = resourcesProduction.timeMilisecondsToMetalProduction(metalBalance);
            if(crystalBalance > 0){
                long tmp = resourcesProduction.timeMilisecondsToCrystalProduction(crystalBalance);
                if(tmp > timeToProductionResources)
                    timeToProductionResources = tmp;
            }
            if(deuteriumBalance > 0){
                long tmp = resourcesProduction.timeMilisecondsToDeuteriumProduction(deuteriumBalance);
                if(tmp > timeToProductionResources)
                    timeToProductionResources = tmp;
            }
            if(energyBalance > 0 && building.getRequiredResources().getEnergy() > 0){
                itemAutoBuilder.setStatus(Status.NOT_ENOUGH_ENERGY);
                itemAutoBuilder.setStatusTimeInMilliseconds();
                itemAutoBuilder.setEndTimeInSeconds(System.currentTimeMillis()/1000 + SECONDS_WAIT_WHEN_OFF_STATUS);
                return;
            }
            //Jeżeli pierwszy item w kolejce ma status NOT_ENOUGH_RESOURCES
            if(planetQueue.get(0).getStatus() == Status.NOT_ENOUGH_RESOURCES){
                itemAutoBuilder.setEndTimeInSeconds(planetQueue.get(0).getEndTimeInSeconds());
                itemAutoBuilder.setStatus(Status.WAIT);
                itemAutoBuilder.setStatusTimeInMilliseconds();
            }
            itemAutoBuilder.setEndTimeInSeconds(currentTime + timeToProductionResources/1000);
            itemAutoBuilder.setStatus(Status.NOT_ENOUGH_RESOURCES);
            itemAutoBuilder.setStatusTimeInMilliseconds(currentTime);
        }

        if(itemAutoBuilder.isBuildingOgameStatusUndefined()){
            itemAutoBuilder.setStatus(Status.DATA_DOWNLOADING);
            itemAutoBuilder.setStatusTimeInMilliseconds();
            itemAutoBuilder.setEndTimeInSeconds(0);
            building.setProductionTime(null);
            building.setRequiredResources(null);
        }
    }

    private void dataDownloading(ItemAutoBuilder itemAutoBuilder) {
        Building building = itemAutoBuilder.getBuilding();
        Type type =  building.getDataTechnology().getType();
        Planet planet = itemAutoBuilder.getPlanet();
        ProductionTime productionTime;
        RequiredResources requiredResources;
        ogame.Status status;
        int energyConsumption = -1;
        int curentLevel;
        int listIndex = building.getDataTechnology().getListIndex();

        //Clicking on planet
        if(!clickPlanet(planet))
            return;
        if(itemAutoBuilder.isProductionBuilding()){
            //Suppliec clicking
            if(!clickSupplies())
                return;
            //Data was download on first time execute.
            if(itemAutoBuilder.isDataDownloaded()) {
                status = Supplies.statusOfBuilding(OgameWeb.webDriver,listIndex);
                building.setStatus(status);
                itemAutoBuilder.setStatus(Status.DATA_DOWNLOADED);
                itemAutoBuilder.setStatusTimeInMilliseconds();
                return;
            }
            //Shows building details
            if(!clickOnBuilding(type,listIndex))
                return;
            //Dowloads data
            productionTime = Supplies.productionTimeOfBuilding(OgameWeb.webDriver);
            requiredResources = Supplies.getRequiredResources(OgameWeb.webDriver,listIndex);
            curentLevel = Supplies.levelOfBuilding(OgameWeb.webDriver,listIndex);
            status = Supplies.statusOfBuilding(OgameWeb.webDriver,listIndex);
            energyConsumption = Supplies.energyConsumption(OgameWeb.webDriver);
            //When tries upgrade a upgraded building.
            if(itemAutoBuilder.isBuildingAchievedUpgradeLevel(curentLevel)){
                itemAutoBuilder.setStatus(Status.FINISHED);
                itemAutoBuilder.setStatusTimeInMilliseconds();
                return;
            }
            if(itemAutoBuilder.isBuildingUpgrading(status)){
                long endTimeOfUpgrading = Supplies.endDateOfUpgradeBuilding(OgameWeb.webDriver,listIndex);
                itemAutoBuilder.setEndTimeInSeconds(endTimeOfUpgrading);
                itemAutoBuilder.setStatus(Status.UPGRADING);
                itemAutoBuilder.setStatusTimeInMilliseconds();
            }
            //Update building data
            building.setProductionTime(productionTime);
            building.setRequiredResources(requiredResources);
            building.setLevel(curentLevel);
            building.setStatus(status);
            if(energyConsumption != -1)
                itemAutoBuilder.setEnergyConsumption(energyConsumption);
        }
        else{
            //Facilities clicking
            if(!clickFacilities())
                return;
            //Data was download on first time execute.
            if(itemAutoBuilder.isDataDownloaded()) {
                status = Facilities.statusOfBuilding(OgameWeb.webDriver,listIndex);
                building.setStatus(status);
                itemAutoBuilder.setStatus(Status.DATA_DOWNLOADED);
                itemAutoBuilder.setStatusTimeInMilliseconds();
                return;
            }
            //Shows building details
            if(!clickOnBuilding(type,listIndex))
                return;
            //Dowloads data
            productionTime = Facilities.productionTimeOfBuilding(OgameWeb.webDriver);
            requiredResources = Facilities.getRequiredResources(OgameWeb.webDriver,listIndex);
            curentLevel = Facilities.levelOfBuilding(OgameWeb.webDriver,listIndex);
            status = Facilities.statusOfBuilding(OgameWeb.webDriver,listIndex);
            if(itemAutoBuilder.isBuildingAchievedUpgradeLevel(curentLevel)){
                itemAutoBuilder.setStatus(Status.FINISHED);
                itemAutoBuilder.setStatusTimeInMilliseconds();
                return;
            }
            if(itemAutoBuilder.isBuildingUpgrading(status)){
                long endTimeOfUpgading = Facilities.endDateOfUpgradeBuilding(OgameWeb.webDriver,listIndex);
//                itemAutoBuilder.getBuilding().setProductionTime(productionTime);??
                itemAutoBuilder.setEndTimeInSeconds(endTimeOfUpgading);
                itemAutoBuilder.setStatus(Status.UPGRADING);
                itemAutoBuilder.setStatusTimeInMilliseconds();
            }
            //Update building data
            building.setProductionTime(productionTime);
            building.setRequiredResources(requiredResources);
            building.setLevel(curentLevel);
            building.setStatus(status);

        }
        AppLog.print(AutoBuilderLeafTask.class.getName(),2,"Downloads data of " + building.getName() + ". " +
                "Current level : " + curentLevel + " ("+itemAutoBuilder.getUpgradeLevel()+"^), Required resources: " + requiredResources +
                ", Production time: " + productionTime + ", " +(energyConsumption != -1 ? ", energy consumption: " + energyConsumption : "") + ".");

        itemAutoBuilder.setStatus(Status.DATA_DOWNLOADED);
        itemAutoBuilder.setStatusTimeInMilliseconds(System.currentTimeMillis());
    }
}
