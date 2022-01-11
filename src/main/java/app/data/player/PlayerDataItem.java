package app.data.player;

public class PlayerDataItem {
    private final String timeMilliseconds;
    private final String time;
    private final String date;
    private final String points;
    private final String position;
    private final String honorPoints;
    private final String SEPARATOR =";";

    public PlayerDataItem(long timeMilliseconds, String time, String date, int points, int position, int honorPoints) {
        this.timeMilliseconds = Long.toString(timeMilliseconds);
        this.time = time;
        this.date = date;
        this.points = Integer.toString(points);
        this.position = Integer.toString(position);
        this.honorPoints = Integer.toString(honorPoints);
    }

    public PlayerDataItem(String loadedText){
        String [] separatedText = loadedText.split(SEPARATOR);
        timeMilliseconds = separatedText[0];
        date = separatedText[1];
        time = separatedText[2];
        points = separatedText[3];
        position = separatedText[4];
        honorPoints = separatedText[5];
    }

    public String getTimeMilliseconds() {
        return timeMilliseconds;
    }

    @Override
    public String toString() {
        return  timeMilliseconds + SEPARATOR +
                date + SEPARATOR +
                time + SEPARATOR +
                points + SEPARATOR +
                position + SEPARATOR +
                honorPoints + SEPARATOR;
    }
}
