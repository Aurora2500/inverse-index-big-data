package es.ulpgc.bigdata.inverse_index.datamart.sql;

import es.ulpgc.bigdata.inverse_index.datamart.Datamart;

import java.nio.file.Path;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SqlDatamart implements Datamart {
	private final Connection conn;
	private ScheduledFuture<?> debounceCommitFuture = null;
	private final ScheduledExecutorService executorService = java.util.concurrent.Executors.newSingleThreadScheduledExecutor();
	private final static int DEBOUNCE_TIME_MILLIS = 2000;

	public SqlDatamart(Path path) throws SQLException {
		String url =  "jdbc:sqlite:" + path;
		this.conn = DriverManager.getConnection(url);
		assertTables();
		conn.setAutoCommit(false);
	}

	private void assertTables() throws SQLException {
		// create table
		Statement stmt = conn.createStatement();
		stmt.execute("CREATE TABLE IF NOT EXISTS words (" +
			"word TEXT, " +
			"book INTEGER" +
			")"
		);
	}

	@Override
	public Set<Integer> index(String token) throws Exception {
		Set<Integer> documents = new HashSet<>();
		PreparedStatement stmt = conn.prepareStatement("SELECT DISTINCT  book FROM words WHERE word = ?");
		stmt.setString(1, token);
		ResultSet results = stmt.executeQuery();
		while (results.next()) {
			documents.add(results.getInt(1));
		}
		return documents;
	}

	@Override
	public Set<Integer> documents() throws Exception {
		Set<Integer> documents = new HashSet<>();
		Statement stmt = conn.createStatement();
		ResultSet results = stmt.executeQuery("SELECT DISTINCT book FROM words");
		while(results.next()) {
			documents.add(results.getInt(1));
		}
		return documents;
	}

	@Override
	public void add(int document, Set<String> tokens) throws Exception {
		debounceCommit();
		PreparedStatement stmt = conn.prepareStatement("INSERT INTO words (word, book) VALUES (?, ?)");
		for (String token : tokens) {
			stmt.setString(1, token);
			stmt.setInt(2, document);
			stmt.executeUpdate();
		}
		stmt.close();
	}

	private void debounceCommit() {
		if (debounceCommitFuture != null) {
			debounceCommitFuture.cancel(false);
		}

		debounceCommitFuture = executorService.schedule(() -> {
			try {
				conn.commit();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}, DEBOUNCE_TIME_MILLIS, TimeUnit.MILLISECONDS);
	}
}
