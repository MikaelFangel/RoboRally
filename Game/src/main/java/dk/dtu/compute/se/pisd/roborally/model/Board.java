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
package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.fieldaction.Checkpoint;
import dk.dtu.compute.se.pisd.roborally.controller.fieldaction.PriorityAntenna;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static dk.dtu.compute.se.pisd.roborally.model.Phase.INITIALISATION;

/**
 * The board model containing all the data about the current board.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class Board extends Subject {
    public boolean gameOver = false;
    public final int width;
    public final int height;
    public final String boardName;
    private final Space[][] spaces;
    private List<Player> players = new ArrayList<>();
    public Player current;
    public Phase phase = INITIALISATION;
    private int numOfCheckPoints;
    public int step = 0;
    public boolean stepMode;

    public Board(int width, int height, @NotNull String boardName) {
        this.boardName = boardName;
        this.width = width;
        this.height = height;
        spaces = new Space[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Space space = new Space(this, x, y);
                spaces[x][y] = space;
            }
        }
        this.stepMode = false;
    }

    public Board(int width, int height) {
        this(width, height, "defaultboard");
    }

    public Space getSpace(int x, int y) {
        if (x >= 0 && x < width &&
                y >= 0 && y < height) {
            return spaces[x][y];
        } else {
            return null;
        }
    }

    /**
     * Gets the number of players on the board
     *
     * @return number of players on the board
     */
    public int getPlayersNumber() {
        return players.size();
    }

    /**
     * Add a player to the board
     *
     * @param player player to add to the board
     */
    public void addPlayer(@NotNull Player player) {
        if (player.board == this && !players.contains(player)) {
            players.add(player);
            notifyChange();
        }
    }

    /**
     * Get a player based on his/hers index in the list
     *
     * @param i index of the player to get
     * @return player at given index
     */
    public Player getPlayer(int i) {
        if (i >= 0 && i < players.size()) {
            return players.get(i);
        } else {
            return null;
        }
    }

    /**
     * Gets the list of players on the board
     *
     * @return list of players on the baord
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Set the list of players on board
     *
     * @param players list of players to set on board
     */
    public void setPlayers(List<Player> players) {
        this.players = players;
    }


    /**
     * Get the players which turn it is
     *
     * @return current player
     */
    public Player getCurrentPlayer() {
        return current;
    }

    /**
     * Change the current player
     *
     * @param player new current player
     */
    public void setCurrentPlayer(Player player) {
        if (player != this.current && players.contains(player)) {
            this.current = player;
            notifyChange();
        }
    }

    /**
     * Get the placement of the priority antenna
     *
     * @return space containing the priority antenna
     */
    public Space getPriorityAntennaSpace() {
        for (Space[] spaceArr : spaces) {
            for (Space space : spaceArr) {
                if (space.getActions().size() > 0 && space.getActions().get(0) instanceof PriorityAntenna) {
                    return space;
                }
            }
        }
        return spaces[4][4]; // Default value, should never return this if board.csv is made with one.
    }

    /**
     * Get the current game phase
     *
     * @return current game phase
     */
    public Phase getPhase() {
        return phase;
    }

    /**
     * Set the current game phase
     *
     * @param phase new phase
     */
    public void setPhase(Phase phase) {
        if (phase != this.phase) {
            this.phase = phase;
            notifyChange();
        }
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        if (step != this.step) {
            this.step = step;
            notifyChange();
        }
    }

    public boolean isStepMode() {
        return stepMode;
    }

    public void setStepMode(boolean stepMode) {
        if (stepMode != this.stepMode) {
            this.stepMode = stepMode;
            notifyChange();
        }
    }

    public int getPlayerNumber(@NotNull Player player) {
        if (player.board == this) {
            return players.indexOf(player);
        } else {
            return -1;
        }
    }

    /**
     * Returns the neighbour of the given space of the board in the given heading.
     * The neighbour is returned only, if it can be reached from the given space
     * (no walls or obstacles in either of the involved spaces); otherwise,
     * null will be returned.
     *
     * @param space   the space for which the neighbour should be computed
     * @param heading the heading of the neighbour
     * @return the space in the given direction; null if there is no (reachable) neighbour
     */
    public Space getNeighbour(@NotNull Space space, @NotNull Heading heading) {
        if (space.getWalls().contains(heading)) {
            return null;
        }
        // TODO needs to be implemented based on the actual spaces
        //      and obstacles and walls placed there. For now it,
        //      just calculates the next space in the respective
        //      direction in a cyclic way.

        // XXX an other option (not for now) would be that null represents a hole
        //     or the edge of the board in which the players can fall

        int x = space.x;
        int y = space.y;
        switch (heading) {
            case SOUTH -> y = (y + 1) % height;
            case WEST -> x = (x + width - 1) % width;
            case NORTH -> y = (y + height - 1) % height;
            case EAST -> x = (x + 1) % width;
        }
        Heading reverse = Heading.values()[(heading.ordinal() + 2) % Heading.values().length];
        Space result = getSpace(x, y);
        if (result != null) {
            if (result.getWalls().contains(reverse)) {
                return null;
            }
        }
        return result;
    }

    public String getStatusMessage() {
        // this is actually a view aspect, but for making assignment V1 easy for
        // the students, this method gives a string representation of the current
        // status of the game

        return "Phase: " + getPhase().name() +
                ", Current Player = " + getCurrentPlayer().getName() +
                ", Step: " + getStep() + "\n";
    }

    public void nextPlayer(Player player) {
        if (getPlayerNumber(player) == getPlayersNumber() - 1)
            setCurrentPlayer(getPlayer(0));
        else
            setCurrentPlayer(getPlayer(getPlayerNumber(player) + 1));

    }

    public Space[][] getSpaces() {
        return spaces;
    }

    /**
     * Sets the maximum checkpoint number on the checkpoint space used to calculate the winning player
     *
     * @author Frederik Greve Petersen
     */
    public void setCheckpointsWithNumber() {
        findNumberOfCheckPoints();
        Checkpoint.setHighestCheckpointNumber(numOfCheckPoints);
    }

    /**
     * Finds how many checkpoint that are placed on the board and the sets the field numOfCheckpoints
     * with the counted value
     *
     * @author Frederik Greve Petersen
     */
    private void findNumberOfCheckPoints() {
        int counter = 0;
        for (Space[] space : spaces) {
            for (int j = 0; j < spaces[0].length; j++) {
                if (space[j].getActions().size() > 0 &&
                        space[j].getActions().get(0) instanceof Checkpoint) {

                    counter++;
                }
            }
        }
        numOfCheckPoints = counter;
    }


    public int getNumOfCheckPoints() {
        return numOfCheckPoints;
    }

}
