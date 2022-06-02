package dk.dtu.compute.se.pisd.roborally.fileaccess;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.StartGear;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.*;
import dk.dtu.compute.se.pisd.roborally.model.*;
import netscape.javascript.JSObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ReadWriteGame {
    private static final String SAVED_BOARDS_FOLDER = "savedBoards";
    private static final String JSON_EXT = "json";

    public static void writeGameToDisk(String saveName, String json){
        // Saving the board template using GSON
        ClassLoader classLoader = SaveLoadGame.class.getClassLoader();

        String filename = Objects.requireNonNull(classLoader.getResource(SAVED_BOARDS_FOLDER)).getPath() + "/"
                +  saveName + "." + JSON_EXT;

        GsonBuilder simpleBuilder = new GsonBuilder().
                registerTypeAdapter(FieldAction.class, new Adapter<FieldAction>()).
                setPrettyPrinting();
        Gson gson = simpleBuilder.create();

        FileWriter fileWriter = null;
        JsonWriter writer = null;
        try {
            fileWriter = new FileWriter(filename);
            writer = gson.newJsonWriter(fileWriter);

            writer.jsonValue(json);
            writer.flush();

            writer.close();
        } catch (IOException e1) {
            if (writer != null) {
                try {
                    writer.close();
                    fileWriter = null;
                } catch (IOException e2) {}
            }
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e2) {}
            }
        }
    }

    public static String readGameFromDisk(String resourcePath){
        // TODO Make this read the json file
        ClassLoader classLoader = SaveLoadGame.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(resourcePath);

        try {
            String json = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            return json; // Change
        } catch (IOException e){
            System.out.println("Failed reading board from disk");

            return null;
        }
    }
}
