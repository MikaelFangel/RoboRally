package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.httpclient.Client;
import dk.dtu.compute.se.pisd.roborally.fileaccess.SerializeState;
import javafx.application.Platform;

public class Updater extends Thread {
    GameController gameController;
    boolean update = true;
    boolean run = true;
    Client client;

    public void run() {
        while (run) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (update) {
                gameController.refreshUpdater();
                updateBoardUi();
            }
        }
    }

    public void updateBoardUi() {
        if (!gameController.board.gameOver){
            gameController.board = SerializeState.deserializeGame(client.getGameState(), true);
            Platform.runLater(gameController::updateBoard);
        }
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

    public boolean getUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public void setRun(boolean run) {
        this.run = run;
    }
}
