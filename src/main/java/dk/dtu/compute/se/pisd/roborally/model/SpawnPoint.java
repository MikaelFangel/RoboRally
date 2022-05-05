package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;

import java.util.ArrayList;
import java.util.List;

public class SpawnPoint extends Subject implements boardSquares{

    private String color;
    private Player player;

    private List<Heading> walls = new ArrayList<>();
    private List<FieldAction> actions = new ArrayList<>();

    public final Board board;

    public final int x;
    public final int y;

    public SpawnPoint(Board board, int x, int y, String color){
        this.board = board;
        this.x = x;
        this.y = y;
        this.color = color;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public List<Heading> getWalls() {
        return null;
    }

    @Override
    public List<FieldAction> getActions() {
        return null;
    }
}
