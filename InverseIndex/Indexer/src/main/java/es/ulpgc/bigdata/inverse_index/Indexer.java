package es.ulpgc.bigdata.inverse_index;

import es.ulpgc.bigdata.inverse_index.datamart.Datamart;

import java.nio.file.WatchService;
import java.util.Set;

public class Indexer {
	private final FileDatalake datalake;
	private final Datamart datamart;

	public Indexer(FileDatalake datalake, Datamart datamart) {
		this.datalake = datalake;
		this.datamart = datamart;
	}

	private Set<String> tokenize(String document) {
		return null;
	}

	private Set<String> tokenizeDocument(int document) {
		return null;
	}

	public void index() throws Exception {
		// get already indexed documents
		Set<Integer> indexedDocuments = datamart.documents();
		Set<Integer> datalakeDocuments = datalake.documents();
		datalakeDocuments.removeAll(indexedDocuments);
		for (Integer document: datalakeDocuments) {
			Set<String> tokens = tokenizeDocument(document);
			datamart.add(document, tokens);
		}

		WatchService watchService = datalake.watchService();

	}
}
