package app.data.planets;

import app.data.LSD;
import app.data.MatMadFile;
import app.data.StaticStrings;
import ogame.utils.log.AppLog;

import java.io.*;
import java.util.ArrayList;

public class ColonyData implements LSD {
    private final ArrayList<ColonyDataItem> colonyDataItemList = new ArrayList<>();

    public ColonyData(){
        load();
    }

    @Override
    public boolean load() {
        String path = StaticStrings.PLAYER_FOLDER_PATH +
                StaticStrings.COLONIES_DATA_FILE + StaticStrings.FILE_EXTEND_TXT;

        if(MatMadFile.isFileExists(path)) {
            try {
                FileReader reader = new FileReader(path);
                BufferedReader bufferedReader = new BufferedReader(reader);

                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    ColonyDataItem colonyDataItem = new ColonyDataItem(line);
                    colonyDataItemList.add(colonyDataItem);
                }
                reader.close();
                return true;

            } catch (IOException e) {
                e.printStackTrace();
                AppLog.print(ColonyData.class.getName(),1,"When try data load.");
            }
        }
        return false;
    }

    @Override
    public boolean save() {
        String path = StaticStrings.PLAYER_FOLDER_PATH +
                StaticStrings.COLONIES_DATA_FILE +StaticStrings.FILE_EXTEND_TXT;
        String folder = StaticStrings.PLAYER_FOLDER_PATH;

        if(MatMadFile.isFolderExists(folder)){
            try {
                FileWriter writer = new FileWriter(path);
                BufferedWriter bufferedWriter = new BufferedWriter(writer);

                for(ColonyDataItem colonyDataItem : colonyDataItemList){
                    bufferedWriter.write(colonyDataItem.toString());
                    bufferedWriter.newLine();
                }
                bufferedWriter.close();
                AppLog.print(ColonyData.class.getName(),0,"Data saved.");
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                AppLog.print(ColonyData.class.getName(),1,"When try data save.");
            }
        }
        return false;
    }

    public boolean save(ColonyDataItem colonyDataItem) {
        String path = StaticStrings.PLAYER_FOLDER_PATH +
                StaticStrings.COLONIES_DATA_FILE +StaticStrings.FILE_EXTEND_TXT;
        String folder = StaticStrings.PLAYER_FOLDER_PATH;

        if(MatMadFile.isFolderExists(folder)){
            try {
                FileWriter writer = new FileWriter(path, true);
                BufferedWriter bufferedWriter = new BufferedWriter(writer);

                bufferedWriter.write(colonyDataItem.toString());
                bufferedWriter.newLine();
                colonyDataItemList.add(colonyDataItem);

                bufferedWriter.close();
                AppLog.print(Planets.class.getName(),0,"Data saved.");
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                AppLog.print(Planets.class.getName(),1,"When try data save.");
            }
        }
        return false;
    }

    /**
     * Checks the planet exist on list.
     * @param colonyDataItem Searching planet.
     * @return If exist returns true.
     */
    private boolean exists(ColonyDataItem colonyDataItem) {
        if(colonyDataItemList.size() > 0) {
            for(ColonyDataItem colonyDataItemTmp : colonyDataItemList) {
                if(colonyDataItem.getId().equals(colonyDataItemTmp.getId()))
                    return true;
            }
        }
        return false;
    }

    /**
     * Adds new planet to list.
     * @param colonyDataItem Adding planet.
     */
    public boolean add(ColonyDataItem colonyDataItem) {
        if(!exists(colonyDataItem)) {
            colonyDataItemList.add(colonyDataItem);
            AppLog.print(ColonyData.class.getName(),0,"Added new colony data. " + colonyDataItem.toString());
            return true;
        }
        else
            AppLog.print(ColonyData.class.getName(),0,"Colony data exists on list.");
        return false;
    }
}
