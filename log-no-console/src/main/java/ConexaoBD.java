import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

class ConexaoBD {

    private static final String URL = "jdbc:mysql://localhost:3306/banco_que_sera_utilizado"; // Modifique para o seu banco
    private static final String USUARIO = "root";  // Usuário do banco
    private static final String SENHA = "senha_que_sera_utilizada";  // Senha do banco

    public static Connection conectar() throws SQLException {
        Connection conexao = null;

        try {
            // Conectar ao banco de dados
            conexao = DriverManager.getConnection(URL, USUARIO, SENHA);
            System.out.println("Conexão realizada com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro ao conectar: " + e.getMessage());
            throw e;
        }

        return conexao;
    }

    public static void main(String[] args) {
        try {
            conectar();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
