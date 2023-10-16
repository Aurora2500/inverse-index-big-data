package es.ulpgc.bigdata;

public class Document {
    private final String title;
    private final String author;
    private final String date;
    private final String lang;
    private final String text;

    public Document(String title, String author, String date, String lang, String text) {
        this.title = title;
        this.author = author;
        this.date = date;
        this.lang = lang;
        this.text = text;
    }
}
