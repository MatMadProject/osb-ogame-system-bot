package app.data.player;

import app.data.LSD;
import app.data.MatMadFile;
import app.data.StaticStrings;
import ogame.utils.log.AppLog;
import ogame.utils.watch.Timer;

import java.io.*;
import java.util.ArrayList;

public class PlayerData implements LSD{
    private ArrayList<PlayerDataItem> playerDataItemArrayList = new ArrayList<>();
    private final long EXECUTE_WAIT_TIME = 8*60*60*1000L;

    public PlayerData(){
        load();
    }

    @Override
    public boolean load() {
        String path = StaticStrings.PLAYER_FOLDER_PATH +
                StaticStrings.PLAYER_DATA_FILE + StaticStrings.FILE_EXTEND_TXT;

        if(MatMadFile.isFileExists(path)) {
            try {
                FileReader reader = new FileReader(path);
                BufferedReader bufferedReader = new BufferedReader(reader);

                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    PlayerDataItem playerDataItem = new PlayerDataItem(line);
                    playerDataItemArrayList.add(playerDataItem);
                }
                reader.close();
                return true;

            } catch (IOException e) {
                e.printStackTrace();
                AppLog.print(PlayerData.class.getName(),1,"When try data load.");
            }
        }
        return false;
    }

    @Override
    public boolean save() {
        String path = StaticStrings.PLAYER_FOLDER_PATH +
                StaticStrings.PLAYER_DATA_FILE +StaticStrings.FILE_EXTEND_TXT;
        String folder = StaticStrings.PLAYER_FOLDER_PATH;

        if(MatMadFile.isFolderExists(folder)){
            try {
                FileWriter writer = new FileWriter(path);
                BufferedWriter bufferedWriter = new BufferedWriter(writer);

                for(PlayerDataItem playerDataItem : playerDataItemArrayList){
                    bufferedWriter.write(playerDataItem.toString());
                    bufferedWriter.newLine();
                }
                bufferedWriter.close();
                AppLog.print(PlayerData.class.getName(),0,"Data saved.");
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                AppLog.print(PlayerData.class.getName(),1,"When try data save.");
            }
        }
        return false;
    }

    public boolean save(PlayerDataItem playerDataItem) {
        String path = StaticStrings.PLAYER_FOLDER_PATH +
                StaticStrings.PLAYER_DATA_FILE +StaticStrings.FILE_EXTEND_TXT;
        String folder = StaticStrings.PLAYER_FOLDER_PATH;

        if(MatMadFile.isFolderExists(folder)){
            try {
                FileWriter writer = new FileWriter(path, true);
                BufferedWriter bufferedWriter = new BufferedWriter(writer);

                bufferedWriter.write(playerDataItem.toString());
                bufferedWriter.newLine();
                playerDataItemArrayList.add(playerDataItem);

                bufferedWriter.close();
                AppLog.print(PlayerData.class.getName(),0,"Data saved.");
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                AppLog.print(PlayerData.class.getName(),1,"When try data save.");
            }
        }
        return false;
    }

    private PlayerDataItem latestItem(){
        int size = playerDataItemArrayList.size();
        if(size > 0)
            return playerDataItemArrayList.get(size-1);

        return null;
    }

    public Timer timeLeftToNextExecute(){
        PlayerDataItem playerDataItem = latestItem();
        if(playerDataItem != null){
            long executeTime = Long.parseLong(latestItem().getTimeMilliseconds());
            return new Timer(executeTime,executeTime + EXECUTE_WAIT_TIME);
        }

        return new Timer(0,0);
    }
}
