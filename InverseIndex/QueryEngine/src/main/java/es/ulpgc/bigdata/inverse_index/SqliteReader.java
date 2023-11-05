package es.ulpgc.bigdata.inverse_index;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


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

    public List<String> getBooksForWord(String word) {
        List<String> books = new ArrayList<>();
        String url = "jdbc:sqlite:metadata.db";

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT word, book FROM words WHERE word = ?")) {
            preparedStatement.setString(1, word);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String booksStr = resultSet.getString("book");
                books = Arrays.asList(booksStr.split(", "));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return books;
    }

    public List<Document> getMetadataForBooks(List<String> books) {
        ArrayList<Document> documents = new ArrayList<>();
        String url = "jdbc:sqlite:metadata.db";

        try (Connection connection = DriverManager.getConnection(url)) {
            for (String book : books) {
                try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT id, title, text, lang, date, author FROM metadata WHERE title = ?")) {
                    preparedStatement.setString(1, book);
                    ResultSet resultSet = preparedStatement.executeQuery();

                    if (resultSet.next()) {
                        int documentId = resultSet.getInt("id");
                        String date = resultSet.getString("date");
                        String author = resultSet.getString("author");
                        String text = resultSet.getString("text");
                        String title = resultSet.getString("title");
                        String lang = resultSet.getString("lang");

                        StringBuilder first200Words = null;
                        if (text != null) {
                            // Split the text into words
                            String[] words = text.split("\\s+");

                            // Take the first 200 words or less if the text has less than 200 words
                            first200Words = new StringBuilder();
                            for (int i = 0; i < Math.min(words.length, 200); i++) {
                                first200Words.append(words[i]).append(" ");
                            }
                            // Append "..." to the end if there are more words in the original text
                            if (words.length > 200) {
                                first200Words.append("...");
                            }
                        }
                        Document document = new Document(documentId, date, author, first200Words.toString(), title, lang);
                        documents.add(document);

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return documents;
    }

}




