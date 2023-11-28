package es.ulpgc.bigdata.inverse_index.clients;

import com.google.gson.Gson;
import spark.Route;

public class Controller {

    public static Route getBookByIdRoute() {
        return (req, res) -> {
            int bookId = Integer.parseInt(req.params(":id")); // Obtener el ID del libro desde la solicitud
            Books book = DatabaseReader.retrieveDocumentById(bookId); // Utilizar retrieveDocumentById para obtener el libro

            if (book != null) {
                String json = convertirAJson(book);

                res.type("application/json");
                return json;
            } else {
                res.status(404); // Si el libro no se encuentra, devolver un código de estado 404
                return "Libro no encontrado";
            }
        };
    }

    private static String convertirAJson(Books libro) {
        Gson gson = new Gson();
        return gson.toJson(libro);
    }
}
