package app.data.local;

public enum Language {

    GERMANY("GER","Germany"),
    POLAND("PL","Poland");

    private final String SYMBOL;
    private final String LANGUAGE;

    Language(String SYMBOL, String LANGUAGE) {
        this.SYMBOL = SYMBOL;
        this.LANGUAGE = LANGUAGE;
    }

    public String getSYMBOL() {
        return SYMBOL;
    }

    public String getLANGUAGE() {
        return LANGUAGE;
    }

    @Override
    public String toString() {
        return SYMBOL  + " - " + LANGUAGE;
    }
}
