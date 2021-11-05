package app.data;

import app.data.autobuilder.ListItemAutoBuilder;
import app.data.planets.Planets;
import ogame.utils.log.AppLog;

public class DataLoader {
    public static Planets planets;
    public static ListItemAutoBuilder listItemAutoBuilder;

    public DataLoader(){
        planets = new Planets();
        listItemAutoBuilder = new ListItemAutoBuilder();
        AppLog.print(DataLoader.class.getName(),0,"Bot data loaded success.");
    }
}
