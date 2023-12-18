package es.ulpgc.bigdata;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import static spark.Spark.init;
import static spark.Spark.port;

public class Controller {
    private TimerTask task;
    private File datalakeRoot;

    public Controller(File datalakeRoot) {
        this.datalakeRoot = datalakeRoot;
    }

    public void start() {
        Timer timer = new Timer();
        task = new TimerTask() {
            public void run() {
                executeProgram();
            }
        };
        timer.scheduleAtFixedRate(task, 0, TimerAPI.getTimerPeriod());
    }

    private void executeProgram() {
        ContentExtractor contentExtractor = new ContentExtractor();
        FileSystemDatalake dataLake = new FileSystemDatalake(datalakeRoot);

        int id = 1;
        int millisecondsPerMinute = 60_000;

        while (id <= TimerAPI.getMaxBooks()) {
            long startTime = System.currentTimeMillis();
            int booksDownloadedThisMinute = 0;


            while (booksDownloadedThisMinute < TimerAPI.getBooksPerMinute()) {
                String bookText = ContentExtractor.textExtractor(id);

                if (bookText != null) {
                    FileSystemDatalake.saveText(id, bookText);
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
