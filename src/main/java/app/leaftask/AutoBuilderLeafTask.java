package app.leaftask;

import app.data.DataLoader;
import app.data.autobuilder.ItemAutoBuilder;
import app.data.autobuilder.Status;
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
                    try{
                        for(ItemAutoBuilder itemAutoBuilder : queueList){
                            final Status status = itemAutoBuilder.getStatus();
                            switch(status){
                                case ADDED:
                                case DATA_DOWNLOADING:
                                    downloadData(itemAutoBuilder);
                                    break;
                                case DATA_DOWNLOADED:
                                    checksData(itemAutoBuilder);
                                    break;
                                case WAIT:
                                case NOT_ENOUGH_RESOURCES:
                                case NOT_ENOUGH_ENERGY:
                                case OFF:
                                    wait(itemAutoBuilder);
                                    break;
                                case STARTING:
                                    upgrade(itemAutoBuilder);
                                    break;
                                case UPGRADING:
                                    isFinished(itemAutoBuilder);
                                    break;
                                case FINISHED:
                                    finish(itemAutoBuilder, DataLoader.listItemAutoBuilder.getQueueListFromPlanet(itemAutoBuilder.getPlanet()));
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

    private void finish(ItemAutoBuilder itemAutoBuilder, ArrayList<ItemAutoBuilder> queueList) {
        Building building = itemAutoBuilder.getBuilding();
        Planet planet = itemAutoBuilder.getPlanet();
        building.setProductionTime(null);
        building.setRequiredResources(null);
        itemAutoBuilderToRemove = itemAutoBuilder;
        DataLoader.listItemAutoBuilder.getHistoryList().add(itemAutoBuilder);
        DataLoader.planets.setUpdateData(true);
        if(building.getDataTechnology().getType() == Type.PRODUCTION){
            planet.setUpdateResourceBuilding(true);
            planet.setUpdateResourcesProduction(true);
        }else
            planet.setUpdateTechnologyBuilding(true);
        //Starting buliding next object on queue list.
        ItemAutoBuilder index1 = queueList.get(1);
        index1.setStatus(Status.DATA_DOWNLOADING);
        index1.setTimer(null);
    }

    private void isFinished(ItemAutoBuilder itemAutoBuilder) {
        long timeToFinish = itemAutoBuilder.timeToFinish();
        if(timeToFinish < 0){
            itemAutoBuilder.setStatus(Status.FINISHED);
            itemAutoBuilder.getBuilding().setStatus(ogame.Status.DISABLED);
            long currentTime = System.currentTimeMillis();
            itemAutoBuilder.setStatusTime(currentTime);
            itemAutoBuilder.setFinishTime(currentTime);
        }
    }

    private void upgrade(ItemAutoBuilder itemAutoBuilder) {
        ArrayList<ItemAutoBuilder> planetQueue = DataLoader.listItemAutoBuilder.getQueueListFromPlanet(itemAutoBuilder.getPlanet());
        int indexOfList = planetQueue.indexOf(itemAutoBuilder);
        //Is on first position on queueList
        if(indexOfList == 0){
            Building building = itemAutoBuilder.getBuilding();
            Type type =  building.getDataTechnology().getType();
            Planet planet = itemAutoBuilder.getPlanet();
            int listIndex = building.getDataTechnology().getListIndex();
            //Clicking on planet
            if(!clickPlanet(planet))
                return;
            if(type == Type.PRODUCTION){
                //Suppliec clicking
                do{
                    Supplies.click(OgameWeb.webDriver);
                    Waiter.sleep(200,300);
                    if(getAntiLooping().check()){
                        getAntiLooping().reset();
                        return;
                    }
                }while(!Supplies.visible(OgameWeb.webDriver));
                //SUpgrade building
                do{
                    Supplies.upgradeBuilding(OgameWeb.webDriver,listIndex);
                    Waiter.sleep(200,300);
                    if(getAntiLooping().check()){
                        getAntiLooping().reset();
                        return;
                    }
                }while(Supplies.statusOfBuilding(OgameWeb.webDriver,listIndex) != ogame.Status.ACTIVE);

            }
            else{
                //Facilities clicking
                do{
                    Facilities.click(OgameWeb.webDriver);
                    Waiter.sleep(200,300);
                    if(getAntiLooping().check()){
                        getAntiLooping().reset();
                        return;
                    }
                }while(!Facilities.visible(OgameWeb.webDriver)); // Jest niewidoczne
                //Upgrade building
                do{
                    Facilities.upgradeBuilding(OgameWeb.webDriver,listIndex);
                    Waiter.sleep(200,300);
                    if(getAntiLooping().check()){
                        getAntiLooping().reset();
                        return;
                    }
                }while(Facilities.statusOfBuilding(OgameWeb.webDriver,listIndex) != ogame.Status.ACTIVE);

            }
            building.setStatus(ogame.Status.ACTIVE);
            AppLog.print(AutoBuilderLeafTask.class.getName(),2,"Start upgrade " + building.getName() +
                    " to level " + itemAutoBuilder.getUpgradeLevel() + ".");
            itemAutoBuilder.setStatus(Status.UPGRADING);
            long currentTime = System.currentTimeMillis();
            itemAutoBuilder.setStatusTime(currentTime);
            itemAutoBuilder.updateFinishTime(currentTime);
        }else{
            //Checks is any building is upgrading
            if(DataLoader.listItemAutoBuilder.isAnyBuildingUprading(planetQueue)){
                itemAutoBuilder.setStatus(Status.WAIT);
                long currentTime = System.currentTimeMillis();
                itemAutoBuilder.setStatusTime(currentTime);
                ItemAutoBuilder itemAutoBuilderUpgrading = DataLoader.listItemAutoBuilder.getUpgradingBuilding(planetQueue);
                itemAutoBuilder.setTimer(new Timer(currentTime,itemAutoBuilderUpgrading.getFinishTime()));
            }
        }
    }

    private void wait(ItemAutoBuilder itemAutoBuilder) {
        if(itemAutoBuilder.getTimer().isTimeLeft(System.currentTimeMillis())){
            itemAutoBuilder.setStatus(Status.DATA_DOWNLOADING);
            itemAutoBuilder.setStatusTime(System.currentTimeMillis());
            itemAutoBuilder.setTimer(null);
            Overview.click(OgameWeb.webDriver);
        }
    }

    private void checksData(ItemAutoBuilder itemAutoBuilder) {
        Building building = itemAutoBuilder.getBuilding();
        Planet planet = itemAutoBuilder.getPlanet();
        long TIME_WAIT_WHEN_OFF_STATUS = 3600 * 1000L;
        long TIME_WAIT_WHEN_ON_STATUS = 60 * 1000L;
        ArrayList<ItemAutoBuilder> planetQueue = DataLoader.listItemAutoBuilder.getQueueListFromPlanet(itemAutoBuilder.getPlanet());
        if(building.getStatus() == ogame.Status.OFF){
            itemAutoBuilder.setStatus(Status.OFF);
            long currentTime = System.currentTimeMillis();
            itemAutoBuilder.setStatusTime(currentTime);
            itemAutoBuilder.setTimer(new Timer(currentTime,currentTime+ TIME_WAIT_WHEN_OFF_STATUS));
            return;
        }
        if(building.getStatus() == ogame.Status.ON){
            if(planetQueue.indexOf(itemAutoBuilder) == 0){
                itemAutoBuilder.setStatus(Status.STARTING);
                itemAutoBuilder.setStatusTime();

            }else{
                itemAutoBuilder.setStatus(Status.WAIT);
                long currentTime = System.currentTimeMillis();
                itemAutoBuilder.setStatusTime(currentTime);
                if(DataLoader.listItemAutoBuilder.isAnyBuildingUprading(planetQueue)){
                    ItemAutoBuilder itemAutoBuilderUpgrading = DataLoader.listItemAutoBuilder.getUpgradingBuilding(planetQueue);
                    itemAutoBuilder.setTimer(new Timer(currentTime, itemAutoBuilderUpgrading.getFinishTime()));
                }else{
                    itemAutoBuilder.setTimer(new Timer(currentTime,currentTime + TIME_WAIT_WHEN_ON_STATUS));
                }
            }
            return;
        }
        if(building.getStatus() == ogame.Status.ACTIVE){
            if(DataLoader.listItemAutoBuilder.isAnyBuildingUprading(planetQueue)){
                itemAutoBuilder.setStatus(Status.WAIT);
                long currentTime = System.currentTimeMillis();
                itemAutoBuilder.setStatusTime(currentTime);
                ItemAutoBuilder itemAutoBuilderUpgrading = DataLoader.listItemAutoBuilder.getUpgradingBuilding(planetQueue);
                itemAutoBuilder.setTimer(new Timer(currentTime, itemAutoBuilderUpgrading.getFinishTime()));
                return;
            }
        }
        if(building.getStatus() == ogame.Status.DISABLED){
            if(DataLoader.listItemAutoBuilder.isAnyBuildingUprading(planetQueue)){
                itemAutoBuilder.setStatus(Status.WAIT);
                long currentTime = System.currentTimeMillis();
                itemAutoBuilder.setStatusTime(currentTime);
                ItemAutoBuilder itemAutoBuilderUpgrading = DataLoader.listItemAutoBuilder.getUpgradingBuilding(planetQueue);
                itemAutoBuilder.setTimer(new Timer(currentTime, itemAutoBuilderUpgrading.getFinishTime()));
                return;
            }
            RequiredResources requiredResources = itemAutoBuilder.getBuilding().getRequiredResources();
            ResourcesProduction resourcesProduction = itemAutoBuilder.getPlanet().getResourcesProduction();
            int metal = ResourcesBar.metal(OgameWeb.webDriver);
            int crystal = ResourcesBar.crystal(OgameWeb.webDriver);
            int deuterium = ResourcesBar.deuterium(OgameWeb.webDriver);
            int energy = ResourcesBar.energyBalanace(OgameWeb.webDriver);

            int metalBalance = requiredResources.getMetal() - metal;
            int crystalBalance = requiredResources.getCrystal() - crystal;
            int deuteriumBalance = requiredResources.getDeuterium() - deuterium;
            int energyBalance = requiredResources.getEnergy() - energy;

            planet.getResources().setMetal(metal);
            planet.getResources().setCrystal(crystal);
            planet.getResources().setDeuterium(deuterium);

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
            if(energyBalance > 0 && building.getRequiredResources().getEnergy() > 0){
                itemAutoBuilder.setStatus(Status.NOT_ENOUGH_ENERGY);
                itemAutoBuilder.setStatusTime(currentTime);
                itemAutoBuilder.setTimer(new Timer(currentTime,currentTime+ TIME_WAIT_WHEN_OFF_STATUS));
                return;
            }
            //Jeżeli pierwszy item w kolejce ma status NOT_ENOUGH_RESOURCES
            if(planetQueue.get(0).getStatus() == Status.NOT_ENOUGH_RESOURCES){
                itemAutoBuilder.setTimer(planetQueue.get(0).getTimer());
                itemAutoBuilder.setStatus(Status.WAIT);
                itemAutoBuilder.setStatusTime(currentTime);
            }
            itemAutoBuilder.setTimer(new Timer(currentTime,currentTime+timeToResourceProduction));
            itemAutoBuilder.setStatus(Status.NOT_ENOUGH_RESOURCES);
            itemAutoBuilder.setStatusTime(currentTime);
        }

        if(building.getStatus() == ogame.Status.UNDEFINED){
            itemAutoBuilder.setStatus(Status.DATA_DOWNLOADING);
            itemAutoBuilder.setStatusTime(System.currentTimeMillis());
            itemAutoBuilder.setTimer(null);
            building.setProductionTime(null);
            building.setRequiredResources(null);
        }
    }

    private void downloadData(ItemAutoBuilder itemAutoBuilder) {
        itemAutoBuilder.setStatus(Status.DATA_DOWNLOADING);
        itemAutoBuilder.setStatusTime(System.currentTimeMillis());
        Building building = itemAutoBuilder.getBuilding();
        Type type =  building.getDataTechnology().getType();
        Planet planet = itemAutoBuilder.getPlanet();
        int listIndex = building.getDataTechnology().getListIndex();
        //Clicking on planet
        if(!clickPlanet(planet))
            return;

        if(type == Type.PRODUCTION){
            //Suppliec clicking
            if(!clickSupplies())
                return;
            //Data was download on first time execute.
            if(building.getProductionTime() != null && building.getRequiredResources() != null) {
                ogame.Status status = Supplies.statusOfBuilding(OgameWeb.webDriver,listIndex);
                building.setStatus(status);
                itemAutoBuilder.setStatus(Status.DATA_DOWNLOADED);
                itemAutoBuilder.setStatusTime(System.currentTimeMillis());
                return;
            }
            //Shows building details
            if(!clickOnBuilding(type,listIndex))
                return;
//            do{
//                Supplies.clickOnBuilding(OgameWeb.webDriver,listIndex);
//                Waiter.sleep(200,300);
//                if(getAntiLooping().check()){
//                    getAntiLooping().reset();
//                    return;
//                }
//            }while(!Supplies.visibleBuildingDetails(OgameWeb.webDriver,listIndex));
            //Dowloads data
            ProductionTime productionTime = Supplies.productionTimeOfBuilding(OgameWeb.webDriver);
            RequiredResources requiredResources = Supplies.getRequiredResources(OgameWeb.webDriver,listIndex);
            int level = Supplies.levelOfBuilding(OgameWeb.webDriver,listIndex);
            ogame.Status status = Supplies.statusOfBuilding(OgameWeb.webDriver,listIndex);
            int energyConsumption = Supplies.energyConsumption(OgameWeb.webDriver);
            //When tries upgrade a upgraded building.
            if(itemAutoBuilder.isBuildingAchievedUpgradeLevel(level)){
                itemAutoBuilder.setStatus(Status.FINISHED);
                itemAutoBuilder.setStatusTime(System.currentTimeMillis());
                return;
            }
            //Update building data
            building.setProductionTime(productionTime);
            building.setRequiredResources(requiredResources);
            building.setLevel(level);
            building.setStatus(status);
            if(energyConsumption != -1)
                itemAutoBuilder.setEnergyConsumption(energyConsumption);

            AppLog.print(AutoBuilderLeafTask.class.getName(),2,"Downloads data of " + building.getName() + ". " +
                    "Upgrade level : " + (level + 1) + ", " + requiredResources +
                    (energyConsumption != -1 ? ", energy consumption: " + energyConsumption : "") + ".");
        }
        else{
            //Facilities clicking
            do{
                Facilities.click(OgameWeb.webDriver);
                Waiter.sleep(200,300);
                if(getAntiLooping().check()){
                    getAntiLooping().reset();
                    return;
                }
            }while(!Facilities.visible(OgameWeb.webDriver)); // Jest niewidoczne
            //Data was download on first time execute.
            if(building.getProductionTime() != null && building.getRequiredResources() != null) {
                ogame.Status status = Facilities.statusOfBuilding(OgameWeb.webDriver,listIndex);
                building.setStatus(status);
                itemAutoBuilder.setStatus(Status.DATA_DOWNLOADED);
                itemAutoBuilder.setStatusTime(System.currentTimeMillis());
                return;
            }
            //Shows building details
            do{
                Facilities.clickOnBuilding(OgameWeb.webDriver,building.getDataTechnology().getListIndex());
                Waiter.sleep(200,300);
                if(getAntiLooping().check()){
                    getAntiLooping().reset();
                    return;
                }
            }while(!Facilities.visibleBuildingDetails(OgameWeb.webDriver,building.getDataTechnology().getListIndex()));
            //Dowloads data
            ProductionTime productionTime = Facilities.productionTimeOfBuilding(OgameWeb.webDriver);
            RequiredResources requiredResources = Facilities.getRequiredResources(OgameWeb.webDriver,listIndex);
            int level = Facilities.levelOfBuilding(OgameWeb.webDriver,listIndex);
            ogame.Status status = Facilities.statusOfBuilding(OgameWeb.webDriver,listIndex);

            //Update building data
            building.setProductionTime(productionTime);
            building.setRequiredResources(requiredResources);
            building.setLevel(level);
            building.setStatus(status);


            AppLog.print(AutoBuilderLeafTask.class.getName(),2,"Downloads data of " + building.getName() + " . " +
                    "Upgrade level : " + (level + 1) + ", Required resources: " + requiredResources +  ".");
        }
        itemAutoBuilder.setStatus(Status.DATA_DOWNLOADED);
        itemAutoBuilder.setStatusTime(System.currentTimeMillis());
    }
}
