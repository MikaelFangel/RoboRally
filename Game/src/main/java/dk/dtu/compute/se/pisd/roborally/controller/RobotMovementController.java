package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.controller.fieldaction.PriorityAntenna;
import dk.dtu.compute.se.pisd.roborally.model.*;
import org.jetbrains.annotations.NotNull;

import static dk.dtu.compute.se.pisd.roborally.model.Command.AGAIN;

/**
 * This class controls the movement of the robots.
 *
 * @author Christian Andersen, s133288@dtu.dk
 */
public class RobotMovementController {

    final private GameController gameController;

    public RobotMovementController(GameController gameController) {
        this.gameController = gameController;
    }

    // ------- Card Commands --------

    /**
     * Move the player one space forward in the direction they are pointing.
     * Also accounts for a situation where another player occupies the slot, and pushes them out of the way.
     * The push is recursive to ensure that they ain't pushed onto an occupied slot either.
     *
     * @param player the player which is subject to move
     */
    public void moveForward(@NotNull Player player, int moves) {
        for (int i = 0; i < moves; i++) {
            try {
                Heading heading = player.getHeading();
                Space target = gameController.board.getNeighbour(player.getSpace(), heading);
                if (target == null ||
                        (target.getActions().size() > 0 && target.getActions().get(0) instanceof PriorityAntenna))
                    throw new ImpossibleMoveException(player, player.getSpace(), heading);
                if (isOccupied(target)) {
                    Player playerBlocking = target.getPlayer();
                    Heading targetCurrentHeading = playerBlocking.getHeading();
                    playerBlocking.setHeading(player.getHeading());
                    moveForward(playerBlocking, 1);
                    playerBlocking.setHeading(targetCurrentHeading);
                }
                target.setPlayer(player);
            } catch (ImpossibleMoveException e) {
                // Do nothing for now...
            }
        }
    }

    /**
     * Turn the player right based upon their current heading
     *
     * @param player the player which is subject to move
     */
    public void turnRight(@NotNull Player player) {
        if (player.board == gameController.board) {
            player.setHeading(player.getHeading().next());
        }
    }

    /**
     * Turn the player left based upon their current heading
     *
     * @param player the player which is subject to move
     */
    public void turnLeft(@NotNull Player player) {
        if (player.board == gameController.board) {
            player.setHeading(player.getHeading().prev());
        }
    }

    /**
     * Rotate player 180 degrees.
     *
     * @param player the player to turn around
     */
    public void uTurn(Player player) {
        turnLeft(player);
        turnLeft(player);
    }

    /**
     * Move player backwards 1 space.
     *
     * @param player the player to move backward
     */
    public void moveBackward(Player player) {
        // Easy but not logical way to move player
        uTurn(player);
        moveForward(player, 1);
        uTurn(player);
    }

    /**
     * Repeats the prev player action
     *
     * @param player the player's action to repeat
     */
    public void again(Player player, int step) {
        if (step < 1) return;
        Command prevCommand = player.getProgramField(step - 1).getCard().command;
        if (prevCommand == AGAIN)
            again(player, step - 1);
        else {
            player.getProgramField(step).setCard(new CommandCard(prevCommand));
            gameController.executeNextStep();
            player.getProgramField(step).setCard(new CommandCard(AGAIN));
        }
    }
    public void Removespam(Player player){
        player.getDamagecards().remove(Command.SPAM);
    }


    // ------- private methods --------

    private boolean isPushable(Player player, Heading headed) {
        //TODO
        //should contain a method to determinate if a player can push another player.
        //need to check on the player getting pushed, but also any other player that might be a part of a chain of pushes.
        Space target = gameController.board.getNeighbour(player.getSpace(), headed);
        //if (hitting a wall)

        if (target.getPlayer() != null) {
            //TODO recursive call until last player found
        }

        return true;
    }

    private boolean isConveyet(Player player, Heading headed) {
        //TODO
        //Litle different the the normal push, since here we need to only check if the player we are might hit
        //is going to get moved by a conveyer, and is not getting blocked from that movement them self.
        return true;
    }

    private boolean isOccupied(Space space) {
        Space target = gameController.board.getSpace(space.x, space.y);
        return target.getPlayer() != null;
    }

    class ImpossibleMoveException extends Exception {
        public ImpossibleMoveException(Player player, Space space, Heading heading) {
            super("Move impossible");
        }
    }

}
