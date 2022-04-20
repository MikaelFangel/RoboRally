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
import dk.dtu.compute.se.pisd.roborally.controller.ConveyorBelt;
import dk.dtu.compute.se.pisd.roborally.controller.PriorityAntenna;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class SpaceView extends StackPane implements ViewObserver {

    public final Space space;

    public SpaceView(@NotNull Space space) {
        this.space = space;
        this.setId("space");

        // Adds the walls using css
        for (Heading wall : space.getWalls()) {
            this.getStyleClass().add("space_" + wall.toString().toLowerCase());
        }

        this.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/space.css")).toExternalForm());

        if ((space.x + space.y) % 2 == 0) {
            this.setStyle("-fx-background-color: white;");
        } else {
            this.setStyle("-fx-background-color: black;");
        }

        // TODO: Add to css file
        if(space.getActions().size() > 0 && space.getActions().get(0) instanceof ConveyorBelt) {
            switch (((ConveyorBelt) space.getActions().get(0)).getHeading()) {
                case NORTH -> this.setStyle("-fx-background-image: url(file:src/main/resources/conveyorBelt.png)");
                case EAST ->  this.setStyle("-fx-background-image: url(file:src/main/resources/conveyorBeltEast.png)");
                case SOUTH -> this.setStyle("-fx-background-image: url(file:src/main/resources/conveyorBeltSouth.png)");
                case WEST -> this.setStyle("-fx-background-image: url(file:src/main/resources/conveyorBeltWest.png)");
            }
        }

        if(space.getActions().size() > 0 && space.getActions().get(0) instanceof PriorityAntenna) {
            this.setStyle("-fx-background-image: url(file:src/main/resources/priorityAntenna.png)");
        }
        // updatePlayer();

        // This space view should listen to changes of the space
        space.attach(this);
        update(space);
    }

    private void updatePlayer() {
        this.getChildren().clear();

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
