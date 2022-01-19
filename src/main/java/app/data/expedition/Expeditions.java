package app.data.expedition;

import app.data.LSD;
import app.data.MatMadFile;
import app.data.StaticStrings;
import ogame.utils.log.AppLog;

import java.io.*;
import java.util.ArrayList;

public class Expeditions  implements LSD, Serializable {

    private static final long serialVersionUID = 1992L;
    private ArrayList<Expedition> expeditionList = new ArrayList<>();
    private int maxExpeditions = 1; //-1
    private int id = 0;

    public Expeditions (){
        load();
    }

    @Override
    public boolean load() {
        String path = StaticStrings.PLAYER_FOLDER_PATH +
                StaticStrings.EXPEDITION_FILE +StaticStrings.FILE_EXTEND;

        if(MatMadFile.isFileExists(path)) {
            File file = new File(path);

            FileInputStream f;
            try {
                f = new FileInputStream(file);
                ObjectInputStream o = new ObjectInputStream(f);

                Expeditions object = (Expeditions) o.readObject();
                this.expeditionList = object.getExpeditionList();
                this.maxExpeditions = object.getMaxExpeditions();
                id = id();

                o.close();
                f.close();

                return true;
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                AppLog.print(Expeditions.class.getName(),1,"When try data load.");
            }
        }
        return false;
    }

    @Override
    public boolean save() {
        String folder = StaticStrings.PLAYER_FOLDER_PATH;

        String path = StaticStrings.PLAYER_FOLDER_PATH +
                StaticStrings.EXPEDITION_FILE +StaticStrings.FILE_EXTEND;

        try {
            if(MatMadFile.isFolderExists(folder)) {
                FileOutputStream f = new FileOutputStream(path);
                ObjectOutputStream o = new ObjectOutputStream(f);

                o.writeObject(this);
                o.close();
                f.close();
//                AppLog.print(Expedition.class.getName(),0,"Data saved.");
                return true;
            }
            else
                AppLog.print(Expedition.class.getName(),1,"When try data save.");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void saveLog(String log){
        String path = StaticStrings.PLAYER_FOLDER_PATH +
                StaticStrings.EXPEDITION_LOG_FILE +StaticStrings.FILE_EXTEND_TXT;
        String folder = StaticStrings.PLAYER_FOLDER_PATH;

        if(MatMadFile.isFolderExists(folder)){
            try {
                FileWriter writer = new FileWriter(path,true);
                BufferedWriter bufferedWriter = new BufferedWriter(writer);

                bufferedWriter.write(log);
                bufferedWriter.newLine();

                bufferedWriter.close();
                AppLog.print(Expeditions.class.getName(),0,"Data saved.");
            } catch (IOException e) {
                e.printStackTrace();
                AppLog.print(Expeditions.class.getName(),1,"When try data save.");
            }
        }
    }

    public void add(Expedition expedition){
        if(expeditionList.size() < maxExpeditions){
            expeditionList.add(expedition);
            AppLog.print(Expeditions.class.getName(),0,"Added new expeditions.");

        }else{
            AppLog.print(Expeditions.class.getName(),0,"Achieves maximum expeditions value.");
        }
    }

    public ArrayList<Expedition> getExpeditionList() {
        return expeditionList;
    }

    public int getMaxExpeditions() {
        return maxExpeditions;
    }

    public void setMaxExpeditions(int maxExpeditions) {
        this.maxExpeditions = maxExpeditions;
    }

    public int getId(){
        id++;
        return id;
    }
    public int id(){return id;}
}
