package app.leaftask;

import app.data.DataLoader;
import app.data.planets.Planets;
import ogame.OgameWeb;
import ogame.Status;
import ogame.buildings.Building;
import ogame.buildings.DataTechnology;
import ogame.planets.Fields;
import ogame.planets.Planet;
import ogame.planets.PlanetsList;
import ogame.planets.Temperature;
import ogame.researches.Research;
import ogame.researches.Type;
import ogame.tabs.Facilities;
import ogame.tabs.Overview;
import ogame.tabs.ResourceSettings;
import ogame.tabs.Supplies;
import ogame.utils.Waiter;

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
            do{
                Overview.click(OgameWeb.webDriver);
                Waiter.sleep(200,300);
                if(getAntiLooping().check()){
                    getAntiLooping().reset();
                    return;
                }
            }while(!Overview.visible(OgameWeb.webDriver)); // Jest niewidoczne

            //Klikanie w właściwą planetę
            Planet tmpPlanet;
            do{
                if(PlanetsList.clickOnPlanet(OgameWeb.webDriver,planet.getPositionOnList())){
                    tmpPlanet = PlanetsList.selectedPlanet(OgameWeb.webDriver);
                    break;
                }
                Waiter.sleep(200,200);
                if(getAntiLooping().check()){
                    getAntiLooping().reset();
                    return;
                }
                tmpPlanet = PlanetsList.selectedPlanet(OgameWeb.webDriver);
            }while(tmpPlanet == null || tmpPlanet.getId().equals(planet.getId()));
            //Planet doesn't exist on planet list, maybe was deleted.
            if(tmpPlanet == null)
                return;
            Waiter.sleep(500,750);
            int builtUpFields = Overview.builtUpFields(OgameWeb.webDriver);
            int maxPlanetFields = Overview.maxPlanetFields(OgameWeb.webDriver);
            int minTemperature = Overview.minTemperature(OgameWeb.webDriver);
            int maxTemperature = Overview.maxTemperature(OgameWeb.webDriver);
            int diameter = Overview.planetDiameter(OgameWeb.webDriver);

            planet.setFields(new Fields(builtUpFields,maxPlanetFields));
            if(planet.getTemperature() == null)
                planet.setTemperature(new Temperature(minTemperature, maxTemperature));
            if(planet.getDiameter() == 0)
                planet.setDiameter(diameter);
            planet.setUpdateTime(System.currentTimeMillis());
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
               Planet tmpPlanet;
               do {
                   if (PlanetsList.clickOnPlanet(OgameWeb.webDriver, planet.getPositionOnList())){
                       tmpPlanet = PlanetsList.selectedPlanet(OgameWeb.webDriver);
                       break;
                   }
                   Waiter.sleep(200, 200);
                   if (getAntiLooping().check()) {
                       getAntiLooping().reset();
                       return;
                   }
                   tmpPlanet = PlanetsList.selectedPlanet(OgameWeb.webDriver);
               } while (tmpPlanet == null || tmpPlanet.getId().equals(planet.getId()));
               //Planet doesn't exist on planet list, maybe was deleted.
               if(tmpPlanet == null)
                   return;
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
                Planet tmpPlanet;
                do {
                    if (PlanetsList.clickOnPlanet(OgameWeb.webDriver, planet.getPositionOnList())){
                        tmpPlanet = PlanetsList.selectedPlanet(OgameWeb.webDriver);
                        break;
                    }
                    Waiter.sleep(200, 200);
                    if (getAntiLooping().check()) {
                        getAntiLooping().reset();
                        return;
                    }
                    tmpPlanet = PlanetsList.selectedPlanet(OgameWeb.webDriver);
                } while (tmpPlanet == null || tmpPlanet.getId().equals(planet.getId()));
                //Planet doesn't exist on planet list, maybe was deleted.
                if(tmpPlanet == null)
                    return;
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
                Planet tmpPlanet;
                do {
                    if (PlanetsList.clickOnPlanet(OgameWeb.webDriver, planet.getPositionOnList())){
                        tmpPlanet = PlanetsList.selectedPlanet(OgameWeb.webDriver);
                        break;
                    }
                    Waiter.sleep(200, 200);
                    if (getAntiLooping().check()) {
                        getAntiLooping().reset();
                        return;
                    }
                    tmpPlanet = PlanetsList.selectedPlanet(OgameWeb.webDriver);
                } while (tmpPlanet == null || tmpPlanet.getId().equals(planet.getId()));
                //Planet doesn't exist on planet list, maybe was deleted.
                if(tmpPlanet == null)
                    return;
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
