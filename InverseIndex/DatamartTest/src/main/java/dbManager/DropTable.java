package dbManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DropTable {

    public DropTable() {
        try (Connection conn = connect()) {
            Statement statement = conn.createStatement();
            dropMetaDataTable(statement);
            dropWordsTable(statement);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void dropMetaDataTable(Statement statement) throws SQLException {
        statement.execute("DROP TABLE IF EXISTS metadata;");
        System.out.println("Table dropped");
    }
    public void dropWordsTable(Statement statement) throws SQLException {
        statement.execute("DROP TABLE IF EXISTS words;");
        System.out.println("Table dropped");
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
