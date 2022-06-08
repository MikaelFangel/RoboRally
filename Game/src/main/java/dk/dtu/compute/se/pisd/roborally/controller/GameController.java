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

import dk.dtu.compute.se.pisd.httpclient.Client;
import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.fileaccess.SerializeState;
import dk.dtu.compute.se.pisd.roborally.model.*;
import dk.dtu.compute.se.pisd.roborally.view.BoardView;
import javafx.application.Platform;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class GameController {
    public Board board;
    private int playerNum; // Given from the server
    final public RobotMovementController rmc;
    final private AppController appController;

    public final Client client;

    private boolean isNewlyLoadedDefaultBoard = false;
    private boolean skipProgrammingPhase = true;

    private Updater updater;

    public GameController(AppController appController, @NotNull Board board, Client client) {
        this.appController = appController;
        this.board = board;
        this.client = client;
        rmc = new RobotMovementController(this);

        if (client != null) {
            client.updateGame(SerializeState.serializeGame(board));

            // Remove print
            playerNum = client.getRobotNumber();
            System.out.println("Robot number: " + playerNum);

            updater = new Updater();
            updater.setGameController(this);
            updater.setClient(client);
            updater.start();
        }
    }

    /**
     * This is just some dummy controller operation to make a simple move to see something
     * happening on the board. This method should eventually be deleted!
     *
     * @param space the space to which the current player should move
     */
    public void moveCurrentPlayerToSpace(@NotNull Space space) {
        if (space.board == board) {
            Player currentPlayer = board.getCurrentPlayer();
            if (currentPlayer != null && space.getPlayer() == null) {
                currentPlayer.setSpace(space);

                // TODO Implemented in rest of game controller with correct player change
                if (space.getActions().size() > 0) {
                    FieldAction action = space.getActions().get(0);
                    action.doAction(this, space);
                }

                int playerNumber = (board.getPlayerNumber(currentPlayer) + 1) % board.getPlayersNumber();
                board.setCurrentPlayer(board.getPlayer(playerNumber));
            }
        }
    }

    /**
     * Start the programming phase and clear all registers
     */
    public void startProgrammingPhase() {
        // All this should be done for the first reload for a newly constructed board
        isNewlyLoadedDefaultBoard = SaveLoadGame.getNewBoardCreated();

        refreshUpdater();

        if (isNewlyLoadedDefaultBoard || !skipProgrammingPhase) {
            board.setPhase(Phase.PROGRAMMING);
            board.setCurrentPlayer(board.getPlayer(0));
            board.setStep(0);

            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Player player = board.getPlayer(i);
                if (player != null) {
                    for (int j = 0; j < Player.NO_REGISTERS; j++) {
                        CommandCardField field = player.getProgramField(j);
                        field.setCard(null);
                        field.setVisible(true);
                    }

                    for (int j = 0; j < Player.NO_CARDS; j++) {
                        CommandCardField field = player.getCardField(j);
                        if (!player.getDamagecards().isEmpty()) {
                            if (player.getDamagecards().size() > j) {
                                field.setCard(new CommandCard(player.getDamagecards().get(j)));
                            } else
                                field.setCard(generateRandomCommandCard());
                        } else
                            field.setCard(generateRandomCommandCard());
                        field.setVisible(true);

                    }
                }
            }
        } else {
            skipProgrammingPhase = false;
        }
    }

    public CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        ArrayList<Command> commandList = new ArrayList<>();
        for (int i = 0; i < 9; i++){
            commandList.add(commands[i]);
        }
        int random = (int) (Math.random() * commandList.size()); //TODO her er du
        return new CommandCard(commandList.get(random));
    }

    public static CommandCard generateRandomDamageCard() {
        Command[] commands = Command.values();
        ArrayList<Command> dmgCommandList = new ArrayList<>();
        for (int i = 9; i < 13; i++){
            dmgCommandList.add(commands[i]);
        }
        int random = (int) (Math.random() * dmgCommandList.size()); //TODO bruger måske
        return new CommandCard(dmgCommandList.get(random));
    }

    public CommandCard generateRandomSpecialCard(){
        Command[] commands = Command.values();
        ArrayList<Command> specCommandList = new ArrayList<>();   //TODO bruger måske
        for (int i = 13; i < 19; i++) {
            specCommandList.add(commands[i]);
        }
        int random = (int) (Math.random() * specCommandList.size());
        return new CommandCard(specCommandList.get(random));
    }



    /**
     * Changes the phase from programming to activation.
     */
    public void finishProgrammingPhase() {
        // TODO make if-statement to check whether it's singleplayer or not, else it will not work.

        if (board.getPlayerNumber(board.getCurrentPlayer()) == board.getPlayers().size()-1){
            makeProgramFieldsInvisible();
            makeProgramFieldsVisible(0);
            doPriorityAntennaAction();

            // Reset all energy cubes
            for (Space[] row : board.getSpaces()) {
                for (Space space : row) {
                    if (space.getActions().size() > 0 &&
                            space.getActions().get(0) instanceof Energy energy) {
                        energy.setHasEnergyCube(true);
                    }
                }
            }

            board.setPhase(Phase.ACTIVATION);
            board.setCurrentPlayer(board.getPlayer(0));
            board.setStep(0);

            if (client != null){
                refreshUpdater();
                pushGameState();
            }

        } else if (client != null) {
            changePlayer(board.getCurrentPlayer(), board.step);
        }



    }

    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_REGISTERS) {
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Player player = board.getPlayer(i);
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }

    /**
     * Execute all players registers iteratively until empty og phase change
     */
    public void executePrograms() {
        board.setStepMode(false);
        continuePrograms();
    }

    /**
     * Execute one player register at the time and change player afterwards
     */
    public void executeStep() {
        board.setStepMode(true);
        continuePrograms();
    }

    /**
     * Set the phase back to activation, execute the given command and the change player
     *
     * @param command the command to execute before player change
     */
    public void executeCommandAndResumeActivation(Command command) {
        board.setPhase(Phase.ACTIVATION);

        Player currentPlayer = board.getCurrentPlayer();
        executeCommand(currentPlayer, command);
        changePlayer(currentPlayer, board.getStep());
    }

    public void updateBoard() {
        appController.getRoboRally().createBoardView(this);
    }

    private void continuePrograms() {
        do {
            executeNextStep();

        } while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode());
    }

    protected void executeNextStep() {
        Player currentPlayer = board.getCurrentPlayer();
        if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null) {
            int step = board.getStep();
            if (step >= 0 && step < Player.NO_REGISTERS) {
                CommandCard card = currentPlayer.getProgramField(step).getCard();
                if (card != null) {
                    Command command = card.command;
                    executeCommand(currentPlayer, command);
                }
                if (board.getPhase() == Phase.ACTIVATION) {
                    changePlayer(currentPlayer, step);
                }
            } else {
                // this should not happen
                assert false;
            }
        } else {
            // this should not happen
            assert false;
        }
    }

    public void endGame() {
        appController.stopGame();

    }

    private void doPriorityAntennaAction() {
        Space antennaSpace = board.getPriorityAntennaSpace();
        antennaSpace.getActions().get(0).doAction(this, antennaSpace);
    }

    public void assertPlayerPriorityAndChangeBoardPlayers(Space antennaSpace) {
        List<Player> players = board.getPlayers();
        List<Integer> playersPriority = new ArrayList<>();

        // Get distance for each player to the antenna
        for (Player player : players) {
            Space playerSpace = player.getSpace();

            double totalDistance = Math.sqrt(Math.pow(Math.abs(playerSpace.x - antennaSpace.x), 2) + Math.pow(Math.abs(playerSpace.y - antennaSpace.y), 2));
            totalDistance = Math.round(totalDistance * 100); // To remove decimals
            playersPriority.add((int) totalDistance);
        }

        // Prioritize player according to their distance to the antenna.
        List<Player> prioritizedPlayers = new ArrayList<>();
        for (int i = 0; i <= (board.width + board.height) * 100; i++) {
            for (int j = 0; j < players.size(); j++) {
                if (playersPriority.get(j) == i) {
                    prioritizedPlayers.add(players.get(j));
                }
            }
        }

        board.setPlayers(prioritizedPlayers);
        board.setCurrentPlayer(prioritizedPlayers.get(0));

        recreatePlayersView();
    }

    private void changePlayer(Player currentPlayer, int step) {
        int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
        if (nextPlayerNumber < board.getPlayersNumber()) {
            board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
        } else {
            step++;
            boardElementsActivationOrder();
            if (step < Player.NO_REGISTERS) {
                makeProgramFieldsVisible(step);
                board.setStep(step);
                board.setCurrentPlayer(board.getPlayer(0));
            } else {
                startProgrammingPhase();
            }
        }
        pushGameState();
        refreshUpdater();
    }

    private void executeCommand(@NotNull Player player, Command command) {
        if (player.board == board && command != null) {
            // XXX This is a very simplistic way of dealing with some basic cards and
            //     their execution. This should eventually be done in a more elegant way
            //     (this concerns the way cards are modelled as well as the way they are executed).
            switch (command) {
                case MOVE1 -> rmc.moveForward(player, 1);
                case MOVE2 -> rmc.moveForward(player, 2);
                case MOVE3, SPEEDROUTINE -> rmc.moveForward(player, 3);
                case RIGHT -> rmc.turnRight(player);
                case LEFT -> rmc.turnLeft(player);
                case OPTION_LEFT_RIGHT, SANDBOXROUTINE, WEASELROUTINE -> board.setPhase(Phase.PLAYER_INTERACTION);
                case UTURN -> rmc.uTurn(player);
                case MOVEBACK -> rmc.moveBackward(player);
                case AGAIN, REPEATROUTINE -> rmc.again(player, board.getStep());
                case SPAM -> rmc.Removespam(player);
                case ENERGYROUTINE -> energyRoutine(player);
                default -> {
                }
                // DO NOTHING (for now)
            }
            if (client != null)
                client.updateGame(SerializeState.serializeGame(board));
        }
    }


    /**
     * Move one card from one place to another
     *
     * @param source the card to move
     * @param target the target to move the source card to
     * @return true if the operation was successful and false if not
     */
    public boolean moveCards(@NotNull CommandCardField source, @NotNull CommandCardField target) {
        CommandCard sourceCard = source.getCard();
        CommandCard targetCard = target.getCard();
        if (sourceCard != null && targetCard == null) {
            target.setCard(sourceCard);
            source.setCard(null);
            return true;
        } else {
            return false;
        }
    }

    public int energyRoutine(@NotNull Player player) {
        int playerEnergyCount = player.getEnergyCount();
        playerEnergyCount++;
        return playerEnergyCount;
    }

    public void recreatePlayersView() {
        BoardView boardView = appController.getRoboRally().getBoardView();
        boardView.updatePlayersView();
    }

    private void boardElementsActivationOrder() {
        List<Player> players = board.getPlayers();
        ArrayDeque<Player> actionsToBeHandled = new ArrayDeque<>(board.getPlayersNumber());

        for (int i = 2; i > 0; i--) {
            for (Player player : players) {
                if (!player.getSpace().getActions().isEmpty() &&
                        player.getSpace().getActions().get(0) instanceof ConveyorBelt spaceBelt &&
                        (spaceBelt.getNumberOfMoves() == i)) { //check if the space have an action
                    actionsToBeHandled.add(player);
                }
            }
            int playersInQueue = actionsToBeHandled.size();
            int j = 0;
            while (!actionsToBeHandled.isEmpty()) {
                Player currentPlayer = actionsToBeHandled.pop();
                Space startLocation = currentPlayer.getSpace();
                if (!currentPlayer.getSpace().getActions().get(0).doAction(this, currentPlayer.getSpace())) {             //we couldn't move the player right now, so we re adds them to the queue
                    currentPlayer.setSpace(startLocation);
                    actionsToBeHandled.add(currentPlayer);
                }
                j++;
                if (j == playersInQueue)
                    if (playersInQueue == actionsToBeHandled.size()) { //if we tried to move all players in queue and had no success
                        actionsToBeHandled.clear();
                        break;
                    } else {
                        j = 0;
                        playersInQueue = actionsToBeHandled.size();
                    }
            }
        }

        //activate PushPanel
        for (Player player : players) {
            if (!player.getSpace().getActions().isEmpty() &&
                    player.getSpace().getActions().get(0) instanceof PushPanel)
                player.getSpace().getActions().get(0).doAction(this, player.getSpace());
        }

        //activate gears
        for (Player player : players) {
            if (!player.getSpace().getActions().isEmpty() &&
                    player.getSpace().getActions().get(0) instanceof RotatingGear)
                player.getSpace().getActions().get(0).doAction(this, player.getSpace());
        }

        //activate lasers
        for (Space[] space : board.getSpaces()) {
            for (Space space1 : space) {
                if (!space1.getActions().isEmpty() &&
                        space1.getActions().get(0) instanceof Laser)
                    space1.getActions().get(0).doAction(this, space1);
            }
        }

        //TODO implement robot Lasers fire here

        //activate energy space
        for (Player player : players) {
            if (!player.getSpace().getActions().isEmpty() &&
                    player.getSpace().getActions().get(0) instanceof Energy)
                player.getSpace().getActions().get(0).doAction(this, player.getSpace());
        }

        //activate checkpoints
        for (Player player : players) {
            if (!player.getSpace().getActions().isEmpty() &&
                    player.getSpace().getActions().get(0) instanceof Checkpoint)
                player.getSpace().getActions().get(0).doAction(this, player.getSpace());
        }
    }


    public void refreshUpdater(){
        if (client != null) {
            if (isMyTurn()) {
                updater.setUpdate(false);
                System.out.println("No longer updates");
            } else {
                updater.setUpdate(true);
                System.out.println("Now updates");
            }
        }
    }
    private void pullGameState(){
        Board board = SerializeState.deserializeGame(client.getGameState(), true);
        this.board = board;
        Platform.runLater(this::updateBoard);
    }

    private void pushGameState(){
        client.updateGame(SerializeState.serializeGame(board));
    }

    public boolean isMyTurn(){
        if (board.getCurrentPlayer() == board.getPlayer(playerNum)){
            System.out.println("Is my turn");
            return true;
        }
        else {
            System.out.println("Is not my turn");
            return false;
        }

    }

    public void setPlayerNumber(int num){
        playerNum = num;
    }

    public int getPlayerNumber(){
        return playerNum;
    }
}
