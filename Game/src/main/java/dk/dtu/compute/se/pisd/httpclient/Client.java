package dk.dtu.compute.se.pisd.httpclient;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
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

    private String server = "http://localhost:8080/gameState";

    /**
     * Updates the game state on the game server with a JSON string
     *
     * @param gameState JSON string to update state with
     */
    @Override
    public void updateGame(String gameState) {
        HttpRequest request = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofString(gameState))
                .uri(URI.create(server))
                .setHeader("User-Agent", "RoboRally Client")
                .setHeader("Content-Type", "application/json")
                .build();
        CompletableFuture<HttpResponse<String>> response =
                HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        try {
            String result = response.thenApply(HttpResponse::body).get(5, SECONDS);
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
                .uri(URI.create(server))
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

    public String getServer() {
        return server;
    }

    public void setServer(String server) throws IllegalIPExeception {
        // Simple regex pattern to check for string contains ip
        Pattern pattern = Pattern.compile("^(?:\\d{1,3}\\.){3}\\d{1,3}$");
        Matcher matcher = pattern.matcher(server);
        System.out.println(matcher.find());
        if (matcher.find())
            this.server = "http://" + server + ":8080/gameState";
        else
            throw new IllegalIPExeception();
    }

    public class IllegalIPExeception extends Exception {
        public IllegalIPExeception() {
            super("Not a valid IP");
        }
    }
}
