package dk.dtu.compute.se.pisd.roborally.controller.fieldaction;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Command;
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

            int height = board.height;
            int width = board.width;

            // Loop that runs until the laser hits a player
            while (next != null && next.getPlayer() == null) {
                int x = next.x;
                int y = next.y;

                switch (direction) {
                    case SOUTH -> {
                        if (y == height - 1) next = null;
                    }
                    case WEST -> {
                        if (x == 0) next = null;
                    }
                    case NORTH -> {
                        if (y == 0) next = null;
                    }
                    case EAST -> {
                        if (x == width - 1) next = null;
                    }
                }

                // Gets next board element
                if (next != null)
                    next = board.getNeighbour(next, direction);
            }

            // Only gives damage if there is a player on the board
            if (next != null && next.getPlayer() != null) {
                // Give player damage card
                System.out.println("Hit player: " + next.getPlayer() + " with laser!");
                next.getPlayer().setDamagecards(Command.SPAM);
            }
        } else {
            return false;
        }
        return true;
    }
}
