package dtu.compute.http;

import org.springframework.stereotype.Service;

public class Server {
    private String id;
    private String title;
    private transient String gameState;
    private int amountOfPlayers;
    private int maxAmountOfPlayers;

    public Server(String title, int id){
        this.id = String.valueOf(id);
        this.title = title;
        this.amountOfPlayers = 1;
    }

    public void addPlayer(){
        amountOfPlayers++;
    }
    public void removePlayer() {amountOfPlayers--;}
    public boolean isEmpty(){
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

    public String getId(){
        return id;
    }

    public int getMaxAmountOfPlayers() {
        return maxAmountOfPlayers;
    }

    public void setMaxAmountOfPlayers(int amountOfPlayers) {
        this.maxAmountOfPlayers = amountOfPlayers;
    }
}
