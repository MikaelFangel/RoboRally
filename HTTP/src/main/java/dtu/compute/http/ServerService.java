package dtu.compute.http;

import com.google.gson.Gson;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Objects;

@Service
public class ServerService implements IStatusComm{
    ArrayList<Server> servers = new ArrayList<>();
    private int id = 0;

    /**
     * @author Christian Andersen
     *
     * @param id of the game
     * @param gameState of the game
     */
    @Override
    public void updateGame(String id, String gameState) {
        Server server = findServer(id);
        server.setGameState(gameState);
        if (server.getMaxAmountOfPlayers() != 0) //if the max amount of player is set, we are done
            return;
        server.setMaxAmountOfPlayers(StringUtils.countOccurrencesOf(gameState, "Player "));
    }

    /**
     * @author Christian Andersen
     *
     * @param serverId of the game
     * @return the game in Json format
     */
    @Override
    public String getGameState(String serverId) {
        return (findServer(serverId)).getGameState();
    }

    /**
     * @author Christian Andersen
     *
     * @param title name of the server
     * @return gameID
     */
    @Override
    public String hostGame(String title) {
        servers.add(new Server(title, id));
        String newServerId = String.valueOf(id);
        id++;
        return newServerId;
    }

    /**
     * @author Christian Andersen
     *
     * @return list of servers
     */
    @Override
    public String listGames() {
        Gson gson = new Gson();

        ArrayList<Server> server = new ArrayList<>();
        servers.forEach(e -> {
            if (e.getAmountOfPlayers() != e.getMaxAmountOfPlayers()) {
                server.add(e);
            }
        });
        return gson.toJson(server);
    }

    /**
     * @author Christian Andersen
     *
     * @param serverToJoin is the serverID
     * @return RobotID
     */
    @Override
    public String joinGame(String serverToJoin) {
        Server s = findServer(serverToJoin);
        if (s == null)
            return "Server doesn't exist";
        if (s.getAmountOfPlayers() >= s.getMaxAmountOfPlayers())
            return "Server is full";
        s.addPlayer();
        return String.valueOf(s.getARobot());
    }

    /**
     * @author Christian Andersen
     *
     * @param serverId of the Game
     * @param robot that the player control
     */
    @Override
    public void leaveGame(String serverId, int robot) {
        Server server = findServer(serverId);
        assert server != null;
        server.setPlayerSpotFilled(robot, false);
        server.removePlayer();
        if (server.isEmpty())
            servers.remove(server);
    }

    /**
     * @author Christian Andersen
     *
     * @param serverId
     * @return
     *
     * finds the server that has the serverID matching the one sent by the player
     */
    private Server findServer(String serverId){
        for (Server e : servers) {
            if (Objects.equals(e.getId(), serverId)) {
                return e;
            }
        }
        return null;
    }
}