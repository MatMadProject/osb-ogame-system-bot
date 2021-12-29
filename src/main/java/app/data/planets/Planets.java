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

public class Planets implements LSD, Serializable {

    private static final long serialVersionUID = 1992L;
    private ArrayList<Planet> planetList = new ArrayList<>();
    private boolean updateData = true;
    private boolean initPlanetList = true;

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
                AppLog.print(Planets.class.getName(),1,"When try data load.");
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
                FileOutputStream f = new FileOutputStream(path);
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

    public ArrayList<Planet> getPlanetList() {
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

    /**
     * Downloads planet from list.
     * @param planet ***
     */
    public Planet getPlanet(Planet planet) {
        if(planetList.size() > 0) {
            for(Planet p : planetList) {
                if(p.getId().equals(planet.getId()))
                    return p;
            }
        }
        return null;
    }

    /**
     * Returns list with planets coordinates. Coordinate format [x:xxx:xx].
     * @return If error will exist return empty list.
     */
    public List<String> planetsCoordinate() {
        List<String> list = new ArrayList<>();

        for(Planet planet : planetList)
            list.add(planet.getCoordinate().getText());

        return list;
    }

    public void activeAllUpdateFlag(){
        for(Planet planet : planetList){
            planet.setUpdateTechnologyBuilding(true);
            planet.setUpdateResourcesProduction(true);
            planet.setUpdateResourceBuilding(true);
        }
    }


    public boolean isUpdateData() {
        return updateData;
    }

    public void setUpdateData(boolean updateData) {
        this.updateData = updateData;
    }

    public boolean isInitPlanetList() {
        return initPlanetList;
    }

    public void setInitPlanetList(boolean initPlanetList) {
        this.initPlanetList = initPlanetList;
    }
}
