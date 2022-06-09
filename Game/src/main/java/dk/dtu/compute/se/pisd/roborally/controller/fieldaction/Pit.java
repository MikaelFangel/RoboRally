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
 * @author Ahmad Sandhu, Mikael Fangel
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
                switch (pit.boardNum) {
                    case 1:

                        board.getSpace(4,9).setPlayer(space.getPlayer());
                        reboot.doAction(gameController, space);
                        player.setDmgcards(Command.SPAM);
                        player.setDmgcards(Command.SPAM);
                        //boardNum = 0;
                        break;

                    case 2:
                        board.getSpace(7,0).setPlayer(space.getPlayer());
                        reboot.doAction(gameController, space);
                        player.setDmgcards(Command.SPAM);
                        player.setDmgcards(Command.SPAM);
                        //boardNum = 0;
                        break;



                }
            }

        }
        return true;
    }
}
