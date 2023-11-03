package es.ulpgc.bigdata;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        // this is the datalake root file taken in arguments
        File datalakeRoot = new File(args[0]);
        new ContentExtractor();
        new FileSystemDatalake(datalakeRoot);

        // below here it must be the timer with the execution rules

        int id = 1; // first book id

        int booksPerMinute = 10; // Number of books to download per minute
        long millisecondsPerMinute = 60_000; // Number of milliseconds in a minute

        while (id <= 1000) { // how much we want
            long startTime = System.currentTimeMillis(); // Record the start time of the minute
            int booksDownloadedThisMinute = 0;

            while (booksDownloadedThisMinute < booksPerMinute) {
                // below here we call the textExtractor function that will make the HTTP request and obtain the text of the book
                String bookText = ContentExtractor.textExtractor(id);

                // if the text is valid, we save it in the datalake root calling the function saveText
                if (bookText != null) {
                    FileSystemDatalake.saveText(id, bookText);
                    booksDownloadedThisMinute++;
                }

                id++; // increment the id to get the next book in the following iteration
            }

            long elapsedTime = System.currentTimeMillis() - startTime;
            if (elapsedTime < millisecondsPerMinute) {
                try {
                    Thread.sleep(millisecondsPerMinute - elapsedTime); // Sleep for the remaining time in the minute
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
