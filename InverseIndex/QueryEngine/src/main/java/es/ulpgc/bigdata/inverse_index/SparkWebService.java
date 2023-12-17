package es.ulpgc.bigdata.inverse_index;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import es.ulpgc.bigdata.inverse_index.datamart.Datamart;
import es.ulpgc.bigdata.inverse_index.datamart.Document;
import spark.Spark;

import java.util.List;
import java.util.Set;

public class SparkWebService {
    private final static int SUMMARY_LENGTH = 200;
    private final static int MAX_LIMIT = 30;
    private final Datamart datamart;
    Gson gson = new Gson();

    public SparkWebService(Datamart datamart) {
        this.datamart = datamart;
    }

    public void startServer() {
        Spark.port(8080);

        Spark.get("/v1/search", (request, response) -> {
            String word = request.queryParams("word");
            if (word == null) {
                response.status(400);
                return "Missing word parameter\n";
            }

            String offsetStr = request.queryParams("offset");
            String limitStr = request.queryParams("limit");
            int offset = offsetStr == null ? 0 : Integer.parseInt(offsetStr);
            int limit = limitStr == null ? 10 : Integer.parseInt(limitStr);
            limit = Math.min(limit, MAX_LIMIT);
            try {
                Set<Document> documents = datamart.indexDocuments(word);
                List<Document> sortedDocuments = documents.stream().sorted((a, b) -> b.id() - a.id()).toList();
                response.header("X-Backend-Server", "ServidorSpark");
                response.type("application/json");
                response.type("application/json");
                JsonArray responseJson = new JsonArray();
                for (int i = offset; i < Math.min(offset + limit, sortedDocuments.size()); i++) {
                    Document document = sortedDocuments.get(i);
                    JsonObject el = summaryJsonObject(document);
                    responseJson.add(el);
                }
                return gson.toJson(documents) + "\n";
            } catch (Exception e) {
                response.status(500);
                return "Internal server error\n";
            }
        });

        Spark.get("/v1/document/:id", (request, response) -> {
            String idStr = request.params(":id");
            if (idStr == null) {
                response.status(400);
                return "Missing id parameter\n";
            }
          response.header("X-Backend-Server", "ServidorSpark");
          int id = Integer.parseInt(request.params(":id"));
            response.redirect("https://www.gutenberg.org/cache/epub/" + id + "/pg" + id + ".txt");
            return "";
        });
    }

    private static JsonObject summaryJsonObject(Document doc) {
        JsonObject el = new JsonObject();
        el.addProperty("id", doc.id());
        if (doc.title() != null) {
	        el.addProperty("title", doc.title());
        }
        if (doc.author() != null) {
          el.addProperty("author", doc.author());
        }
        if (doc.date() != null) {
          el.addProperty("date", doc.date());
        }
        if (doc.lang() != null) {
          el.addProperty("language", doc.lang());
        }
        String summary = doc.summary();
        el.addProperty("summary", summary);
        el.addProperty("length", doc.length());
        return el;
    }
}