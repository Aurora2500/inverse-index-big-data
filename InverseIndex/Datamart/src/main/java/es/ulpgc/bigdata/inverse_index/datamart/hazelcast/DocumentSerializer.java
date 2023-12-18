package es.ulpgc.bigdata.inverse_index.datamart.hazelcast;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;
import es.ulpgc.bigdata.inverse_index.datamart.Document;

import java.io.IOException;

public class DocumentSerializer implements StreamSerializer<Document> {
	@Override
	public void write(ObjectDataOutput objectDataOutput, Document document) throws IOException {
		objectDataOutput.writeInt(document.id());
		objectDataOutput.writeUTF(document.date());
		objectDataOutput.writeUTF(document.author());
		objectDataOutput.writeUTF(document.title());
		objectDataOutput.writeUTF(document.lang());
		objectDataOutput.writeInt(document.length());
		objectDataOutput.writeUTF(document.summary());
	}

	@Override
	public Document read(ObjectDataInput objectDataInput) throws IOException {
		int id = objectDataInput.readInt();
		String date = objectDataInput.readUTF();
		String author = objectDataInput.readUTF();
		String title = objectDataInput.readUTF();
		String lang = objectDataInput.readUTF();
		int length = objectDataInput.readInt();
		String summary = objectDataInput.readUTF();
		return new Document(id, date, author, title, lang, length, summary);
	}

	@Override
	public int getTypeId() {
		return 700012;
	}
}
