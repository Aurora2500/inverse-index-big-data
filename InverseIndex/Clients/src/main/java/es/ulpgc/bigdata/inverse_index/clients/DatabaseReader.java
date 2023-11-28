package es.ulpgc.bigdata.inverse_index.clients;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DatabaseReader {

    public static Books retrieveDocumentById(int id) {
        Books books = null;
        String url = "jdbc:sqlite:metadata.db";

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT id, date, author, text, title, lang FROM books WHERE id = ?")) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int documentId = resultSet.getInt("id");
                String date = resultSet.getString("date");
                String author = resultSet.getString("author");
                String text = resultSet.getString("text");
                String title = resultSet.getString("title");
                String lang = resultSet.getString("lang");

                books = new Books(documentId, date, author, text, title, lang);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return books;
    }

}
