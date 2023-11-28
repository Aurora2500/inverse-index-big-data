package es.ulpgc.bigdata.inverse_index.datamart.file;

import es.ulpgc.bigdata.inverse_index.datamart.Datamart;
import es.ulpgc.bigdata.inverse_index.datamart.Metadata;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FileDatamart implements Datamart {
	private final Path root;

	private static final int SHORT_LENGTH = 3;
	private static final String SHORT_DIR = "short";

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
		Path neighbourDir;
		if (word.length() <= SHORT_LENGTH) {
			neighbourDir = root.resolve(SHORT_DIR);
		} else {
			String beginning = word.substring(0, 2);
			neighbourDir = root.resolve(beginning);
		}
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
	@Override
	public Map<Integer, Metadata> getDocumentMetadata() throws IOException {
		Map<Integer, Metadata> metadataMap = new HashMap<>();

		Files.list(root).flatMap(groupDir -> {
			try {
				return Files.list(groupDir);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}).forEach(wordFile -> {
			try {
				Set<Integer> documents = readWordFile(wordFile);

				for (int documentId : documents) {
					Metadata metadata = readMetadataFromFile(documentId, wordFile);
					metadataMap.put(documentId, metadata);
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});

		return metadataMap;
	}

	private Metadata readMetadataFromFile(int documentId, Path wordFile) throws IOException {
		Path metadataFilePath = wordFile.getParent().resolve("metadata").resolve(documentId + ".txt");
		List<String> lines = Files.readAllLines(metadataFilePath);


		String date = lines.get(0).split(":")[1].trim();
		String author = lines.get(1).split(":")[1].trim();
		String title = lines.get(2).split(":")[1].trim();
		String lang = lines.get(3).split(":")[1].trim();
		String text = lines.get(3).split(":")[1].trim();

		return new Metadata(documentId, date, author, title, lang, text);
	}
	// En FileDatamart
	@Override
	public Set<String> getTokensForDocument(int documentId) throws Exception {
		Set<String> tokens = new HashSet<>();
		Files.list(root).flatMap(groupDir -> {
			try {
				return Files.list(groupDir);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}).forEach(wordFile -> {
			try {
				Set<Integer> documents = readWordFile(wordFile);

				if (documents.contains(documentId)) {
					// Agregar el token asociado a este documento
					tokens.add(wordFile.getFileName().toString());
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
		return tokens;
	}

}
