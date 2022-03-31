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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This is all the available commands for the player and the command
 * cards of the game
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public enum Command {

    // This is a very simplistic way of realizing different commands.

    MOVE1("Move 1"),
    MOVE2("Move 2"),
    MOVE3("Move 3"),
    RIGHT("Turn Right"),
    LEFT("Turn Left"),
    OPTION_LEFT_RIGHT("Left OR Right", LEFT, RIGHT),
    UTURN("U-Turn"),
    BACKWARD("Backward"),
    AGAIN("Again!");

    final public String displayName;

    final private List<Command> options;

    Command(String displayName, Command... options) {
        this.displayName = displayName;
        this.options = List.of(options);
    }

    /**
     * Check if the command is interactive (holds 1 or more options)
     * @return true if there is 1 or more option and false if there isn't any
     */
    public boolean isInteractive() {
        return !options.isEmpty();
    }

    /**
     * Get all available option for one command
     * @return a list of all options
     */
    public List<Command> getOptions() {
        return options;
    }

}
