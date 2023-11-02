package es.ulpgc.bigdata.inverse_index.datamart.file;

import es.ulpgc.bigdata.inverse_index.datamart.Datamart;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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

	@Override
	public Set<Integer> index(String word) throws Exception {
		Path wordFile = assertWord(word);
		
	}
}
