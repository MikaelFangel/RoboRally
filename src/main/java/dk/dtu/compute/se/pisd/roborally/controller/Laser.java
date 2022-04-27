package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Space;

public class Laser extends FieldAction {

    private int numberOfLasers;
    private Heading heading;

    public Heading getHeading() {
        return heading;
    }

    public void setHeading(Heading heading) {
        this.heading = heading;
    }

    public void setNumberOfLasers(int numberOfLasers) {
        this.numberOfLasers = numberOfLasers;
    }

    public int getNumberOfLasers() {
        return numberOfLasers;
    }

    // TODO test function
    @Override
    public boolean doAction(GameController gameController, Space space) {
        if (space.getActions().size() > 0) {
            Laser laser = (Laser) space.getActions().get(0);
            Heading direction = laser.getHeading();
            Board board = gameController.board;
            Space next = space;

            // Loop that runs until the laser hits a player
            while (next != null && next.getPlayer() == null) {
                // Makes sure the loop only runs until the board ends
                if(next.x == board.width - 1 ||
                next.x == 0 ||
                next.y == board.height -1 ||
                next.y == 0)
                    break;

                // Gets next board element
                next = board.getNeighbour(next, direction);
            }

            // Only gives damage if there is a player on the board
            assert (next != null);
            if (next.getPlayer() != null) {
                // Give player damage card
            }
        } else {
            return false;
        }
        return true;
    }
}
