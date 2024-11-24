package Backend.Slack;

import org.json.JSONObject;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Slack {
    private static HttpClient client = HttpClient.newHttpClient();
    private static final String url = "https://hooks.slack.com/services/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";

    public static void enviarMensagem(JSONObject content) throws InterruptedException, IOException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .header("accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(content.toString()))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(String.format("Status Code: %d", response.statusCode()));
        System.out.println(String.format("Body: %s", response.body()));
    }
}
