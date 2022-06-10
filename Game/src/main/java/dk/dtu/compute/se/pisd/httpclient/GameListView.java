package dk.dtu.compute.se.pisd.httpclient;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.StringReader;

/**
 * this will create a new window with a listview of the servers available.
 * This is runned as a seperate window and thread to allow the player to browse games while in a game him self.
 *
 * @author Christian Andersen
 */
public class GameListView {
    private Stage stage;
    private TableView<Game> table = new TableView<>();
    private ObservableList<Game> data = FXCollections.observableArrayList();
    AppController app;

    public GameListView(Client c, AppController app) {
        this.app = app;
        stage = new Stage();
        Scene scene = new Scene(new Group());

        stage.setTitle("List of Games");
        stage.setWidth(600);
        stage.setHeight(550);

        final Label label = new Label("Available games\nselect one");
        label.setFont(new Font("Arial", 20));

        table.setEditable(false);

        TableColumn id = new TableColumn("ID");
        id.setMaxWidth(50);
        id.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Game, String>, ObservableValue<String>>)
                p -> new ReadOnlyObjectWrapper(p.getValue().getId())
        );

        TableColumn serverName = new TableColumn("Server Name");
        serverName.setMaxWidth(200);
        serverName.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Game, String>, ObservableValue<String>>)
                p -> new ReadOnlyObjectWrapper(p.getValue().getTitle())
        );

        TableColumn players = new TableColumn("Players");
        players.setMaxWidth(100);
        players.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Game, Integer>, ObservableValue<Integer>>)
                p -> new ReadOnlyObjectWrapper(p.getValue().getAmountOfPlayers())
        );

        TableColumn maxPlayers = new TableColumn("Max Players");
        maxPlayers.setMaxWidth(100);
        maxPlayers.setResizable(false);
        maxPlayers.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Game, Integer>, ObservableValue<Integer>>)
                p -> new ReadOnlyObjectWrapper(p.getValue().getMaxAmountOfPlayers())
        );

        table.setItems(data);
        table.getColumns().addAll(id, serverName, players, maxPlayers);

        Button button = new Button("Join Game");
        button.setOnAction(e -> {
            app.stopGame();
            if (!table.getSelectionModel().isEmpty()) app.joinGame(table.getSelectionModel().getSelectedItem().getId());
            stage.close();
        });

        Button refresh = new Button("Refresh");
        refresh.setOnAction(e -> addServer(c.listGames()));

        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(label, table, button, refresh);

        ((Group) scene.getRoot()).getChildren().addAll(vbox);

        stage.setScene(scene);
        //stage.show();
    }

    /**
     * Here we update the list of data for the listview. This is done by taking the string from the server, and using Json converting.
     * This can be done, because we have the Server object on client side, so it knows how the server constructed the list.
     *
     * @param s list of servers in a Json string format
     * @author Christian Andersen
     */
    public void addServer(String s) {
        Gson test = new Gson();
        JsonReader jReader = new JsonReader(new StringReader(s));
        Game[] games = test.fromJson(jReader, Game[].class);
        data.clear();
        data.addAll(games);
    }

    public void viewTable() {
        stage.show();
    }
}
