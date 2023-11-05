package es.ulpgc.bigdata.inverse_index;




import com.google.gson.Gson;
import spark.Spark;

import java.util.List;

public class SparkWebService {

    public void startServer() {
        Spark.port(4567);

        Spark.get("/v1/search", (request, response) -> {
            String id = request.queryParams("id");
            String word = request.queryParams("word");

            if (id != null) {
                // Procesa la búsqueda por ID
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
                        return "Document not found for the specified ID.";
                    }
                } catch (NumberFormatException e) {
                    response.status(400); // Bad Request
                    return "Invalid ID. It must be an integer.";
                }
            } else if (word != null) {
                // Procesa la búsqueda por palabra
                try {
                    SqliteReader sqliteReader = new SqliteReader();
                    List<String> books = sqliteReader.getBooksForWord(word);

                    if (!books.isEmpty()) {
                        List<Document> documents = sqliteReader.getMetadataForBooks(books);
                        Gson gson = new Gson();
                        response.type("application/json");
                        return gson.toJson(documents);
                    } else {
                        response.status(404); // Not Found
                        return "Documents not found for the specified word.";
                    }
                } catch (Exception e) {
                    response.status(400); // Bad Request
                    return "Error processing the request: " + e.getMessage();
                }
            } else {
                // Manejar caso en el que no se proporciona ni "id" ni "word"
                response.status(400); // Bad Request
                return "You must provide either 'id' or 'word' as a query parameter.";
            }
        });
    }
}