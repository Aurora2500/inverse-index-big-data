package es.ulpgc.bigdata.inverse_index;

import spark.Spark;

public class Main {
	public static void main(String[] args) {
		System.out.println("Hello world!");
		Spark.port(8080);
		Spark.get("/", (request, response) -> {

			return "woah";
		});
	}
}