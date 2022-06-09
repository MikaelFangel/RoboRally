package dtu.compute.http;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ServerController {

    @Autowired
    private IStatusComm statusComm;


    /**
     * @author Christian Andersen
     *
     * @param s contains the players chosen title of the game
     * @return http response
     */
    @PostMapping(value = "/game")
    public ResponseEntity<String> createGame(@RequestBody String s){
        String newServerID = statusComm.hostGame(s);
        if (newServerID == null) //something went wrong
            return ResponseEntity.internalServerError().body("Server couldn't start");
        return ResponseEntity.ok().body(newServerID);
    }

    /**
     * @author Christian Andersen
     *
     * @return http response where body contains a list of servers
     */
    //listOfGame
    @GetMapping(value = "/game")
    public ResponseEntity<String> listOfGame(){
        return ResponseEntity.ok().body(statusComm.listGames());
    }

    //Join game
    @PutMapping(value = "/game/{id}")
    public ResponseEntity<String> joinGame(@PathVariable String id){
        String response = statusComm.joinGame(id);
        if (response.equals("Server doesn't exist"))
            return ResponseEntity.status(404).body(response);
        if (response.equals("Server is full"))
            return ResponseEntity.badRequest().body(response);
        return ResponseEntity.ok().body(response);

    }

    /**
     * @author Christian Andersen
     *
     * Removes the player from the game
     *
     * @param id of the game
     * @param robot number in the game
     */
    //leave game
    @PostMapping(value = "/game/{id}/{robot}")
    public void leaveGame(@PathVariable String id, @PathVariable String robot){
        statusComm.leaveGame(id, Integer.parseInt(robot));
    }

    /**
     * @author Christian Andersen
     *
     * @param id of the game
     * @return http response where body contains a Json string format
     */
    @GetMapping(value = "/gameState/{id}")
    public ResponseEntity<String> getGameState(@PathVariable String id)
    {
        return ResponseEntity.ok().body(statusComm.getGameState(id));
    }

    /**
     * @author Christian Andersen
     *
     * @param id of the game
     * @param game Json string that contain the game state
     * @return http response
     */
    @PutMapping(value = "/gameState/{id}")
    public ResponseEntity<String> setGameState(@PathVariable String id, @RequestBody String game)
    {
        statusComm.updateGame(id,game);
        return ResponseEntity.ok().body("ok");
    }
}
