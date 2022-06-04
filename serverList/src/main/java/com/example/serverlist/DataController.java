package com.example.serverlist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class DataController {

    @Autowired
    private IServerList serverList;

    @GetMapping(value = "/servers")
    public ResponseEntity<String> getServerList(){
        return ResponseEntity.ok().body(serverList.getServerList());
    }

    /*@PutMapping(value = "/servers")
    public ResponseEntity<String> listServer(){

    }*/
}
