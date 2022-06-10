package dk.dtu.compute.se.pisd.httpclient;

/**
 * This class is not directly used, but is needed for Json to understand how the string from the server is formattet.
 *
 * @author Christian Andersen
 */
public class Game {
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

    public int getMaxAmountOfPlayers() {
        return maxAmountOfPlayers;
    }
}
