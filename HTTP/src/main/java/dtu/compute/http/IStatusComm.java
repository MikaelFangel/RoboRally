package dk.dtu.compute.se.pisd.httpclient;

import java.util.Date;

public interface IStatusComm {
    void updateGame(String id, String gameState);

    String getGameState(String serverId);

    void hostGame();

    String listGames();

    String joinGame(String serverToJoin);
}
