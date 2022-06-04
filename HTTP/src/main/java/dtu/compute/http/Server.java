package dtu.compute.http;

import org.springframework.stereotype.Service;

public class Server {
    private String id;
    private String title;
    private transient String gameState;
    private int amountOfPlayers;

    public Server(String title, int id){
        this.id = String.valueOf(id);
        this.title = title;
        this.amountOfPlayers = 1;
    }

    public void addPlayer(){
        amountOfPlayers++;
    }

    public String getGameState() {
        return gameState;
    }

    public void setGameState(String gameState) {
        this.gameState = gameState;
    }
}
