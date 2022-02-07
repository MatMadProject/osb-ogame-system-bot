package app.data.autobuilder;

import ogame.DataTechnology;
import ogame.buildings.RequiredResources;

public class LevelBuildingItem {
    private final String millisecondsTimeStamp;
    private final String dataTechnologyValue;
    private final String level;
    private final String metalRequired;
    private final String crystalRequired;
    private final String deuteriumRequired;
    private final String energyRequired;
    private final String SEPARATOR =";";
    private final DataTechnology dataTechnology;

    public LevelBuildingItem(String millisecondsTimeStamp, String dataTechnologyValue, String metalRequired,
                             String crystalRequired, String deuteriumRequired, String energyRequired, String level) {
        this.millisecondsTimeStamp = millisecondsTimeStamp;
        this.dataTechnologyValue = dataTechnologyValue;
        this.level = level;
        this.metalRequired = metalRequired;
        this.crystalRequired = crystalRequired;
        this.deuteriumRequired = deuteriumRequired;
        this.energyRequired = energyRequired;
        this.dataTechnology = DataTechnology.getFromValue(dataTechnologyValue);
    }
    public LevelBuildingItem(long millisecondsTimeStamp, DataTechnology dataTechnology, RequiredResources requiredResources, int level) {
        this.millisecondsTimeStamp = String.valueOf(millisecondsTimeStamp);
        this.dataTechnologyValue = dataTechnology.getValue();
        this.level = String.valueOf(level);
        this.metalRequired = String.valueOf(requiredResources.getMetal());
        this.crystalRequired = String.valueOf(requiredResources.getCrystal());
        this.deuteriumRequired = String.valueOf(requiredResources.getDeuterium());
        this.energyRequired = String.valueOf(requiredResources.getEnergy());
        this.dataTechnology = dataTechnology;
    }


    public LevelBuildingItem(String loadedText){
        String [] separatedText = loadedText.split(SEPARATOR);
        millisecondsTimeStamp = separatedText[0];
        dataTechnologyValue = separatedText[1];
        metalRequired = separatedText[2];
        crystalRequired = separatedText[3];
        deuteriumRequired = separatedText[4];
        energyRequired = separatedText[5];
        level = separatedText[6];
        this.dataTechnology = DataTechnology.getFromValue(dataTechnologyValue);
    }

    public String getMillisecondsTimeStamp() {
        return millisecondsTimeStamp;
    }

    public String getDataTechnologyValue() {
        return dataTechnologyValue;
    }

    public String getMetalRequired() {
        return metalRequired;
    }

    public String getCrystalRequired() {
        return crystalRequired;
    }

    public String getDeuteriumRequired() {
        return deuteriumRequired;
    }

    public String getEnergyRequired() {
        return energyRequired;
    }

    public DataTechnology getDataTechnology() {
        return dataTechnology;
    }

    public String getLevel() {
        return level;
    }

    @Override
    public String toString() {
        return millisecondsTimeStamp + SEPARATOR +
                dataTechnologyValue + SEPARATOR +
                metalRequired + SEPARATOR +
                crystalRequired + SEPARATOR +
                deuteriumRequired + SEPARATOR +
                energyRequired + SEPARATOR +
                level + SEPARATOR;
    }
}
