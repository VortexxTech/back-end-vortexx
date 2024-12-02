package Backend.ApiArquivos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.HashMap;

@Service
public class ArquivosService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Map<String, Boolean> verificarBairroNoArquivo(String bairroNome) {
        // A consulta SQL que verifica se os dados para o bairro estão preenchidos
        String sql = "SELECT valorM2, densidade, idh FROM DadosInseridos WHERE bairro = ?";

        // Executa a consulta no banco de dados
        Map<String, Object> resultado = jdbcTemplate.queryForMap(sql, bairroNome);

        // Cria um mapa para armazenar a disponibilidade dos dados
        Map<String, Boolean> dadosDisponiveis = new HashMap<>();

        // Verifica se os dados estão preenchidos (não são nulos)
        dadosDisponiveis.put("valorM2", resultado.get("valorM2") != null);
        dadosDisponiveis.put("densidade", resultado.get("densidade") != null);
        dadosDisponiveis.put("idh", resultado.get("idh") != null);

        return dadosDisponiveis;
    }
}