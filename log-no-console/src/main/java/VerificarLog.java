import java.io.IOException;
import java.sql.SQLOutput;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Scanner;

public class VerificarLog {
    /* public static void main(String[] args) {
        // Pegando o horário e a data de agora
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        // Variável para formatar a data
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        // Formatando a data
        String logAplicacaoIniciada = formatter.format(timestamp);

        Scanner leitor = new Scanner(System.in);
        System.out.println("""
                ******************************************
                * APLICAÇÃO INICIADA! - %S *
                ******************************************
                """.formatted(logAplicacaoIniciada));

        System.out.println("""
                Seja bem-vindo a Vortexx!
                Qual o seu nome?""");

        String nomeUsuario = leitor.nextLine();

        String logUsuarioLogado = formatter.format(timestamp); // ARMAZENANDO O HORÁRIO QUE O USUÁRIO LOGOU
        System.out.println("""
                
                **************************************************
                * USUÁRIO LOGOU NA APLICAÇÃO! - %S *
                **************************************************
                """.formatted(logAplicacaoIniciada));

        System.out.println("""
                Olá %s! Vamos começar a construir?""".formatted(nomeUsuario));

        System.out.println("""
                Onde você quer construir?""");
        String local = leitor.nextLine();

        System.out.println("""
                
                Em %s a média de custo do metro quadrado é R$ 7100. O PIB da região é 2.7 bilhões, a densidade demográfica é 7,5 mil e a quantidade populacional da região é 44,41 milhões.""".formatted(local));

        String logSimulacaoFeita = formatter.format(timestamp); // ARMAZENANDO O HORÁRIO QUE A SIMULAÇÃO FOI FEITA
        System.out.println("""
                
                **************************************************
                * SIMULAÇÃO FEITA! - %S *
                **************************************************
                """.formatted(logSimulacaoFeita));

        System.out.println("""
                ********************************************
                * APLICAÇÃO FINALIZADA! - %S *
                ********************************************
                """.formatted(logSimulacaoFeita));


    }*/

    public static void main(String[] args) throws IOException {
        LerArquivos arquivos = new LerArquivos();

        // O parametro "csvFile" procura o arquivo csv que vai converter
        // E o parametro "pathXls" indica a pasta e o nome do arquivo que ele vai criar
        //arquivos.converterCsvToXls("./src/main/java/book.csv","./src/main/java/book.xls");

        arquivos.lerXls("./src/main/java/book.xls");

    }
}

