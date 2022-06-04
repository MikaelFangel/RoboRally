/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.designpatterns.observer.Observer;
import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;

import dk.dtu.compute.se.pisd.httpclient.Client;
import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.model.Board;

import dtu.compute.http.HttpApplication;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class AppController implements Observer {


    final private List<Integer> PLAYER_NUMBER_OPTIONS = Arrays.asList(2, 3, 4, 5, 6);
    final private List<String> BOARD_OPTIONS = Arrays.asList("dizzy_highway", "dodge_this", "high_octane", "risky_crossing");

    final private RoboRally roboRally;

    private GameController gameController;

    private Client client = new Client();

    private boolean serverStart = false;

    public AppController(@NotNull RoboRally roboRally) {
        this.roboRally = roboRally;
    }

    public void newGame() {
        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(PLAYER_NUMBER_OPTIONS.get(0), PLAYER_NUMBER_OPTIONS);
        dialog.setTitle("Player number");
        dialog.setHeaderText("Select number of players");
        Optional<Integer> result = dialog.showAndWait();

        if (result.isPresent()) {
            if (gameController != null) {
                // The UI should not allow this, but in case this happens anyway.
                // give the user the option to save the game or abort this operation!
                if (!stopGame()) {
                    return;
                }
            }
            ChoiceDialog<String> dialogB = new ChoiceDialog<>(BOARD_OPTIONS.get(0), BOARD_OPTIONS);
            dialogB.setTitle("CHOOSE BOARD");
            dialogB.setHeaderText("Select which board to play");
            Optional<String> resultB = dialogB.showAndWait();
            if (resultB.isPresent()) {
                Board board = SaveLoadGame.newBoard(result.get(), resultB.get());
                //initializePlayers(board);
                setupGameController(board);
            }
        }
    }

    public void saveGame() {
        TextInputDialog dialogS = new TextInputDialog();

        dialogS.setTitle("SAVE GAME");
        dialogS.setHeaderText("Enter a Save game name");

        final Optional<String> resultS = dialogS.showAndWait();
        resultS.ifPresent(s -> SaveLoadGame.saveBoardToDisk(gameController.board, s));
    }

    public void loadGame() {
        if (gameController == null) {
            TextInputDialog dialogL = new TextInputDialog();
            dialogL.setTitle("LOAD GAME");
            dialogL.setHeaderText("Enter a Save game name  you want to load");

            final Optional<String> resultL = dialogL.showAndWait();
            if (resultL.isPresent()) {
                Board board = SaveLoadGame.loadBoard(resultL.get());
                setupGameController(board);
            }
        }
    }

    public void startServer(){
        client.hostGame("my first game");
    }

    public void closeServer(){
        //client.setServer("");
        serverStart = false;
    }

    public void connectToServer(){
        TextInputDialog popup = new TextInputDialog("Insert IP");
        popup.setTitle("Connect");
        Optional<String> input = popup.showAndWait();
        try {
            if (input.isPresent())
                client.setServer(input.get());
        } catch (Client.IllegalIPExeception e) {
            e.printStackTrace();
        }
    }

    public void disconnectFromServer(){
        stopGame();
    }

    private void setupGameController(Board board) {
        gameController = new GameController(this, Objects.requireNonNull(board));
        board.setCurrentPlayer(board.getPlayer(0));
        gameController.startProgrammingPhase();

        roboRally.createBoardView(gameController);
    }

    /**
     * Stop playing the current game, giving the user the option to save
     * the game or to cancel stopping the game. The method returns true
     * if the game was successfully stopped (with or without saving the
     * game); returns false, if the current game was not stopped. In case
     * there is no current game, false is returned.
     *
     * @return true if the current game was stopped, false otherwise
     */
    public boolean stopGame() {
        if (gameController != null) {

            gameController = null;
            roboRally.createBoardView(null);
            return true;
        }
        return false;
    }

    public void exit() {
        if (gameController != null) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Exit RoboRally?");
            alert.setContentText("Are you sure you want to exit RoboRally?");
            Optional<ButtonType> result = alert.showAndWait();

            if (result.isEmpty() || result.get() != ButtonType.OK) {
                return; // return without exiting the application
            }
        }

        // If the user did not cancel, the RoboRally application will exit
        // after the option to save the game
        if (gameController == null || stopGame()) {
            Platform.exit();
        }
    }

    public boolean isGameRunning() {
        return gameController != null;
    }

    public boolean isServerStarted(){return serverStart;}


    @Override
    public void update(Subject subject) {
        // XXX do nothing for now
    }
    public void initializePlayers(Board board){
        for (int i = 0; i < PLAYER_NUMBER_OPTIONS.get(0);i++){
           board.getPlayer(i).populateCards(gameController);

        }
    }


    public RoboRally getRoboRally() {
        return roboRally;
    }

}
