package app.data;

import app.data.autobuilder.LevelBuildings;
import app.data.autobuilder.ListItemAutoBuilder;
import app.data.autobuilder.ListItemAutoResearch;
import app.data.autobuilder.Researches;
import app.data.local.Language;
import app.data.local.LocalNameGameObjects;
import app.data.shipyard.ListDefenceItem;
import app.data.expedition.Expeditions;
import app.data.planets.ColonyData;
import app.data.planets.Planets;
import app.data.player.PlayerData;
import app.data.shipyard.ListShipItem;
import app.data.transport.ListTransportItem;
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
    public static LevelBuildings levelBuildings;
    public static ListTransportItem listTransportItem;
    public static LocalNameGameObjects localNameGameObjects;

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
        levelBuildings = new LevelBuildings();
        listTransportItem = new ListTransportItem();
        localNameGameObjects = new LocalNameGameObjects(Language.GERMANY); // todo darkosb
        AppLog.print(DataLoader.class.getName(),0,"Bot data loaded success.");
    }
}
