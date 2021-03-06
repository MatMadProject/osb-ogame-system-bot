package app.leaftask;

import app.data.DataLoader;
import app.data.local.LocalName;
import app.data.planets.ColonyData;
import app.data.planets.ColonyDataItem;
import app.data.planets.Planets;
import app.data.player.PlayerData;
import app.data.player.PlayerDataItem;
import ogame.OgameWeb;
import ogame.ResourcesBar;
import ogame.Status;
import ogame.buildings.Building;
import ogame.DataTechnology;
import ogame.planets.*;
import ogame.researches.Research;
import ogame.Type;
import ogame.tabs.*;
import ogame.utils.Waiter;
import ogame.utils.watch.Date;
import ogame.utils.watch.Time;

import java.util.ArrayList;
import java.util.List;

public class ImperiumLeafTask extends LeafTask{

    public ImperiumLeafTask(int index, long sleepms, String name) {
        super(index, sleepms, name);
    }
    private int tick = 0;

    @Override
    public void execute() {
        if(isRun()){
            if(isSleepTimeOut(System.currentTimeMillis())){
                if(DataLoader.planets.isUpdateData()) {
                    updateData();
                    tickUpdate();
                }
                if(DataLoader.playerData.timeLeftToNextExecute().isTimeLeft(System.currentTimeMillis()))
                    updatePlayerData();
                if(DataLoader.researches.isUpdateData())
                    updateResearches(DataLoader.researches.getResearchList());
                if(!DataLoader.localNameGameObjects.isDataLoaded()){
                    setShipLocalName();
                    setDefenceLocalName();
                }

                setLastTimeExecute(System.currentTimeMillis());
            }
        }
    }


    private void updateData(){
        Planets planets = DataLoader.planets;
        if(planets != null){
            List<Planet> list = planets.getPlanetList();
            switch (tick){
                case 0:
                    planetInformation(list);
                    break;
                case 1:
                    planetTechnologyBuilding(list);
                    break;
                case 2:
                    planetProductionBuilding(list);
                    break;
                case 3:
                    planetProduction(list);
                    break;
            }
            DataLoader.planets.save();
        }
    }

    private void setDefenceLocalName() {
        if(!DataLoader.localNameGameObjects.isDefenceLocalNameLoaded()){
            List<DataTechnology> defence = DataTechnology.dataTechnologyList(Type.DEFENCE);
            if(!clickDefence())
                return;
            for(DataTechnology dataTechnology : defence){
                String localName = Defence.localNameOfDefence(OgameWeb.webDriver,dataTechnology);
                DataLoader.localNameGameObjects.addLocalName(new LocalName(localName,dataTechnology));
            }
            DataLoader.localNameGameObjects.setDefenceLocalNameLoaded();
        }
    }

    private void setShipLocalName() {
        if(!DataLoader.localNameGameObjects.isShipsLocalNameLoaded()){
            List<DataTechnology> batlleShips = DataTechnology.dataTechnologyList(Type.BATTLE);
            List<DataTechnology> civilShips = DataTechnology.dataTechnologyList(Type.CIVIL);
            if(!clickShipyard())
                return;
            for(DataTechnology dataTechnology : batlleShips){
                String localName = Shipyard.localNameOfShip(OgameWeb.webDriver,dataTechnology);
                DataLoader.localNameGameObjects.addLocalName(new LocalName(localName,dataTechnology));
            }
            for(DataTechnology dataTechnology : civilShips){
                String localName = Shipyard.localNameOfShip(OgameWeb.webDriver,dataTechnology);
                DataLoader.localNameGameObjects.addLocalName(new LocalName(localName,dataTechnology));
            }
            DataLoader.localNameGameObjects.setShipsLocalNameLoaded();
        }
    }

    private void updatePlayerData() {
        //Klikanie podgl??d
        if(!clickOverview())
            return;
        Waiter.sleep(250,250);
        int position = Overview.position(OgameWeb.webDriver);
        int points = Overview.points(OgameWeb.webDriver);
        int honorPoints = Overview.honorPoints(OgameWeb.webDriver);
        long currentTime = System.currentTimeMillis();
        PlayerData playerData = DataLoader.playerData;
        PlayerDataItem playerDataItem = new PlayerDataItem(currentTime, Time.get(currentTime), Date.get(currentTime),
                points,position,honorPoints);
        if(position != -1)
            playerData.save(playerDataItem);
    }

    private void updateResearches(ArrayList<Research> researchList) {
        //Klikanie badania
        do{
            ogame.tabs.Research.click(OgameWeb.webDriver);
            Waiter.sleep(200,300);
            if(getAntiLooping().check()){
                getAntiLooping().reset();
                return;
            }
        }while(!ogame.tabs.Research.visible(OgameWeb.webDriver)); // Jest niewidoczne
        // Data downloads
        for(Research research : researchList){
            int listIndex = research.getDataTechnology().getListIndex();
            Type type = research.getDataTechnology().getType();
            int level = ogame.tabs.Research.levelOfResearch(OgameWeb.webDriver,listIndex, type);
            String localName = ogame.tabs.Research.localNameOfResearch(OgameWeb.webDriver,listIndex, type);
            Status status = ogame.tabs.Research.statusOfResearch(OgameWeb.webDriver,listIndex, type);
            research.setLevel(level);
            research.setLocalName(localName);
            research.setStatus(status);
            if(!DataLoader.localNameGameObjects.isResearchesLocalNameLoaded()){
                DataLoader.localNameGameObjects.addLocalName(new LocalName(localName,research.getDataTechnology()));
            }
        }
        if(!DataLoader.localNameGameObjects.isResearchesLocalNameLoaded())
            DataLoader.localNameGameObjects.setResearchesLocalNameLoaded();
        DataLoader.researches.save();
        DataLoader.researches.setUpdateData(false);
    }

    public void planetInformation(List<Planet> list){
        for(Planet planet : list){
            if(planet.isUpdatePlanetInformation()){
                //Klikanie podgl??d
                clickOverview();
                //Klikanie w w??a??ciw?? planet??
                clickPlanet(planet);
                Waiter.sleep(500,750);
                int builtUpFields = Overview.builtUpFields(OgameWeb.webDriver);
                int maxPlanetFields = Overview.maxPlanetFields(OgameWeb.webDriver);
                int minTemperature = Overview.minTemperature(OgameWeb.webDriver);
                int maxTemperature = Overview.maxTemperature(OgameWeb.webDriver);
                int diameter = Overview.planetDiameter(OgameWeb.webDriver);
                long energy = ResourcesBar.energyBalanace(OgameWeb.webDriver);
                long metal = ResourcesBar.metal(OgameWeb.webDriver);
                long crystal = ResourcesBar.crystal(OgameWeb.webDriver);
                long deuterium = ResourcesBar.deuterium(OgameWeb.webDriver);

                planet.setFields(new Fields(builtUpFields,maxPlanetFields));
                planet.getResources().setEnergy(energy);
                planet.getResources().setMetal(metal);
                planet.getResources().setCrystal(crystal);
                planet.getResources().setDeuterium(deuterium);

                if(planet.getTemperature() == null)
                    planet.setTemperature(new Temperature(minTemperature, maxTemperature));
                if(planet.getDiameter() == 0)
                    planet.setDiameter(diameter);
                planet.setUpdateTime(System.currentTimeMillis());
                if(!planet.isColonyDataAdded()){
                    ColonyDataItem colonyDataItem = new ColonyDataItem(planet);
                    ColonyData colonyData = DataLoader.colonyData;
                    if(colonyData.add(colonyDataItem))
                        colonyData.save(colonyDataItem);

                    planet.setColonyDataAdded();
                }
                planet.setUpdatePlanetInformation(false);
            }
        }
    }

    public void planetProduction(List<Planet> list){
        for(Planet planet : list) {
           if(planet.isUpdateResourcesProduction()){
               //Klikanie podgl??d
               do {
                   ResourceSettings.click(OgameWeb.webDriver);
                   Waiter.sleep(200, 200);
                   if (getAntiLooping().check()) {
                       getAntiLooping().reset();
                       return;
                   }
               } while (!ResourceSettings.visible(OgameWeb.webDriver)); // Jest niewidoczne

               //Klikanie w w??a??ciw?? planet??
               clickPlanet(planet);
               Waiter.sleep(500,750);
               Building deuteriumSynthesizer = planet.getBuilding(DataTechnology.DEUTERIUM_SYNTHESIZER);
               int metalPerHour = ResourceSettings.metalPerHour(OgameWeb.webDriver,deuteriumSynthesizer);
               int metalPerDay = ResourceSettings.metalPerDay(OgameWeb.webDriver,deuteriumSynthesizer);
               int metalPerWeek = ResourceSettings.metalPerWeek(OgameWeb.webDriver,deuteriumSynthesizer);

               int crystalPerHour = ResourceSettings.crystalPerHour(OgameWeb.webDriver,deuteriumSynthesizer);
               int crystalPerDay = ResourceSettings.crystalPerDay(OgameWeb.webDriver,deuteriumSynthesizer);
               int crystalPerWeek = ResourceSettings.crystalPerWeek(OgameWeb.webDriver,deuteriumSynthesizer);

               int deuteriumPerHour = ResourceSettings.deuteriumPerHour(OgameWeb.webDriver,deuteriumSynthesizer);
               int deuteriumPerDay = ResourceSettings.deuteriumPerDay(OgameWeb.webDriver,deuteriumSynthesizer);
               int deuteriumPerWeek = ResourceSettings.deuteriumPerWeek(OgameWeb.webDriver,deuteriumSynthesizer);

               planet.getResourcesProduction().setMetalPerHour(metalPerHour);
               planet.getResourcesProduction().setMetalPerDay(metalPerDay);
               planet.getResourcesProduction().setMetalPerWeek(metalPerWeek);
               planet.getResourcesProduction().setCrystalPerHour(crystalPerHour);
               planet.getResourcesProduction().setCrystalPerDay(crystalPerDay);
               planet.getResourcesProduction().setCrystalPerWeek(crystalPerWeek);
               planet.getResourcesProduction().setDeuteriumPerHour(deuteriumPerHour);
               planet.getResourcesProduction().setDeuteriumPerDay(deuteriumPerDay);
               planet.getResourcesProduction().setDeuteriumPerWeek(deuteriumPerWeek);

               planet.setUpdateResourcesProduction(false);
           }
        }
    }

    public void planetProductionBuilding(List<Planet> list){
        for(Planet planet : list) {
            if(planet.isUpdateResourceBuilding()){
                //Klikanie podgl??d
                do {
                    Supplies.click(OgameWeb.webDriver);
                    Waiter.sleep(200, 200);
                    if (getAntiLooping().check()) {
                        getAntiLooping().reset();
                        return;
                    }
                } while (!Supplies.visible(OgameWeb.webDriver)); // Jest niewidoczne

                //Klikanie w w??a??ciw?? planet??
                clickPlanet(planet);
                Waiter.sleep(500,750);
                for(int i = 1; i <= 10; i++) {
                    String dataTechnology = Supplies.dataTechnologyOfBuilding(OgameWeb.webDriver,i);
                    Building building = planet.getBuilding(DataTechnology.getFromValue(dataTechnology));
                    Status status = Supplies.statusOfBuilding(OgameWeb.webDriver,i);
                    String localName = Supplies.localNameOfBuilding(OgameWeb.webDriver,i);
                    int level = Supplies.levelOfBuilding(OgameWeb.webDriver, i);
                    if(building != null){
                        building.setLevel(level);
                        building.setLocalName(localName);
                        building.setStatus(status);
                    }
                    if(!DataLoader.localNameGameObjects.isProductionBuildingsLocalNameLoaded())
                        DataLoader.localNameGameObjects.addLocalName(new LocalName(localName,building.getDataTechnology()));

                }
                if(!DataLoader.localNameGameObjects.isProductionBuildingsLocalNameLoaded())
                    DataLoader.localNameGameObjects.setProductionBuildingsLocalNameLoaded();
                planet.setUpdateResourceBuilding(false);
            }
        }
    }

    public void planetTechnologyBuilding(List<Planet> list){
        for(Planet planet : list) {
            if(planet.isUpdateTechnologyBuilding()){
                //Klikanie podgl??d
                do {
                    Facilities.click(OgameWeb.webDriver);
                    Waiter.sleep(200, 200);
                    if (getAntiLooping().check()) {
                        getAntiLooping().reset();
                        return;
                    }
                } while (!Facilities.visible(OgameWeb.webDriver)); // Jest niewidoczne

                //Klikanie w w??a??ciw?? planet??
                clickPlanet(planet);
                Waiter.sleep(500,750);
                for(int i = 1; i <= 8; i++) {
                    String dataTechnology = Facilities.dataTechnologyOfBuilding(OgameWeb.webDriver,i);
                    Building building = planet.getBuilding(DataTechnology.getFromValue(dataTechnology));
                    Status status = Facilities.statusOfBuilding(OgameWeb.webDriver,i);
                    String localName = Facilities.localNameOfBuilding(OgameWeb.webDriver,i);
                    int level = Facilities.levelOfBuilding(OgameWeb.webDriver, i);
                    building.setLevel(level);
                    building.setLocalName(localName);
                    building.setStatus(status);
                    if(!DataLoader.localNameGameObjects.isTechnologyBuildingsLocalNameLoaded())
                        DataLoader.localNameGameObjects.addLocalName(new LocalName(localName,building.getDataTechnology()));

                }
                if(!DataLoader.localNameGameObjects.isTechnologyBuildingsLocalNameLoaded())
                    DataLoader.localNameGameObjects.setTechnologyBuildingsLocalNameLoaded();
                planet.setUpdateTechnologyBuilding(false);
            }
        }
    }

    private void tickUpdate(){
        tick++;
        if(tick > 3){
            DataLoader.planets.setUpdateData(false);
            tick = 0;
        }
    }
}
