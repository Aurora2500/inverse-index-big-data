package es.ulpgc.bigdata.inverse_index.clients;

import com.google.gson.Gson;
import es.ulpgc.bigdata.inverse_index.datamart.Metadata;
import spark.Route;

import es.ulpgc.bigdata.inverse_index.datamart.Datamart;
import es.ulpgc.bigdata.inverse_index.datamart.file.FileDatamart;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

public class Controller {

    private static final Datamart datamart;

    static {
        // Puedes ajustar la ruta del directorio según tus necesidades
        String datamartDirectory = "C:\\Users\\Usuario\\Documents\\GitHub\\inverse-index-big-data\\InverseIndex\\Datamart";
        try {
            datamart = new FileDatamart(Path.of(datamartDirectory));
        } catch (IOException e) {
            throw new RuntimeException("Error al inicializar FileDatamart", e);
        }
    }

    public static Route getBookByIdRoute() {
        return (req, res) -> {
            int bookId = Integer.parseInt(req.params(":id"));
            Set<String> tokens = getTokensForBook(bookId);
            Map<Integer, Metadata> metadataMap = getDocumentMetadata();

            if (metadataMap.containsKey(bookId)) {
                Metadata metadata = metadataMap.get(bookId);

                // Crear un mapa con la información que deseas devolver como JSON
                Map<String, Object> responseMap = Map.of(
                        "id", metadata.id,
                        "date", metadata.date,
                        "author", metadata.author,
                        "text", metadata.text,
                        "lang", metadata.lang,
                        "title", metadata.title
                );

                String json = convertirAJson(responseMap);

                res.type("application/json");
                return json;
            } else {
                res.status(404);
                return "Libro no encontrado";
            }
        };
    }

    private static Set<String> getTokensForBook(int bookId) {
        try {
            // Utiliza el nuevo método que obtiene los tokens para un documento específico
            return datamart.getTokensForDocument(bookId);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener tokens para el libro con ID " + bookId, e);
        }
    }

    private static Map<Integer, Metadata> getDocumentMetadata() {
        try {
            // Utiliza el nuevo método que obtiene los metadatos de todos los documentos
            return datamart.getDocumentMetadata();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener metadatos de documentos", e);
        }
    }

    private static String convertirAJson(Object object) {
        Gson gson = new Gson();
        return gson.toJson(object);
    }
}
