package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Space;

public class RotatingGear extends FieldAction{
    public enum Direction{
        LEFT,
        RIGHT;
    }

    private Direction direction;

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    public boolean doAction(GameController gameController, Space space) {
        if(space.getActions().size() > 0) {
            RotatingGear gear = (RotatingGear) space.getActions().get(0);
            if(gear.getDirection() == Direction.LEFT) {
                gameController.rmc.turnLeft(space.getPlayer());
            } else {
                gameController.rmc.turnRight(space.getPlayer());
            }
        } else {
            return false;
        }

        return true;
    }
}
