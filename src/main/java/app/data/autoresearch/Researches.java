package app.data.autoresearch;

import app.data.LSD;
import app.data.MatMadFile;
import app.data.StaticStrings;
import ogame.researches.DataTechnology;
import ogame.researches.Research;
import ogame.utils.log.AppLog;

import java.io.*;
import java.util.ArrayList;

public class Researches implements LSD, Serializable {

    private static final long serialVersionUID = 1992L;
    private ArrayList<Research> researchList = new ArrayList<>();
    private boolean updateData = true;

    public Researches (){
        if(!load()){
            for(DataTechnology dataTechnology : DataTechnology.values()){
                if(dataTechnology == ogame.researches.DataTechnology.UNDEFINED)
                    continue;
                Research tmp = new Research(dataTechnology.name(),dataTechnology.getValue());
                researchList.add(tmp);
            }
        }
    }

    @Override
    public boolean load() {
        String path = StaticStrings.PLAYER_FOLDER_PATH +
                StaticStrings.RESEARCH_FILE +StaticStrings.FILE_EXTEND;

        if(MatMadFile.isFileExists(path)) {
            File file = new File(path);

            FileInputStream f;
            try {
                f = new FileInputStream(file);
                ObjectInputStream o = new ObjectInputStream(f);

                Researches object = (Researches) o.readObject();
                this.researchList = object.getResearchList();

                o.close();
                f.close();

                return true;
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                AppLog.print(Researches.class.getName(),1,"When try data load.");
            }
        }
        return false;
    }

    @Override
    public boolean save() {
        String folder = StaticStrings.PLAYER_FOLDER_PATH;

        String path = StaticStrings.PLAYER_FOLDER_PATH +
                StaticStrings.RESEARCH_FILE +StaticStrings.FILE_EXTEND;

        try {
            if(MatMadFile.isFolderExists(folder)) {
                FileOutputStream f = new FileOutputStream(path);
                ObjectOutputStream o = new ObjectOutputStream(f);

                o.writeObject(this);
                o.close();
                f.close();
                AppLog.print(Researches.class.getName(),0,"Data saved.");
                return true;
            }
            else
                AppLog.print(Researches.class.getName(),1,"When try data save.");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public ArrayList<Research> getResearchList() {
        return researchList;
    }

    /**
     * Adds new research to list.
     * @param research Adding research.
     */
    public void add(Research research) {
        if(!exists(research)) {
            researchList.add(research);
            AppLog.print(Researches.class.getName(),0,"Added new research. " + research.getName());
        }
        else
            AppLog.print(Researches.class.getName(),0,"Research " + research.getName() + " exists on list.");
    }

    /**
     * Checks the research exist on list.
     * @param research Seearchin research.
     * @return If exist returns true.
     */
    private boolean exists(Research research) {
        if(researchList.size() > 0)
            for(Research r : researchList)
                if(research.equals(r))
                    return true;

        return false;
    }

    /**
     * Removes research from list.
     * @param  research ***.
     */
    public void remove(Research research) {
        researchList.remove(research);
        AppLog.print(Research.class.getName(),0,"Removed research. " + research.getName());
    }

    /**
     * Downloads research from list.
     * @param dataTechnology Research dataTechnology
     */
    public Research getResearch(DataTechnology dataTechnology) {
        for(Research r : researchList)
            if(r.getDataTechnology() == dataTechnology)
                return r;

        return null;
    }

    public boolean isUpdateData() {
        return updateData;
    }

    public void setUpdateData(boolean updateData) {
        this.updateData = updateData;
    }
}
