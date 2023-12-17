package es.ulpgc.bigdata.inverse_index.datamart;

public record Document(int id, String date, String author, String title, String lang, int length, String summary) {
	public static final int MAX_SUMMARY_LENGTH = 100;
}
