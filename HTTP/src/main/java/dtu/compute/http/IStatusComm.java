package dtu.compute.http;

public interface IStatusComm {

    public void updateGame(String gameState);
    public String getGameState();
    public boolean conGreeting();
}