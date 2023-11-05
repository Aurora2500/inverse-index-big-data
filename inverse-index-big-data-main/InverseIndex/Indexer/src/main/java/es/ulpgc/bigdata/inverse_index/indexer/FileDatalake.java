package es.ulpgc.bigdata.inverse_index.indexer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class FileDatalake {
	private final Path root;

	public FileDatalake(Path root) {
		this.root = root;
	}


	public Set<Integer> documents() throws IOException {
		Set<Integer> documents = new HashSet<>();
		Predicate<String> isDocument = Pattern.compile("\\d+\\.json").asMatchPredicate();
		for (Path document: Files.list(root).toList()) {
			if (!Files.isRegularFile(document)) continue;
			String name = document.getFileName().toString();
			if (!isDocument.test(name)) continue;
			documents.add(Integer.parseInt(name.split("\\.")[0]));
		}
		return documents;
	}

	public String readDocument(int document) throws IOException {
		Gson gson = new Gson();
		JsonObject el = gson.fromJson(Files.readString(root.resolve(document + ".json")), JsonObject.class);
		return el.get("text").getAsString();
	}

	public void blockAndListen(BiConsumer<Integer, String> documentConsumer) throws IOException, InterruptedException {
		WatchService ws = root.getFileSystem().newWatchService();
		root.register(ws, java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY);
		for (;;) {
			WatchKey key = ws.take();
			for (var event : key.pollEvents()) {
				String name = event.context().toString();
				if (!Pattern.compile("\\d+\\.json").asMatchPredicate().test(name)) continue;
				int document = Integer.parseInt(name.split("\\.")[0]);
				documentConsumer.accept(document, readDocument(document));
			}
			boolean valid = key.reset();
			if (!valid) {
				break;
			}
		}
	}
}
