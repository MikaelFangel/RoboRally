package dk.dtu.compute.se.pisd.roborally.fileaccess;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.BoardTemplate;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.PlayerTemplate;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.SpaceTemplate;
import dk.dtu.compute.se.pisd.roborally.model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SaveLoadGame {

    final static private List<String> PLAYER_COLORS = Arrays.asList("red", "green", "blue", "orange", "grey", "magenta");

    private static final String DEFAULT_BOARD = "defaultboard";

    private static final String BOARDS_FOLDER = "boards";
    private static final String JSON_EXT = "json";

    public static void saveBoard(Board board, String name) {
        // Setting up the board template
        BoardTemplate template = new BoardTemplate();
        template.width = board.width;
        template.height = board.height;
        template.phase = board.phase.toString();
        template.step = board.step;
        template.stepMode = board.stepMode;

        for (int i=0; i<board.width; i++) {
            for (int j=0; j<board.height; j++) {
                Space space = board.getSpace(i,j);
                if (!space.getWalls().isEmpty() || !space.getActions().isEmpty()) {
                    SpaceTemplate spaceTemplate = new SpaceTemplate();
                    spaceTemplate.x = space.x;
                    spaceTemplate.y = space.y;
                    spaceTemplate.actions.addAll(space.getActions());
                    spaceTemplate.walls.addAll(space.getWalls());
                    template.spaces.add(spaceTemplate);
                }
            }
        }

        // Setting up players
        List<Player> players = board.getPlayers();
        List<PlayerTemplate> playersTemplate = new ArrayList<>();

        for (int i = 0; i < players.size(); i++) {
            PlayerTemplate playerTemplate = new PlayerTemplate();

            playerTemplate.name = players.get(i).name;
            playerTemplate.color = players.get(i).color;
            playerTemplate.energyCount = players.get(i).energyCount;
            playerTemplate.spaceX = players.get(i).space.x;
            playerTemplate.spaceY = players.get(i).space.y;
            playerTemplate.heading = players.get(i).heading.toString();

            playersTemplate.add(playerTemplate);
        }
        template.players = playersTemplate;
        template.currentPlayer = board.getPlayerNumber(board.getCurrentPlayer());


        // Saving the board template using GSON
        ClassLoader classLoader = SaveLoadGame.class.getClassLoader();
        // TODO: this is not very defensive, and will result in a NullPointerException
        //       when the folder "resources" does not exist! But, it does not need
        //       the file "simpleCards.json" to exist!
        String filename = Objects.requireNonNull(classLoader.getResource(BOARDS_FOLDER)).getPath() + "/"
                +  name + "." + JSON_EXT;

        GsonBuilder simpleBuilder = new GsonBuilder().
                registerTypeAdapter(FieldAction.class, new Adapter<FieldAction>()).
                setPrettyPrinting();
        Gson gson = simpleBuilder.create();

        FileWriter fileWriter = null;
        JsonWriter writer = null;
        try {
            fileWriter = new FileWriter(filename);
            writer = gson.newJsonWriter(fileWriter);
            gson.toJson(template, template.getClass(), writer);

            writer.close();
        } catch (IOException e1) {
            if (writer != null) {
                try {
                    writer.close();
                    fileWriter = null;
                } catch (IOException e2) {}
            }
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e2) {}
            }
        }
    }

    public static Board loadBoard(String name) {
        ClassLoader classLoader = SaveLoadGame.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(BOARDS_FOLDER + "/" + name + "." + JSON_EXT);

        if (inputStream == null) {
            System.out.println(BOARDS_FOLDER + "/" + name + "." + JSON_EXT+"\nFolder does not exist");
        }

        GsonBuilder simpleBuilder = new GsonBuilder().registerTypeAdapter(FieldAction.class, new Adapter<FieldAction>());
        Gson gson = simpleBuilder.create();

        Board result;
        JsonReader reader = null;
        try {
            reader = gson.newJsonReader(new InputStreamReader(inputStream));
            BoardTemplate template = gson.fromJson(reader, BoardTemplate.class);

            // Actual Loading of the board
            result = new Board(template.width, template.height);

            result.phase = Phase.valueOf(template.phase);
            result.step = template.step;
            result.stepMode = template.stepMode;

            for (SpaceTemplate spaceTemplate: template.spaces) {
                Space space = result.getSpace(spaceTemplate.x, spaceTemplate.y);
                if (space != null) {
                    space.getActions().addAll(spaceTemplate.actions);
                    space.getWalls().addAll(spaceTemplate.walls);
                    space.setPlayer(null);
                }
            }

            // Loading Players
            for (int i = 0; i < template.players.size(); i++) {
                PlayerTemplate playerTemplate = template.players.get(i);

                Player newPlayer = new Player(result, playerTemplate.color, playerTemplate.name);
                result.addPlayer(newPlayer);
                newPlayer.setSpace(result.getSpace(playerTemplate.spaceX, playerTemplate.spaceY));
                newPlayer.heading = Heading.valueOf(playerTemplate.heading);
                newPlayer.energyCount = playerTemplate.energyCount;


            }
            int currentPlayerIndex = template.currentPlayer;
            result.setCurrentPlayer(result.getPlayer(currentPlayerIndex));


            System.out.println("Finished Loading");

            reader.close();
            return result;
        } catch (IOException e1) {
            if (reader != null) {
                try {
                    reader.close();
                    inputStream = null;
                } catch (IOException e2) {}
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e2) {}
            }
        }
        return null;
    }

    public static Board newBoard(int numPlayers){
        Board newBoard;

        ClassLoader classLoader = SaveLoadGame.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(BOARDS_FOLDER + "/" + DEFAULT_BOARD + "." + JSON_EXT);

        if (inputStream == null){
            System.out.println("Does not exists");
        }

        GsonBuilder simpleBuilder = new GsonBuilder().registerTypeAdapter(FieldAction.class, new Adapter<FieldAction>());
        Gson gson = simpleBuilder.create();

        try {
            JsonReader reader = gson.newJsonReader(new InputStreamReader(inputStream));
            BoardTemplate template = gson.fromJson(reader, BoardTemplate.class);

            newBoard = new Board(template.width, template.height);

            for (SpaceTemplate spaceTemplate: template.spaces) {
                Space space = newBoard.getSpace(spaceTemplate.x, spaceTemplate.y);
                if (space != null) {
                    space.getActions().addAll(spaceTemplate.actions);
                    space.getWalls().addAll(spaceTemplate.walls);
                    space.setPlayer(null);
                }
            }

            for (int i = 0; i < numPlayers; i++) {
                Player newPlayer = new Player(newBoard, PLAYER_COLORS.get(i), "Player " + (i+1));
                newBoard.addPlayer(newPlayer);
                newPlayer.setSpace(newBoard.getSpace(i % newBoard.width, i));
            }

            reader.close();

        } catch (Exception e){
            System.out.println("Failed Reading");
            newBoard = new Board(8,8);
        }

        return newBoard;
    }
}
