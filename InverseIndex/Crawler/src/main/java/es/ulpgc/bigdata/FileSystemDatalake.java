package es.ulpgc.bigdata;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.Map;

public class FileSystemDatalake {
    private static File datalakeRoot;

    public FileSystemDatalake(File datalakeRoot) {
        FileSystemDatalake.datalakeRoot = datalakeRoot;
        if (!datalakeRoot.exists()) {
            if (FileSystemDatalake.datalakeRoot.mkdirs()) {
                System.out.println("The Datalake has been successfully created at: " + datalakeRoot.getAbsolutePath());
            } else {
                System.out.println("The Datalake could not be created at: " + datalakeRoot.getAbsolutePath());
            }
        } else {
            System.out.println("The Datalake already exists in: " + datalakeRoot.getAbsolutePath());
        }
    }

    public static void saveText(int id, String text) {
        File file = new File(datalakeRoot, id + ".json");
        try {
            Map<String, String> dat = DocumentParser.transform(id, text);
					if (dat == null) {
							System.out.println("It couldn't be saved because the content of the book " + id + " is empty");
					} else {
							File jsonFile = new File(datalakeRoot, id + ".json");
							if (file.exists()){
									try (FileWriter writer = new FileWriter(jsonFile)) {
											Gson gson = new GsonBuilder().setPrettyPrinting().create();
											String json = gson.toJson(dat);
											writer.write(json);
											System.out.println("File '"+ id +".json"+"' has been succesfully updated");
									}
							} else{
									try (FileWriter writer = new FileWriter(jsonFile)) {
											Gson gson = new GsonBuilder().setPrettyPrinting().create();
											String json = gson.toJson(dat);
											writer.write(json);
											System.out.println("Saved '" + id + ".json'");
									}
							}

					}
				} catch (IOException e) {
            System.out.println("Error while saving " + id + ", with this exception: " + e);
            e.printStackTrace();
        }
    }
}