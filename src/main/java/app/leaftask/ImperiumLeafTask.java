package app.leaftask;

import app.data.DataLoader;
import app.data.planets.Planets;
import ogame.OgameWeb;
import ogame.planets.Fields;
import ogame.planets.Planet;
import ogame.planets.PlanetsList;
import ogame.planets.Temperature;
import ogame.tabs.Overview;
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
                    break;
                case 2:

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
                Waiter.sleep(100,100);
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
                Waiter.sleep(100,100);
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

    private void tickUpdate(){
        tick++;
        if(tick > 3)
            tick = 0;
    }

}
