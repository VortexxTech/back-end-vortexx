package Backend.Slack;

import org.json.JSONObject;

import java.io.IOException;

public class App {
    public static void enviarNotificacao(String mensagem) throws IOException, InterruptedException {
        JSONObject json = new JSONObject();

        json.put("text", mensagem);

        Slack.enviarMensagem(json);
    }
}
