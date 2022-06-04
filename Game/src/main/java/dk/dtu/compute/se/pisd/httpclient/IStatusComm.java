package dk.dtu.compute.se.pisd.httpclient;

import java.util.Date;

public interface IStatusComm {
    void updateGame(String gameState);

    String getGameState();

    String hostGame(String title);

    String listGames();

    String joinGame(String serverToJoin);
}
