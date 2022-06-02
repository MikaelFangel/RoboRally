package dk.dtu.compute.se.pisd.httpclient;

import java.util.Date;

public interface IStatusComm {
    public Date lastUpdateTime = new Date(System.currentTimeMillis());

    public Date getLastUpdate();

    public void updateGame(String gameState);

    public String getGameState();
}
