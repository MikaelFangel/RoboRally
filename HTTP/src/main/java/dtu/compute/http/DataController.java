package dtu.compute.http;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class DataController {

    @Autowired
    private IStatusComm statusComm;

    @GetMapping(value = "/greeting")
    public ResponseEntity<String> conGreeting()
    {
        return ResponseEntity.ok().body("OK");
    }


    @GetMapping(value = "/gameState")
    public ResponseEntity<String> getGameState()
    {
        return ResponseEntity.ok().body(statusComm.getGameState());
    }

    @PutMapping(value = "/gameState")
    public ResponseEntity<String> setGameState(@RequestBody String g)
    {
        statusComm.updateGame(g);
        return ResponseEntity.ok().body("OK");
    }
}
