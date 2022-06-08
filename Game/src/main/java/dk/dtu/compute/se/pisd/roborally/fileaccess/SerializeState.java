package dk.dtu.compute.se.pisd.roborally.fileaccess;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.*;
import dk.dtu.compute.se.pisd.roborally.model.*;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class SerializeState {

    /**
     * Serializes the board into a String in JSON format.
     * @param board board to to be serialized
     * @return String of serialized board in JSON format
     */
    public static String serializeGame(Board board){
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

        for (Player player : players) {
            PlayerTemplate playerTemplate = new PlayerTemplate();
            CommandCardFieldTemplate[] programTemplate = new CommandCardFieldTemplate[player.program.length];
            CommandCardFieldTemplate[] cardsTemplate = new CommandCardFieldTemplate[player.cards.length];

            playerTemplate.name = player.name;
            playerTemplate.color = player.color;
            playerTemplate.energyCount = player.energyCount;
            playerTemplate.checkPoints = player.checkPoints;
            playerTemplate.priority = player.priority;
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
                if (card.card == null) {
                    commandTemplate.type = "";
                } else {
                    commandTemplate.type = card.card.command.name();
                    List<String> options = new ArrayList<>();
                    for (Command option : card.card.command.options) {
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
                if (card.card == null) {
                    commandTemplate.type = "";
                } else {
                    commandTemplate.type = card.card.command.name();
                    List<String> options = new ArrayList<>();
                    for (Command option : card.card.command.options) {
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

            playerTemplate.program = programTemplate;
            playerTemplate.cards = cardsTemplate;

            playersTemplate.add(playerTemplate);
        }
        template.players = playersTemplate;
        if (board.getCurrentPlayer() == null)
            template.currentPlayer = 0;
        else
            template.currentPlayer = board.getPlayerNumber(board.getCurrentPlayer());


        // Saving the board template using GSON
        GsonBuilder simpleBuilder = new GsonBuilder().
                registerTypeAdapter(FieldAction.class, new Adapter<FieldAction>()).
                setPrettyPrinting();
        Gson gson = simpleBuilder.create();

        return gson.toJson(template, template.getClass());
    }

    /**
     * Deserializes a  string in JSON format to a Board.
     * This function behaves differently based on the board to be loaded has values about player state or not. Therefore
     * we can specify if such informations shall be loaded.
     * @param jsonString The JSON String do be deserialized.
     * @param savedGame Boolean telling if the board to be loaded has been saved from a previous game or not
     * @return Board class of deserialized string
     */
    public static Board deserializeGame(String jsonString, boolean savedGame){
        GsonBuilder simpleBuilder = new GsonBuilder().registerTypeAdapter(FieldAction.class, new Adapter<FieldAction>());
        Gson gson = simpleBuilder.create();

        JsonReader reader = gson.newJsonReader(new StringReader(jsonString));
        Board result;

        BoardTemplate template = gson.fromJson(reader, BoardTemplate.class);

        // Actual Loading of the board
        result = new Board(template.width, template.height);

        if (savedGame){
            result.phase = Phase.valueOf(template.phase);
            result.step = template.step;
            result.stepMode = template.stepMode;
        }

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
            newPlayer.priority = playerTemplate.priority;


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
        }

        if (savedGame){
            int currentPlayerIndex = template.currentPlayer;
            result.setCurrentPlayer(result.getPlayer(currentPlayerIndex));
        }

        return result;
    }
}
