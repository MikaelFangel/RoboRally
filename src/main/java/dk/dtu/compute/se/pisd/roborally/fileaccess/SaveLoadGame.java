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
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SaveLoadGame {

    private static final String DEFAULT_BOARD = "defaultboard";
    private static final String DEFAULT_PLAYERS = "defaultPlayers";

    private static final String GAMES_FOLDER = "games";
    private static final String JSON_EXT = "json";

    private static String nameOfGame;

    public static void saveGame(String gameName, GameController gameController){
        nameOfGame = gameName;

        prepareDirectories(gameName);
        savePlayers(gameController.board.getPlayers());
        saveBoard(gameController.board, gameName);
    }

    public static void savePlayers(List<Player> players){
        // Setup
        for (int i = 0; i < players.size(); i++){
            PlayerTemplate playerTemplates = new PlayerTemplate();

            playerTemplates = new PlayerTemplate();
            playerTemplates.name = players.get(i).name;
            playerTemplates.color = players.get(i).color;
            playerTemplates.energyCount = players.get(i).energyCount;

            playerTemplates.spaceX = players.get(i).space.x;
            playerTemplates.spaceY = players.get(i).space.y;

            playerTemplates.heading = players.get(i).heading.toString();

            // Saving
            ClassLoader classLoader = SaveLoadGame.class.getClassLoader();

            GsonBuilder simpleBuilder = new GsonBuilder().registerTypeAdapter(FieldAction.class, new Adapter<FieldAction>()).setPrettyPrinting();
            Gson gson = simpleBuilder.create();

            FileWriter fileWriter = null;
            JsonWriter writer = null;

            try {
                String filename = Objects.requireNonNull(classLoader.getResource(GAMES_FOLDER)).getPath() + "/" + nameOfGame + "/"
                        + "players" + "/"+ "player" + String.valueOf(i+1) +  "." + JSON_EXT;

                fileWriter = new FileWriter(filename);
                writer = gson.newJsonWriter(fileWriter);
                gson.toJson(playerTemplates, playerTemplates.getClass(), writer);
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
    }

    public static List<Player> loadPlayers(String name, Board board){
        if (name == null){
            name = DEFAULT_PLAYERS;
        }

        List<Player> createdPlayers = new ArrayList<>();

        ClassLoader classLoader = SaveLoadGame.class.getClassLoader();
        GsonBuilder simpleBuilder = new GsonBuilder().registerTypeAdapter(FieldAction.class, new Adapter<FieldAction>());
        Gson gson = simpleBuilder.create();
        JsonReader reader = null;

        String playerDirPath = Objects.requireNonNull(classLoader.getResource(GAMES_FOLDER)).getPath() + "/" + name + "/"
                + "players";
        File playersDir = new File(String.valueOf(playerDirPath));

        if (!playersDir.exists()){
            System.out.println("Directory Does NOT exists!");
        }

        for (int i = 0; i < playersDir.listFiles().length; i++) {
            InputStream inputStream = classLoader.getResourceAsStream(GAMES_FOLDER + "/" + name + "/" + "players"
                    + "/" + "player" + String.valueOf(i+1) + "." + JSON_EXT);


            if (inputStream == null) {
                // TODO Return basic players!!
            }

            try {
                reader = gson.newJsonReader(new InputStreamReader(inputStream));
                PlayerTemplate playerTemplate = gson.fromJson(reader, PlayerTemplate.class);

                //for (PlayerTemplate playerTemplate : playerTemplates){
                Player newPlayer = new Player(board, playerTemplate.color, playerTemplate.name);
                newPlayer.setSpace(board.getSpace(playerTemplate.spaceX, playerTemplate.spaceY));
                newPlayer.heading = Heading.valueOf(playerTemplate.heading);
                newPlayer.energyCount = playerTemplate.energyCount;

                createdPlayers.add(newPlayer);

                reader.close();
                //}

            } catch (IOException e){
                System.out.println(e);
            }
        }
        return createdPlayers;
    }

    public static void saveBoard(Board board, String name) {
        // Setting up the board template
        BoardTemplate template = new BoardTemplate();
        template.width = board.width;
        template.height = board.height;

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

        // Saving the board template using GSON
        ClassLoader classLoader = SaveLoadGame.class.getClassLoader();
        // TODO: this is not very defensive, and will result in a NullPointerException
        //       when the folder "resources" does not exist! But, it does not need
        //       the file "simpleCards.json" to exist!
        String filename =
                Objects.requireNonNull(classLoader.getResource(GAMES_FOLDER)).getPath() + "/" + nameOfGame + "/" +  "board" + "." + JSON_EXT;

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
        if (name == null) {
            name = DEFAULT_BOARD;
        }

        ClassLoader classLoader = SaveLoadGame.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(GAMES_FOLDER + "/" + name + "/" + "board" + "." + JSON_EXT);
        if (inputStream == null) {
            // TODO these constants should be defined somewhere
            return new Board(8,8);
        }

        // In simple cases, we can create a Gson object with new Gson():
        GsonBuilder simpleBuilder = new GsonBuilder().
                registerTypeAdapter(FieldAction.class, new Adapter<FieldAction>());
        Gson gson = simpleBuilder.create();

        Board result;
        // FileReader fileReader = null;
        JsonReader reader = null;
        try {
            // fileReader = new FileReader(filename);
            reader = gson.newJsonReader(new InputStreamReader(inputStream));
            BoardTemplate template = gson.fromJson(reader, BoardTemplate.class);

            result = new Board(template.width, template.height);
            for (SpaceTemplate spaceTemplate: template.spaces) {
                Space space = result.getSpace(spaceTemplate.x, spaceTemplate.y);
                if (space != null) {
                    space.getActions().addAll(spaceTemplate.actions);
                    space.getWalls().addAll(spaceTemplate.walls);
                }
            }
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

    public static void prepareDirectories(String gameName){
        ClassLoader classLoader = SaveLoadGame.class.getClassLoader();

        String gameDirName = Objects.requireNonNull(classLoader.getResource(GAMES_FOLDER)).getPath() + "/" +  gameName;
        String playersDirName = Objects.requireNonNull(classLoader.getResource(GAMES_FOLDER)).getPath() + "/" +  gameName + "/" + "players";

        File gameDirectory = new File(String.valueOf(gameDirName));
        File playersDirectory = new File(String.valueOf(playersDirName));

        gameDirectory.mkdir();
        playersDirectory.mkdir();


    }
}
