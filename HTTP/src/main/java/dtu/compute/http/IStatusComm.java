package dtu.compute.http;

import java.util.Date;

public interface IStatusComm {
    void updateGame(String id, String gameState);

    String getGameState(String serverId);

    String hostGame(String title);

    String listGames();

    String joinGame(String serverToJoin);
}

