package es.ulpgc.bigdata.inverse_index.datamart;

import java.util.Set;

public interface Datamart {
	Set<Integer> index(String token) throws Exception;
	Set<Integer> documents() throws Exception;
	void add(int document, Set<String> tokens) throws Exception;
}
