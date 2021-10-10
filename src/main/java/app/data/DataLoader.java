package app.data;

import app.data.planets.Planets;
import ogame.utils.log.AppLog;

public class DataLoader {
    public static Planets planets;

    public DataLoader(){
        planets = new Planets();
        AppLog.print(DataLoader.class.getName(),0,"Bot data loaded success.");
    }
}
