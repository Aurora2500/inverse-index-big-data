package es.ulpgc.bigdata.inverse_index.datamart;

import java.util.Set;

public interface Datamart {
	Set<Integer> index(String word) throws Exception;
}
