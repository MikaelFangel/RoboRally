package dk.dtu.compute.se.pisd.httpclient;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class Server {
    private String id;
    private String title;
    private transient String gameState;
    private int amountOfPlayers;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getAmountOfPlayers() {
        return amountOfPlayers;
    }
}