package es.ulpgc.bigdata.inverse_index;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class SqliteReader {


    public static Document retrieveDocumentById(int id) {
        Document document = null;
        String url = "jdbc:sqlite:metadata.db";

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT id, date, author, text, title, lang FROM metadata WHERE id = ?")) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int documentId = resultSet.getInt("id");
                String date = resultSet.getString("date");
                String author = resultSet.getString("author");
                String text = resultSet.getString("text");
                String title = resultSet.getString("title");
                String lang = resultSet.getString("lang");

                document = new Document(documentId, date, author, text, title, lang);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return document;
    }

    public static Document retrieveDocumentInfo(int id) {
        Document document = null;
        String url = "jdbc:sqlite:metadata.db";

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT id, date, author, text, title, lang FROM metadata WHERE id = ?")) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int documentId = resultSet.getInt("id");
                String date = resultSet.getString("date");
                String author = resultSet.getString("author");
                String title = resultSet.getString("title");
                String lang = resultSet.getString("lang");

                String text = resultSet.getString("text");
                if (text != null) {
                    // Split the text into words
                    String[] words = text.split("\\s+");

                    // Take the first 200 words or less if the text has less than 200 words
                    StringBuilder first200Words = new StringBuilder();
                    for (int i = 0; i < Math.min(words.length, 200); i++) {
                        first200Words.append(words[i]).append(" ");
                    }
                    // Append "..." to the end if there are more words in the original text
                    if (words.length > 200) {
                        first200Words.append("...");
                    }
                    document = new Document(documentId, date, author, first200Words.toString(), title, lang);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return document;
    }



}

