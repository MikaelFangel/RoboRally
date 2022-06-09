package dk.dtu.compute.se.pisd.roborally.controller.fieldaction;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.controller.SaveLoadGame;
import dk.dtu.compute.se.pisd.roborally.controller.fieldaction.FieldAction;
import dk.dtu.compute.se.pisd.roborally.fileaccess.ReadWriteGame;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Command;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

import java.util.Objects;

/**
 * @author Mikael Fangel
 */
public class Pit extends FieldAction {
    public int boardNum;
    RebootToken reboot = new RebootToken();
    @Override
    public boolean doAction(GameController gameController, Space space) {
        Pit pit = (Pit) space.getActions().get(0);
        Board board = gameController.board;

        if (space.getActions().size() > 0) {
            Player player = space.getPlayer();

            if (player != null) {
                if (pit.boardNum == 1) {
                    gameController.moveCurrentPlayerToSpace(board.getSpace(4, 9));
                    reboot.doAction(gameController, space);
                    player.setDmgcards(Command.SPAM);
                }
                if (boardNum == 2){
                    gameController.moveCurrentPlayerToSpace(board.getSpace(7, 0));
                    reboot.doAction(gameController, space);
                    player.setDmgcards(Command.SPAM);
                }
            }

        }
        return false;
    }
}
