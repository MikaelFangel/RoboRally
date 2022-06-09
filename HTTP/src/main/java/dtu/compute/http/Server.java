package dtu.compute.http;

/**
 * @author Christian Andersen
 */
public class Server {
    private final String id;
    private transient String gameState;
    private int amountOfPlayers;
    private int maxAmountOfPlayers;
    private transient boolean[] playerSpotFilled;

    public Server(String title, int id) {
        this.id = String.valueOf(id);
        this.amountOfPlayers = 1;
    }

    public void addPlayer() {
        amountOfPlayers++;
    }

    public void removePlayer() {
        amountOfPlayers--;
    }

    public boolean isEmpty() {
        return amountOfPlayers == 0;
    }

    public int getAmountOfPlayers() {
        return amountOfPlayers;
    }

    public String getGameState() {
        return gameState;
    }

    public void setGameState(String gameState) {
        this.gameState = gameState;
    }

    public String getId() {
        return id;
    }

    public int getMaxAmountOfPlayers() {
        return maxAmountOfPlayers;
    }

    public void setMaxAmountOfPlayers(int amountOfPlayers) {
        this.maxAmountOfPlayers = amountOfPlayers;
        this.playerSpotFilled = new boolean[amountOfPlayers];
        playerSpotFilled[0] = true;
    }

    public int getARobot() {
        for (int i = 0; i < maxAmountOfPlayers; i++)
            if (!playerSpotFilled[i]) {
                playerSpotFilled[i] = true;
                return i;
            }
        return 0; //shouldn't happend
    }

    public void setPlayerSpotFilled(int i, boolean flag) {
        playerSpotFilled[i] = flag;
    }
}
