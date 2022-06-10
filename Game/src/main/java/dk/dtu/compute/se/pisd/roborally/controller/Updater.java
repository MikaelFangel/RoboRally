package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.httpclient.Client;
import dk.dtu.compute.se.pisd.roborally.fileaccess.SerializeState;
import javafx.application.Platform;

import java.util.TimerTask;

/**
 * The updater is a runnable thread that about every 1 second gets the latest game state from the server
 * and when the game state is received it the deserializes the json file and sends an async message
 * to update the ui.
 *
 * @author Mikael Fangel
 */
public class Updater extends TimerTask {
    private static GameController gameController;
    private static AppController appController;
    private static boolean update = true;
    private static Client client;

    public void run() {
        if (update) {
            gameController.refreshUpdater();
            if (!gameController.board.gameOver) {
                String jsonBoard = client.getGameState();
                if (!jsonBoard.contains("error")) {
                    gameController.board = SerializeState.deserializeGame(client.getGameState(), true);
                    Platform.runLater(this::updateBoard);
                } else
                    cancel();

            }
        }
    }

    /**
     * Generates a new board view when board is changed
     */
    public void updateBoard() {
        appController.getRoboRally().createBoardView(gameController);
    }

    public static void setClient(Client client) {
        Updater.client = client;
    }

    public static void setGameController(GameController gameController) {
        Updater.gameController = gameController;
    }

    public static boolean getUpdate() {
        return update;
    }

    public static void setUpdate(boolean update) {
        Updater.update = update;
    }

    public static void setAppController(AppController appController) {
        Updater.appController = appController;
    }
}
