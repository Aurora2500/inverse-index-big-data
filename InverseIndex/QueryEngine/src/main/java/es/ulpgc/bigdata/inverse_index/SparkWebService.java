package es.ulpgc.bigdata.inverse_index;




import com.google.gson.Gson;
import spark.Spark;

import java.util.List;

public class SparkWebService {

    public void startServerById() {
        Spark.port(4567);

        Spark.get("/v1/search", (request, response) -> {
            String id = request.queryParams("id");

            try {
                int documentId = Integer.parseInt(id);
                Document document = SqliteReader.retrieveDocumentById(documentId);

                if (document != null) {
                    Gson gson = new Gson();
                    String json = gson.toJson(document);
                    response.type("application/json");
                    return json;
                } else {
                    response.status(404); // Not Found
                    return "Documento no encontrado para el ID especificado.";
                }
            } catch (NumberFormatException e) {
                response.status(400); // Bad Request
                return "ID no válido. Debe ser un número entero.";
            }
        });

    }

    public void startServerByDocument() {
        Spark.port(4567);

        Spark.get("/v1/search", (request, response) -> {
            String word = request.queryParams("word");

            try {
                SqliteReader sqliteReader = new SqliteReader();  // Crea una instancia de la clase SqliteReader
                List<String> books = sqliteReader.getBooksForWord(word);

                if (!books.isEmpty()) {
                    List<Document> documents = sqliteReader.getMetadataForBooks(books);
                    Gson gson = new Gson();
                    response.type("application/json");
                    return gson.toJson(documents);
                } else {
                    response.status(404); // Not Found
                    return "Documentos no encontrados para la palabra especificada.";
                }
            } catch (Exception e) {
                response.status(400); // Bad Request
                return "Error al procesar la solicitud: " + e.getMessage();
            }
        });
    }
}

