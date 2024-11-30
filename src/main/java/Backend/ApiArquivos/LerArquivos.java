package Backend.ApiArquivos;

import java.io.FileOutputStream;
import java.io.*;
import java.sql.Timestamp;
import java.util.*;

import Backend.Bairros;
import Backend.DBConnectionProvider;

import S3.S3Provider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

public class LerArquivos {
    private static final Logger log = LogManager.getLogger(LerArquivos.class);
    private static final S3Client s3Client = new S3Provider().getS3Client();

    public void lerArquivoS3(String bucketName, String archiveName) {

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

    public void converterCsvToXls(String csvFile, String pathXls) {

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
//            fazerLog("Conversão Concluída!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void lerXls(String caminhoArquivo, String tipoDado) {
        try (FileInputStream fis = new FileInputStream(caminhoArquivo);
             HSSFWorkbook workbook = new HSSFWorkbook(fis)) {

            HSSFSheet sheet = workbook.getSheetAt(0);
            List<Long> linha = new ArrayList<>();
            List<String> algarismos = Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "-");

            // Mapeamento de dados por tipo
            Map<Bairros, Map<String, Double>> dadosBairro = new HashMap<>();

            for (Row currentRow : sheet) {
                currentRow.forEach(cell -> {
                    if (cell != null) {
                        Boolean celulaNumerica = false;

                        if (!cell.getCellType().equals(CellType.BLANK) && !cell.getStringCellValue().isEmpty()) {
                            Character primeiroCaracter = cell.getStringCellValue().charAt(0);

                            for (String algarismo : algarismos) {
                                if (primeiroCaracter.toString().equals(algarismo)) {
                                    celulaNumerica = true;
                                }
                            }
                        }

                        if (celulaNumerica) {
                            linha.add(Math.round(Double.parseDouble(cell.getStringCellValue())));
                        }
                    }
                });

                if (!linha.isEmpty()) {
                    // Define o valor corretamente com base na linha
                    Double valor = Double.parseDouble(linha.get(0).toString());  // Supondo que o valor principal está na primeira posição (ajuste conforme necessário)

                    // Inicializa o Map para o bairro, se não existir
                    for (Bairros bairro : Bairros.values()) {
                        if (!dadosBairro.containsKey(bairro)) {
                            dadosBairro.put(bairro, new HashMap<>());
                        }

                        Map<String, Double> bairroDados = dadosBairro.get(bairro);

                        // Adiciona o valor no map de acordo com o tipo de dado
                        if (tipoDado.equals("valorM2")) {
                            bairroDados.put("valorM2", valor);
                        } else if (tipoDado.equals("densidade")) {
                            bairroDados.put("densidade", valor);
                        } else if (tipoDado.equals("idh")) {
                            bairroDados.put("idh", valor);
                        }
                    }
                    linha.clear();
                }
            }

            // Chama o método para inserir os dados
            inserirDados(dadosBairro);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Métod0 para mapear os dados do bairro de acordo com o tipo de dado
    public Map<Bairros, Double> mapearDados(String tipoDado, Double valor) {
        Map<Bairros, Double> dados = new HashMap<>();
        for (Bairros bairro : Bairros.values()) {
            // Dependendo do tipo de dado, faz o mapeamento
            if (tipoDado.equals("valorM2")) {
                dados.put(bairro, valor);  // ValorM2
            } else if (tipoDado.equals("densidade")) {
                dados.put(bairro, valor);  // Densidade Demográfica
            } else if (tipoDado.equals("idh")) {
                dados.put(bairro, valor);  // IDH
            }
        }
        return dados;
    }

    public void inserirDados(Map<Bairros, Map<String, Double>> dadosBairro) {
        DBConnectionProvider dbConnectionProvider = new DBConnectionProvider();
        JdbcTemplate connection = dbConnectionProvider.getConnection();

        for (Bairros bairro : Bairros.values()) {
            // Verifica se os dados do bairro existem no Map de dados
            if (dadosBairro.containsKey(bairro)) {
                Map<String, Double> bairroDados = dadosBairro.get(bairro);

                // Recupera os valores para cada tipo de dado
                Double valorM2 = bairroDados.get("valorM2"); // Supondo que custoM2 esteja sendo mapeado com "custom2"
                Double idh = bairroDados.get("idh"); // IDH
                Double densidade = bairroDados.get("densidade"); // Densidade Demográfica

                // Pega a zona de acordo com o bairro
                String zona = bairro.getZona(); // Zona, já disponível no enum ou estrutura Bairro

                // Define a data de inserção (utilizando a data atual, por exemplo)
                Timestamp dataInsercao = new Timestamp(System.currentTimeMillis());

                // Inserção no banco de dados
                String sql = "INSERT INTO DadosInseridos (zona, bairro, valorm2, densidade, dtInsercao, idh) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";

                connection.update(sql, zona, bairro.getNome(), valorM2, densidade, dataInsercao, idh);
            }
        }
    }

//    public void fazerLog(String situacao) {
//        // Pegando o horário e a data de agora
//        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//
//        // Variável para formatar a data
//        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//
//        // Formatando a data
//        String log = formatter.format(timestamp);
//
//        System.out.println(log + " - %s".formatted(situacao));
//
//        String LOG_FILE_PATH = "log.txt";  //caminho para o bucket S3
//
//        try (FileWriter fw = new FileWriter(LOG_FILE_PATH, true);
//             PrintWriter pw = new PrintWriter(fw)) {
//
//            pw.println(timestamp + " - " + situacao);
//        } catch (IOException e) {
//            System.out.println("Erro ao escrever no arquivo de log: " + e.getMessage());
//        }
//
//        String S3_KEY = "logs/log.txt"; // Caminho e nome do arquivo no S3
//
//        String S3_NAME = "vortex-tech"; // Nome do bucket
//
////        S3Client s3 = new S3Provider().getS3Client();
//
//        try {
//            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
//                    .bucket(S3_NAME)
//                    .key(S3_KEY)
//                    .build();
//
//            // Realiza o upload do arquivo log.txt para o S3
////            s3.putObject(putObjectRequest, Path.of(LOG_FILE_PATH));
//            System.out.println("Log enviado para o S3 com sucesso.");
//
//        } catch (S3Exception e) {
//            System.err.println("Erro ao enviar o log para o S3: " + e.awsErrorDetails().errorMessage());
//        } finally {
////            s3.close();
//        }
}


