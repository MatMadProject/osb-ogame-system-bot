package app.data.autobuilder;

import app.data.LSD;
import app.data.MatMadFile;
import app.data.StaticStrings;
import ogame.DataTechnology;
import ogame.Status;
import ogame.buildings.Building;
import ogame.planets.Planet;
import ogame.utils.log.AppLog;

import java.io.*;
import java.util.ArrayList;

public class ListItemAutoBuilder implements Serializable, LSD {
    private static final long serialVersionUID = 1992L;

    private ArrayList<ItemAutoBuilder> queueList = new ArrayList<>();
    private ArrayList<ItemAutoBuilder> historyList = new ArrayList<>();

    public ListItemAutoBuilder(){
        load();
    }

    @Override
    public boolean load() {
        String path = StaticStrings.PLAYER_FOLDER_PATH +
                StaticStrings.AUTO_BUILDER_FILE +StaticStrings.FILE_EXTEND;

        if(MatMadFile.isFileExists(path)) {
            File file = new File(path);

            FileInputStream f;
            try {
                f = new FileInputStream(file);
                ObjectInputStream o = new ObjectInputStream(f);

                ListItemAutoBuilder object = (ListItemAutoBuilder) o.readObject();
                this.queueList = object.getQueueList();
                this.historyList = object.getHistoryList();

                o.close();
                f.close();

                return true;
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                AppLog.print(ListItemAutoBuilder.class.getName(),1,"When try data load.");
            }
        }
        return false;
    }

    @Override
    public boolean save() {
        String folder = StaticStrings.PLAYER_FOLDER_PATH;

        String path = StaticStrings.PLAYER_FOLDER_PATH +
                StaticStrings.AUTO_BUILDER_FILE +StaticStrings.FILE_EXTEND;

        try {
            if(MatMadFile.isFolderExists(folder)) {
                FileOutputStream f = new FileOutputStream(path);
                ObjectOutputStream o = new ObjectOutputStream(f);

                o.writeObject(this);
                o.close();
                f.close();
//                AppLog.print(ListItemAutoBuilder.class.getName(),0,"Data saved.");
                return true;
            }
            else {
                AppLog.print(ListItemAutoBuilder.class.getName(),1,"When try data save.");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public ArrayList<ItemAutoBuilder> getQueueList() {
        return queueList;
    }

    public ArrayList<ItemAutoBuilder> getHistoryList() {
        return historyList;
    }

    public ArrayList<ItemAutoBuilder> get50LatestItemOfHistoryList() {
        ArrayList<ItemAutoBuilder> list = new ArrayList<>();
        int size = historyList.size();
        if(size > 50){
            for(int i = size - 1 ; i > size - 50; i--)
                list.add(historyList.get(i));

            return list;
        }
        return historyList;
    }

    public ArrayList<ItemAutoBuilder> getLatestItemOfHistoryList(int listSize) {
        ArrayList<ItemAutoBuilder> list = new ArrayList<>();
        int size = historyList.size();
        if(size > listSize){
            for(int i = size - 1 ; i > size - listSize; i--)
                list.add(historyList.get(i));

            return list;
        }
        return historyList;
    }

    /**
     * Adds to queue new item if the queue not contains it.
     * @param itemAutoBuilder ***
     * @return If queue contains item return false. Else retuns true.
     */
    public boolean addToQueue(ItemAutoBuilder itemAutoBuilder){
        if(queueList.contains(itemAutoBuilder))
            return false;
        else{
            queueList.add(itemAutoBuilder);
            save();
            return true;
        }
    }

    /**
     * Download highest level of building on parametr from queue.
     * @param building ***
     * @return Highest level of building on queue list.
     */
    public int getHighestLevelOfBuildingOnQueue(Building building){
        int level = building.getLevel();
        for(ItemAutoBuilder itemAutoBuilder : queueList){
            Building tmpBuilding = itemAutoBuilder.getBuilding();
            if(tmpBuilding.equals(building))
                if(level < itemAutoBuilder.getUpgradeLevel())
                    level = itemAutoBuilder.getUpgradeLevel();
        }
        return level;
    }

    /**
     * Download highest level of building on parametr from queue.
     * @param building ***
     * @return Highest level of building on queue list.
     */
    public int getHighestLevelOfBuildingOnQueue(Building building, ArrayList<ItemAutoBuilder> queueList){
        int level = building.getLevel();
        for(ItemAutoBuilder itemAutoBuilder : queueList){
            Building tmpBuilding = itemAutoBuilder.getBuilding();
            if(tmpBuilding.equals(building))
                if(level < itemAutoBuilder.getUpgradeLevel())
                    level = itemAutoBuilder.getUpgradeLevel();
        }
        return level;
    }

    public boolean isAnyBuildingUprading(){
        ItemAutoBuilder itemAutoBuilder = getUpgradingBuilding();

        return itemAutoBuilder != null;
    }

    public boolean isAnyBuildingUpradingOnPlanet(Planet planet){
        ItemAutoBuilder itemAutoBuilder = getUpgradingBuildingOnPlanet(planet);

        return itemAutoBuilder != null;
    }

    public ItemAutoBuilder getUpgradingBuilding(){
        return queueList.stream()
                .filter(item -> item.getBuilding().getStatus() == Status.ACTIVE)
                .findFirst()
                .orElse(null);
    }

    public ItemAutoBuilder getUpgradingBuildingOnPlanet(Planet planet){
        ArrayList<ItemAutoBuilder> queuePlanet = getQueueListFromPlanet(planet);
        return queuePlanet.stream()
                .filter(item -> item.getBuilding().getStatus() == Status.ACTIVE)
                .findFirst()
                .orElse(null);
    }
    public ItemAutoBuilder naniteFactoryUpgradingOnPlanet(Planet planet){
        ArrayList<ItemAutoBuilder> queuePlanet = getQueueListFromPlanet(planet);
        return queuePlanet.stream()
                .filter(item -> item.getBuilding().getDataTechnology() == DataTechnology.NANITE_FACTORY)
                .findFirst()
                .orElse(null);
    }
    public ItemAutoBuilder shipyardUpgradingOnPlanet(Planet planet){
        ArrayList<ItemAutoBuilder> queuePlanet = getQueueListFromPlanet(planet);
        return queuePlanet.stream()
                .filter(item -> item.getBuilding().getDataTechnology() == DataTechnology.SHIPYARD)
                .findFirst()
                .orElse(null);
    }
    public ItemAutoBuilder researchLaboratorydUpgradingOnPlanet(Planet planet){
        ArrayList<ItemAutoBuilder> queuePlanet = getQueueListFromPlanet(planet);
        return queuePlanet.stream()
                .filter(item -> item.getBuilding().getDataTechnology() == DataTechnology.RESEARCH_LABORATORY)
                .findFirst()
                .orElse(null);
    }
    public boolean isNaniteFactoryUpradingOnPlanet(Planet planet){
        ItemAutoBuilder building = naniteFactoryUpgradingOnPlanet(planet);
        return building != null && building.isBuildingUpgrading();
    }
    public boolean isShipyardUpradingOnPlanet(Planet planet){
        ItemAutoBuilder building = shipyardUpgradingOnPlanet(planet);
        return building != null && building.isBuildingUpgrading();
    }
    public boolean isResearchLaboratorydUpradingOnPlanet(Planet planet){
        ItemAutoBuilder building = researchLaboratorydUpgradingOnPlanet(planet);
        return building != null && building.isBuildingUpgrading();
    }
    public void setStatusOnAllItems(Planet planet, app.leaftask.Status status){
        ArrayList<ItemAutoBuilder> queuePlanet = getQueueListFromPlanet(planet);
        for(ItemAutoBuilder itemAutoBuilder : queuePlanet)
            itemAutoBuilder.setStatus(status);
    }
    public void setStatusOnAllItemsWithoutFirst(Planet planet, app.leaftask.Status status){
        ArrayList<ItemAutoBuilder> queuePlanet = getQueueListFromPlanet(planet);
        for(int i = 1; i<queuePlanet.size(); i++)
            queuePlanet.get(i).setStatus(status);
    }
    public void startNextBuildingOnPlanet(Planet planet){
        ArrayList<ItemAutoBuilder> queuePlanet = getQueueListFromPlanet(planet);
        if(queuePlanet.size() > 1){
            queuePlanet.get(1).setStatus(app.leaftask.Status.DATA_DOWNLOADING);
            queuePlanet.get(1).setEndTimeInSeconds(0);
        }
    }

    /**
     * Downloads building queue for planet.
     * @param planet ***
     * @return If queue is empty return empty list.
     */
    public ArrayList<ItemAutoBuilder> getQueueListFromPlanet(Planet planet) {
        ArrayList<ItemAutoBuilder> queueList = new ArrayList<>();
        for(ItemAutoBuilder itemAutoBuilder : this.queueList){
            if(itemAutoBuilder.getPlanet().equals(planet))
                queueList.add(itemAutoBuilder);
        }
        return queueList;
    }
}
