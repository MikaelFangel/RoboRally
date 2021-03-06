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

import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

/**
 * The top menu bar in roborally
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class RoboRallyMenuBar extends MenuBar {

    private final AppController appController;
    private final MenuItem saveGame;
    private final MenuItem newGame;
    private final MenuItem loadGame;
    private final MenuItem stopGame;
    private final MenuItem startServer;
    private final MenuItem connectServer;
    private final MenuItem disconnectServer;

    public RoboRallyMenuBar(AppController appController) {
        this.appController = appController;

        Menu controlMenu = new Menu("File");
        this.getMenus().add(controlMenu);

        Menu serverMenu = new Menu("Multiplayer");
        this.getMenus().add(serverMenu);

        newGame = new MenuItem("New Game");
        newGame.setOnAction(e -> this.appController.newGame());
        controlMenu.getItems().add(newGame);

        stopGame = new MenuItem("Stop Game");
        stopGame.setOnAction(e -> this.appController.stopGame());
        controlMenu.getItems().add(stopGame);

        saveGame = new MenuItem("Save Game");
        saveGame.setOnAction(e -> this.appController.saveGame());
        controlMenu.getItems().add(saveGame);

        loadGame = new MenuItem("Load Game");
        loadGame.setOnAction(e -> this.appController.loadGame());
        controlMenu.getItems().add(loadGame);

        MenuItem exitApp = new MenuItem("Exit");
        exitApp.setOnAction(e -> this.appController.exit());
        controlMenu.getItems().add(exitApp);

        //Online
        startServer = new MenuItem("Host game");
        startServer.setOnAction(e -> {
            this.appController.stopGame();
            this.appController.hostGame();
        });
        serverMenu.getItems().add(startServer);

        connectServer = new MenuItem("Connect to server");
        connectServer.setOnAction(e -> this.appController.connectToServer());
        serverMenu.getItems().add(connectServer);

        disconnectServer = new MenuItem("Disconnect from server");
        disconnectServer.setOnAction(e -> {
            this.appController.disconnectFromServer();
            this.appController.stopGame();
        });
        serverMenu.getItems().add(disconnectServer);

        //show the menubar
        controlMenu.setOnShowing(e -> update());
        controlMenu.setOnShown(e -> this.updateBounds());
        serverMenu.setOnShowing(e -> update());
        serverMenu.setOnShown(e -> this.updateBounds());
        update();
    }

    public void update() {
        if (appController.isGameRunning()) {
            newGame.setVisible(false);
            stopGame.setVisible(true);
            saveGame.setVisible(true);
            loadGame.setVisible(false);
        } else {
            newGame.setVisible(true);
            stopGame.setVisible(false);
            saveGame.setVisible(false);
            loadGame.setVisible(true);
        }
        startServer.setVisible(true);
        connectServer.setVisible(true);
        disconnectServer.setVisible(true);
    }

}
