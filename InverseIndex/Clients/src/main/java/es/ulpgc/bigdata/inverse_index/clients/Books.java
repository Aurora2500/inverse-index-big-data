package es.ulpgc.bigdata.inverse_index.clients;

 public class Books {
    private int id;
    private String date;
    private String author;
    private String text;
    private String lang;
    private String title;

    public Books(int id, String date, String author, String text, String lang, String title) {
        this.id = id;
        this.date = date;
        this.author = author;
        this.text = text;
        this.lang = lang;
        this.title = title;
    }

}