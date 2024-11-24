package Backend.Slack;

import org.json.JSONObject;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws InterruptedException, IOException {
        JSONObject json = new JSONObject();

        json.put("text", "Olá mundo!");

        Slack.enviarMensagem(json);
    }
}
