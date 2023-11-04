package org.example;

import dbManager.DropTable;

import java.io.File;

public class DatamartController {

    private final MetadataExtractor metadataCall = new MetadataExtractor();
    private final WordsExtractor wordsCall = new WordsExtractor();
    public void run(File datalake) {
        if (datalake.exists() && datalake.isDirectory()) {
            new MetadataExtractor().filter(datalake);
            new DropTable();
            metadataCall.filter(datalake);
            wordsCall.filter();

        } else {
            System.out.println("The directory is not valid or not exists.");

        }

    }
}
