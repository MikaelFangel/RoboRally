package dk.dtu.compute.se.pisd.roborally.controller;

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

            Space next = gameController.board.getNeighbour(space, direction);

            while (next != null && next.getPlayer() == null) {
                next = gameController.board.getNeighbour(next, direction);
            }

            if (next.getPlayer() != null) {
                // Give player damage card
            }
        } else {
            return false;
        }
        return false;
    }
}
