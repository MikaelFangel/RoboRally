package dk.dtu.compute.se.pisd.roborally.controller.fieldaction;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.*;

import java.util.ArrayList;

/**
 * @author Mikael Fangel
 */
public class RebootToken extends FieldAction {
    @Override
    public boolean doAction(GameController gameController, Space space) {

        return true;
    }

    public static void respawnPlayer(Board board, Player player){
        Space rebootToken = findToken(board);

        if (rebootToken != null){
            // Place player on space
            player.setSpace(rebootToken);

            // Deactive Registers
            player.setRegistersDisabled(true);

            // Give 2 SPAM
            player.setDmgcards(Command.SPAM);
            player.setDmgcards(Command.SPAM);
        }
    }

    private static Space findToken(Board board){
        for (int i = 0; i < board.getSpaces().length; i++) {
            for (int j = 0; j < board.getSpaces()[0].length; j++) {
                Space curSpace = board.getSpace(j, i);
                if (curSpace.getActions().size() > 0 && curSpace.getActions().get(0) instanceof RebootToken){
                    return curSpace;
                }
            }
        }
        return null;
    }
}
