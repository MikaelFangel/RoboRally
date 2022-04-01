package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.*;
import org.jetbrains.annotations.NotNull;

import static dk.dtu.compute.se.pisd.roborally.model.Command.AGAIN;

/**
 * This class controls the movement of the robots.
 * @Author Christian Andersen, s133288@dtu.dk
 */
public class RobotMovementController {

    final private GameController gameController;

    final private Board board;

    public RobotMovementController(GameController gameController, Board board){
        this.board = board;
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
     *
     * @param player the player which is subject to move
     */
    public void turnRight(@NotNull Player player) {
        if (player.board == board) {
            player.setHeading(player.getHeading().next());
        }
    }

    /**
     * Turn the player left based upon their current heading
     *
     * @param player the player which is subject to move
     */
    public void turnLeft(@NotNull Player player) {
        if (player.board == board) {
            player.setHeading(player.getHeading().prev());
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

}
