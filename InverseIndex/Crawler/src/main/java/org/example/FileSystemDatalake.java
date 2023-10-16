package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class FileSystemDatalake {
    private static File datalakeRoot;

    public FileSystemDatalake(File datalakeRoot) {
        FileSystemDatalake.datalakeRoot = datalakeRoot;

        //check if the root directory of the Datalake already exists
        if (!datalakeRoot.exists()) {
            //attempt to create the root directory
            if (FileSystemDatalake.datalakeRoot.mkdirs()) {
                System.out.println("The Datalake has been successfully created at: " + datalakeRoot.getAbsolutePath());
            }
            else {
                System.out.println("The Datalake could not be created at: " + datalakeRoot.getAbsolutePath());
            }
        }
        else {
            System.out.println("The Datalake already exists in: " + datalakeRoot.getAbsolutePath());
        }
    }
    public static void saveText(int id, String text) {
        try {
            Map<String, String> dat = LibroGutenberg.transform(id, text);
            if (dat != null) {
                //create a JSON file in the Datalake directory.
                File jsonFile = new File(datalakeRoot, id + ".json");

                try (FileWriter writer = new FileWriter(jsonFile)) {
                    //converts the data to JSON and write in the file
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String json = gson.toJson(dat);
                    writer.write(json);
                }

                System.out.println("Saved " + id);
            }
            else {
                System.out.println("It couldn't be saved because the content of the book " + id + " is empty");
            }
        } catch (IOException e) {
            System.out.println("Error while saving " + id + ", with this exception: " + e);
            e.printStackTrace();
        }
    }
}

