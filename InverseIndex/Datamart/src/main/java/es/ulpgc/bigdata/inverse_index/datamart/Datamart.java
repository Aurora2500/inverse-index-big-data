package es.ulpgc.bigdata.inverse_index.datamart;

import java.util.Map;
import java.util.Set;

public interface Datamart {
	Set<Integer> index(String token) throws Exception;
	Set<Integer> documents() throws Exception;
	void add(int document, Set<String> tokens) throws Exception;
	Map<Integer, Metadata> getDocumentMetadata() throws Exception; // Nuevo método para obtener metadatos
	Set<String> getTokensForDocument(int documentId) throws Exception;

}
