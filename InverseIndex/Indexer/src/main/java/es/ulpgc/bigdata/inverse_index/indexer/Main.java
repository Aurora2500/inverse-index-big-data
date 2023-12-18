package es.ulpgc.bigdata.inverse_index.indexer;

import es.ulpgc.bigdata.inverse_index.datamart.Datamart;
import es.ulpgc.bigdata.inverse_index.datamart.file.FileDatamart;
import es.ulpgc.bigdata.inverse_index.datamart.hazelcast.HazelcastDatamart;
import es.ulpgc.bigdata.inverse_index.datamart.sql.SqlDatamart;

import java.nio.file.Path;

public class Main {
	public static void main(String[] args) throws Exception {
		//assert that the arguments are correct
		if (args.length < 2) {
			System.out.println("Usage: java -jar indexer.jar <datalake> <datamart>");

			System.exit(1);
		}
		FileDatalake datalake = new FileDatalake(Path.of(args[0]));
		Datamart datamart = new HazelcastDatamart();
		Indexer indexer = new Indexer(datalake, datamart);
		indexer.index();
	}
}