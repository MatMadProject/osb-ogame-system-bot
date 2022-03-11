package app.data.local;

import ogame.DataTechnology;

import java.util.Objects;

public class LocalName {
    public final String VALUE;
    public final DataTechnology DATA_TECHNOLOGY;
    private final String SEPARATOR =";";

    public LocalName(String VALUE, DataTechnology DATA_TECHNOLOGY) {
        this.VALUE = VALUE;
        this.DATA_TECHNOLOGY = DATA_TECHNOLOGY;
    }
    public LocalName(String loadedText) {
        String [] separatedText = loadedText.split(SEPARATOR);
        this.DATA_TECHNOLOGY = DataTechnology.getFromValue(separatedText[0]);
        this.VALUE = separatedText[1];
    }

    public String getVALUE() {
        return VALUE;
    }

    public DataTechnology getDATA_TECHNOLOGY() {
        return DATA_TECHNOLOGY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LocalName)) return false;
        LocalName localName = (LocalName) o;
        return Objects.equals(getVALUE(), localName.getVALUE()) && getDATA_TECHNOLOGY() == localName.getDATA_TECHNOLOGY();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getVALUE(), getDATA_TECHNOLOGY());
    }

    @Override
    public String toString() {
        return DATA_TECHNOLOGY.getValue() + SEPARATOR
                + VALUE;
    }
}
