package es.ulpgc.bigdata.inverse_index.datamart.hazelcast;

import com.hazelcast.config.*;
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
		Config cfg = new Config();
		NetworkConfig network = cfg.getNetworkConfig();
		JoinConfig join = network.getJoin();
		join.getMulticastConfig().setEnabled(false);
		//list of ip members joining setup
		join.getTcpIpConfig().setEnabled(true).addMember("192.168.217.188").addMember("192.168.217.168");
		//list of ip members interface setup
		network.getInterfaces().setEnabled(true).addInterface("192.168.217.168").addInterface("192.168.217.188");
		instance = Hazelcast.newHazelcastInstance(cfg);
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
