package dk.dtu.compute.se.pisd.roborally.fileaccess;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import dk.dtu.compute.se.pisd.roborally.controller.*;
import dk.dtu.compute.se.pisd.roborally.exceptions.BoardNotFoundException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ReadWriteGame {
    private static final String SAVED_BOARDS_FOLDER = "savedBoards";
    private static final String DEFAULT_BOARDS_FOLDER = "boards";
    private static final String JSON_EXT = "json";

    /**
     * Writes a deserialized Board in form of a string to a specific directory for saved boards
     * The functions is only usable for saving boards and cannot be used for general purpose for saving.
     * @param saveName The name of the .json file to be saved.
     * @param json The JSON string that should be written to a file
     */
    public static void writeGameToDisk(String saveName, String json){
        // Saving the board template using GSON
        ClassLoader classLoader = ReadWriteGame.class.getClassLoader();

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
                } catch (IOException e2) {
                }
            }
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e2) {}
            }
        }
    }

    /**
     * Reads a String from a file in the target directory.
     * @param resourcePath The path to the folder containing the boards.
     * @return Full string of everything that is contained within the file.
     */
    public static String readGameFromDisk(String resourcePath) throws BoardNotFoundException {
        try {
            ClassLoader classLoader = ReadWriteGame.class.getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream(resourcePath);
            if (inputStream == null) throw new BoardNotFoundException(resourcePath);

            String json = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            return json; // Change
        } catch (IOException e){
            BoardNotFoundException exception = new BoardNotFoundException(resourcePath);
            throw exception;
        }
    }

    public static List<String> getNamesOfSavedBoards(){
        File[] listOfFiles = getFilesInFolder(SAVED_BOARDS_FOLDER);
        List<String> fileNames = new ArrayList<>();

        for (File file : listOfFiles){
            fileNames.add(file.getName().replace(".json", ""));
        }

        return fileNames;
    }
    
    public static List<String> getNamesOfDefaultBoard(){
        File[] listOfFiles = getFilesInFolder(DEFAULT_BOARDS_FOLDER);
        List<String> fileNames = new ArrayList<>();

        for (File file : listOfFiles){
            fileNames.add(file.getName().replace(".json", ""));
        }

        return fileNames;
    }

    private static File[] getFilesInFolder(String folderName){
        ClassLoader classLoader = ReadWriteGame.class.getClassLoader();
        String fullPath = classLoader.getResource(folderName).getPath();

        File folder = new File(fullPath);
        File[] listOfFiles = folder.listFiles();

        return listOfFiles;
    }
}
