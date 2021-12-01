package app.data.player;

public class PlayerDataItem {
    private final String timeMiliseconds;
    private final String time;
    private final String date;
    private final String points;
    private final String position;
    private final String honorPoints;
    private final String SEPARATOR =";";

    public PlayerDataItem(long timeMiliseconds, String time, String date, int points, int position, int honorPoints) {
        this.timeMiliseconds = Long.toString(timeMiliseconds);
        this.time = time;
        this.date = date;
        this.points = Integer.toString(points);
        this.position = Integer.toString(position);
        this.honorPoints = Integer.toString(honorPoints);
    }

    public PlayerDataItem(String loadedText){
        String [] separatedText = loadedText.split(SEPARATOR);
        timeMiliseconds = separatedText[0];
        date = separatedText[1];
        time = separatedText[2];
        points = separatedText[3];
        position = separatedText[4];
        honorPoints = separatedText[5];
    }

    public String getTimeMiliseconds() {
        return timeMiliseconds;
    }

    @Override
    public String toString() {
        return  timeMiliseconds + SEPARATOR +
                date + SEPARATOR +
                time + SEPARATOR +
                points + SEPARATOR +
                position + SEPARATOR +
                honorPoints + SEPARATOR;
    }
}
