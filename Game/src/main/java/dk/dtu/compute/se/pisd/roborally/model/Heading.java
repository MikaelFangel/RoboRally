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

/**
 * This gives the possible headings for objects in the game
 * and makes it possible to turn them either clockwise or
 * counterclockwise,
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public enum Heading {
    SOUTH, WEST, NORTH, EAST;

    /**
     * Get the next heading clockwise
     *
     * @return the next heading
     */
    public Heading next() {
        return values()[(this.ordinal() + 1) % values().length];
    }

    /**
     * Get the previous heading counterclockwise
     *
     * @return the previous heading
     */
    public Heading prev() {
        return values()[(this.ordinal() + values().length - 1) % values().length];
    }
}
