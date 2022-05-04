package dk.dtu.compute.se.pisd.roborally.fileaccess;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.*;
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
    private static final String SAVED_BOARDS_FOLDER = "savedBoards";

    private static final String JSON_EXT = "json";

    private static boolean boardLoaded = false;

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
            Player player = players.get(i);

            PlayerTemplate playerTemplate = new PlayerTemplate();
            CommandCardFieldTemplate[] programTemplate = new CommandCardFieldTemplate[player.program.length];
            CommandCardFieldTemplate[] cardsTemplate = new CommandCardFieldTemplate[player.cards.length];

            playerTemplate.name = player.name;
            playerTemplate.color = player.color;
            playerTemplate.energyCount = player.energyCount;
            playerTemplate.checkPoints = player.checkPoints;
            playerTemplate.spaceX = player.space.x;
            playerTemplate.spaceY = player.space.y;
            playerTemplate.heading = player.heading.toString();

            // Saving players cards
            for (int j = 0; j < player.cards.length; j++) {
                CommandCardField card = player.cards[j];
                CommandCardFieldTemplate commandCardFieldTemplate = new CommandCardFieldTemplate();
                CommandCardTemplate commandCardTemplate = new CommandCardTemplate();
                CommandTemplate commandTemplate = new CommandTemplate();

                // The command of the card
                if (card.card == null){
                    commandTemplate.type = "";
                } else {
                    commandTemplate.type = card.card.command.name();
                    List<String> options = new ArrayList<>();
                    for (Command option : card.card.command.options){
                        options.add(String.valueOf(option));
                    }
                }
                // The command card
                commandCardTemplate.command = commandTemplate;

                // Command Card Field
                commandCardFieldTemplate.card = commandCardTemplate;
                commandCardFieldTemplate.visible = card.visible;

                cardsTemplate[j] = commandCardFieldTemplate;
            }

            // Saving players registers
            for (int j = 0; j < player.program.length; j++) {
                CommandCardField card = player.program[j];
                CommandCardFieldTemplate commandCardFieldTemplate = new CommandCardFieldTemplate();
                CommandCardTemplate commandCardTemplate = new CommandCardTemplate();
                CommandTemplate commandTemplate = new CommandTemplate();

                // The command of the card
                if (card.card == null){
                    commandTemplate.type = "";
                } else {
                    commandTemplate.type = card.card.command.name();
                    List<String> options = new ArrayList<>();
                    for (Command option : card.card.command.options){
                        options.add(String.valueOf(option));
                    }
                }
                // The command card
                commandCardTemplate.command = commandTemplate;

                // Command Card Field
                commandCardFieldTemplate.card = commandCardTemplate;
                commandCardFieldTemplate.visible = card.visible;

                programTemplate[j] = commandCardFieldTemplate;
            }

            // Last REMOVE COMMENT
            playerTemplate.program = programTemplate;
            playerTemplate.cards = cardsTemplate;

            playersTemplate.add(playerTemplate);
        }
        template.players = playersTemplate;
        template.currentPlayer = board.getPlayerNumber(board.getCurrentPlayer());


        // Saving the board template using GSON
        ClassLoader classLoader = SaveLoadGame.class.getClassLoader();
        // TODO: this is not very defensive, and will result in a NullPointerException
        //       when the folder "resources" does not exist! But, it does not need
        //       the file "simpleCards.json" to exist!
        String filename = Objects.requireNonNull(classLoader.getResource(SAVED_BOARDS_FOLDER)).getPath() + "/"
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
        InputStream inputStream = classLoader.getResourceAsStream(SAVED_BOARDS_FOLDER + "/" + name + "." + JSON_EXT);

        if (inputStream == null) {
            System.out.println(BOARDS_FOLDER + "/" + name + "." + JSON_EXT+"\nFolder does not exist");
        }

        GsonBuilder simpleBuilder = new GsonBuilder().registerTypeAdapter(FieldAction.class, new Adapter<FieldAction>());
        Gson gson = simpleBuilder.create();

        Board result;
        JsonReader reader = null;
        try {
            assert inputStream != null;
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
                newPlayer.checkPoints = playerTemplate.checkPoints;


                CommandCardField[] newCards = new CommandCardField[playerTemplate.cards.length];
                CommandCardField[] newProgram = new CommandCardField[playerTemplate.program.length];

                // Loading players cards
                for (int j = 0; j < playerTemplate.cards.length; j++) {
                    String commandName = playerTemplate.cards[j].card.command.type;
                    if (commandName.equals("")){
                        CommandCardField ccf = new CommandCardField(newPlayer);
                        newCards[j] = ccf;
                    } else {
                        Command c = Command.valueOf(commandName);
                        CommandCard cc = new CommandCard(c);
                        CommandCardField ccf = new CommandCardField(newPlayer);
                        ccf.setCard(cc);
                        ccf.setVisible(playerTemplate.cards[j].visible);
                        newCards[j] = ccf;
                    }
                }

                // loading players program
                for (int j = 0; j < playerTemplate.program.length; j++) {
                    String commandName = playerTemplate.program[j].card.command.type;
                    if (commandName.equals("")){
                        CommandCardField ccf = new CommandCardField(newPlayer);
                        newProgram[j] = ccf;
                    } else {
                        Command c = Command.valueOf(commandName);
                        CommandCard cc = new CommandCard(c);
                        CommandCardField ccf = new CommandCardField(newPlayer);
                        ccf.setCard(cc);
                        ccf.setVisible(playerTemplate.program[j].visible);
                        newProgram[j] = ccf;
                    }
                }

                // Finish up
                newPlayer.cards = newCards;
                newPlayer.program = newProgram;
                System.out.println("");
            }
            int currentPlayerIndex = template.currentPlayer;
            result.setCurrentPlayer(result.getPlayer(currentPlayerIndex));

            // Important
            boardLoaded = true;
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

    public static Board newBoard(int numPlayers, String boardName){
        Board newBoard;

        ClassLoader classLoader = SaveLoadGame.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(BOARDS_FOLDER + "/" + boardName + "." + JSON_EXT);

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

    public static boolean getBoardLoaded(){
        return boardLoaded;
    }
}
