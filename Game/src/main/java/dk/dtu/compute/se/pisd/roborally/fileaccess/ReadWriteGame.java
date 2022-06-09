package dk.dtu.compute.se.pisd.roborally.fileaccess;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import dk.dtu.compute.se.pisd.roborally.controller.fieldaction.FieldAction;
import dk.dtu.compute.se.pisd.roborally.exceptions.BoardNotFoundException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Frederik G. Petersen (s215834)
 *
 * Can Read serialized Roborally boards from the drive and Write serialized board to the drive.
 * Provides functionality for getting files in specific folder needed for the game.
 */
public class ReadWriteGame {
    private static final String SAVED_BOARDS_FOLDER = "savedBoards";
    private static final String DEFAULT_BOARDS_FOLDER = "boards";
    private static final String JSON_EXT = "json";

    /**
     * @author Frederik G. Petersen (s215834)
     *
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
        filename = filename.replaceAll("%20"," ");
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
                } catch (IOException ignored) {
                }
            }
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException ignored) {}
            }
        }
    }

    /**
     * @author Frederik G. Petersen (s215834)
     *
     * Reads a String from a file in the target directory.
     * @param resourcePath The path to the folder containing the boards.
     * @return Full string of everything that is contained within the file.
     */
    public static String readGameFromDisk(String resourcePath) throws BoardNotFoundException {
        try {
            ClassLoader classLoader = ReadWriteGame.class.getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream(resourcePath);
            if (inputStream == null) throw new BoardNotFoundException(resourcePath);

            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8); // Change
        } catch (IOException e){
            throw new BoardNotFoundException(resourcePath);
        }
    }

    /**
     * @author Frederik G. Petersen (s215834)
     *
     * Gets the names of saved boards in an already specified folder.
     * @return List of string, with names of each file, without .json extension
     */
    public static List<String> getNamesOfSavedBoards(){
        File[] listOfFiles = getFilesInFolder(SAVED_BOARDS_FOLDER);
        List<String> fileNames = new ArrayList<>();

        for (File file : listOfFiles){
            fileNames.add(file.getName().replace(".json", ""));
        }

        return fileNames;
    }

    /**
     * @author Frederik G. Petersen (s215834)
     *
     * Gets the names of default boards in an already specified folder.
     * @return List of string, with names of each file, without .json extension
     */
    public static List<String> getNamesOfDefaultBoard(){
        File[] listOfFiles = getFilesInFolder(DEFAULT_BOARDS_FOLDER);
        List<String> fileNames = new ArrayList<>();

        for (File file : listOfFiles){
            fileNames.add(file.getName().replace(".json", ""));
        }

        return fileNames;
    }

    /**
     * @author Frederik G. Petersen (s215834)
     *
     * Extracts all files in a folder located on the highest level in resources.
     * @param folderName The name of the folder
     * @return File Array with all files in that folder
     */
    private static File[] getFilesInFolder(String folderName){
        ClassLoader classLoader = ReadWriteGame.class.getClassLoader();
        String fullPath = Objects.requireNonNull(classLoader.getResource(folderName)).getPath();

        // The folder cannot be found on some OS when there is white space in folder names.
        fullPath = fullPath.replace("%20", " ");

        File folder = new File(fullPath);

        return folder.listFiles();
    }
}
