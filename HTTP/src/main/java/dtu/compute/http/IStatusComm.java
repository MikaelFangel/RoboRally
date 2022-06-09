package dtu.compute.http;

/**
 * Interface between client and server
 *
 * @author Christian Andersen, Mikael Fangel
 */
public interface IStatusComm {
    void updateGame(String id, String gameState);

    String getGameState(String serverId);

    String hostGame(String title);

    String listGames();

    String joinGame(String serverToJoin);

    void leaveGame(String serverId, int i);
}

