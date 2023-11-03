package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dbManager.CreateTable;
import objects.Book;

import java.io.File;
import java.util.Objects;

public class MetadataExtractor {

    public void filter() {
        String datalakeDir = "C:/Users/Jaime/Desktop/Datalake/";
        File datalake = new File(datalakeDir);
        File[] folders = datalake.listFiles();

        try {
            if (folders != null) {

                for (File folder : folders) {

                    String folderName = folder.getName();
                    String newDatalakeDir = datalakeDir + folderName + "/";
                    File newDatalake = new File(newDatalakeDir);
                    File[] books = newDatalake.listFiles();
                    System.out.println(folderName);

                    if (books != null) {
                        for (File book : books) {
                            String fileName = book.getName();
                            System.out.println(fileName);

                            if (fileName.endsWith(".json")) {
                                int docId = Integer.parseInt(fileName.substring(0, fileName.length() - 5));
                                ObjectMapper objectMapper = new ObjectMapper();
                                JsonNode rootNode = objectMapper.readTree(new File(newDatalakeDir + fileName));

                                String date = null;
                                String author = null;
                                String text = null;
                                String title = null;
                                String lang = null;

                                if (rootNode.has("date")) {
                                    date = rootNode.get("date").asText();
                                }

                                if (rootNode.has("author")) {
                                    author = rootNode.get("author").asText();
                                }

                                if (rootNode.has("text")) {
                                    text = rootNode.get("text").asText();
                                }

                                if (rootNode.has("title")) {
                                    title = rootNode.get("title").asText();
                                }

                                if (rootNode.has("lang")) {
                                    lang = rootNode.get("lang").asText();
                                }


                                Book content = new Book(docId, date, author, text, title, lang);

                                new CreateTable().insertBookTable(Objects.requireNonNull(content));

                            }
                        }
                        System.out.println("File finished\n");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

    }


}
