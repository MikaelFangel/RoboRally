package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.exceptions.BoardNotFoundException;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class GameControllerTest {

    private final int TEST_WIDTH = 8;
    private final int TEST_HEIGHT = 8;

    private GameController gameController;

    @BeforeEach
    void setUp() {
        Board board = new Board(TEST_WIDTH, TEST_HEIGHT);
        gameController = new GameController(null ,board, null);
        for (int i = 0; i < 6; i++) {
            Player player = new Player(board, null,"Player " + i);
            board.addPlayer(player);
            player.setSpace(board.getSpace(i, i));
            player.setHeading(Heading.values()[i % Heading.values().length]);
        }
        board.setCurrentPlayer(board.getPlayer(0));
    }

    @AfterEach
    void tearDown() {
        gameController = null;
    }

    @Test
    void moveCurrentPlayerToSpace() {
        Board board = gameController.board;
        Player player1 = board.getPlayer(0);
        Player player2 = board.getPlayer(1);

        gameController.moveCurrentPlayerToSpace(board.getSpace(0, 4));

        Assertions.assertEquals(player1, board.getSpace(0, 4).getPlayer(), "Player " + player1.getName() + " should beSpace (0,4)!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
        Assertions.assertEquals(player2, board.getCurrentPlayer(), "Current player should be " + player2.getName() +"!");
    }

    @Test
    void moveForward() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.rmc.moveForward(current, 1);

        Assertions.assertEquals(current, board.getSpace(0, 1).getPlayer(), "Player " + current.getName() + " should beSpace (0,1)!");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
    }
    @Test
    void energyRoutine(){
        Board board = gameController.board;
        Player player = new Player(board,null,"Player");
        int energyCount = player.getEnergyCount();
        Assertions.assertEquals(5,energyCount);
        energyCount = gameController.energyRoutine(player);
        Assertions.assertEquals(6,energyCount);
    }

    /**
     * @author Frederik Greve Petersen
     */
    @Test
    void testPriorityAntenna(){
        try {
            Board board = SaveLoadGame.newBoard(3, "dizzy_highway");
            board.setPlayers(new ArrayList<>());
            GameController gc = new GameController(null, board, null);

            // Create players
            for (int i = 3; i > 0; i--) {
                Player player = new Player(board, null,"Player " + i);
                board.addPlayer(player);
                player.setSpace(board.getSpace(i+1, 5));
                player.setHeading(Heading.values()[i % Heading.values().length]);
            }
            List<Player> playersBefore = board.getPlayers();
            gc.assertPlayerPriorityAndChangeBoardPlayers();
            List<Player> playersAfter = board.getPlayers();

            if (playersBefore.get(0) == playersAfter.get(2) &&
                playersBefore.get(1) == playersAfter.get(1) &&
                playersBefore.get(2) == playersAfter.get(0)){
                Assertions.assertTrue(true);
            } else {
                Assertions.fail();
            }

        } catch (BoardNotFoundException e){
            System.out.println("Board not found for test");
            Assertions.fail();
        }
    }

    /**
     * @author Frederik Greve Petersen
     */
    @Test
    void testCheckPointsInCorrectOrder(){
        Board board = null;
        try {
            board = SaveLoadGame.newBoard(3, "dodge_this");
            GameController gc = new GameController(null, board, null);

            board.setCurrentPlayer(board.getPlayers().get(0));
            gc.moveCurrentPlayerToSpace(board.getSpace(8, 6));
            board.setCurrentPlayer(board.getPlayers().get(0));
            gc.moveCurrentPlayerToSpace(board.getSpace(12, 9));
            board.setCurrentPlayer(board.getPlayers().get(0));

            gc.moveCurrentPlayerToSpace(board.getSpace(8, 3)); // Will cause exception

        } catch (BoardNotFoundException e){
            Assertions.fail(); // Board not found
        } catch (ExceptionInInitializerError e2){
            Assertions.assertEquals(3, board.getCurrentPlayer().checkPoints);
        }
    }

    /**
     * @author Frederik Greve Petersen
     */
    @Test
    void testCheckPointsInWrongOrder(){
        Board board = null;
        try {
            board = SaveLoadGame.newBoard(3, "dodge_this");
            GameController gc = new GameController(null, board, null);

            board.setCurrentPlayer(board.getPlayers().get(0));
            gc.moveCurrentPlayerToSpace(board.getSpace(12, 9));
            board.setCurrentPlayer(board.getPlayers().get(0));
            gc.moveCurrentPlayerToSpace(board.getSpace(8, 3));
            board.setCurrentPlayer(board.getPlayers().get(0));

            gc.moveCurrentPlayerToSpace(board.getSpace(8, 6)); // Will cause exception

        } catch (BoardNotFoundException e){
            Assertions.fail(); // Board not found
        } catch (ExceptionInInitializerError e2){
            Assertions.assertEquals(1, board.getCurrentPlayer().checkPoints);
        }
    }
}