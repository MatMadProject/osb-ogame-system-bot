package app.data.planets;

import ogame.planets.Moon;
import ogame.planets.Planet;

import java.util.ArrayList;

public class ComboBoxPlanet {
    final Object object;
    final String VALUE;

    public ComboBoxPlanet(Object galaxyObject) {
        this.object = galaxyObject;
        if(this.object instanceof Planet)
            this.VALUE = ((Planet)this.object).getCoordinate().getText();
        else
            this.VALUE = ((Moon)this.object).getCoordinate().getText()+" [M]";
    }

    public Object getObject() {
        return object;
    }

    public static ArrayList<ComboBoxPlanet> planetList(ArrayList<Planet> planetList){
        ArrayList<ComboBoxPlanet> list = new ArrayList<>();
        for(Planet planet : planetList)
            list.add(new ComboBoxPlanet(planet));
        return list;
    }
    public static ArrayList<ComboBoxPlanet> planetAndMoonList(ArrayList<Planet> planetList){
        ArrayList<ComboBoxPlanet> list = new ArrayList<>();
        for(Planet planet : planetList){
            list.add(new ComboBoxPlanet(planet));
            if(planet.getMoon() != null)
                list.add(new ComboBoxPlanet(planet.getMoon()));
        }
        return list;
    }

    @Override
    public String toString() {
        return VALUE;
    }
}
