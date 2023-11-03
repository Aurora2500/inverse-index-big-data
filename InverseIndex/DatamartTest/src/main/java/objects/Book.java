package objects;

public record Book(Integer id, String date, String author, String text, String title, String lang){

    @Override
    public Integer id() {
        return id;
    }
    @Override
    public String date() {
        return date;
    }

    @Override
    public String author() {
        return author;
    }

    @Override
    public String text() {
        return text;
    }

    @Override
    public String title() {
        return title;
    }

    @Override
    public String lang() {
        return lang;
    }
}