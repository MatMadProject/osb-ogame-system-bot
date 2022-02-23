package app.data.transport;

import app.data.LSD;
import app.data.MatMadFile;
import app.data.StaticStrings;
import ogame.utils.log.AppLog;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ListTransportItem implements Serializable, LSD {
    private static final long serialVersionUID = 1992L;
    private int id = 0;
    private List<TransportItem> queueList = new ArrayList<>();
    private List<TransportItem> historyList = new ArrayList<>();

    public ListTransportItem(){
        load();
    }

    @Override
    public boolean load() {
        String path = StaticStrings.PLAYER_FOLDER_PATH +
                StaticStrings.AUTOTRANSPORT_FILE +StaticStrings.FILE_EXTEND;

        if(MatMadFile.isFileExists(path)) {
            File file = new File(path);

            FileInputStream f;
            try {
                f = new FileInputStream(file);
                ObjectInputStream o = new ObjectInputStream(f);

                ListTransportItem object = (ListTransportItem) o.readObject();
                this.id = object.id();
                this.historyList = object.getHistoryList();
                this.queueList = object.getQueueList();

                o.close();
                f.close();

                return true;
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                AppLog.print(ListTransportItem.class.getName(),1,"When try data load.");
            }
        }
        return false;
    }

    @Override
    public boolean save() {
        String folder = StaticStrings.PLAYER_FOLDER_PATH;

        String path = StaticStrings.PLAYER_FOLDER_PATH +
                StaticStrings.AUTOTRANSPORT_FILE +StaticStrings.FILE_EXTEND;

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
                AppLog.print(ListTransportItem.class.getName(),1,"When try data save.");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getId() {
        id++;
        return id;
    }
    public int id(){return id;}

    public List<TransportItem> getQueueList() {
        return queueList;
    }

    public List<TransportItem> getHistoryList() {
        return historyList;
    }

    public boolean add(TransportItem transportItem){
        if(!isExistOnQueueList(transportItem) || transportItem.isSingleExecute()){
            queueList.add(transportItem);
            save();
            return true;
        }
        return false;
    }
    public void addToHistory(TransportItem transportItem){
        historyList.add(transportItem);
    }
    public boolean isExistOnQueueList(TransportItem newTransportItem){
        TransportItem transportItem = null;
        if(!queueList.isEmpty())
            transportItem = queueList.stream()
                    .filter(item -> item.getPlanetStart().equals(newTransportItem.getPlanetStart())
                    && item.getPlanetEnd().equals(newTransportItem.getPlanetEnd()))
                    .findFirst().orElse(null);

        return transportItem != null;
    }
    public List<TransportItem> get50LatestItemOfHistoryList() {
        ArrayList<TransportItem> list = new ArrayList<>();
        int size = historyList.size();
        if(size > 50){
            for(int i = size - 1 ; i > size - 50; i--)
                list.add(historyList.get(i));

            return list;
        }
        return historyList;
    }
}
