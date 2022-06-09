package dtu.compute.http;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author Christian Andersen
 */
@RestController
public class ServerController {

    @Autowired
    private IStatusComm statusComm;


    /**
     * @param s contains the players chosen title of the game
     * @return http response
     * @author Christian Andersen
     */
    @PostMapping(value = "/game")
    public ResponseEntity<String> createGame(@RequestBody String s) {
        String newServerID = statusComm.hostGame(s);
        if (newServerID == null) //something went wrong
            return ResponseEntity.internalServerError().body("Server couldn't start");
        return ResponseEntity.ok().body(newServerID);
    }

    /**
     * List all games currently running on the server
     *
     * @return http response where body contains a list of servers
     * @author Christian Andersen
     */
    @GetMapping(value = "/game")
    public ResponseEntity<String> listOfGame() {
        return ResponseEntity.ok().body(statusComm.listGames());
    }

    /**
     * Join a game on the server
     *
     * @param id for server to join
     * @return "ok" if join was successful
     */
    @PutMapping(value = "/game/{id}")
    public ResponseEntity<String> joinGame(@PathVariable String id) {
        String response = statusComm.joinGame(id);
        if (response.equals("Server doesn't exist"))
            return ResponseEntity.status(404).body(response);
        if (response.equals("Server is full"))
            return ResponseEntity.badRequest().body(response);
        return ResponseEntity.ok().body(response);
    }

    /**
     * Removes the player from the game
     *
     * @param id    of the game
     * @param robot number in the game
     * @author Christian Andersen
     */
    @PostMapping(value = "/game/{id}/{robot}")
    public void leaveGame(@PathVariable String id, @PathVariable String robot) {
        statusComm.leaveGame(id, Integer.parseInt(robot));
    }

    /**
     * Gets the current game state on a game placed on the server. The game state
     * is retrieved using its id
     *
     * @param id of the game
     * @return http response where body contains a Json string format
     * @author Christian Andersen
     */
    @GetMapping(value = "/gameState/{id}")
    public ResponseEntity<String> getGameState(@PathVariable String id) {
        return ResponseEntity.ok().body(statusComm.getGameState(id));
    }

    /**
     * Updates the current game state on a game based on it id.
     *
     * @param id   of the game
     * @param game Json string that contain the game state
     * @return http response
     * @author Christian Andersen
     */
    @PutMapping(value = "/gameState/{id}")
    public ResponseEntity<String> setGameState(@PathVariable String id, @RequestBody String game) {
        statusComm.updateGame(id, game);
        return ResponseEntity.ok().body("ok");
    }
}
