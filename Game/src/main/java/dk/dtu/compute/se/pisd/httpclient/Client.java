package dk.dtu.compute.se.pisd.httpclient;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import static java.util.concurrent.TimeUnit.*;

public class Client implements IStatusComm {
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10)).build();

    private String server = "http://localhost:8080";

    @Override
    public void updateGame(String gameState) {

    }

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
            result = response.thenApply((r) -> r.body()).get(5, SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }

        return result;
    }
}
