package app.data.autobuilder;

import app.data.LSD;
import app.data.MatMadFile;
import app.data.StaticStrings;
import ogame.Status;
import ogame.researches.Research;
import ogame.utils.log.AppLog;

import java.io.*;
import java.util.ArrayList;

public class ListItemAutoResearch implements Serializable, LSD {

    private static final long serialVersionUID = 1992L;

    private ArrayList<ItemAutoResearch> queueList = new ArrayList<>();
    private ArrayList<ItemAutoResearch> historyList = new ArrayList<>();

    public ListItemAutoResearch(){
        load();
    }
    @Override
    public boolean load() {
        String path = StaticStrings.PLAYER_FOLDER_PATH +
                StaticStrings.AUTO_RESEARCH_FILE +StaticStrings.FILE_EXTEND;

        if(MatMadFile.isFileExists(path)) {
            File file = new File(path);

            FileInputStream f;
            try {
                f = new FileInputStream(file);
                ObjectInputStream o = new ObjectInputStream(f);

                ListItemAutoResearch object = (ListItemAutoResearch) o.readObject();
                this.queueList = object.getQueueList();
                this.historyList = object.getHistoryList();

                o.close();
                f.close();

                return true;
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                AppLog.print(ListItemAutoResearch.class.getName(),1,"When try data load.");
            }
        }
        return false;
    }

    @Override
    public boolean save() {
        String folder = StaticStrings.PLAYER_FOLDER_PATH;

        String path = StaticStrings.PLAYER_FOLDER_PATH +
                StaticStrings.AUTO_RESEARCH_FILE +StaticStrings.FILE_EXTEND;

        try {
            if(MatMadFile.isFolderExists(folder)) {
                FileOutputStream f = new FileOutputStream(path);
                ObjectOutputStream o = new ObjectOutputStream(f);

                o.writeObject(this);
                o.close();
                f.close();
//                AppLog.print(ListItemAutoResearch.class.getName(),0,"Data saved.");
                return true;
            }
            else {
                AppLog.print(ListItemAutoResearch.class.getName(),1,"When try data save.");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public ArrayList<ItemAutoResearch> getQueueList() {
        return queueList;
    }

    public ArrayList<ItemAutoResearch> getHistoryList() {
        return historyList;
    }

    public ArrayList<ItemAutoResearch> get50LatestItemOfHistoryList() {
        ArrayList<ItemAutoResearch> list = new ArrayList<>();
        int size = historyList.size();
        if(size > 50){
            for(int i = size - 1; i > size - 50; i--)
                list.add(historyList.get(i));

            return list;
        }
        return historyList;
    }

    public ArrayList<ItemAutoResearch> getLatestItemOfHistoryList(int listSize) {
        ArrayList<ItemAutoResearch> list = new ArrayList<>();
        int size = historyList.size();
        if(size > listSize){
            for(int i = size - 1; i > size - listSize; i--)
                list.add(historyList.get(i));

            return list;
        }
        return historyList;
    }

    /**
     * Adds to queue new item if the queue not contains it.
     * @param itemAutoResearch ***
     * @return If queue contains item return false. Else retuns true.
     */
    public boolean addToQueue(ItemAutoResearch itemAutoResearch){
        if(queueList.contains(itemAutoResearch))
            return false;
        else{
            queueList.add(itemAutoResearch);
            save();
            return true;
        }
    }

    /**
     * Download highest level of research on parametr from queue.
     * @param research ***
     * @return Highest level of research on queue list.
     */
    public int getHighestLevelOfResearchOnQueue(Research research){
        int level = research.getLevel();
        for(ItemAutoResearch itemAutoResearch : queueList){
            Research tmpResearch = itemAutoResearch.getResearch();
            if(tmpResearch.equals(research))
                if(level < itemAutoResearch.getUpgradeLevel())
                    level = itemAutoResearch.getUpgradeLevel();
        }
        return level;
    }
    public boolean isAnyResearchUprading(){
        ItemAutoResearch itemAutoResearch = getUpgradingResearch();
        return itemAutoResearch != null;
    }
    public ItemAutoResearch getUpgradingResearch(){
        return queueList.stream()
                .filter(item -> item.getResearch().getStatus() == Status.ACTIVE)
                .findFirst()
                .orElse(null);
    }
    public void startNextResearchOnQueue(){
        if(queueList.size() > 1){
            queueList.get(1).setStatus(app.leaftask.Status.DATA_DOWNLOADING);
            queueList.get(1).setEndTimeInSeconds(0);
        }
    }
}
