package app.data.planets;

import ogame.planets.Planet;

import java.util.ArrayList;

public class ComboBoxPlanet {
    final Planet PLANET;
    final String VALUE;

    public ComboBoxPlanet(Planet PLANET) {
        this.PLANET = PLANET;
        this.VALUE = this.PLANET.getCoordinate().getText();
    }

    public Planet getPlanet() {
        return PLANET;
    }

    public static ArrayList<ComboBoxPlanet> list (ArrayList<Planet> planetList){
        ArrayList<ComboBoxPlanet> list = new ArrayList<>();
        for(Planet planet : planetList)
            list.add(new ComboBoxPlanet(planet));
        return list;
    }

    @Override
    public String toString() {
        return VALUE;
    }
}
