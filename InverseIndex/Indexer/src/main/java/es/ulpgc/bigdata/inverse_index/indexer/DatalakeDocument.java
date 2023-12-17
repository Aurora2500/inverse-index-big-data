package es.ulpgc.bigdata.inverse_index.indexer;

import es.ulpgc.bigdata.inverse_index.datamart.Document;

public record DatalakeDocument (int id, String date, String author, String title, String lang, String content) {

	public Document toDataMart() {
		return new Document(id, date, author, title, lang, content.length(), content.substring(0, Math.min(Document.MAX_SUMMARY_LENGTH, content.length())));
	}
}
