package dtu.compute.http;

import com.google.gson.Gson;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ServerService implements IStatusComm{
    ArrayList<Server> servers = new ArrayList<>();
    private int id = 0;

    @Override
    public void updateGame(String id, String gameState) {
        servers.get(Integer.parseInt(id)).setGameState(gameState);
    }

    @Override
    public String getGameState(String serverId) {
        return servers.get(Integer.parseInt(serverId)).getGameState();
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
        return gson.toJson(servers);
    }

    @Override
    public String joinGame(String serverToJoin) {
        servers.get(Integer.parseInt(serverToJoin)).addPlayer();
        return "ok";
    }

    @Override
    public void leaveGame(String serverId) {
        Server server = servers.get(Integer.parseInt(serverId));
        server.removePlayer();
        if (server.isEmpty())
            servers.remove(server);
    }
}