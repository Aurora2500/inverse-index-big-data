package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ContentExtractor {

    public static String textExtractor(int id) {
        String url = "https://www.gutenberg.org/cache/epub/" + id + "/pg" + id + ".txt";

        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            //HTTP request
            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                    response.append("\n"); //add line breaks to maintain the text format
                }
                in.close();
                return response.toString();
            }
            else {
                System.out.println("An error occurred while retrieving the text from the book " + id + ", response code: " + responseCode);
                return null;
            }
        }
        catch (IOException e) {
            System.out.println("Error when trying to obtain the text from the book " + id);
            e.printStackTrace();
            return null;
        }
    }
}
