package dk.dtu.compute.se.pisd.httpclient;

/**
 * This interface is used to ensure that we have the methods on both client and server side.
 *
 * @author Christian Andersen, Mikael Fangel
 */
public interface IStatusComm {
    void updateGame(String gameState);

    String getGameState();

    String hostGame(String title);

    String listGames();

    String joinGame(String serverToJoin);

    void leaveGame();
}
