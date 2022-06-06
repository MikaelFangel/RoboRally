package dtu.compute.http;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ServerController {

    @Autowired
    private IStatusComm statusComm;

    //host Game
    @PostMapping(value = "/game")
    public ResponseEntity<String> createGame(@RequestBody String s){
        String newServerID = statusComm.hostGame(s);
        if (newServerID == null) //something went wrong
            return ResponseEntity.internalServerError().body("Server couldn't start");
        return ResponseEntity.ok().body(newServerID);
    }

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

    //leave game
    @PostMapping(value = "/game/{id}")
    public void leaveGame(@PathVariable String id){
        statusComm.leaveGame(id);
    }

    @GetMapping(value = "/gameState/{id}")
    public ResponseEntity<String> getGameState(@PathVariable String id)
    {
        return ResponseEntity.ok().body(statusComm.getGameState(id));
    }

    @PutMapping(value = "/gameState/{id}")
    public ResponseEntity<String> setGameState(@PathVariable String id, @RequestBody String g)
    {
        statusComm.updateGame(id,g);
        return ResponseEntity.ok().body("ok");
    }
}
