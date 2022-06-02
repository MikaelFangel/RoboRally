package dk.dtu.compute.se.pisd.httpclient;

import java.util.Date;

public interface IStatusComm {
    public void updateGame(String gameState);

    public String getGameState();
}
