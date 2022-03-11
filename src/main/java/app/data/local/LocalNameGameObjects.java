package app.data.local;

import app.data.LSD;
import app.data.MatMadFile;
import app.data.StaticStrings;
import ogame.DataTechnology;
import ogame.utils.log.AppLog;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class LocalNameGameObjects implements Serializable, LSD {
    private static final long serialVersionUID = 1992L;
    private final Language language;
    private final List<LocalName> localNameOfGameObjects = new ArrayList<>();
    private boolean productionBuildingsLocalNameLoaded;
    private boolean technologyBuildingsLocalNameLoaded;
    private boolean researchesLocalNameLoaded;
    private boolean shipsLocalNameLoaded;
    private boolean defenceLocalNameLoaded;

    public LocalNameGameObjects(Language language){
        this.language = language;
        load();
        productionBuildingsLocalNameLoaded = containsProductionBuildings();
        technologyBuildingsLocalNameLoaded = containsTechnologyBuildings();
        researchesLocalNameLoaded = containsResearch();
        shipsLocalNameLoaded = containsShips();
        defenceLocalNameLoaded = containsDefence();
    }
    @Override
    public boolean load() {
        String path = StaticStrings.PLAYER_FOLDER_PATH
                + language.getSYMBOL().toLowerCase()
                + StaticStrings.LOCAL_NAME_FILE +StaticStrings.FILE_EXTEND_TXT;

        if(MatMadFile.isFileExists(path)) {
            try {
                FileReader reader = new FileReader(path);
                BufferedReader bufferedReader = new BufferedReader(reader);

                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    LocalName localName = new LocalName(line);
                    localNameOfGameObjects.add(localName);
                }
                reader.close();
                return true;

            } catch (IOException e) {
                e.printStackTrace();
                AppLog.print(LocalNameGameObjects.class.getName(),1,"When try data load.");
            }
        }
        return false;
    }

    @Override
    public boolean save() {
        String folder = StaticStrings.PLAYER_FOLDER_PATH;

        String path = StaticStrings.PLAYER_FOLDER_PATH
                + language.getSYMBOL().toLowerCase()
                + StaticStrings.LOCAL_NAME_FILE +StaticStrings.FILE_EXTEND_TXT;

        if(MatMadFile.isFolderExists(folder)){
            try {
                FileWriter writer = new FileWriter(path);
                BufferedWriter bufferedWriter = new BufferedWriter(writer);

                for(LocalName localName : localNameOfGameObjects){
                    bufferedWriter.write(localName.toString());
                    bufferedWriter.newLine();
                }
                bufferedWriter.close();
                AppLog.print(LocalNameGameObjects.class.getName(),0,"Data saved.");
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                AppLog.print(LocalNameGameObjects.class.getName(),1,"When try data save.");
            }
        }
        return false;

    }

    public String getLocalName(DataTechnology dataTechnology){
        LocalName localName = localNameOfGameObjects.stream()
                .filter(object -> object.getDATA_TECHNOLOGY() == dataTechnology)
                .findFirst()
                .orElse(null);
        return localName != null ? localName.VALUE:null;
    }
    private boolean containsResearch(){
        LocalName localName = localNameOfGameObjects.stream()
                .filter(object -> object.getDATA_TECHNOLOGY() == DataTechnology.ENERGY_TECHOLOGY)
                .findFirst()
                .orElse(null);
        return localName != null;
    }
    private boolean containsProductionBuildings(){
        LocalName localName = localNameOfGameObjects.stream()
                .filter(object -> object.getDATA_TECHNOLOGY() == DataTechnology.METAL_MINE)
                .findFirst()
                .orElse(null);
        return localName != null;
    }
    private boolean containsTechnologyBuildings(){
        LocalName localName = localNameOfGameObjects.stream()
                .filter(object -> object.getDATA_TECHNOLOGY() == DataTechnology.ROBOTICS_FACTORY)
                .findFirst()
                .orElse(null);
        return localName != null;
    }
    private boolean containsShips(){
        LocalName localName = localNameOfGameObjects.stream()
                .filter(object -> object.getDATA_TECHNOLOGY() == DataTechnology.FIGHTER_LIGHT)
                .findFirst()
                .orElse(null);
        return localName != null;
    }
    private boolean containsDefence(){
        LocalName localName = localNameOfGameObjects.stream()
                .filter(object -> object.getDATA_TECHNOLOGY() == DataTechnology.ROCKET_LAUNCHER)
                .findFirst()
                .orElse(null);
        return localName != null;
    }

    public void addLocalName(LocalName localName){
        if(!isExist(localName))
            localNameOfGameObjects.add(localName);
    }

    public boolean isExist(LocalName newLocalName){
        LocalName localName = localNameOfGameObjects.stream()
                .filter(object -> object.equals(newLocalName))
                .findFirst()
                .orElse(null);
        return localName != null;
    }

    public boolean isDataLoaded() {
        return productionBuildingsLocalNameLoaded
                && technologyBuildingsLocalNameLoaded
                && researchesLocalNameLoaded
                && shipsLocalNameLoaded
                && defenceLocalNameLoaded;
    }

    public boolean isProductionBuildingsLocalNameLoaded() {
        return productionBuildingsLocalNameLoaded;
    }

    public void setProductionBuildingsLocalNameLoaded() {
        this.productionBuildingsLocalNameLoaded = true;
        save();
    }

    public boolean isTechnologyBuildingsLocalNameLoaded() {
        return technologyBuildingsLocalNameLoaded;
    }

    public void setTechnologyBuildingsLocalNameLoaded() {
        this.technologyBuildingsLocalNameLoaded = true;
        save();
    }

    public boolean isResearchesLocalNameLoaded() {
        return researchesLocalNameLoaded;
    }

    public void setResearchesLocalNameLoaded() {
        this.researchesLocalNameLoaded = true;
        save();
    }

    public boolean isShipsLocalNameLoaded() {
        return shipsLocalNameLoaded;
    }

    public void setShipsLocalNameLoaded() {
        this.shipsLocalNameLoaded = true;
        save();
    }

    public boolean isDefenceLocalNameLoaded() {
        return defenceLocalNameLoaded;
    }

    public void setDefenceLocalNameLoaded() {
        this.defenceLocalNameLoaded = true;
        save();
    }
}
