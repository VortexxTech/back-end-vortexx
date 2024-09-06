import java.sql.SQLOutput;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public class VerificarLog {
    public static void main(String[] args) {
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
                
                Em %s a média de custo do metro quadrado é R$ 7100.""".formatted(local));

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


    }
    }

