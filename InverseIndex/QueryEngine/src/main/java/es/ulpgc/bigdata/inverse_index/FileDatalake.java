package es.ulpgc.bigdata.inverse_index;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileDatalake {
	Path root;
	private static final Gson gson = new Gson();

	public FileDatalake(Path root) {
		this.root = root;
	}

	public Document getDocument(int id) throws IOException {
		Path documentPath = root.resolve(id + ".json");
		String documentJson = Files.readString(documentPath);
		JsonObject documentObj = gson.fromJson(documentJson, JsonObject.class);
		return new Document(
			id,
			documentObj.get("title") == null ? null : documentObj.get("title").getAsString(),
			documentObj.get("author") == null ? null : documentObj.get("author").getAsString(),
			documentObj.get("date") == null ? null : documentObj.get("date").getAsString(),
			documentObj.get("lang") == null ? null : documentObj.get("lang").getAsString(),
			documentObj.get("text") == null ? null : documentObj.get("text").getAsString()
		);
	}
}
