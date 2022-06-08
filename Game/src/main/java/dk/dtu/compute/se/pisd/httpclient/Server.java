package dk.dtu.compute.se.pisd.httpclient;

public class Server {
    private String id;
    private String title;
    private transient String gameState;
    private int amountOfPlayers;
    private int maxAmountOfPlayers;
    private transient boolean[] playerSpotFilled;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getAmountOfPlayers() {
        return amountOfPlayers;
    }

    public int getMaxAmountOfPlayers(){
        return maxAmountOfPlayers;
    }
}
