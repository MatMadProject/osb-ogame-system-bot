package app.data;

import app.data.planets.Planets;
import ogame.utils.log.AppLog;

import java.io.File;

public class MatMadFile {
    /**
     * Checks file is exist on path
     * @param path File path.
     * @return Returns true if exists.
     */
    public static boolean isFileExists(String path) {
        File file = new File(path);

        if(file.exists())
            return true;
        else {
            AppLog.print(MatMadFile.class.getName(),1,"File on path: " +path + " deosn't exist.");
            return false;
        }
    }

    /**
     * Checks folder is exist on path
     * @param path Folder path.
     * @return Returns true if exists.
     */
    public static boolean isFolderExists(String path) {
        File file = new File(path);
        if(file.exists())
            return true;
        else {
            AppLog.print(MatMadFile.class.getName(),0,"Folder on path: " +path + " deosn't exist.");
            file.mkdir();
            AppLog.print(MatMadFile.class.getName(),0,"Created folder on path: " +path);
            return false;
        }
    }
}
