/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.*;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Creates the StackPane for each space and clears the player from the space after each move
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class SpaceView extends StackPane implements ViewObserver {

    public final Space space;

    public SpaceView(@NotNull Space space) {
        this.space = space;
        this.setId("space");
        this.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/space.css")).toExternalForm());

        ImageView bg = new ImageView(new Image("space.png"));
        this.getChildren().add(bg);

        if (space.getActions().size() > 0) {
            ImageView imageView = bg;
            if (space.getActions().get(0) instanceof ConveyorBelt conveyorBelt) {
                Image conBelt;
                if (conveyorBelt.getNumberOfMoves() <= 1) {
                    conBelt = new Image("conveyorBelt.png");
                } else {
                    conBelt = new Image("conveyorBeltBlue.png");
                }
                imageView = new ImageView(conBelt);
                imageView.setRotate((90 * conveyorBelt.getHeading().ordinal()) % 360);

            } else if (space.getActions().get(0) instanceof Laser laser) {
                imageView = new ImageView(new Image("laser" + laser.getNumberOfLasers() + ".png"));
                imageView.setRotate((90 * laser.getHeading().ordinal()) % 360);

            } else if (space.getActions().get(0) instanceof PushPanel pushPanel) {
                imageView = new ImageView(new Image("pushPanel.png"));
                imageView.setRotate((90 * pushPanel.getHeading().ordinal()) % 360);

            } else if (space.getActions().get(0) instanceof Energy) {
                imageView = new ImageView(new Image("energy.png"));

            } else if (space.getActions().get(0) instanceof RotatingGear rotatingGear) {
                if (rotatingGear.getDirection() == RotatingGear.Direction.RIGHT) {
                    imageView = new ImageView(new Image("rotatingGearRight.png"));
                } else {
                    imageView = new ImageView(new Image("rotatingGearLeft.png"));
                }

            } else if (space.getActions().get(0) instanceof Pit) {
                imageView = new ImageView(new Image("pit.png"));

            } else if (space.getActions().get(0) instanceof Checkpoint checkpoint) {
                switch (checkpoint.getCheckpointNumber()) {
                    case 1 -> imageView = new ImageView(new Image("checkPoint1.png"));
                    case 2 -> imageView = new ImageView(new Image("checkPoint2.png"));
                    case 3 -> imageView = new ImageView(new Image("checkPoint3.png"));
                    case 4 -> imageView = new ImageView(new Image("checkPoint4.png"));
                    case 5 -> imageView = new ImageView(new Image("checkPoint5.png"));
                    case 6 -> imageView = new ImageView(new Image("checkPoint6.png"));
                    default -> imageView = new ImageView(new Image("space.png"));
                }

            } else if (space.getActions().get(0) instanceof PriorityAntenna) {
                imageView = new ImageView(new Image("priorityAntenna.png"));

            } else if (space.getActions().get(0) instanceof StartGear) {
                // TODO Needs an image to show it
            }

            this.getChildren().add(imageView);
        }

        // Needs to be last because walls can overlap
        for (Heading wall : space.getWalls()) {
            ImageView wallPic = new ImageView(new Image("wall.png"));
            wallPic.setRotate((90 * wall.ordinal()) % 360);
            this.getChildren().add(wallPic);
        }
        // updatePlayer();

        // This space view should listen to changes of the space
        space.attach(this);
        update(space);
    }

    private void updatePlayer() {
        // To update player position. Should be programmed more defensively
        for (int i = 0; i < this.getChildren().size(); i++) {
            if (this.getChildren().get(i).getClass().getSimpleName().equals("Polygon")) {
                this.getChildren().remove(i);
            }
        }

        Player player = space.getPlayer();
        if (player != null) {
            Polygon arrow = new Polygon(0.0, 0.0,
                    10.0, 20.0,
                    20.0, 0.0);
            try {
                arrow.setFill(Color.valueOf(player.getColor()));
            } catch (Exception e) {
                arrow.setFill(Color.MEDIUMPURPLE);
            }

            arrow.setRotate((90 * player.getHeading().ordinal()) % 360);
            this.getChildren().add(arrow);
        }
    }

    @Override
    public void updateView(Subject subject) {
        if (subject == this.space) {
            updatePlayer();
        }
    }

}
