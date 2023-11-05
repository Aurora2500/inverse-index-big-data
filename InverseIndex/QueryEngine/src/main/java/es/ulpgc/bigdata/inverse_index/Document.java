package es.ulpgc.bigdata.inverse_index;

public class Document {
	int id;
		public String title;
		public String author;
		public String date;
		public String language;
		public String content;

		public Document(int id, String title, String author, String date, String language, String content) {
				this.id = id;
				this.title = title;
				this.author = author;
				this.date = date;
				this.language = language;
				this.content = content;
		}
}
