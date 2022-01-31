package app.data.shipyard;

import app.data.LSD;
import app.data.MatMadFile;
import app.data.StaticStrings;
import ogame.planets.Planet;
import ogame.utils.log.AppLog;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ListDefenceItem implements Serializable, LSD {
    private static final long serialVersionUID = 1992L;
    private int id = 0;
    List<DefenceItem> queueList = new ArrayList<>();
    List<DefenceItem> historyList = new ArrayList<>();

    public ListDefenceItem(){
        load();
    }

    @Override
    public boolean load() {
        String path = StaticStrings.PLAYER_FOLDER_PATH +
                StaticStrings.DEFENCE_FILE +StaticStrings.FILE_EXTEND;

        if(MatMadFile.isFileExists(path)) {
            File file = new File(path);

            FileInputStream f;
            try {
                f = new FileInputStream(file);
                ObjectInputStream o = new ObjectInputStream(f);

                ListDefenceItem object = (ListDefenceItem) o.readObject();
                this.id = object.id();
                this.historyList = object.getHistoryList();
                this.queueList = object.getQueueList();

                o.close();
                f.close();

                return true;
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                AppLog.print(ListDefenceItem.class.getName(),1,"When try data load.");
            }
        }
        return false;
    }

    @Override
    public boolean save() {
        String folder = StaticStrings.PLAYER_FOLDER_PATH;

        String path = StaticStrings.PLAYER_FOLDER_PATH +
                StaticStrings.DEFENCE_FILE +StaticStrings.FILE_EXTEND;

        try {
            if(MatMadFile.isFolderExists(folder)) {
                FileOutputStream f = new FileOutputStream(path);
                ObjectOutputStream o = new ObjectOutputStream(f);

                o.writeObject(this);
                o.close();
                f.close();
                return true;
            }
            else {
                AppLog.print(ListDefenceItem.class.getName(),1,"When try data save.");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<DefenceItem> getQueueList() {
        return queueList;
    }

    public List<DefenceItem> getHistoryList() {
        return historyList;
    }
    public int getId(){
        id++;
        return id;
    }
    public int id(){return id;}

    public boolean add(DefenceItem defenceItem){
        if(isExistOnQueueList(defenceItem))
            return false;
        else {
            queueList.add(defenceItem);
            save();
        }
        return true;
    }
    public void addToHistory(DefenceItem defenceItem){
        historyList.add(defenceItem);
    }
    public boolean isExistOnQueueList(DefenceItem newDefenceItem){
        if(!queueList.isEmpty()){
            for(DefenceItem defenceItem : queueList){
                if(newDefenceItem.equals(defenceItem))
                    return true;
            }
        }
        return false;
    }
    public List<DefenceItem> get50LatestItemOfHistoryList() {
        ArrayList<DefenceItem> list = new ArrayList<>();
        int size = historyList.size();
        if(size > 50){
            for(int i = size - 1 ; i > size - 50; i--)
                list.add(historyList.get(i));

            return list;
        }
        return historyList;
    }

    public ArrayList<DefenceItem> getPlanetQueue(Planet planet){
        ArrayList<DefenceItem> list = new ArrayList<>();
        for(DefenceItem defenceItem : queueList){
            if(defenceItem.getPlanet().equals(planet))
                list.add(defenceItem);
        }
        return list;
    }

    public DefenceItem getDefenceBuildingOnPlanet(Planet planet){
        ArrayList<DefenceItem> planetQueue = getPlanetQueue(planet);
        for(DefenceItem defenceItem : planetQueue)
            if(defenceItem.getStatus() == Status.BUILDING)
                return defenceItem;

        return null;
    }
    public DefenceItem getDefenceStartingOnPlanet(Planet planet){
        ArrayList<DefenceItem> planetQueue = getPlanetQueue(planet);
        for(DefenceItem defenceItem : planetQueue)
            if(defenceItem.getStatus() == Status.STARTING)
                return defenceItem;

        return null;
    }

    public boolean isDefenceBuildingOnPlanet(Planet planet){
        DefenceItem defenceItem = getDefenceBuildingOnPlanet(planet);
        DefenceItem startingItem = getDefenceStartingOnPlanet(planet);
        return defenceItem != null || startingItem != null;
    }
}
