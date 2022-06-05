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
        return ResponseEntity.ok().body(newServerID);
    }

    //listOfGame
    @GetMapping(value = "/game")
    public ResponseEntity<String> listOfGame(){
        String servers = statusComm.listGames();
        return ResponseEntity.ok().body(servers);
    }

    //Join game
    @PutMapping(value = "/game/{id}")
    public ResponseEntity<String> joinGame(@PathVariable String id){
        if (statusComm.joinGame(id).equals("ok"))
            return ResponseEntity.ok().body("ok");
        else
            return ResponseEntity.badRequest().body("error");
    }

    //leave game
    @PostMapping(value = "/game/{id}")
    public ResponseEntity<String> leaveGame(@PathVariable String id){
        statusComm.leaveGame(id);
        return ResponseEntity.ok().body("ok");
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
