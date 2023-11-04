package es.ulpgc.bigdata;

import static spark.Spark.*;

public class TimerAPI {
    private static long timerPeriod = 5 * 60_000; // 5 minutes in milliseconds by default
    private static int booksPerMinute = 100; // 100 books per minute by default
    private static int maxBooks = 1000; // 1000 max books by default

    public static void setupTimerAPI() {
        post("/v1/execution/configuration", (request, response) -> {
            System.out.println("\n");
            if (request.queryParams().contains("maxbooks")) {
                int max = Integer.parseInt(request.queryParams("maxbooks"));
                setMaxBooks(max);
                System.out.println("Max books successfully setted on: " + getMaxBooks());
            }
            if (request.queryParams().contains("booksperminute")) {
                int books = Integer.parseInt(request.queryParams("booksperminute"));
                setBooksPerMinute(books);
                System.out.println("Books per minute successfully settled on: " + getBooksPerMinute());

            }
            if (request.queryParams().contains("timerperiod")) {
                long minutes = Long.parseLong(request.queryParams("timerperiod"));
                setTimerPeriod(minutes);
                System.out.println("Timer period successfully settled on: " + getTimerPeriod()/60_000 + " minutes");

            }
            System.out.println("\n");
            response.status(200);
            response.body("Execution configuration set successfully. Max books: " + getMaxBooks() + ", Books per minute: " + getBooksPerMinute() + " and Timer period set on: " + getTimerPeriod() / 60_000 + " minutes.");
            return null;
        });

    }

    public static void setTimerPeriod(long minutes) {
        timerPeriod = minutes * 60_000;
    }

    public static void setBooksPerMinute(int books) {
        booksPerMinute = books;
    }

    public static void setMaxBooks(int max) {
        maxBooks = max;
    }

    public static long getTimerPeriod() {
        return timerPeriod;
    }

    public static int getBooksPerMinute() {
        return booksPerMinute;
    }

    public static int getMaxBooks() {
        return maxBooks;
    }
}
