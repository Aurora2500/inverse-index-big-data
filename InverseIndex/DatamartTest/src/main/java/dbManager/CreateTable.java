package dbManager;

import objects.Book;

import java.sql.*;

public class CreateTable {

    public CreateTable() {
        try (Connection conn = connect()) {
            Statement statement = conn.createStatement();
            createMetaDataTable(statement);
            createWordsTable(statement);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void createMetaDataTable(Statement statement) throws SQLException {
        statement.execute("CREATE TABLE IF NOT EXISTS metadata (" +
                "id INTEGER PRIMARY KEY, " +
                "date TEXT, " +
                "author TEXT, " +
                "text TEXT," +
                "title TEXT," +
                "lang TEXT" +
                ")");
    }

    public void createWordsTable(Statement statement) throws SQLException {
        statement.execute("CREATE TABLE IF NOT EXISTS words (" +
                "word TEXT, " +
                "book TEXT" +
                ")");
    }


    public void insertWordsTable(String word, String book) {
        String sql = "INSERT INTO words(word,book) VALUES(?,?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, word);
            pstmt.setString(2, book);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public void insertBookTable(Book book) {
        String sql = "INSERT INTO metadata(id,date,author,text,title,lang) VALUES(?,?,?,?,?,?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, book.id());
            pstmt.setString(2, book.date());
            pstmt.setString(3, book.author());
            pstmt.setString(4, book.text());
            pstmt.setString(5, book.title());
            pstmt.setString(6, book.lang());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public Connection connect() {
        Connection conn;
        try {
            String url = "jdbc:sqlite:metadata.db/";
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return conn;
    }
}
