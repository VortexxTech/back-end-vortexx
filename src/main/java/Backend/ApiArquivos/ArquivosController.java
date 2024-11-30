package Backend.ApiArquivos;

import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/ApiArquivos")
public class ArquivosController {

    public Map<String, Boolean> verificarBairroNoArquivo(String bairroNome) {
        // Simulação de consulta ao banco de dados
        Map<String, Boolean> dadosDisponiveis = new HashMap<>();

        // Aqui você deve consultar o banco para verificar os dados disponíveis
        // Exemplo fictício:
        if (bairroNome.equals("Centro")) {
            dadosDisponiveis.put("valorM2", true);
            dadosDisponiveis.put("densidade", false);
            dadosDisponiveis.put("idh", true);
        } else {
            dadosDisponiveis.put("valorM2", true);
            dadosDisponiveis.put("densidade", true);
            dadosDisponiveis.put("idh", false);
        }

        return dadosDisponiveis;
    }

    @GetMapping("/verificarBairro/{bairroNome}")
    public Map<String, Boolean> verificarBairro(@PathVariable String bairroNome) {
        return verificarBairroNoArquivo(bairroNome);
    }
}