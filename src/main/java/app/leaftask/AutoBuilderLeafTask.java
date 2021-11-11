package app.leaftask;

import app.data.DataLoader;
import app.data.autobuilder.ItemAutoBuilder;
import app.data.autobuilder.Status;
import ogame.OgameWeb;
import ogame.ResourcesBar;
import ogame.buildings.Building;
import ogame.buildings.RequiredResources;
import ogame.planets.Planet;
import ogame.planets.PlanetsList;
import ogame.planets.ResourcesProduction;
import ogame.planets.Type;
import ogame.tabs.Facilities;
import ogame.tabs.Supplies;
import ogame.utils.Waiter;
import ogame.utils.log.AppLog;
import ogame.utils.watch.Timer;
import ogame.watch.ProductionTime;
import org.jetbrains.annotations.NotNull;

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
                    for(ItemAutoBuilder itemAutoBuilder :queueList){
                        final Status STATUS = itemAutoBuilder.getStatus();
                        if(STATUS == Status.ADDED)
                            downloadData(itemAutoBuilder);
                        if(STATUS == Status.DATA_DOWNLOADING)
                            downloadData(itemAutoBuilder);
                        if(STATUS == Status.WAIT)
                            wait(itemAutoBuilder);
                        if(STATUS == Status.NOT_ENOUGH_RESOURCES || itemAutoBuilder.getStatus() == Status.DISABLED)
                            wait(itemAutoBuilder);
                        if(STATUS == Status.NOT_ENOUGH_ENERGY)
                            itemAutoBuilder.setStatus(Status.STARTING);
                        if(STATUS == Status.STARTING)
                            upgrade(itemAutoBuilder);
                        if(STATUS == Status.FINISHED) {
                            itemAutoBuilderToRemove = itemAutoBuilder;
                            DataLoader.listItemAutoBuilder.getHistoryList().add(itemAutoBuilder);
                            DataLoader.planets.setUpdateData(true);
                        }
                    }
                    for(ItemAutoBuilder itemAutoBuilder :queueList){
                        final Status STATUS = itemAutoBuilder.getStatus();
                        if(STATUS == Status.DATA_DOWNLOADED)
                            checksData(itemAutoBuilder);
                        if(STATUS == Status.UPGRADING)
                            isFinished(itemAutoBuilder);
                    }
                    if(itemAutoBuilderToRemove != null){
                        queueList.remove(itemAutoBuilderToRemove);
                        itemAutoBuilderToRemove = null;
                    }
                }
                setLastTimeExecute(System.currentTimeMillis());
            }
        }
    }

    private void wait(ItemAutoBuilder itemAutoBuilder) {
        if(itemAutoBuilder.getTimer().isTimeLeft(System.currentTimeMillis())){
            itemAutoBuilder.setStatus(Status.DATA_DOWNLOADING);
            itemAutoBuilder.setStatusTime(System.currentTimeMillis());
            itemAutoBuilder.setTimer(null);
        }
    }

    private void isFinished(@NotNull ItemAutoBuilder itemAutoBuilder) {
        if(itemAutoBuilder.timeToFinish() < 0){
            itemAutoBuilder.setStatus(Status.FINISHED);
            long currentTime = System.currentTimeMillis();
            itemAutoBuilder.setStatusTime(currentTime);
            itemAutoBuilder.setFinishTime(currentTime);
        }
    }

    private void upgrade(ItemAutoBuilder itemAutoBuilder) {
        Building building = itemAutoBuilder.getBuilding();
        Type type =  building.getDataTechnology().getType();
        Planet planet = itemAutoBuilder.getPlanet();
        int listIndex = building.getDataTechnology().getListIndex();
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
            //Shows building details
            do{
                Supplies.upgradeBuilding(OgameWeb.webDriver,listIndex);
                Waiter.sleep(200,300);
                if(getAntiLooping().check()){
                    getAntiLooping().reset();
                    return;
                }
            }while(Supplies.statusOfBuilding(OgameWeb.webDriver,listIndex) != ogame.Status.ACTIVE);

            ogame.Status status = Supplies.statusOfBuilding(OgameWeb.webDriver,listIndex);
            building.setStatus(status);

            AppLog.print(AutoBuilderLeafTask.class.getName(),2,"Start upgrade " + building.getName() +
                    " to level " + itemAutoBuilder.getUpgradeLevel() + ".");
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
            //Shows building details
            do{
                Facilities.upgradeBuilding(OgameWeb.webDriver,listIndex);
                Waiter.sleep(200,300);
                if(getAntiLooping().check()){
                    getAntiLooping().reset();
                    return;
                }
            }while(Facilities.statusOfBuilding(OgameWeb.webDriver,listIndex) != ogame.Status.ACTIVE);
            //Dowloads data
            ogame.Status status = Facilities.statusOfBuilding(OgameWeb.webDriver,listIndex);

            building.setStatus(status);

            AppLog.print(AutoBuilderLeafTask.class.getName(),2,"Start upgrade " + building.getName() +
                    " to level " + itemAutoBuilder.getUpgradeLevel() + ".");
        }
        itemAutoBuilder.setStatus(Status.UPGRADING);
        long currentTime = System.currentTimeMillis();
        itemAutoBuilder.setStatusTime(currentTime);
        itemAutoBuilder.updateFinishTime(currentTime);
    }

    public void downloadData(ItemAutoBuilder itemAutoBuilder){
        itemAutoBuilder.setStatus(Status.DATA_DOWNLOADING);
        itemAutoBuilder.setStatusTime(System.currentTimeMillis());
        Building building = itemAutoBuilder.getBuilding();
        Type type =  building.getDataTechnology().getType();
        Planet planet = itemAutoBuilder.getPlanet();
        int listIndex = building.getDataTechnology().getListIndex();

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

            //Shows building details
            do{
                Supplies.clickOnBuilding(OgameWeb.webDriver,listIndex);
                Waiter.sleep(200,300);
                if(getAntiLooping().check()){
                    getAntiLooping().reset();
                    return;
                }
            }while(!Supplies.visibleBuildingDetails(OgameWeb.webDriver,listIndex));
            //Dowloads data
            ProductionTime productionTime = Supplies.productionTimeOfBuilding(OgameWeb.webDriver);
            RequiredResources requiredResources = Supplies.getRequiredResources(OgameWeb.webDriver,listIndex);
            int level = Supplies.levelOfBuilding(OgameWeb.webDriver,listIndex);
            ogame.Status status = Supplies.statusOfBuilding(OgameWeb.webDriver,listIndex);
            int energyConsumption = Supplies.energyConsumption(OgameWeb.webDriver);

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

    public void checksData(ItemAutoBuilder itemAutoBuilder){
        Building building = itemAutoBuilder.getBuilding();
        itemAutoBuilder.setStatusTime(System.currentTimeMillis());
        if(building.getStatus() == ogame.Status.ON){
            int energyBalance = ResourcesBar.energyBalanace(OgameWeb.webDriver);
            if(energyBalance <= itemAutoBuilder.getEnergyConsumption()){
                itemAutoBuilder.setStatus(Status.NOT_ENOUGH_ENERGY);
                return;
            }
            if(DataLoader.listItemAutoBuilder.getQueueList().indexOf(itemAutoBuilder) == 0)
                itemAutoBuilder.setStatus(Status.STARTING);
            else
                itemAutoBuilder.setStatus(Status.DATA_DOWNLOADING);
        }

        if(building.getStatus() == ogame.Status.DISABLED){
            if(DataLoader.listItemAutoBuilder.isAnyBuildingUprading()){
                itemAutoBuilder.setStatus(Status.WAIT);
                ItemAutoBuilder itemAutoBuilderUpgrading = DataLoader.listItemAutoBuilder.getUpgradingBuilding();
                itemAutoBuilder.setTimer(new Timer(System.currentTimeMillis(),itemAutoBuilderUpgrading.getFinishTime()));
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
                itemAutoBuilder.setStatus(Status.NOT_ENOUGH_ENERGY);
                itemAutoBuilder.setStatusTime(System.currentTimeMillis());
            }

            long currentTime = System.currentTimeMillis();
            itemAutoBuilder.setTimer(new Timer(currentTime,currentTime+timeToResourceProduction));
            itemAutoBuilder.setStatus(Status.NOT_ENOUGH_RESOURCES);
            itemAutoBuilder.setStatusTime(System.currentTimeMillis());
            return;
        }
        if(building.getStatus() == ogame.Status.OFF){
            itemAutoBuilder.setStatus(Status.DISABLED);
        }
    }
}
