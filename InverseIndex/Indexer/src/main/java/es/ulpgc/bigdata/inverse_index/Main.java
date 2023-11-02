package es.ulpgc.bigdata.inverse_index;

import es.ulpgc.bigdata.inverse_index.datamart.Datamart;
import es.ulpgc.bigdata.inverse_index.datamart.file.FileDatamart;

import java.io.IOException;
import java.nio.file.Path;

public class Main {
	public static void main(String[] args) throws IOException {
		FileDatalake datalake = new FileDatalake(Path.of(args[1]));
		Datamart datamart = new FileDatamart(Path.of(args[2]));
		Indexer indexer = new Indexer(datalake, datamart);
		indexer.index();
	}
}