package app.leaftask;

import app.data.DataLoader;
import app.data.planets.Planets;
import ogame.OgameWeb;
import ogame.planets.Coordinate;
import ogame.planets.Moon;
import ogame.planets.Planet;
import ogame.planets.PlanetsList;

import java.util.ArrayList;

public class PlanetsLeafTask extends LeafTask{

    public PlanetsLeafTask(int index, long sleepms, String name, boolean run) {
        super(index, sleepms, name, run);
    }
    private ArrayList<Planet> currentPlanetList = new ArrayList<>();
    @Override
    public void execute() {
        if(isRun()){
            if(isSleepTimeOut(System.currentTimeMillis())){
                if(DataLoader.planets.isInitPlanetList())
                    init();
                setLastTimeExecute(System.currentTimeMillis());
            }
        }
    }

    private void init(){
        int numberOfPlanets = PlanetsList.numberOfPlanet(OgameWeb.webDriver);
        Planets planets = DataLoader.planets;
        for(int i = 1; i <= numberOfPlanets; i++){
            String id = PlanetsList.planetID(OgameWeb.webDriver,i);

            Planet planet = new Planet(id,i);

            String name = PlanetsList.nameOfPlanet(OgameWeb.webDriver,i);
            planet.setName(name);

            String coordinate = PlanetsList.coordinateOfPlanet(OgameWeb.webDriver, i);
            planet.setCoordinate(new Coordinate(coordinate));
            Moon moon;
            if(PlanetsList.planetHasMoon(OgameWeb.webDriver,i)){
                moon = new Moon(i);
                moon.setCoordinate(new Coordinate(coordinate));
                planet.setMoon(moon);
            }
            //Update data planet: name and position on list
            Planet tmpPlanet = planets.getPlanet(planet);
            if(tmpPlanet != null) {
                tmpPlanet.setName(name);
                tmpPlanet.setPositionOnList(i);
            }
            planets.add(planet);
            currentPlanetList.add(planet);
        }

        //Removing deleted planet
        if(!currentPlanetList.isEmpty()){
            ArrayList<Planet> planetToDelete = getPlanetToDeleteFromFile(currentPlanetList,planets.getPlanetList());
            for(Planet planet : planetToDelete)
                planets.remove(planet);

            if(planets.getPlanetList().size() > 0) {
                planets.setInitPlanetList(false);
                planets.save();
            }
            currentPlanetList.clear();
        }
        planets.activeAllUpdateFlag();
    }

     private ArrayList<Planet> getPlanetToDeleteFromFile(ArrayList<Planet> currentPlanetList, ArrayList<Planet>listFromFile){
        ArrayList<Planet> list = (ArrayList<Planet>) listFromFile.clone();

        for(Planet planet : currentPlanetList)
            if(listFromFile.contains(planet))
                list.remove(planet);

         return list;
    }
}
