package es.ulpgc.bigdata.inverse_index.datamart.sql;

import es.ulpgc.bigdata.inverse_index.datamart.Datamart;
import es.ulpgc.bigdata.inverse_index.datamart.Document;

import java.nio.file.Path;
import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
		stmt.execute("CREATE TABLE IF NOT EXISTS books (" +
				"id INTEGER, " +
				"date TEXT, " +
				"author TEXT, " +
				"title TEXT, " +
				"lang TEXT, " +
				"summary TEXT" +
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
	public Set<Document> indexDocuments(String token) throws Exception {
		Set<Document> documents = new HashSet<>();
		PreparedStatement stmt = conn.prepareStatement("SELECT b.id, b.date, b.author, b.title, b.lang, b.summary FROM words w JOIN books b ON b.id = w.book WHERE w.word = ?");
		stmt.setString(1, token);
		ResultSet results = stmt.executeQuery();
		while (results.next()) {
			documents.add(new Document(
					results.getInt(1),
					results.getString(2),
					results.getString(3),
					results.getString(4),
					results.getString(5),
					results.getString(6)));
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
	public void add(Document document, Set<String> tokens) throws Exception {
		debounceCommit();
		PreparedStatement word_stmt = conn.prepareStatement("insert into words (word, book) VALUES (?, ?)");
		for (String token : tokens) {
			word_stmt.setString(1, token);
			word_stmt.setInt(2, document.id());
			word_stmt.executeUpdate();
		}
		word_stmt.close();
		PreparedStatement doc_stmt = conn.prepareStatement("INSERT INTO books (id, date, author, title, lang, summary) VALUES (?, ?, ?, ?, ?, ?)");
		doc_stmt.setInt(1, document.id());
		doc_stmt.setString(2, document.date());
		doc_stmt.setString(3, document.author());
		doc_stmt.setString(4, document.title());
		doc_stmt.setString(5, document.lang());
		doc_stmt.setString(6, document.summary());
		doc_stmt.executeUpdate();
		doc_stmt.close();
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
