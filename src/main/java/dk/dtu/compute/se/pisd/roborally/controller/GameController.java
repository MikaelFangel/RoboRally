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

import dk.dtu.compute.se.pisd.roborally.model.*;
import org.jetbrains.annotations.NotNull;

import static dk.dtu.compute.se.pisd.roborally.model.Command.AGAIN;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class GameController {

    final public Board board;

    public GameController(@NotNull Board board) {
        this.board = board;
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
                int playerNumber = (board.getPlayerNumber(currentPlayer) + 1) % board.getPlayersNumber();
                board.setCurrentPlayer(board.getPlayer(playerNumber));
            }
        }
    }

    /**
     * Start the programming phase and clear all registers
     */
    public void startProgrammingPhase() {
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

    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);
        return new CommandCard(commands[random]);
    }

    /**
     * Changes the phase from programming to activation.
     */
    public void finishProgrammingPhase() {
        makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);
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

    private void executeNextStep() {
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

    private void changePlayer(Player currentPlayer, int step) {
        int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
        if (nextPlayerNumber < board.getPlayersNumber()) {
            board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
        } else {
            step++;
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
                case MOVE1 -> this.moveForward(player,1);
                case MOVE2 -> this.moveForward(player,2);
                case MOVE3 -> this.moveForward(player,3);
                case RIGHT -> this.turnRight(player);
                case LEFT -> this.turnLeft(player);
                case OPTION_LEFT_RIGHT -> board.setPhase(Phase.PLAYER_INTERACTION);
                case UTURN -> this.uTurn(player);
                case MOVEBACK -> this.moveBackward(player);
                case AGAIN -> this.again(player,board.getStep());
                default -> {}
                // DO NOTHING (for now)
            }
        }
    }

    // ------- Card Commands --------

    /**
     * Move the player one space forward in the direction they are pointing.
     * Also accounts for a situation where another player occupies the slot, and pushes them out of the way.
     * The push is recursive to ensure that they ain't pushed onto an occupied slot either.
     * @param player the player which is subject to move
     */
    public void moveForward(@NotNull Player player, int moves) {
        for (int i = 0; i < moves; i++) {
            Space space = player.getSpace();
            if (player.board == board && space != null) {
                Heading heading = player.getHeading();
                Space target = board.getNeighbour(space, heading);
                if (target != null) {
                    if (target.getPlayer() != null) {
                        Player playerBlocking = target.getPlayer();
                        Heading targetCurrentHeading = playerBlocking.getHeading();
                        playerBlocking.setHeading(player.getHeading());
                        moveForward(playerBlocking, 1);
                        playerBlocking.setHeading(targetCurrentHeading);
                    }
                    target.setPlayer(player);
                }
            }
        }
    }

    /**
     * Turn the player right based upon their current heading
     * @param player the player which is subject to move
     */
    public void turnRight(@NotNull Player player) {
        if (player.board == board) {
            player.setHeading(player.getHeading().next());
        }
    }

    /**
     * Turn the player left based upon their current heading
     * @param player the player which is subject to move
     */
    public void turnLeft(@NotNull Player player) {
        if (player.board == board) {
            player.setHeading(player.getHeading().prev());
        }
    }

    /**
     * Move one card from one place to another
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

    /**
     * Rotate player 180 degrees.
     * @param player the player to turn around
     */
    public void uTurn(Player player){
        turnLeft(player);
        turnLeft(player);
    }

    /**
     * Move player backwards 1 space.
     * @param player the player to move backward
     */
    public void moveBackward(Player player){
        // Easy but not logical way to move player
        uTurn(player);
        moveForward(player, 1);
        uTurn(player);
    }

    /**
     * Repeats the prev player action
     * @param player the player's action to repeat
     */
    public void again(Player player, int step){
        if (step < 1) return;
        if (player.getProgramField(step-1).getCard().command == AGAIN)
            again(player, step-1);
        else {
            Command command = player.getProgramField(step - 1).getCard().command;
            executeCommand(player, command);
        }
    }
}
