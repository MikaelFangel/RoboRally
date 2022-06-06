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
import dk.dtu.compute.se.pisd.httpclient.ServerListView;
import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.exceptions.BoardNotFoundException;
import dk.dtu.compute.se.pisd.roborally.fileaccess.ReadWriteGame;
import dk.dtu.compute.se.pisd.roborally.fileaccess.SerializeState;
import dk.dtu.compute.se.pisd.roborally.model.Board;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import org.jetbrains.annotations.NotNull;

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

    final private RoboRally roboRally;

    private GameController gameController;

    private final Client client = new Client();

    private boolean serverStart = false;

    private boolean serverClientMode = false;

    private final ServerListView slv = new ServerListView(client, this);

    public AppController(@NotNull RoboRally roboRally) {
        this.roboRally = roboRally;
    }

    public void newGame() {
        Optional<Integer> numPlayers = askUserForNumberOfPlayers();

        if (numPlayers.isPresent()) {
            // The UI should not allow this, but in case this happens anyway.
            // give the user the option to save the game or abort this operation!
        if (gameController != null && !stopGame()) return;

        createNewGame(numPlayers.get(), false);
        }
    }

    private void createNewGame(int numPlayers, boolean prevFailed) {
        Optional<String> chosenBoard = askUserWhichDefaultBoard(prevFailed);
        if (chosenBoard.isPresent()){
            try {
                Board board = SaveLoadGame.newBoard(numPlayers, chosenBoard.get());
                setupGameController(board);
                if (client.isConnectedToServer())
                    client.updateGame(SerializeState.serializeGame(board));
            } catch (BoardNotFoundException e){
                createNewGame(numPlayers, true);
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
            createLoadedGame(false);
        }
    }

    private void createLoadedGame(boolean prevFailed){
        Optional<String> chosenBoard = askUserWhichSavedBoard(prevFailed);

        if (chosenBoard.isPresent()){
            try {
                if ("Test204".equals(chosenBoard.get())){
                    System.out.println("Is the same");
                }

                Board board = SaveLoadGame.loadBoard(chosenBoard.get());
                setupGameController(board);
            } catch (BoardNotFoundException e){
                createLoadedGame(true);
            }
        }
    }

    public void hostGame(String... errorMessage) {
        TextInputDialog serverCreation = new TextInputDialog();
        serverCreation.setTitle("Start game server");
        serverCreation.setHeaderText("Server name:");
        if (errorMessage.length != 0){
            serverCreation.setHeaderText(errorMessage[0]);
        }
        Optional<String> result = serverCreation.showAndWait();
        if (result.isEmpty())
            return;
        String response = client.hostGame(String.valueOf(result));
        if (!Objects.equals(response, "success"))
            hostGame(response);
        else {
            serverClientMode = true;
            newGame();
        }
    }

    public void joinGame(String id){
        String message = client.joinGame(id);
        if (message.equals("error"))
            showErrorMessage(new String[]{"Error", "Can't connect to server","refresh and try again"});
    }

    public void connectToServer() {
        slv.addServer(client.listGames());
        slv.viewTable();
    }

    public void disconnectFromServer() {
        client.leaveGame();
    }

    private void setupGameController(Board board) {
        gameController = new GameController(this, Objects.requireNonNull(board), serverClientMode ? client : null);
        initializePlayers(board);
        board.setCurrentPlayer(board.getPlayer(0));
        gameController.startProgrammingPhase();

        roboRally.createBoardView(gameController);
    }

    private Optional<Integer> askUserForNumberOfPlayers(){
        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(PLAYER_NUMBER_OPTIONS.get(0), PLAYER_NUMBER_OPTIONS);
        dialog.setTitle("Player number");
        dialog.setHeaderText("Select number of players");

        return dialog.showAndWait();
    }

    private Optional<String> askUserWhichDefaultBoard(boolean prevFailed){

        List<String> allDefaultBoardNames = ReadWriteGame.getNamesOfDefaultBoard();
        ChoiceDialog<String> dialog = new ChoiceDialog<>(allDefaultBoardNames.get(0), allDefaultBoardNames);

        dialog.setTitle("CHOOSE BOARD");
        if (prevFailed)
            dialog.setHeaderText("The board does not exists. Select another one");
        else
            dialog.setHeaderText("Select which board to play");

        return dialog.showAndWait();
    }

    private Optional<String> askUserWhichSavedBoard(boolean prevFailed){
        // Get all files in resource
        List<String> allSavedBoardNames = ReadWriteGame.getNamesOfSavedBoards();
        ChoiceDialog<String> dialog = new ChoiceDialog<>(allSavedBoardNames.get(0), allSavedBoardNames);

        dialog.setTitle("LOAD GAME");
        if (prevFailed)
            dialog.setHeaderText("The board did not exists. Try another");
        else
            dialog.setHeaderText("Pick a game to load!");

        return dialog.showAndWait();
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


    @Override
    public void update(Subject subject) {
        // XXX do nothing for now
    }

    public void initializePlayers(Board board){
        for (int i = 0; i < board.getPlayersNumber();i++) {
            board.getPlayer(i).populateCards(gameController);
        }

    }

    public RoboRally getRoboRally() {
        return roboRally;
    }

    public void showErrorMessage(String[] reason){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(reason[0]);
        alert.setHeaderText(reason[1]);
        alert.setContentText(reason[2]);
        alert.showAndWait();
    }

}
