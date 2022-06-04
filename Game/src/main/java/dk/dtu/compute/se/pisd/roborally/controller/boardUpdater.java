package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.httpclient.Client;
import dk.dtu.compute.se.pisd.roborally.fileaccess.SerializeState;

public class boardUpdater extends Thread {
    public void run(GameController gameController, Client client) throws InterruptedException {
        while(true) {
            gameController.board = SerializeState.deserializeGame(client.getGameState(), false);
            Thread.sleep(5000);
        }
    }
}
