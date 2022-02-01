package app.data;

import app.data.autobuilder.ListItemAutoBuilder;
import app.data.autoresearch.ListItemAutoResearch;
import app.data.autoresearch.Researches;
import app.data.shipyard.ListDefenceItem;
import app.data.expedition.Expeditions;
import app.data.planets.ColonyData;
import app.data.planets.Planets;
import app.data.player.PlayerData;
import app.data.shipyard.ListShipItem;
import ogame.utils.log.AppLog;

public class DataLoader {
    public static Planets planets;
    public static Researches researches;
    public static ListItemAutoBuilder listItemAutoBuilder;
    public static ListItemAutoResearch listItemAutoResearch;
    public static ColonyData colonyData;
    public static PlayerData playerData;
    public static Expeditions expeditions;
    public static ListDefenceItem listDefenceItem;
    public static ListShipItem listShipItem;

    public DataLoader(){
        planets = new Planets();
        researches = new Researches();
        expeditions = new Expeditions();
        listDefenceItem = new ListDefenceItem();
        listItemAutoBuilder = new ListItemAutoBuilder();
        listItemAutoResearch = new ListItemAutoResearch();
        listShipItem = new ListShipItem();
        colonyData = new ColonyData();
        playerData = new PlayerData();
        AppLog.print(DataLoader.class.getName(),0,"Bot data loaded success.");
    }
}
