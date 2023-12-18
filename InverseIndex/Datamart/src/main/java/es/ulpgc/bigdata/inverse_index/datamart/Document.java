package es.ulpgc.bigdata.inverse_index.datamart;

public record Document(int id, String date, String author, String title, String lang, int length, String summary) {
	public static final int MAX_SUMMARY_LENGTH = 100;

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Document) {
			return ((Document) obj).id == id;
		}
		return false;
	}
}
