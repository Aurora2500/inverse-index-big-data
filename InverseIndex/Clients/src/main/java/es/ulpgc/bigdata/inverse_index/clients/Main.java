package es.ulpgc.bigdata.inverse_index.clients;

import spark.Spark;


public class Main {
    public static void main(String[] args) {
        Spark.port(8082);

        Spark.get("/books/:id", Controller.getBookByIdRoute());
    }
}
