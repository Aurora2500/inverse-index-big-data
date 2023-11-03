package org.example;

import dbManager.DropTable;

public class DatamartController {

    private final MetadataExtractor metadataCall = new MetadataExtractor();
    private final WordsExtractor wordsCall = new WordsExtractor();
    public void run() {
        new DropTable();
        metadataCall.filter();
        wordsCall.filter();

    }
}
