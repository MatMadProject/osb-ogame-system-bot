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
import ogame.tabs.Overview;
import ogame.tabs.ResourceSettings;
import ogame.tabs.Supplies;
import ogame.utils.Waiter;

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
                updateData();
                tickUpdate();
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

                    break;
                case 4:

                    break;
            }
        }
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
                if(PlanetsList.clickOnPlanet(OgameWeb.webDriver,planet.getPositionOnList()))
                    break;
                Waiter.sleep(200,200);
                if(getAntiLooping().check()){
                    getAntiLooping().reset();
                    return;
                }
                tmpPlanet = PlanetsList.selectedPlanet(OgameWeb.webDriver);
            }while(tmpPlanet == null || tmpPlanet.getId().equals(planet.getId()));

            int builtUpFields = Overview.builtUpFields(OgameWeb.webDriver);
            int maxPlanetFields = Overview.maxPlanetFields(OgameWeb.webDriver);
            int minTemperature = Overview.minTemperature(OgameWeb.webDriver);
            int maxTemperature = Overview.maxTemperature(OgameWeb.webDriver);
            int diameter = Overview.planetDiameter(OgameWeb.webDriver);

            planet.setFields(new Fields(builtUpFields,maxPlanetFields));
            planet.setTemperature(new Temperature(minTemperature, maxTemperature));
            planet.setDiameter(diameter);
            planet.setUpdateTime(System.currentTimeMillis());
        }
    }

    public void planetProduction(List<Planet> list){
        for(Planet planet : list) {
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
                if (PlanetsList.clickOnPlanet(OgameWeb.webDriver, planet.getPositionOnList()))
                    break;
                Waiter.sleep(200, 200);
                if (getAntiLooping().check()) {
                    getAntiLooping().reset();
                    return;
                }
                tmpPlanet = PlanetsList.selectedPlanet(OgameWeb.webDriver);
            } while (tmpPlanet == null || tmpPlanet.getId().equals(planet.getId()));

            int metalPerHour = ResourceSettings.metalPerHour(OgameWeb.webDriver);
            int metalPerDay = ResourceSettings.metalPerDay(OgameWeb.webDriver);
            int metalPerWeek = ResourceSettings.metalPerWeek(OgameWeb.webDriver);

            int crystalPerHour = ResourceSettings.crystalPerHour(OgameWeb.webDriver);
            int crystalPerDay = ResourceSettings.crystalPerDay(OgameWeb.webDriver);
            int crystalPerWeek = ResourceSettings.crystalPerWeek(OgameWeb.webDriver);

            int deuteriumPerHour = ResourceSettings.deuteriumPerHour(OgameWeb.webDriver);
            int deuteriumPerDay = ResourceSettings.deuteriumPerDay(OgameWeb.webDriver);
            int deuteriumPerWeek = ResourceSettings.deuteriumPerWeek(OgameWeb.webDriver);

            planet.getResourcesProduction().setMetalPerHour(metalPerHour);
            planet.getResourcesProduction().setMetalPerDay(metalPerDay);
            planet.getResourcesProduction().setMetalPerWeek(metalPerWeek);
            planet.getResourcesProduction().setCrystalPerHour(crystalPerHour);
            planet.getResourcesProduction().setCrystalPerDay(crystalPerDay);
            planet.getResourcesProduction().setCrystalPerWeek(crystalPerWeek);
            planet.getResourcesProduction().setDeuteriumPerHour(deuteriumPerHour);
            planet.getResourcesProduction().setDeuteriumPerDay(deuteriumPerDay);
            planet.getResourcesProduction().setDeuteriumPerWeek(deuteriumPerWeek);
        }
    }

    public void planetProductionBuilding(List<Planet> list){
        for(Planet planet : list) {
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
                if (PlanetsList.clickOnPlanet(OgameWeb.webDriver, planet.getPositionOnList()))
                    break;
                Waiter.sleep(200, 200);
                if (getAntiLooping().check()) {
                    getAntiLooping().reset();
                    return;
                }
                tmpPlanet = PlanetsList.selectedPlanet(OgameWeb.webDriver);
            } while (tmpPlanet == null || tmpPlanet.getId().equals(planet.getId()));

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
        }
    }

    private void tickUpdate(){
        tick++;
        if(tick > 3)
            tick = 0;
    }

}
