package es.ulpgc.bigdata;

import java.io.File;
import static spark.Spark.init;
import static spark.Spark.port;

public class Main {
    public static void main(String[] args) {
        port(4567);
        TimerAPI.setupTimerAPI();
        init();

        File datalakeRoot = new File(args[0]);
        Controller controller = new Controller(datalakeRoot);
        controller.start();

    }
}

