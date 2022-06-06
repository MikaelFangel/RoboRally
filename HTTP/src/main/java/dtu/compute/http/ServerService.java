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

    @Override
    public void updateGame(String id, String gameState) {
        Server server = findServer(id);
        server.setGameState(gameState);
        if (server.getMaxAmountOfPlayers() != 0)
            return;
        server.setMaxAmountOfPlayers(StringUtils.countOccurrencesOf(gameState, "Player "));
    }

    @Override
    public String getGameState(String serverId) {
        return (findServer(serverId)).getGameState();
    }

    @Override
    public String hostGame(String title) {
        servers.add(new Server(title, id));
        String newServerId = String.valueOf(id);
        id++;
        return newServerId;
    }

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

    @Override
    public String joinGame(String serverToJoin) {
        Server s = findServer(serverToJoin);
        assert s != null;
        if (s.getAmountOfPlayers() == 6)
            return "error";
        s.addPlayer();
        return "ok";
    }

    @Override
    public void leaveGame(String serverId) {
        Server server = findServer(serverId);
        assert server != null;
        server.removePlayer();
        if (server.isEmpty())
            servers.remove(server);
    }

    private Server findServer(String serverId){
        for (Server e : servers) {
            if (Objects.equals(e.getId(), serverId)) {
                return e;
            }
        }
        return null;
    }
}