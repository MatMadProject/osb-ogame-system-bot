package app.data;

import app.data.autobuilder.ListItemAutoBuilder;
import app.data.autoresearch.ListItemAutoResearch;
import app.data.autoresearch.Researches;
import app.data.planets.ColonyData;
import app.data.planets.Planets;
import ogame.utils.log.AppLog;

public class DataLoader {
    public static Planets planets;
    public static Researches researches;
    public static ListItemAutoBuilder listItemAutoBuilder;
    public static ListItemAutoResearch listItemAutoResearch;
    public static ColonyData colonyData;

    public DataLoader(){
        planets = new Planets();
        researches = new Researches();
        listItemAutoBuilder = new ListItemAutoBuilder();
        listItemAutoResearch = new ListItemAutoResearch();
        colonyData = new ColonyData();
        AppLog.print(DataLoader.class.getName(),0,"Bot data loaded success.");
    }
}
