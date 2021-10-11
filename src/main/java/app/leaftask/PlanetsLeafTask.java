package app.leaftask;

import app.data.DataLoader;
import ogame.OgameWeb;
import ogame.planets.Coordinate;
import ogame.planets.Moon;
import ogame.planets.Planet;
import ogame.planets.PlanetsList;

public class PlanetsLeafTask extends LeafTask{

    public PlanetsLeafTask(int index, long sleepms, String name, boolean run) {
        super(index, sleepms, name, run);
    }
    private boolean init = true;

    @Override
    public void execute() {
        if(isRun()){
            if(isSleepTimeOut(System.currentTimeMillis())){
                if(init){
                    init();
                }
                setLastTimeExecute(System.currentTimeMillis());
            }
        }
    }

    private void init(){
        int numberOfPlanets = PlanetsList.numberOfPlanet(OgameWeb.webDriver);

        for(int i = 1; i <= numberOfPlanets; i++){
            String id = PlanetsList.planetID(OgameWeb.webDriver,i);

            Planet planet = new Planet(id,i);

            String name = PlanetsList.nameOfPlanet(OgameWeb.webDriver,i);
            planet.setName(name);

            String coordinate = PlanetsList.coordinateOfPlanet(OgameWeb.webDriver, i);
            planet.setCoordinate(new Coordinate(coordinate));

            if(PlanetsList.planetHasMoon(OgameWeb.webDriver,i)){
                Moon moon = new Moon(i);
                planet.setMoon(moon);
            }
            DataLoader.planets.add(planet);
        }
        if(DataLoader.planets.getPlanetList().size() > 0)
            init = false;
    }

    public boolean isInited() {
        return !init;
    }
}
