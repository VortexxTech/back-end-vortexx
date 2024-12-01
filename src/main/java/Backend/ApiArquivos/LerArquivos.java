package Backend.ApiArquivos;

import java.io.FileOutputStream;
import java.io.*;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import Backend.DBConnectionProvider;
import S3.S3Provider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.S3Client;

public class LerArquivos {
    private static final Logger log = LogManager.getLogger(LerArquivos.class);
    private static final S3Client s3Client = new S3Provider().getS3Client();

    public void converterCsvToXls(String csvFile, String pathXls) {

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile));
             Workbook workbook = new HSSFWorkbook();
             FileOutputStream out = new FileOutputStream(pathXls)) {
            Sheet sheet = workbook.createSheet("Dados CSV");
            String line;
            int rowNum = 0;

            while ((line = br.readLine()) != null) {
                String[] values = line.split(";");
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

    public void lerPreco(String caminhoArquivo){
        try (FileInputStream fis = new FileInputStream(caminhoArquivo);
             HSSFWorkbook workbook = new HSSFWorkbook(fis)) {
            fazerLog("O arquivo Excel foi aberto.");

            // Obtém a primeira planilha
            HSSFSheet sheet = workbook.getSheetAt(0);

            fazerLog("Acessou a planilha");

            List linha = new ArrayList<>();

           DecimalFormat deci = new DecimalFormat("#,00");

            // Itera sobre as linhas
            for (Row CurrentRow : sheet) {
                // Obtém o valor do bairro (aqui estou assumindo que o bairro está na primeira coluna)
                String bairro = CurrentRow.getCell(0) != null ? CurrentRow.getCell(0).getStringCellValue() : "";

                linha.add(String.valueOf(CurrentRow.getRowNum()));

                CurrentRow.forEach(cell -> {
                    if(cell != null){
                        fazerLog("log da celula: " + cell);
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
                    Double custoM2 = Double.parseDouble(linha.get(0).toString());
                    Double variacaoCustoMedio = Double.parseDouble(linha.get(1).toString());
                    Double variacaoAnual = Double.parseDouble(linha.get(3).toString());
                    Double variacaoMensal = Double.parseDouble(linha.get(4).toString());

                    inserirDadosPreco(custoM2, variacaoCustoMedio, variacaoAnual, variacaoMensal, bairro);
                    linha.clear();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void inserirDadosPreco(Double custoM2, Double variacaoMedio, Double variacaoAnual, Double variacaoMensal, String bairro) {
        // Conecta ao banco de dados
        DBConnectionProvider dbConnectionProvider = new DBConnectionProvider();
        JdbcTemplate connection = dbConnectionProvider.getConnection();

        // Verifica se o bairro já existe na tabela DadosInseridos
        String verificaBairroSql = "SELECT COUNT(1) FROM DadosInseridos WHERE bairro_nome = ?";
        Integer bairroExistente = connection.queryForObject(verificaBairroSql, Integer.class, bairro);

        if (bairroExistente > 0) {
            // O bairro já existe, então vamos atualizar os dados dessa linha
            String updateSql = "UPDATE DadosInseridos SET valorM2 = ?, variacaoCustoMedia = ?, variacaoAnual = ?, variacaoMensal = ? WHERE bairro_nome = ?";
            connection.update(updateSql, custoM2, variacaoMedio, variacaoAnual, variacaoMensal, bairro);
            System.out.println("Dados atualizados para o bairro: " + bairro);
        } else {
            // O bairro não existe, vamos inserir uma nova linha para esse bairro
            String insertSql = "INSERT INTO DadosInseridos (bairro_nome, valorM2, variacaoCustoMedia, variacaoAnual, variacaoMensal) VALUES (?, ?, ?, ?, ?)";
            connection.update(insertSql, bairro, custoM2, variacaoMedio, variacaoAnual, variacaoMensal);
            System.out.println("Dados inseridos para o bairro: " + bairro);
        }
    }

    public void lerDensidadeDemografica(String caminhoArquivo) {
        // Obtém o arquivo local
        try (FileInputStream fis = new FileInputStream(caminhoArquivo)) {

            // Lê o arquivo Excel usando Apache POI
            try (HSSFWorkbook workbook = new HSSFWorkbook(fis)) {
                HSSFSheet sheet = workbook.getSheetAt(0);  // Acessa a primeira planilha

                List<String> territorialidades = new ArrayList<>();
                List<Integer> populacao2010 = new ArrayList<>();

                // Itera sobre as linhas do Excel
                for (Row row : sheet) {
                    // Obtenção dos valores das células
                    String territorialidade = row.getCell(0).getStringCellValue();  // Coluna 0: Territorialidades
                    Double populacaoTotal = row.getCell(1).getNumericCellValue();  // Coluna 1: População total 2010

                    // Adiciona os dados nas listas
                    territorialidades.add(territorialidade);
                    populacao2010.add(populacaoTotal.intValue());  // Converte para inteiro

                    // Apenas um exemplo de log, você pode remover ou adaptar conforme necessário
                    System.out.println("Territorialidade: " + territorialidade + " | População Total 2010: " + populacaoTotal);
                }

                // Chama o método para processar os dados
                inserirDadosDensidadeDemografica(territorialidades, populacao2010);

            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void inserirDadosDensidadeDemografica(List<String> territorialidades, List<Integer> populacao2010) {
        // Conecta ao banco de dados
        DBConnectionProvider dbConnectionProvider = new DBConnectionProvider();
        JdbcTemplate connection = dbConnectionProvider.getConnection();

        for (int i = 0; i < territorialidades.size(); i++) {
            String territorialidade = territorialidades.get(i);  // Nome do bairro (territorialidade)
            Integer populacao = populacao2010.get(i);  // População de 2010

            // Verifica se o bairro (territorialidade) já existe na tabela DadosInseridos
            String verificaBairroSql = "SELECT COUNT(1) FROM DadosInseridos WHERE bairro = ?";
            Integer bairroExistente = connection.queryForObject(verificaBairroSql, Integer.class, territorialidade);

            if (bairroExistente > 0) {
                // O bairro já existe, então vamos atualizar os dados dessa linha
                String updateSql = "UPDATE DadosInseridos SET densidade = ? WHERE bairro = ?";
                connection.update(updateSql, populacao, territorialidade);
                System.out.println("Dados atualizados para o bairro: " + territorialidade);
            } else {
                // O bairro não existe, vamos inserir uma nova linha para esse bairro
                String insertSql = "INSERT INTO DadosInseridos (bairro, densidade) VALUES (?, ?)";
                connection.update(insertSql, territorialidade, populacao);
                System.out.println("Dados inseridos para o bairro: " + territorialidade);
            }
        }
    }

    public void lerIDH(String caminhoArquivo) {
        try (FileInputStream fis = new FileInputStream(caminhoArquivo);
             HSSFWorkbook workbook = new HSSFWorkbook(fis)) {

            fazerLog("O arquivo Excel foi aberto.");

            // Obtém a primeira planilha
            HSSFSheet sheet = workbook.getSheetAt(0);
            fazerLog("Acessou a planilha");

            List<String> linha = new ArrayList<>();

            // Itera sobre as linhas
            for (Row currentRow : sheet) {
                currentRow.forEach(cell -> {
                    if (cell != null) {
                        // Lê o conteúdo da célula como texto
                        var cellText = cell.toString();

                        // Checa se a célula contém um valor numérico (IDH geralmente é um número com ponto flutuante)
                        if (!cellText.isEmpty() && cellText.matches("[0-9]+([,.][0-9]+)?")) {
                            linha.add(cellText);
                        }
                    }
                });

                // Quando a linha contiver dados
                if (!linha.isEmpty()) {
                    // Para este exemplo, vamos assumir que a coluna 0 tem o bairro e a coluna 1 tem o valor de IDH
                    String bairro = linha.get(0);
                    Double idh = Double.parseDouble(linha.get(5).replace(",", "."));

                    // Processar os dados ou inserir no banco de dados
                    inserirDadosIDH(bairro, idh);
                    linha.clear();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void inserirDadosIDH(String bairro, Double idh) {
        // Conecta ao banco de dados
        DBConnectionProvider dbConnectionProvider = new DBConnectionProvider();
        JdbcTemplate connection = dbConnectionProvider.getConnection();

        // Verifica se a bairro (bairro) já existe na tabela DadosIDH
        String verificaTerritorialidadeSql = "SELECT COUNT(1) FROM DadosIDH WHERE bairro = ?";
        Integer bairroExistente = connection.queryForObject(verificaTerritorialidadeSql, Integer.class, bairro);

        if (bairroExistente > 0) {
            // O bairro já existe, então vamos atualizar os dados dessa linha
            String updateSql = "UPDATE DadosIDH SET idh = ? WHERE bairro = ?";
            connection.update(updateSql, idh, bairro);
            System.out.println("Dados do IDH atualizados para o bairro: " + bairro);
        } else {
            // O bairro não existe, vamos inserir uma nova linha para esse bairro
            String insertSql = "INSERT INTO DadosIDH (bairro, idh) VALUES (?, ?)";
            connection.update(insertSql, bairro, idh);
            System.out.println("Dados do IDH inseridos para o bairro: " + bairro);
        }
    }

    public void fazerLog(String situacao) {
        // Pegando o horário e a data de agora
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        // Variável para formatar a data
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        // Formatando a data
        String log = formatter.format(timestamp);

        System.out.println(log + " - %s".formatted(situacao));

        String LOG_FILE_PATH = "log.txt";  //caminho para o bucket S3

        try (FileWriter fw = new FileWriter(LOG_FILE_PATH, true);
             PrintWriter pw = new PrintWriter(fw)) {

            pw.println(timestamp + " - " + situacao);
        } catch (IOException e) {
            System.out.println("Erro ao escrever no arquivo de log: " + e.getMessage());
        }

        String S3_KEY = "logs/log.txt"; // Caminho e nome do arquivo no S3

        String S3_NAME = "vortex-tech"; // Nome do bucket

        S3Client s3 = new S3Provider().getS3Client();

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(S3_NAME)
                    .key(S3_KEY)
                    .build();

            // Realiza o upload do arquivo log.txt para o S3
            s3.putObject(putObjectRequest, Path.of(LOG_FILE_PATH));
            System.out.println("Log enviado para o S3 com sucesso.");

        } catch (S3Exception e) {
            System.err.println("Erro ao enviar o log para o S3: " + e.awsErrorDetails().errorMessage());
        } finally {
            s3.close();
        }
    }
}


