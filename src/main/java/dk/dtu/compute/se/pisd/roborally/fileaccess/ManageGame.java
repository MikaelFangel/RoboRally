package dk.dtu.compute.se.pisd.roborally.fileaccess;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.PlayerTemplate;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ManageGame {

    private static final String DEFAULT_PLAYERS = "defaultPlayers";

    private static final String GAMES_FOLDER = "games";
    private static final String JSON_EXT = "json";

    private static String nameOfGame;

    public static void saveGame(String gameName, List<Player> players){
        nameOfGame = gameName;

        prepareDirectories(gameName);
        savePlayers(players);
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
            ClassLoader classLoader = LoadBoard.class.getClassLoader();

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

        ClassLoader classLoader = LoadBoard.class.getClassLoader();
        // TODO make another "/" when adding new directory
        InputStream inputStream = classLoader.getResourceAsStream(GAMES_FOLDER + "/" + "players" + "." + JSON_EXT);
        if (inputStream == null) {
            // TODO Return basic players!!
        }

        GsonBuilder simpleBuilder = new GsonBuilder().registerTypeAdapter(FieldAction.class, new Adapter<FieldAction>());
        Gson gson = simpleBuilder.create();
        JsonReader reader = null;

        List<Player> createdPlayers = new ArrayList<>();

        try {
            reader = gson.newJsonReader(new InputStreamReader(inputStream));
            PlayerTemplate playerTemplate = gson.fromJson(reader, PlayerTemplate.class);

            //for (PlayerTemplate playerTemplate : playerTemplates){
                Player newPlayer = new Player(board, playerTemplate.color, playerTemplate.name);
                newPlayer.space = board.getSpace(playerTemplate.spaceX, playerTemplate.spaceY);
                newPlayer.heading = Heading.valueOf(playerTemplate.heading);
                newPlayer.energyCount = playerTemplate.energyCount;

                createdPlayers.add(newPlayer);

                reader.close();
            //}

        } catch (IOException e){
            System.out.println(e);
        }

        return createdPlayers;
    }

    public static void prepareDirectories(String gameName){
        ClassLoader classLoader = LoadBoard.class.getClassLoader();

        String gameDirName = Objects.requireNonNull(classLoader.getResource(GAMES_FOLDER)).getPath() + "/" +  gameName;
        String playersDirName = Objects.requireNonNull(classLoader.getResource(GAMES_FOLDER)).getPath() + "/" +  gameName + "/" + "players";

        File gameDirectory = new File(String.valueOf(gameDirName));
        File playersDirectory = new File(String.valueOf(playersDirName));

        gameDirectory.mkdir();
        playersDirectory.mkdir();


    }
}
