package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.exceptions.BoardNotFoundException;
import dk.dtu.compute.se.pisd.roborally.fileaccess.SerializeState;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import org.junit.jupiter.api.*;

public class SerializeTest {

    @Test
    void repeatedSerializesAndDeserializesOfDefaultBoard(){
        try {
            Board startBoard = SaveLoadGame.newBoard(2, "dizzy_highway");
            String jsonResult1 = SerializeState.serializeGame(startBoard);

            Board board1 = SerializeState.deserializeGame(jsonResult1, false);
            String jsonResult2 = SerializeState.serializeGame(board1);

            Assertions.assertEquals(jsonResult1, jsonResult2);
        } catch (BoardNotFoundException e){
            assert true;
        }

    }

    @Test
    void repeatedSerializesAndDeserializesOfSavedBoard(){
        try {
            Board startBoard = SaveLoadGame.loadBoard("TestSaveBoard");
            String jsonResult1 = SerializeState.serializeGame(startBoard);

            Board board1 = SerializeState.deserializeGame(jsonResult1, true);
            String jsonResult2 = SerializeState.serializeGame(board1);

            Assertions.assertEquals(jsonResult1, jsonResult2);
        } catch (Exception e){
            System.out.println(e);
        }
    }


}
