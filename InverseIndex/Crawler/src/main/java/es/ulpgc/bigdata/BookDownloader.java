package es.ulpgc.bigdata;


import java.io.File;

public class BookDownloader {
    private File datalakeRoot;

    public BookDownloader(File datalakeRoot) {
        this.datalakeRoot = datalakeRoot;
    }

    public void startDownloading() {
        int id = 1;
        int millisecondsPerMinute = 60_000;

        while (id <= TimerAPI.getMaxBooks()) {
            long startTime = System.currentTimeMillis();
            int booksDownloadedThisMinute = 0;

            while (booksDownloadedThisMinute < TimerAPI.getBooksPerMinute()) {
                String bookText = ContentExtractor.textExtractor(id);

                if (bookText != null) {
                    FileSystemDatalake.saveText( id, bookText);
                    booksDownloadedThisMinute++;
                }
                id++;
            }

            long elapsedTime = System.currentTimeMillis() - startTime;
            if (elapsedTime < millisecondsPerMinute) {
                try {
                    Thread.sleep(millisecondsPerMinute - elapsedTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
