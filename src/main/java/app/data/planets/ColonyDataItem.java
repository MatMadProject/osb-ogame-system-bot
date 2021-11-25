package app.data.planets;

import ogame.planets.Planet;
import java.util.Objects;

public class ColonyDataItem{

    private final String id;
    private final String coordinate;
    private final String diameter;
    private final String maxFields;
    private final String minTemperature;
    private final String maxTemperature;
    private final String SEPARATOR =";";

    public ColonyDataItem(String loadedText) {
        String [] separatedText = loadedText.split(SEPARATOR);
        id = separatedText[0];
        coordinate = separatedText[1];
        diameter = separatedText[2];
        maxFields = separatedText[3];
        minTemperature = separatedText[4];
        maxTemperature = separatedText[5];
    }

    public ColonyDataItem(Planet planet) {
        id = planet.getId();
        coordinate = planet.getCoordinate().getText();
        diameter = planet.getDiameter()+"";
        maxFields = planet.getFields().getMax()+"";
        minTemperature = planet.getTemperature().getMin()+"";
        maxTemperature = planet.getTemperature().getMax()+"";

    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColonyDataItem that = (ColonyDataItem) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return
                id + SEPARATOR +
                coordinate + SEPARATOR +
                diameter + SEPARATOR +
                maxFields + SEPARATOR +
                minTemperature + SEPARATOR +
                maxTemperature + SEPARATOR;
    }
}
