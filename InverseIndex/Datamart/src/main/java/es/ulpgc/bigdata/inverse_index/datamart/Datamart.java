package es.ulpgc.bigdata.inverse_index.datamart;

import java.util.Map;
import java.util.Set;

public interface Datamart {
	Set<Integer> index(String token) throws Exception;
	Set<Document> indexDocuments(String token) throws Exception;
	Set<Integer> documents() throws Exception;
	void add(Document document, Set<String> tokens) throws Exception;
}
