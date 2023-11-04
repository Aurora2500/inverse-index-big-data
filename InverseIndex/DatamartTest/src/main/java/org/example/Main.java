package org.example;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        File datalake = new File(args[0]);
        new DatamartController().run(datalake);

    }

}
