package dk.dtu.compute.se.pisd.httpclient;

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
 * @author
 */
public class Client implements IStatusComm {
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10)).build();

    private String server = "http://localhost:8080";
    private String serverID = "";
    private boolean connectedToServer = false;
    private int robotNumber;

    public boolean isConnectedToServer(){
        return connectedToServer;
    }

    /**
     * Updates the game state on the game server with a JSON string
     *
     * @param gameState JSON string to update state with
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
            String result = response.thenApply(HttpResponse::body).get(5, HOURS);
            // Result ignorer for now
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the current game state as a JSON string
     *
     * @return JSON string with game state
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
            return "Service timeout";
        } finally {
            serverID = "";
        }

        return "success";
    }

    /**
     * Lists all games available on the server
     *
     * @return list of available games
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
     * Joins a game and get the current game state
     *
     * @param serverToJoin the id of the server to join
     * @return gamestate and empty string if game is not up yet
     */
    @Override
    public String joinGame(String serverToJoin) {
        if (!Objects.equals(serverID, ""))
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
            if (message.statusCode() == 200)
                return message.body();
            if (message.statusCode() == 404)
                return message.body();
            robotNumber = Integer.parseInt(message.body());
            serverID = serverToJoin;

        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            return "service timeout";
        }
        return "ok";
    }

    @Override
    public void leaveGame() {
        if (Objects.equals(serverID, ""))
            return;
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .uri(URI.create(server + "/game/" + serverID))
                .header("User-Agent", "RoboRally Client")
                .header("Content-Type", "text/plain")
                .build();
        CompletableFuture<HttpResponse<String>> response =
                HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        serverID = "";
    }

    public String getServer() {
        return server;
    }

    /**
     * Sets the ip address of the server
     *
     * @param server ip of server to communicate with
     * @throws IllegalIPExeception throws illegal ip exception if ip is not valid
     */
    public void setServer(String server) throws IllegalIPExeception {
        // Simple regex pattern to check for string contains ip
        Pattern pattern = Pattern.compile("^(?:\\d{1,3}\\.){3}\\d{1,3}$");
        Matcher matcher = pattern.matcher(server);
        if (matcher.find())
            this.server = "http://" + server + ":8080";
        else
            throw new IllegalIPExeception();
    }

    public class IllegalIPExeception extends Exception {
        public IllegalIPExeception() {
            super("Not a valid IP");
        }
    }
}
