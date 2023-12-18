package es.ulpgc.bigdata.inverse_index.indexer;

import es.ulpgc.bigdata.inverse_index.datamart.Datamart;

import java.io.IOException;
import java.util.Set;

public class Indexer {
	private final FileDatalake datalake;
	private final Datamart datamart;

	public Indexer(FileDatalake datalake, Datamart datamart) {
		this.datalake = datalake;
		this.datamart = datamart;
	}

	private Set<String> tokenizeDocument(DatalakeDocument document) throws IOException {
		return Tokenizer.tokenize(document.content());
	}

	public void index() throws Exception {

		// get already indexed documents
		Set<Integer> indexedDocuments = datamart.documents();
		Set<Integer> datalakeDocuments = datalake.documents();
		// Start the listener to listen for new documents that may have been added while we were indexing
		datalake.startListener();
		datalakeDocuments.removeAll(indexedDocuments);
		System.out.println("Indexing " + datalakeDocuments.size() + " documents");
		for (Integer documentID: datalakeDocuments) {
			DatalakeDocument document = datalake.readDocument(documentID);
			System.out.println("Indexing document " + document.id());
			Set<String> tokens = tokenizeDocument(document);
			System.out.println("Document " + document.id() + " has " + tokens.size() + " tokens");
			datamart.add(document.toDataMart(), tokens);
			System.out.println("Document " + document.id() + " indexed");
		}

		datalake.blockAndListen((document) -> {
			try {
				System.out.println("Indexing document " + document.id());
				Set<String> tokens = tokenizeDocument(document);
				datamart.add(document.toDataMart(), tokens);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}
