package app.data.autobuilder;

import app.data.LSD;
import app.data.MatMadFile;
import app.data.StaticStrings;
import ogame.DataTechnology;
import ogame.buildings.RequiredResources;
import ogame.utils.log.AppLog;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LevelBuildings implements LSD {
    private ArrayList<LevelBuildingItem> levelBuildings = new ArrayList<>();

    public LevelBuildings(){
        load();
    }
    @Override
    public boolean load() {
        String path = StaticStrings.MAIN_PATH +
                StaticStrings.LEVEL_BUILDINGS_FILE + StaticStrings.FILE_EXTEND_TXT;

        if(MatMadFile.isFileExists(path)) {
            try {
                FileReader reader = new FileReader(path);
                BufferedReader bufferedReader = new BufferedReader(reader);

                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    LevelBuildingItem levelBuildingItem = new LevelBuildingItem(line);
                    levelBuildings.add(levelBuildingItem);
                }
                reader.close();
                return true;

            } catch (IOException e) {
                e.printStackTrace();
                AppLog.print(LevelBuildings.class.getName(),1,"When try data load.");
            }
        }
        return false;
    }

    @Override
    public boolean save() {
        String path = StaticStrings.MAIN_PATH +
                StaticStrings.LEVEL_BUILDINGS_FILE +StaticStrings.FILE_EXTEND_TXT;
        String folder = StaticStrings.MAIN_PATH;

        if(MatMadFile.isFolderExists(folder)){
            try {
                FileWriter writer = new FileWriter(path);
                BufferedWriter bufferedWriter = new BufferedWriter(writer);

                for(LevelBuildingItem levelBuildingItem : levelBuildings){
                    bufferedWriter.write(levelBuildingItem.toString());
                    bufferedWriter.newLine();
                }
                bufferedWriter.close();
                AppLog.print(LevelBuildings.class.getName(),0,"Data saved.");
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                AppLog.print(LevelBuildings.class.getName(),1,"When try data save.");
            }
        }
        return false;
    }

    public boolean save(LevelBuildingItem buildingItem) {
        String path = StaticStrings.MAIN_PATH +
                StaticStrings.LEVEL_BUILDINGS_FILE +StaticStrings.FILE_EXTEND_TXT;
        String folder = StaticStrings.MAIN_PATH;

        if(MatMadFile.isFolderExists(folder)){
            try {
                FileWriter writer = new FileWriter(path, true);
                BufferedWriter bufferedWriter = new BufferedWriter(writer);

                bufferedWriter.write(buildingItem.toString());
                bufferedWriter.newLine();
                levelBuildings.add(buildingItem);

                bufferedWriter.close();
                AppLog.print(LevelBuildings.class.getName(),0,"Data saved.");
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                AppLog.print(LevelBuildings.class.getName(),1,"When try data save.");
            }
        }
        return false;
    }

    public boolean addToList(LevelBuildingItem buildingItem){
        levelBuildings.add(buildingItem);
        AppLog.print(LevelBuildings.class.getName(),0,"Add to file " + buildingItem.getDataTechnology().name() + ", level "
                + buildingItem.getLevel() + ", require resources " + buildingItem.getMetalRequired() + " " + buildingItem.getCrystalRequired()
                + " " + buildingItem.getEnergyRequired() + " " + buildingItem.getEnergyRequired() + ".");
        return save(buildingItem);
    }

    public List<LevelBuildingItem> levelBuilding(DataTechnology dataTechnologyBuilding){
        return levelBuildings.stream()
                .filter(item -> item.getDataTechnology() == dataTechnologyBuilding)
                .collect(Collectors.toList());
    }

    public RequiredResources requiredResourcesForBuilding(DataTechnology dataTechnologyBuilding, int level){
        List<LevelBuildingItem> levelBuildingItems = levelBuilding(dataTechnologyBuilding);
        LevelBuildingItem item = levelBuildingItems.stream()
                .filter(building -> Integer.parseInt(building.getLevel()) == level)
                .findFirst()
                .orElse(null);
        return item == null ? null:new RequiredResources(Long.parseLong(item.getMetalRequired()), Long.parseLong(item.getCrystalRequired()),
                Long.parseLong(item.getDeuteriumRequired()), Long.parseLong(item.getEnergyRequired()));
    }
    public boolean isItemExistOnList(DataTechnology dataTechnologyBuilding, int level){
        return levelBuildings.stream()
                .filter(item -> item.getDataTechnology() == dataTechnologyBuilding && Integer.parseInt(item.getLevel()) == level)
                .findFirst()
                .orElse(null) != null;
    }
}
