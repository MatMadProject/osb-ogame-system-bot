package app.data.planets;

import app.data.LSD;
import app.data.MatMadFile;
import app.data.StaticStrings;
import ogame.planets.Coordinate;
import ogame.planets.Planet;
import ogame.utils.log.AppLog;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Planets implements LSD {

    private List<Planet> planetList = new ArrayList<>();

    public Planets() {
        load();
    }

    @Override
    public boolean load() {
        String path = StaticStrings.PLAYER_FOLDER_PATH +
                StaticStrings.PLANETS_FILE +StaticStrings.FILE_EXTEND;

        if(MatMadFile.isFileExists(path)) {
            File file = new File(path);

            FileInputStream f;
            try {
                f = new FileInputStream(file);
                ObjectInputStream o = new ObjectInputStream(f);

                Planets object = (Planets) o.readObject();
                this.planetList = object.getPlanetList();

                o.close();
                f.close();

                return true;
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public boolean save() {
        String folder = StaticStrings.PLAYER_FOLDER_PATH;

        String path = StaticStrings.PLAYER_FOLDER_PATH +
                StaticStrings.PLANETS_FILE +StaticStrings.FILE_EXTEND;

        try {
            if(MatMadFile.isFolderExists(folder)) {
                FileOutputStream f = new FileOutputStream(new File(path));
                ObjectOutputStream o = new ObjectOutputStream(f);

                o.writeObject(this);
                o.close();
                f.close();
                AppLog.print(Planets.class.getName(),0,"Data saved.");
                return true;
            }
            else {
                AppLog.print(Planets.class.getName(),1,"When try data save.");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Planet> getPlanetList() {
        return planetList;
    }

    /**
     * Adds new planet to list.
     * @param planet Adding planet.
     */
    public void add(Planet planet) {
        if(!exists(planet)) {
            planetList.add(planet);
            AppLog.print(Planets.class.getName(),0,"Added new planet. " + planet.getCoordinate().getText());
        }
        else
            AppLog.print(Planets.class.getName(),0,"Planet " + planet.getCoordinate().getText() + "  exists on list.");
    }

    /**
     * Checks the planet exist on list.
     * @param planet Serachinf planet.
     * @return If exist returns true.
     */
    private boolean exists(Planet planet) {
        if(planetList.size() > 0) {
            for(Planet p : planetList) {
                if(p.getId().equals(planet.getId()))
                    return true;
            }
        }
        return false;
    }

    /**
     * Removes planes from list.
     * @param planet Removing planet.
     */
    public void remove(Planet planet) {
        planetList.remove(planet);
        AppLog.print(Planets.class.getName(),0,"Removed planet. " + planet.getCoordinate().getText());
    }

    /**
     * Downloads planet from list.
     * @param coordinate Planet coordinate
     */
    public Planet getPlanet(Coordinate coordinate) {
        for(Planet p : planetList) {
            if(coordinate.equals(p.getCoordinate())) {
                return p;
            }
        }
        return null;
    }
}
