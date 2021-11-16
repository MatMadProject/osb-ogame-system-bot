package app.data;

import app.data.autobuilder.ListItemAutoBuilder;
import app.data.autoresearch.ListItemAutoResearch;
import app.data.autoresearch.Researches;
import app.data.planets.Planets;
import ogame.utils.log.AppLog;

public class DataLoader {
    public static Planets planets;
    public static Researches researches;
    public static ListItemAutoBuilder listItemAutoBuilder;
    public static ListItemAutoResearch listItemAutoResearch;

    public DataLoader(){
        planets = new Planets();
        researches = new Researches();
        listItemAutoBuilder = new ListItemAutoBuilder();
        listItemAutoResearch = new ListItemAutoResearch();
        AppLog.print(DataLoader.class.getName(),0,"Bot data loaded success.");
    }
}
