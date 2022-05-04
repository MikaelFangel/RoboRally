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

import dk.dtu.compute.se.pisd.roborally.fileaccess.SaveLoadGame;
import dk.dtu.compute.se.pisd.roborally.model.*;
import dk.dtu.compute.se.pisd.roborally.view.BoardView;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class GameController {

    final public Board board;
    final public RobotMovementController rmc;
    final private AppController appController;

    public GameController(AppController appController, @NotNull Board board) {
        this.appController = appController;
        this.board = board;
        rmc = new RobotMovementController(this, board);
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
        boolean gameLoaded = SaveLoadGame.getBoardLoaded();
        if (!gameLoaded) {
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
                        field.setCard(generateRandomCommandCard());
                        field.setVisible(true);
                    }
                }
            }
        }
    }

    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length); //TODO her er du
        Stack(commands[random]);
        return new CommandCard(commands[random]);
    }

    /**
     * Changes the phase from programming to activation.
     */
    public void Stack(Command Card) {
        Stack<Command> Cards = new Stack<>();
        for (int i = 0; i < Command.values().length; i++) { //TODO NOT DONE
            Cards.push(Card);
        }
        if ( Card.isInteractive()){
            Cards.remove(0);
        }

    }
    public void finishProgrammingPhase() {
        makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);
        doPriortyAntennaAction();

        board.setPhase(Phase.ACTIVATION);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);
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

    private void doPriortyAntennaAction() {
        Space antennaSpace = board.getPriorityAntennaSpace();
        antennaSpace.getActions().get(0).doAction(this, antennaSpace);
    }

    public void assertPlayerPriorityAndChangeBoardPlayers(Space antennaSpace) {
        List<Player> players = board.getPlayers();
        int[] playersPriority = new int[players.size()];

        for (int i = 0; i < players.size(); i++) {
            int totalDistance = 0;
            Space playerSpace = players.get(i).getSpace();

            totalDistance += Math.abs(playerSpace.x - antennaSpace.x) + Math.abs(playerSpace.y - antennaSpace.y);
            playersPriority[i] = totalDistance;
        }

        List<Player> prioritizedPlayers = new ArrayList<Player>();
        for (int i = 1; i <= board.width + board.height; i++) {
            for (int j = 0; j < playersPriority.length; j++) {
                if (playersPriority[j] == i) {
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
    }

    private void executeCommand(@NotNull Player player, Command command) {
        if (player.board == board && command != null) {
            // XXX This is a very simplistic way of dealing with some basic cards and
            //     their execution. This should eventually be done in a more elegant way
            //     (this concerns the way cards are modelled as well as the way they are executed).
            switch (command) {
                case MOVE1 -> rmc.moveForward(player, 1);
                case MOVE2 -> rmc.moveForward(player, 2);
                case MOVE3 -> rmc.moveForward(player, 3);
                case RIGHT -> rmc.turnRight(player);
                case LEFT -> rmc.turnLeft(player);
                case OPTION_LEFT_RIGHT -> board.setPhase(Phase.PLAYER_INTERACTION);
                case UTURN -> rmc.uTurn(player);
                case MOVEBACK -> rmc.moveBackward(player);
                case AGAIN -> rmc.again(player, board.getStep());
                default -> {
                }
                // DO NOTHING (for now)
            }
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

    public void boardElementsActivationOrder(){
        List<Player> players = board.getPlayers();
        String[] orderOfExecution = new String[] {"ConveyorBelt"};
        ArrayDeque<Player> actionsToBeHandled = new ArrayDeque<>(board.getPlayersNumber());

        for (String fieldType: orderOfExecution) {
            for (Player player : players) {
                if (player.getSpace().getActions().get(0).getClass().getSimpleName().equals(fieldType)) //check if the space have an action
                    actionsToBeHandled.add(player);
            }
            int playersInQueue = actionsToBeHandled.size();
            int i = 0;
            while (!actionsToBeHandled.isEmpty()) {
                Player currentPlayer = actionsToBeHandled.pop();
                Space startLocation = currentPlayer.getSpace();
                //currentPlayer.getSpace().getActions().get(0).doAction() //TODO snak med mikael om dennes implementering
                if (currentPlayer.getSpace() == startLocation) {             //we couldn't move the player right now, so we re adds them to the queue
                    actionsToBeHandled.add(currentPlayer);
                }
                i++;
                if (i == playersInQueue)
                    if (playersInQueue == actionsToBeHandled.size()) //if we tryed to move all players in queue and had no succes
                        break;
                i = 0;
                playersInQueue = actionsToBeHandled.size();
            }
        }
    }
}
