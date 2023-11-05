package es.ulpgc.bigdata.inverse_index;

import es.ulpgc.bigdata.inverse_index.datamart.Datamart;
import es.ulpgc.bigdata.inverse_index.datamart.file.FileDatamart;
import es.ulpgc.bigdata.inverse_index.datamart.sql.SqlDatamart;

import java.nio.file.Path;
import java.sql.SQLException;

public class Main {
	public static void main(String[] args) throws SQLException {
		if (args.length < 2) {
			System.err.println("The query engine needs a path to the datalake and datamart");
			System.exit(1);
		}
		FileDatalake datalake = new FileDatalake(Path.of(args[0]));
		Datamart datamart = new SqlDatamart(Path.of(args[1]));
		SparkWebService webService = new SparkWebService(datalake, datamart);
		webService.startServer();
	}
}
