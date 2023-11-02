package es.ulpgc.bigdata.inverse_index;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchService;
import java.util.HashSet;
import java.util.Set;
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

	public WatchService watchService() throws IOException {
		WatchService ws = root.getFileSystem().newWatchService();
		root.register(ws, java.nio.file.StandardWatchEventKinds.ENTRY_CREATE, java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY);
		return ws;
	}
}
