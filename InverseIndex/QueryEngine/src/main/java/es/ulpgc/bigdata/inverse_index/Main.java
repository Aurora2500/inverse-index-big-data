package es.ulpgc.bigdata.inverse_index;

import es.ulpgc.bigdata.inverse_index.datamart.Datamart;
import es.ulpgc.bigdata.inverse_index.datamart.sql.SqlDatamart;

import java.nio.file.Path;
import java.sql.SQLException;

public class Main {
	public static void main(String[] args) throws SQLException {
		if (args.length < 1) {
			System.err.println("The query engine needs a path to the datamart");
			System.exit(1);
		}
		Datamart datamart = new SqlDatamart(Path.of(args[0]));
		SparkWebService webService = new SparkWebService(datamart);
		webService.startServer();
	}
}
