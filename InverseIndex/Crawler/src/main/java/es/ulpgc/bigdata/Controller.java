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
                startBookDownloading();
            }
        };
        timer.scheduleAtFixedRate(task, 0, TimerAPI.getTimerPeriod());
    }

    private void startBookDownloading() {
        BookDownloader bookDownloader = new BookDownloader(datalakeRoot);
        bookDownloader.startDownloading();
    }
}
