package dtu.compute.http;

public interface IStatusComm {
    void updateGame(String id, String gameState);

    String getGameState(String serverId);

    String hostGame(String title);

    String listGames();

    String joinGame(String serverToJoin);
    void leaveGame(String serverId, int i);
}

