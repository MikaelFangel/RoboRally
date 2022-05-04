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
import dk.dtu.compute.se.pisd.roborally.controller.Checkpoint;
import dk.dtu.compute.se.pisd.roborally.controller.ConveyorBelt;
import dk.dtu.compute.se.pisd.roborally.controller.Laser;
import dk.dtu.compute.se.pisd.roborally.controller.PriorityAntenna;
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

        if (space.getActions().size() > 0 && space.getActions().get(0) instanceof ConveyorBelt conveyorBelt) {
            Image conBelt;
            if (conveyorBelt.getNumberOfMoves() <= 1) {
                conBelt = new Image("conveyorBelt.png");
            } else {
                conBelt = new Image("conveyorBeltBlue.png");
            }

            ImageView conBeltView = new ImageView(conBelt);
            conBeltView.setRotate((90 * conveyorBelt.getHeading().ordinal()) % 360);
            this.getChildren().add(conBeltView);
        }

        if (space.getActions().size() > 0 && space.getActions().get(0) instanceof Laser laser) {
            ImageView laserImg = new ImageView(new Image("laser" + laser.getNumberOfLasers() + ".png"));
            laserImg.setRotate((90 * laser.getHeading().ordinal()) % 360);
            this.getChildren().add(laserImg);
        }

            if (space.getActions().size() > 0 && space.getActions().get(0) instanceof Checkpoint checkpoint) {
            switch (checkpoint.getCheckpointNumber()) {
                case 1 -> this.setStyle("-fx-background-image: url(file:src/main/resources/checkPoint1.png)");
                case 2 -> this.setStyle("-fx-background-image: url(file:src/main/resources/checkPoint2.png)");
                case 3 -> this.setStyle("-fx-background-image: url(file:src/main/resources/checkPoint3.png)");
                case 4 -> this.setStyle("-fx-background-image: url(file:src/main/resources/checkPoint4.png)");
                case 5 -> this.setStyle("-fx-background-image: url(file:src/main/resources/checkPoint5.png)");
                case 6 -> this.setStyle("-fx-background-image: url(file:src/main/resources/checkPoint6.png)");
            }
        }

        if (space.getActions().size() > 0 && space.getActions().get(0) instanceof PriorityAntenna) {
            this.setStyle("-fx-background-image: url(file:src/main/resources/priorityAntenna.png)");
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
            if(this.getChildren().get(i).getClass().getSimpleName().equals("Polygon")) {
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
