package dk.dtu.compute.se.pisd.httpclient;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.StringReader;

public class ServerListView{

    private Stage stage;
    private TableView<Server> table = new TableView<>();
    private ObservableList<Server> data = FXCollections.observableArrayList();
    public ServerListView(Client c){
        stage = new Stage();
        Scene scene = new Scene(new Group());

        stage.setTitle("List of Servers");
        stage.setWidth(300);
        stage.setHeight(550);

        final Label label = new Label("Pick a server");
        label.setFont(new Font("Arial", 20));

        table.setEditable(false);

        TableColumn id = new TableColumn("ID");
        id.setMaxWidth(50);
        id.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Server, String>, ObservableValue<String>>)
                p -> new ReadOnlyObjectWrapper(p.getValue().getId())
        );

        TableColumn serverName = new TableColumn("Server Name");
        serverName.setMaxWidth(200);
        serverName.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Server, String>, ObservableValue<String>>)
                p -> new ReadOnlyObjectWrapper(p.getValue().getTitle())
        );

        TableColumn players = new TableColumn("Players");
        players.setMaxWidth(100);
        players.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Server, Integer>, ObservableValue<Integer>>)
                p -> new ReadOnlyObjectWrapper(p.getValue().getAmountOfPlayers())
        );

        players.setCellValueFactory(
                new PropertyValueFactory<Server, String>("amountOfPlayers")
        );

        table.setItems(data);
        table.getColumns().addAll(id, serverName, players);

        Button button = new Button("Join Game");
        button.setOnAction(e -> c.joinGame(table.getSelectionModel().getSelectedItem().getId()));

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

    public void addServer(String s){
        Gson test = new Gson();
        JsonReader jReader = new JsonReader(new StringReader(s));
        Server[] servers = test.fromJson(jReader,Server[].class);
        data.clear();
        data.addAll(servers);
    }

    public void viewTable(){
        stage.show();
    }
}
