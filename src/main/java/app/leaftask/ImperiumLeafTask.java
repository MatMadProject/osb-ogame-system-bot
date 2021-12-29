package app.leaftask;

import app.data.DataLoader;
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
import ogame.tabs.Facilities;
import ogame.tabs.Overview;
import ogame.tabs.ResourceSettings;
import ogame.tabs.Supplies;
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
                    planetProduction(list);
                    break;
                case 2:
                    planetProductionBuilding(list);
                    break;
                case 3:
                    planetTechnologyBuilding(list);
                    break;
                case 4:
                    if(DataLoader.researches.isUpdateData())
                        updateResearches(DataLoader.researches.getResearchList());
                    break;
            }
            DataLoader.planets.save();
        }
    }

    private void updatePlayerData() {
        //Klikanie podgląd
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
        }
        DataLoader.researches.save();
        DataLoader.researches.setUpdateData(false);
    }

    public void planetInformation(List<Planet> list){
        for(Planet planet : list){
            //Klikanie podgląd
            clickOverview();
            //Klikanie w właściwą planetę
            clickPlanet(planet);
            Waiter.sleep(500,750);
            int builtUpFields = Overview.builtUpFields(OgameWeb.webDriver);
            int maxPlanetFields = Overview.maxPlanetFields(OgameWeb.webDriver);
            int minTemperature = Overview.minTemperature(OgameWeb.webDriver);
            int maxTemperature = Overview.maxTemperature(OgameWeb.webDriver);
            int diameter = Overview.planetDiameter(OgameWeb.webDriver);
            int energy = ResourcesBar.energyBalanace(OgameWeb.webDriver);
            int metal = ResourcesBar.metal(OgameWeb.webDriver);
            int crystal = ResourcesBar.crystal(OgameWeb.webDriver);
            int deuterium = ResourcesBar.deuterium(OgameWeb.webDriver);

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
        }
    }

    public void planetProduction(List<Planet> list){
        for(Planet planet : list) {
           if(planet.isUpdateResourcesProduction()){
               //Klikanie podgląd
               do {
                   ResourceSettings.click(OgameWeb.webDriver);
                   Waiter.sleep(200, 200);
                   if (getAntiLooping().check()) {
                       getAntiLooping().reset();
                       return;
                   }
               } while (!ResourceSettings.visible(OgameWeb.webDriver)); // Jest niewidoczne

               //Klikanie w właściwą planetę
               clickPlanet(planet);
               Waiter.sleep(500,750);
               Building deutheriumSynthesizer = planet.getBuilding(DataTechnology.DEUTERIUM_SYNTHESIZER);
               int metalPerHour = ResourceSettings.metalPerHour(OgameWeb.webDriver,deutheriumSynthesizer);
               int metalPerDay = ResourceSettings.metalPerDay(OgameWeb.webDriver,deutheriumSynthesizer);
               int metalPerWeek = ResourceSettings.metalPerWeek(OgameWeb.webDriver,deutheriumSynthesizer);

               int crystalPerHour = ResourceSettings.crystalPerHour(OgameWeb.webDriver,deutheriumSynthesizer);
               int crystalPerDay = ResourceSettings.crystalPerDay(OgameWeb.webDriver,deutheriumSynthesizer);
               int crystalPerWeek = ResourceSettings.crystalPerWeek(OgameWeb.webDriver,deutheriumSynthesizer);

               int deuteriumPerHour = ResourceSettings.deuteriumPerHour(OgameWeb.webDriver,deutheriumSynthesizer);
               int deuteriumPerDay = ResourceSettings.deuteriumPerDay(OgameWeb.webDriver,deutheriumSynthesizer);
               int deuteriumPerWeek = ResourceSettings.deuteriumPerWeek(OgameWeb.webDriver,deutheriumSynthesizer);

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
                //Klikanie podgląd
                do {
                    Supplies.click(OgameWeb.webDriver);
                    Waiter.sleep(200, 200);
                    if (getAntiLooping().check()) {
                        getAntiLooping().reset();
                        return;
                    }
                } while (!Supplies.visible(OgameWeb.webDriver)); // Jest niewidoczne

                //Klikanie w właściwą planetę
                clickPlanet(planet);
                Waiter.sleep(500,750);
                for(int i = 1; i <= 10; i++) {
                    String dataTechnology = Supplies.dataTechnologyOfBuilding(OgameWeb.webDriver,i);
                    Building building = planet.getBuilding(DataTechnology.getFromValue(dataTechnology));
                    Status status = Supplies.statusOfBuilding(OgameWeb.webDriver,i);
                    String localName = Supplies.localNameOfBuilding(OgameWeb.webDriver,i);
                    int level = Supplies.levelOfBuilding(OgameWeb.webDriver, i);
                    building.setLevel(level);
                    building.setLocalName(localName);
                    building.setStatus(status);
                }
                planet.setUpdateResourceBuilding(false);
            }
        }
    }

    public void planetTechnologyBuilding(List<Planet> list){
        for(Planet planet : list) {
            if(planet.isUpdateTechnologyBuilding()){
                //Klikanie podgląd
                do {
                    Facilities.click(OgameWeb.webDriver);
                    Waiter.sleep(200, 200);
                    if (getAntiLooping().check()) {
                        getAntiLooping().reset();
                        return;
                    }
                } while (!Facilities.visible(OgameWeb.webDriver)); // Jest niewidoczne

                //Klikanie w właściwą planetę
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
                }
                planet.setUpdateTechnologyBuilding(false);
            }
        }
    }

    private void tickUpdate(){
        tick++;
        if(tick > 4){
            DataLoader.planets.setUpdateData(false);
            tick = 0;
        }
    }
}
