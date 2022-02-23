package app.data.shipyard;

import app.data.LSD;
import app.data.MatMadFile;
import app.data.StaticStrings;
import app.leaftask.Status;
import ogame.planets.Planet;
import ogame.utils.log.AppLog;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ListShipItem implements Serializable, LSD {
    private static final long serialVersionUID = 1992L;
    private int id = 0;
    List<ShipItem> queueList = new ArrayList<>();
    List<ShipItem> historyList = new ArrayList<>();

    public ListShipItem(){
        load();
    }
    @Override
    public boolean load() {
        String path = StaticStrings.PLAYER_FOLDER_PATH +
                StaticStrings.SHIP_FILE +StaticStrings.FILE_EXTEND;

        if(MatMadFile.isFileExists(path)) {
            File file = new File(path);

            FileInputStream f;
            try {
                f = new FileInputStream(file);
                ObjectInputStream o = new ObjectInputStream(f);

                ListShipItem object = (ListShipItem) o.readObject();
                this.id = object.id();
                this.historyList = object.getHistoryList();
                this.queueList = object.getQueueList();

                o.close();
                f.close();

                return true;
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                AppLog.print(ListShipItem.class.getName(),1,"When try data load.");
            }
        }
        return false;
    }

    @Override
    public boolean save() {
        String folder = StaticStrings.PLAYER_FOLDER_PATH;

        String path = StaticStrings.PLAYER_FOLDER_PATH +
                StaticStrings.SHIP_FILE +StaticStrings.FILE_EXTEND;

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
                AppLog.print(ListShipItem.class.getName(),1,"When try data save.");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<ShipItem> getQueueList() {
        return queueList;
    }

    public List<ShipItem> getHistoryList() {
        return historyList;
    }

    public int getId() {
        id++;
        return id;
    }
    public int id(){return id;}

    public boolean add(ShipItem shipItem){
        if(!isExistOnQueueList(shipItem) || shipItem.isSingleExecute()){
            queueList.add(shipItem);
            save();
            return true;
        }
        return false;
    }

    public void addToHistory(ShipItem shipItem){
        historyList.add(shipItem);
    }

    public boolean isExistOnQueueList(ShipItem newShipItem){
        ShipItem shipItem = null;
        if(!queueList.isEmpty()){
            shipItem = queueList.stream()
                    .filter(item -> item.getPlanet().equals(newShipItem.getPlanet())
                            && item.getShip().equals(newShipItem.getShip()))
                    .findFirst().orElse(null);
        }
        return shipItem != null;
    }

    public List<ShipItem> get50LatestItemOfHistoryList() {
        ArrayList<ShipItem> list = new ArrayList<>();
        int size = historyList.size();
        if(size > 50){
            for(int i = size - 1 ; i > size - 50; i--)
                list.add(historyList.get(i));

            return list;
        }
        return historyList;
    }

    public List<ShipItem> getPlanetQueue(Planet planet){
        return queueList.stream()
                .filter(shipItem -> shipItem.getPlanet().equals(planet))
                .collect(Collectors.toList());
    }

    public ShipItem getShipBuildingOnPlanet(Planet planet){
        List<ShipItem> planetQueue = getPlanetQueue(planet);
        return planetQueue.stream()
                .filter(shipItem -> shipItem.getStatus() == Status.BUILDING)
                .findFirst().orElse(null);
    }

    public ShipItem getShipStartingOnPlanet(Planet planet){
        List<ShipItem> planetQueue = getPlanetQueue(planet);
        return planetQueue.stream()
                .filter(shipItem -> shipItem.getStatus() == Status.STARTING)
                .findFirst().orElse(null);
    }

    public boolean isShipBuildingOnPlanet(Planet planet){
        ShipItem buildingItem = getShipBuildingOnPlanet(planet);
        ShipItem startingItem = getShipStartingOnPlanet(planet);

        return buildingItem != null || startingItem != null;
    }
}
