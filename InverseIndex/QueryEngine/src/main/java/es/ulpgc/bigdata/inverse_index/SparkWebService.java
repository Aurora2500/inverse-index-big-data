package es.ulpgc.bigdata.inverse_index;




import com.google.gson.Gson;
import spark.Spark;

import java.util.List;

public class SparkWebService {

    public void startServer() {
        Spark.port(4567);

        Spark.get("/v1/search", (request, response) -> {
            String word = request.queryParams("word");

            if (word != null) {
                try {
                    SqliteReader sqliteReader = new SqliteReader();
                    List<String> books = sqliteReader.getBooksForWord(word);

                    if (!books.isEmpty()) {
                        Gson gson = new Gson();
                        response.type("application/json");
                        return gson.toJson(books);
                    } else {
                        response.status(404); // Not Found
                        return "Documents not found for the specified word.";
                    }
                } catch (Exception e) {
                    response.status(400); // Bad Request
                    return "Error processing the request: " + e.getMessage();
                }
            } else {
                response.status(400); // Bad Request
                return "You must provide either 'id' or 'word' as a query parameter.";
            }
        });
    }
}