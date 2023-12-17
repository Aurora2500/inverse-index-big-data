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
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class FileDatalake {
	private final Path root;
	private WatchService ws = null;

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

	public DatalakeDocument readDocument(int document) throws IOException {
		Gson gson = new Gson();
		JsonObject el = gson.fromJson(Files.readString(root.resolve(document + ".json")), JsonObject.class);
		return new DatalakeDocument(
				document,
				el.get("date").getAsString(),
				el.get("author").getAsString(),
				el.get("title").getAsString(),
				el.get("lang").getAsString(),
				el.get("text").getAsString());
	}

	public void startListener() throws IOException {
		ws = root.getFileSystem().newWatchService();
		root.register(ws, java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY);
	}

	public void blockAndListen(Consumer<DatalakeDocument> documentConsumer) throws IOException, InterruptedException {
		for (;;) {
			WatchKey key = ws.take();
			for (var event : key.pollEvents()) {
				String name = event.context().toString();
				if (!Pattern.compile("\\d+\\.json").asMatchPredicate().test(name)) continue;
				int document = Integer.parseInt(name.split("\\.")[0]);
				documentConsumer.accept(readDocument(document));
			}
			boolean valid = key.reset();
			if (!valid) {
				break;
			}
		}
	}
}
