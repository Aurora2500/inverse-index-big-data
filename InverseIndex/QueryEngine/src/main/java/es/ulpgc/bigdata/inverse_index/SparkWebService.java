package es.ulpgc.bigdata.inverse_index;




import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import es.ulpgc.bigdata.inverse_index.datamart.Datamart;
import spark.Spark;

import java.util.List;
import java.util.Set;

public class SparkWebService {
    private final static int SUMMARY_LENGTH = 200;
    private final static int MAX_LIMIT = 30;
    private final Datamart datamart;
    private final FileDatalake datalake;
    Gson gson = new Gson();

    public SparkWebService(FileDatalake datalake, Datamart datamart) {
        this.datalake = datalake;
        this.datamart = datamart;
    }

    public void startServer() {
        Spark.port(4567);

        Spark.get("/v1/search", (request, response) -> {
            String word = request.queryParams("word");
            if (word == null) {
                response.status(400);
                return "Missing word parameter\n";
            }
            // pagination
            String offsetStr = request.queryParams("offset");
            String limitStr = request.queryParams("limit");
            int offset = offsetStr == null ? 0 : Integer.parseInt(offsetStr);
            int limit = limitStr == null ? 10 : Integer.parseInt(limitStr);
            limit = Math.min(limit, MAX_LIMIT);
            try {
                Set<Integer> documentIds = datamart.index(word);
                // get a sorted list of document ids
                List<Integer> sortedIds = documentIds.stream().sorted().toList();
                response.type("application/json");
                JsonArray documents = new JsonArray();
                for (int i = offset; i < Math.min(offset + limit, sortedIds.size()); i++) {
                    int id = sortedIds.get(i);
                    Document document = datalake.getDocument(id);
                    JsonObject el = summaryJsonObject(id, document);
                    documents.add(el);
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
            try {
                int id = Integer.parseInt(idStr);
                Document document = datalake.getDocument(id);
                response.type("application/json");
                return gson.toJson(document);
            } catch (Exception e) {
                response.status(500);
                return "Internal server error\n";
            }
        });
    }

    private static JsonObject summaryJsonObject(int id, Document document) {
        JsonObject el = new JsonObject();
        el.addProperty("id", id);
        if (document.title != null) {
	        el.addProperty("title", document.title);
        }
        if (document.author != null) {
          el.addProperty("author", document.author);
        }
        if (document.date != null) {
          el.addProperty("date", document.date);
        }
        if (document.language != null) {
          el.addProperty("language", document.language);
        }
        String summary = document.content.length() > SUMMARY_LENGTH ?
            document.content.substring(0, SUMMARY_LENGTH) + "..." :
            document.content;
        el.addProperty("summary", summary);
        el.addProperty("length", document.content.length());
        return el;
    }
}