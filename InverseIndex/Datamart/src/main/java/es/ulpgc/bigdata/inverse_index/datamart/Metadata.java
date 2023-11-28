package es.ulpgc.bigdata.inverse_index.datamart;

public class Metadata {
    public final int id;
    public final String date;
    public final String author;
    public final String title;
    public final String lang;
    public final String text;

    public Metadata(int id, String date, String author, String title, String lang, String text) {
        this.id = id;
        this.date = date;
        this.author = author;
        this.title = title;
        this.lang = lang;
        this.text = text;
    }
}
