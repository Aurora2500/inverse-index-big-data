package es.ulpgc.bigdata.inverse_index.datamart.hazelcast;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import es.ulpgc.bigdata.inverse_index.datamart.Datamart;
import es.ulpgc.bigdata.inverse_index.datamart.Document;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class HazelcastDatamart implements Datamart {
	HazelcastInstance instance;

	public HazelcastDatamart() {
		Config config = new Config();
		instance = Hazelcast.newHazelcastInstance(config);
	}

	@Override
	public Set<Integer> index(String token) throws Exception {
		IMap<String, Set<Document>> index = instance.getMap("index");
		return index.get(token).stream().map(Document::id).collect(Collectors.toSet());
	}

	@Override
	public Set<Document> indexDocuments(String token) throws Exception {
		IMap<String, Set<Document>> index = instance.getMap("index");
		return index.get(token);
	}

	@Override
	public Set<Integer> documents() throws Exception {
		IMap<String, Set<Document>> index = instance.getMap("index");
		return index.values().stream().flatMap(Set::stream).map(Document::id).collect(Collectors.toSet());
	}

	@Override
	public void add(Document document, Set<String> tokens) throws Exception {
		IMap<String, Set<Document>> index = instance.getMap("index");
		for (String token : tokens) {
			Set<Document> documents = index.getOrDefault(token, new HashSet<>());
			documents.add(document);
			index.put(token, documents);
		}
	}
}
