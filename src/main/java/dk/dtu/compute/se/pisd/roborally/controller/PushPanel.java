package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

public class PushPanel extends FieldAction {
    private Heading heading;

    public Heading getHeading() {
        return heading;
    }

    public void setHeading(Heading heading) {
        this.heading = heading;
    }

    @Override
    public boolean doAction(GameController gameController, Space space) {
        if (space.getActions().size() > 0) {
            PushPanel pushPanel = (PushPanel) space.getActions().get(0);
            Heading direction = pushPanel.getHeading();
            Board board = gameController.board;

            int x = space.x;
            int y = space.y;

            int height = board.height;
            int width = board.width;

            switch (direction) {
                case SOUTH -> {
                    if (y == height - 1) space = null;
                }
                case WEST -> {
                    if (x == 0) space = null;
                }
                case NORTH -> {
                    if (y == 0) space = null;
                }
                case EAST -> {
                    if (x == width - 1) space = null;
                }
            }

            if (space != null) {
                Space pushArea = board.getNeighbour(space, direction);
                Player player = pushArea.getPlayer();
                if (player != null) {
                    Heading playerHeading = player.getHeading();
                    player.setHeading(heading);
                    gameController.rmc.moveForward(player, 1);
                    player.setHeading(playerHeading);

                    return true;
                }
            }
        }

        return false;
    }
}
