package Model;

import org.json.JSONObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class APIRequest {

    public JSONObject post(String url) throws IOException {

        URL address = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) address.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        StringBuilder json = new StringBuilder("{\"result\":");
        Scanner scan = new Scanner(address.openStream());
        while (scan.hasNext())
            json.append(scan.nextLine());
        scan.close();
        json.append("}");
        return new JSONObject(json.toString());
    }
}