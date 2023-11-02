package es.ulpgc.bigdata.inverse_index.datamart.file;

import es.ulpgc.bigdata.inverse_index.datamart.Datamart;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class FileDatamart implements Datamart {
	private final Path root;

	public FileDatamart(Path root) throws IOException {
		this.root = root;

		if (Files.exists(root)) {
			// The root already exists, assert that it's a directory and follows the format specified
			if (!Files.isDirectory(root)) {
				throw new IllegalArgumentException("The root must be a directory");
			}
		} else {
			// The root doesn't exist, create it
			Files.createDirectories(root);
		}
	}

	private Path assertWord (String word) throws Exception {
		//TODO: handle edge cases
		String beginning = word.substring(0, 2);
		Path neighbourDir = root.resolve(beginning);
		if (!Files.isDirectory(neighbourDir)) {
			if (Files.exists(neighbourDir)) {
				throw new Exception("Unexpected file in datamart " + neighbourDir);
			}
			Files.createDirectory(neighbourDir);
		}
		Path wordFile = neighbourDir.resolve(word);
		if (!Files.isRegularFile(wordFile)) {
			if (Files.exists(wordFile)) {
				throw new Exception("Unexpected non file in datamart " + wordFile);
			}
			Files.createFile(wordFile);
		}

		return wordFile;
	}

	private Set<Integer> readWordFile(Path wordFile) throws IOException {
		byte[] content = Files.readAllBytes(wordFile);
		Set<Integer> documents = new HashSet<>();
		for (int i = 0; i < content.length; i += 4) {
			documents.add(byteToInt(content, i));
		}
		return documents;
	}

	private int byteToInt(byte[] bytes, int offset) {
		return (bytes[offset] << 24) | (bytes[offset + 1] << 16) | (bytes[offset + 2] << 8) | bytes[offset + 3];
	}

	@Override
	public Set<Integer> index(String token) throws Exception {
		Path wordFile = assertWord(token);
		return readWordFile(wordFile);
	}

	@Override
	public Set<Integer> documents() throws IOException {
		Set<Integer> documents = new HashSet<>();
		Files.list(root).flatMap(groupDir -> {
			try {
				return Files.list(groupDir);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}).forEach(wordFile -> {
			try {
				documents.addAll(readWordFile(wordFile));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
		return documents;
	}

	@Override
	public void add(int document, Set<String> tokens) throws Exception {
		for (String token: tokens) {
			Path wordFile = assertWord(token);
			Set<Integer> documents = readWordFile(wordFile);
			documents.add(document);
			byte[] content = new byte[documents.size() * 4];
			int i = 0;
			for (int doc: documents) {
				content[i++] = (byte) (doc >> 24);
				content[i++] = (byte) (doc >> 16);
				content[i++] = (byte) (doc >> 8);
				content[i++] = (byte) (doc);
			}
			Files.write(wordFile, content);
		}
	}
}