package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.httpclient.Client;
import dk.dtu.compute.se.pisd.roborally.fileaccess.SerializeState;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import javafx.application.Platform;

public class Updater extends Thread {
    GameController gameController;

    Client client;

    public void run(){
        while (true) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            updateBoardUi();
        }
    }

    public void updateBoardUi() {
        Board board = SerializeState.deserializeGame(client.getGameState(), true);
        gameController.board = board;
        Platform.runLater(gameController::updateBoard);
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }
}
