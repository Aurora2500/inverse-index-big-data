package es.ulpgc.bigdata.inverse_index;

public class Document {
    private int id;
    private String date;
    private String author;
    private String text;
    private String title;
    private String lang;

    public Document(int id, String date, String author, String text, String title, String lang) {
        this.id = id;
        this.date = date;
        this.author = author;
        this.text = text;
        this.title = title;
        this.lang = lang;
    }

}

