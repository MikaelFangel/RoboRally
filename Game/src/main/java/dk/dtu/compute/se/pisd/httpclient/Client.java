package dk.dtu.compute.se.pisd.httpclient;

import dk.dtu.compute.se.pisd.roborally.exceptions.IllegalIPException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.concurrent.TimeUnit.*;

/**
 * Create a simple http client that can interact with the RoboRally game server
 *
 * @author Mikael Fangel,  Christian Andersen
 */
public class Client implements IStatusComm {
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10)).build();
    private String server = "http://localhost:8080";    //server URL, can later be changed to get this data from a DNS request or pointing directly to a server IP.
    private String serverID = "";                       //will be used after creating the connection, to inform the server what game we are in.
    private boolean connectedToServer = false;          //used to easily check if we already connected to a server, so that we can disconnect from that one first.
    private int robotNumber;                            //Is only used to free up our given robot in case we want to leave a game that has not yet concluded.

    public boolean isConnectedToServer() {
        return connectedToServer;
    }

    /**
     * Updates the game state on the game server with a JSON string containing the latest game state
     * the server should the store this state for future retrieval
     *
     * @param gameState JSON string to update state with
     * @author Mikael Fangel
     */
    @Override
    public void updateGame(String gameState) {
        HttpRequest request = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofString(gameState))
                .uri(URI.create(server + "/gameState/" + serverID))
                .setHeader("User-Agent", "RoboRally Client")
                .setHeader("Content-Type", "application/json")
                .build();
        CompletableFuture<HttpResponse<String>> response =
                HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        try {
            String result = response.thenApply(HttpResponse::body).get(5, SECONDS);
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the current game state as a JSON string which then should be deserialized
     *
     * @return JSON string with game state
     * @author Mikael Fangel
     */
    @Override
    public String getGameState() {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(server + "/gameState/" + serverID))
                .setHeader("User-Agent", "RoboRally Client")
                .header("Content-Type", "application/json")
                .build();
        CompletableFuture<HttpResponse<String>> response =
                HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        String result;
        try {
            result = response.thenApply(HttpResponse::body).get(5, SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    /**
     * Hosts a new game on the server and sets the server id to future communication
     *
     * @param title the title of the new server
     * @return serverId string
     * @author Mikael Fangel, Christian Andersen
     */
    @Override
    public String hostGame(String title) {
        if (!Objects.equals(serverID, ""))
            leaveGame();
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(title))
                .uri(URI.create(server + "/game"))
                .setHeader("User-Agent", "RoboRally Client")
                .header("Content-Type", "text/plain")
                .build();
        CompletableFuture<HttpResponse<String>> response =
                HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        try {
            serverID = response.thenApply(HttpResponse::body).get(5, SECONDS);
            if (response.get().statusCode() == 500)
                return response.get().body();
            connectedToServer = true;
            robotNumber = 0;
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            serverID = "";
            return "Service timeout";
        }

        return "success";
    }

    /**
     * Lists all games available on the server the list is given as JSON string which
     * list the id, name, maximum number of players and current number of players in the game
     *
     * @return list of available games
     * @author Mikael Fangel
     */
    @Override
    public String listGames() {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(server + "/game"))
                .setHeader("User-Agent", "RoboRally Client")
                .header("Content-Type", "application/json")
                .build();
        CompletableFuture<HttpResponse<String>> response =
                HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        String result;
        try {
            result = response.thenApply(HttpResponse::body).get(5, SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            return "server timeout";
        }
        return result;

    }

    /**
     * Joins a game and get the current game state from a specific server id
     *
     * @param serverToJoin the id of the server to join
     * @return gamestate and empty string if game is not up yet
     * @author Christian Andersen
     */
    @Override
    public String joinGame(String serverToJoin) {
        if (!Objects.equals(serverToJoin, ""))
            leaveGame();
        HttpRequest request = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofString(""))
                .uri(URI.create(server + "/game/" + serverToJoin))
                .header("User-Agent", "RoboRally Client")
                .header("Content-Type", "text/plain")
                .build();
        CompletableFuture<HttpResponse<String>> response =
                HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        try {
            HttpResponse<String> message = response.get(5, SECONDS); //gets the message back from the server
            if (message.statusCode() == 404)
                return message.body();
            robotNumber = Integer.parseInt(message.body());
            serverID = serverToJoin;

        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            return "service timeout";
        }
        return "ok";
    }

    /**
     * Tells the server that we want to leave our current game
     *
     * @author Christian Andersen
     */
    @Override
    public void leaveGame() {
        if (Objects.equals(serverID, ""))
            return;
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .uri(URI.create(server + "/game/" + serverID + "/" + robotNumber))
                .header("User-Agent", "RoboRally Client")
                .header("Content-Type", "text/plain")
                .build();
        new Thread(() -> {
            int tries = 0;
            CompletableFuture<HttpResponse<String>> response =
                    HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            do {
                try {
                    response.get(5, SECONDS);
                    break;
                } catch (ExecutionException | InterruptedException e) {
                    break;
                } catch (TimeoutException e) {
                    tries++;
                }
            } while (tries != 10);
        }).start();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        serverID = "";
    }

    /**
     * Sets the ip address of the server in the process of setting up the ip for the server
     * it checks if the ip address can pass a simple regex if it can't it throws
     * an IllegalIPException
     *
     * @param server ip of server to communicate with
     * @throws IllegalIPException throws illegal ip exception if ip is not valid
     * @author Mikael Fangel
     */
    public void setServer(String server) throws IllegalIPException {
        // Simple regex pattern to check for string contains ip
        Pattern pattern = Pattern.compile("^(?:\\d{1,3}\\.){3}\\d{1,3}$");
        Matcher matcher = pattern.matcher(server);
        if (matcher.find())
            this.server = "http://" + server + ":8080";
        else
            throw new IllegalIPException();
    }


    /**
     * @return the robot number given by the server
     * @author Frederik Greve Petersen
     */
    public int getRobotNumber() {
        return robotNumber;
    }
}
