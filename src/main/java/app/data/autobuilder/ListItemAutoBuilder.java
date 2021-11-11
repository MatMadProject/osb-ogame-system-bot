package app.data.autobuilder;

import app.data.LSD;
import app.data.MatMadFile;
import app.data.StaticStrings;
import app.data.planets.Planets;
import ogame.Status;
import ogame.buildings.Building;
import ogame.utils.log.AppLog;

import java.io.*;
import java.util.ArrayList;

public class ListItemAutoBuilder implements Serializable, LSD {
    private static final long serialVersionUID = 1992L;

    private ArrayList<ItemAutoBuilder> queueList = new ArrayList<>();
    private ArrayList<ItemAutoBuilder> historyList = new ArrayList<>();

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
                AppLog.print(Planets.class.getName(),1,"When try data load.");
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

    public ArrayList<ItemAutoBuilder> getQueueList() {
        return queueList;
    }

    public ArrayList<ItemAutoBuilder> getHistoryList() {
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

    public boolean isAnyBuildingUprading(){
        for(ItemAutoBuilder itemAutoBuilder : queueList)
            if(itemAutoBuilder.getBuilding().getStatus() == Status.ACTIVE)
                return true;

        return false;
    }

    public ItemAutoBuilder getUpgradingBuilding(){
        for(ItemAutoBuilder itemAutoBuilder : queueList)
            if(itemAutoBuilder.getBuilding().getStatus() == Status.ACTIVE)
                return itemAutoBuilder;

        return null;
    }
}
