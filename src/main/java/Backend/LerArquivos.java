package Backend;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.io.*;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import Backend.DBConnectionProvider;
import S3.S3Provider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

public class LerArquivos {
    private static final Logger log = LogManager.getLogger(LerArquivos.class);

    public void lerArquivoS3(String bucketName, String archiveName) {

        S3Client s3Client = new S3Provider().getS3Client();

        // Faz a requisição para obter o objeto
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(archiveName)
                .build();

        // Lê o conteúdo do arquivo
        try {
            ResponseInputStream<?> response = s3Client.getObject(getObjectRequest);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Encerra o cliente S3
            s3Client.close();
        }

    }

    public void converterCsvToXls(String csvFile, String pathXls){

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile));
             Workbook workbook = new HSSFWorkbook();
             FileOutputStream out = new FileOutputStream(pathXls)) {
            Sheet sheet = workbook.createSheet("Dados CSV");
            String line;
            int rowNum = 0;

            while ((line = br.readLine()) != null) {
                String[] values = line.split(";"); // Se o separador for diferente, ajuste aqui
                Row row = sheet.createRow(rowNum++);
                for (int i = 0; i < values.length; i++) {
                    row.createCell(i).setCellValue(values[i]);
                }
            }

            workbook.write(out);
            fazerLog("Conversão Concluída!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void lerXls(String caminhoArquivo){
        try (FileInputStream fis = new FileInputStream(caminhoArquivo);
             HSSFWorkbook workbook = new HSSFWorkbook(fis)) {
            fazerLog("O arquivo Excel foi aberto.");

//             criarTabela();

            // Obtém a primeira planilha
            HSSFSheet sheet = workbook.getSheetAt(0);

            fazerLog("Acessou a planilha");

            List linha = new ArrayList<>();

//            DecimalFormat deci = new DecimalFormat("#,00");

            // Itera sobre as linhas
            for (Row CurrentRow : sheet) {
                    //linha.add(String.valueOf(CurrentRow.getRowNum()));

                    CurrentRow.forEach(cell -> {
                        if(cell != null){
                            //fazerLog("log da celula: " + cell);
                            var cellText = cell.toString();

                            char charac = '.';
                            char charac2 = '0';

                            Integer dot = cellText.indexOf(charac);
                            Integer zero = cellText.indexOf(charac2);

                            if(dot != -1 || zero != -1) {
                                linha.add(cellText);
                            }

                        }
                    });

                    if(!linha.isEmpty()){
                        System.out.println(linha);
                        Double custoM2 = Double.parseDouble(linha.getFirst().toString());
                        Double variacaoCustoMedio = Double.parseDouble(linha.get(1).toString());
                        Double variacaoAnual = Double.parseDouble(linha.get(3).toString());
                        Double variacaoMensal = Double.parseDouble(linha.get(4).toString());

                        inserirDados(custoM2, variacaoCustoMedio, variacaoAnual, variacaoMensal);
                        linha.clear();
                    }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void criarTabela() {
        LerArquivos lerArquivos = new LerArquivos();

        DBConnectionProvider dbConnectionProvider = new DBConnectionProvider();
        lerArquivos.fazerLog("Os dados do banco foram recebidos e o banco foi configurado");

        JdbcTemplate connection = dbConnectionProvider.getConnection();
        lerArquivos.fazerLog("A conexão com banco foi estabelecida");

        connection.execute("DROP TABLE IF EXISTS filme");

        connection.execute("""
                CREATE TABLE filme (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    nome VARCHAR(255) NOT NULL,
                    ano INT NOT NULL,
                    genero VARCHAR(255) NOT NULL,
                    diretor VARCHAR(255) NOT NULL
                )
                """);
    }

    public void inserirDados(Double custoM2, Double variacaoMedio, Double variacaoAnual, Double variacaoMensal) {
        DBConnectionProvider dbConnectionProvider = new DBConnectionProvider();
        JdbcTemplate connection = dbConnectionProvider.getConnection();

        String sql = "INSERT INTO Custo (custo_por_m2, variacao_custo_medio, variacao_anual, variacao_mensal) VALUES (?, ?, ?, ?)";

        connection.update(sql, custoM2, variacaoMedio, variacaoAnual, variacaoMensal);
    }

    public void fazerLog(String situacao) {
        // Pegando o horário e a data de agora
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        // Variável para formatar a data
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        // Formatando a data
        String log = formatter.format(timestamp);

        System.out.println(log + " - %s".formatted(situacao));
    }

}
