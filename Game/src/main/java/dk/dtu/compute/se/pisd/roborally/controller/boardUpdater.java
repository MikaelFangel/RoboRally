package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.httpclient.Client;
import dk.dtu.compute.se.pisd.roborally.fileaccess.SerializeState;
import dk.dtu.compute.se.pisd.roborally.model.Board;

public class boardUpdater extends Thread {
    private GameController gameController;
    private Client client;

    public void run() {
        while (true) {
            updateBoard();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void updateBoard() {
        gameController.board = SerializeState.deserializeGame(client.getGameState(), false);
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }
}
