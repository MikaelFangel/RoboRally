package dtu.compute.http;

import com.google.gson.Gson;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;

/**
 * @author Christian Andersen
 */
@Service
public class GameController implements IStatusComm {
    ArrayList<Game> games = new ArrayList<>();
    private int id = 0;

    /**
     * @param id        of the game
     * @param gameState of the game
     */
    @Override
    public void updateGame(String id, String gameState) {
        Game game = findServer(id);
        game.setGameState(gameState);
        if (game.getMaxAmountOfPlayers() != 0) //if the max amount of player is set, we are done
            return;
        game.setMaxAmountOfPlayers(StringUtils.countOccurrencesOf(gameState, "Player "));
    }

    /**
     * @param serverId of the game
     * @return the game in Json format
     */
    @Override
    public String getGameState(String serverId) {
        return (findServer(serverId)).getGameState();
    }

    /**
     * @param title name of the server
     * @return gameID
     */
    @Override
    public String hostGame(String title) {
        games.add(new Game(title, id));
        String newServerId = String.valueOf(id);
        id++;
        return newServerId;
    }

    /**
     * @return list of servers
     */
    @Override
    public String listGames() {
        Gson gson = new Gson();

        ArrayList<Game> game = new ArrayList<>();
        games.forEach(e -> {
            if (e.getAmountOfPlayers() != e.getMaxAmountOfPlayers()) {
                game.add(e);
            }
        });
        return gson.toJson(game);
    }

    /**
     * @param serverToJoin is the serverID
     * @return RobotID
     */
    @Override
    public String joinGame(String serverToJoin) {
        Game s = findServer(serverToJoin);
        if (s == null)
            return "Server doesn't exist";
        if (s.getAmountOfPlayers() >= s.getMaxAmountOfPlayers())
            return "Server is full";
        s.addPlayer();
        return String.valueOf(s.getARobot());
    }

    /**
     * @param serverId of the Game
     * @param robot    that the player control
     */
    @Override
    public void leaveGame(String serverId, int robot) {
        Game game = findServer(serverId);
        if (game == null)
            return;
        if (game.getMaxAmountOfPlayers() != 0)
            game.setPlayerSpotFilled(robot, false);
        game.removePlayer();
        if (game.isEmpty())
            games.remove(game);
    }

    /**
     * finds the server that has the serverID matching the one sent by the player
     *
     * @param serverId of the game that we need to find
     * @return the memory location of that game
     */
    private Game findServer(String serverId) {
        return games.stream()
                .filter(s -> s.getId().equals(serverId))
                .findFirst()
                .orElse(null);
    }
}