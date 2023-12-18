package es.ulpgc.bigdata.inverse_index.datamart.hazelcast;

import com.hazelcast.config.Config;
import com.hazelcast.config.SerializationConfig;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.config.TcpIpConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.multimap.MultiMap;
import es.ulpgc.bigdata.inverse_index.datamart.Datamart;
import es.ulpgc.bigdata.inverse_index.datamart.Document;

import java.util.Set;
import java.util.stream.Collectors;

public class HazelcastDatamart implements Datamart {
	HazelcastInstance instance;

	public HazelcastDatamart() {
		SerializerConfig sc = new SerializerConfig();
		sc.setImplementation(new DocumentSerializer()).setTypeClass(Document.class);
		Config config = new Config();
		config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
		config.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(true);
		config.getNetworkConfig().getJoin().getTcpIpConfig().addMember("127.0.0.1");
		config.getSerializationConfig().addSerializerConfig(sc);
		instance = Hazelcast.newHazelcastInstance(config);
	}

	@Override
	public Set<Integer> index(String token) throws Exception {
		MultiMap<String, Document> index = instance.getMultiMap("index");
		return index.get(token).stream().map(Document::id).collect(Collectors.toSet());
	}

	@Override
	public Set<Document> indexDocuments(String token) throws Exception {
		MultiMap<String, Document> index = instance.getMultiMap("index");
		return index.get(token).stream().collect(Collectors.toSet());
	}

	@Override
	public Set<Integer> documents() throws Exception {
		MultiMap<String, Document> index = instance.getMultiMap("index");
		return index.values().stream().map(Document::id).collect(Collectors.toSet());
	}

	@Override
	public void add(Document document, Set<String> tokens) throws Exception {
		MultiMap<String, Document> index = instance.getMultiMap("index");
		for (String token : tokens) {
			index.put(token, document);
		}
	}
}
