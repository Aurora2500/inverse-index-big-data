package es.ulpgc.bigdata.inverse_index;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SqliteReader {

    public List<String> getBooksForWord(String word) {
        List<String> books = new ArrayList<>();

        String url = "jdbc:sqlite:datamart.db";

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT book FROM words WHERE word = ?")) {
            preparedStatement.setString(1, word);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String book = resultSet.getString("book");
                books.add(book);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return books;
    }
}
