package Backend.ApiArquivos;

import java.io.FileOutputStream;
import java.io.*;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import Backend.DBConnectionProvider;
import S3.S3Provider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.jdbc.core.JdbcTemplate;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.S3Client;

public class LerArquivos {
    private static final Logger logger = LogManager.getLogger(LerArquivos.class);
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

    public InputStream lerArquivoS3(String bucketName, String archiveName) {
        // Faz a requisição para obter o objeto
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(archiveName)
                .build();

        try {
            // Lê o conteúdo do arquivo
            ResponseInputStream<?> response = s3Client.getObject(getObjectRequest);
            return response;
        } catch (SdkException e) {
            // Captura exceções específicas da AWS SDK
            e.printStackTrace();
            return null;
        }
    }

    public void lerPreco(String bucketName, String archiveName) {
        // Lê o arquivo do S3
        InputStream s3InputStream = lerArquivoS3(bucketName, archiveName);

        if (s3InputStream == null) {
            logger.fatal("Não foi possível obter o arquivo do S3.");
            return;
        }

        List<Map<String, Object>> precosVariacoesList = new ArrayList<>();

        try (HSSFWorkbook workbook = new HSSFWorkbook(s3InputStream)) {
            logger.info("O arquivo Excel foi aberto.");

            HSSFSheet sheet = workbook.getSheetAt(0);
            logger.info("Acessou a planilha.");

            for (Row currentRow : sheet) {
                if (currentRow.getRowNum() < 1) continue;

                String cidade = currentRow.getCell(0) != null ? currentRow.getCell(0).getStringCellValue() : "";

                Map<String, Object> linhaData = new HashMap<>();
                linhaData.put("cidade", cidade);

                currentRow.forEach(cell -> {
                    if (cell != null) {
                        String cellText = cell.toString();
                        // Verifica se a célula contém um valor numérico (preços ou variações)
                        if (cellText.indexOf('.') != -1 || cellText.indexOf('0') != -1) {
                            if (cell.getColumnIndex() == 3) {
                                linhaData.put("custoM2", Double.parseDouble(cellText));
                            } else if (cell.getColumnIndex() == 4) {
                                linhaData.put("variacaoCustoMedio", Double.parseDouble(cellText));
                            } else if (cell.getColumnIndex() == 5) {
                                linhaData.put("variacaoAnual", Double.parseDouble(cellText));
                            } else if (cell.getColumnIndex() == 6) {
                                linhaData.put("variacaoMensal", Double.parseDouble(cellText));
                            }
                        }
                    }
                });

                if (linhaData.containsKey("cidade") && linhaData.containsKey("custoM2")
                        && linhaData.containsKey("variacaoCustoMedio") && linhaData.containsKey("variacaoAnual")
                        && linhaData.containsKey("variacaoMensal")) {
                    precosVariacoesList.add(linhaData);
                }
            }

            inserirDadosPreco(precosVariacoesList);

        } catch (IOException e) {
            logger.fatal("Erro ao processar o arquivo Excel: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void inserirDadosPreco(List<Map<String, Object>> precosVariacoesList) {
        DBConnectionProvider dbConnectionProvider = new DBConnectionProvider();
        JdbcTemplate connection = dbConnectionProvider.getConnection();

        for (Map<String, Object> linhaData : precosVariacoesList) {
            String cidade = (String) linhaData.get("cidade");
            Double custoM2 = (Double) linhaData.get("custoM2");
            Double variacaoCustoMedio = (Double) linhaData.get("variacaoCustoMedio");
            Double variacaoAnual = (Double) linhaData.get("variacaoAnual");
            Double variacaoMensal = (Double) linhaData.get("variacaoMensal");

            // Verifica se já existe um registro com a mesma cidade
            String verificaCidadeSql = "SELECT COUNT(1) FROM DadosInseridos WHERE cidade = ?";
            Integer registroExistente = connection.queryForObject(verificaCidadeSql, Integer.class, cidade);

            if (registroExistente > 0) {
                // O registro já existe, atualizamos os dados
                String updateSql = "UPDATE DadosInseridos SET valorM2 = ?, variacaoPrecoM2 = ?, variacaoAnualPrecoM2 = ?, variacaoMensalPrecoM2 = ? WHERE cidade = ?";
                connection.update(updateSql, custoM2, variacaoCustoMedio, variacaoAnual, variacaoMensal, cidade);
                logger.info("Dados atualizados para a cidade: " + cidade);
            }
        }
    }

    public void lerCusto(String bucketName, String archiveName) {
        // Obtém o arquivo do S3
        InputStream s3InputStream = lerArquivoS3(bucketName, archiveName);

        if (s3InputStream == null) {
            logger.error("Não foi possível obter o arquivo do S3.");
            return;
        }

        // Lista para armazenar os dados de bairro e preço
        List<Map<String, Object>> precosList = new ArrayList<>();

        // Processa o arquivo Excel
        try (Workbook workbook = new HSSFWorkbook(s3InputStream)) {
            logger.info("Arquivo Excel aberto com sucesso.");
            Sheet sheet = workbook.getSheetAt(0);
            logger.info("Acessou a planilha.");

            // Itera pelas linhas da planilha, ignorando cabeçalhos
            for (Row row : sheet) {
                if (row.getRowNum() < 2) continue; // Ignorar cabeçalhos

                Cell bairroCell = row.getCell(0);
                Cell custoM2Cell = row.getCell(1);

                if (bairroCell != null && custoM2Cell != null) {
                    String bairro = bairroCell.getStringCellValue();
                    Double custoM2 = custoM2Cell.getNumericCellValue();

                    // Adiciona os dados à lista
                    Map<String, Object> linhaData = new HashMap<>();
                    linhaData.put("bairro", bairro);
                    linhaData.put("custoM2", custoM2);
                    precosList.add(linhaData);
                }
            }

            inserirDadosCusto(precosList);

        } catch (Exception e) {
            logger.error("Erro ao processar o arquivo Excel: " + e.getMessage(), e);
        }
    }

    private void inserirDadosCusto(List<Map<String, Object>> precosList) {
        // Conecta ao banco de dados
        DBConnectionProvider dbConnectionProvider = new DBConnectionProvider();
        JdbcTemplate connection = dbConnectionProvider.getConnection();

        for (Map<String, Object> linhaData : precosList) {
            String bairro = (String) linhaData.get("bairro");
            Double custoM2 = (Double) linhaData.get("custoM2");

            String verificaBairroSql = "SELECT COUNT(1) FROM DadosInseridos WHERE bairro = ?";
            Integer bairroExistente = connection.queryForObject(verificaBairroSql, Integer.class, bairro);

            if (bairroExistente > 0) {
                String updateSql = "UPDATE DadosInseridos SET valorM2 = ? WHERE bairro = ?";
                connection.update(updateSql, custoM2, bairro);
                logger.info("Dados atualizados para o bairro: " + bairro);
            } else {
                String insertSql = "INSERT INTO DadosInseridos (bairro, valorM2) VALUES (?, ?)";
                connection.update(insertSql, bairro, custoM2);
                logger.info("Dados inseridos para o bairro: " + bairro);
            }
        }
    }

    public void lerDensidadeDemografica(String bucketName, String archiveName) {
        InputStream arquivoS3 = lerArquivoS3(bucketName, archiveName);

        if (arquivoS3 != null) {
            List<Map<String, Object>> bairrosDensidadeList = new ArrayList<>();

            try (XSSFWorkbook workbook = new XSSFWorkbook(arquivoS3)) {
                XSSFSheet sheet = workbook.getSheetAt(0);

                // Itera sobre as linhas do Excel
                for (Row row : sheet) {
                    if(row.getCell(0) == null || row.getCell(1) == null) {
                        break;
                    }
                    if(row.getCell(1).getCellType() == CellType.NUMERIC) {
                        String territorialidade = row.getCell(0) != null ? row.getCell(0).getStringCellValue() : "";
                        Double populacaoTotal = row.getCell(1) != null ? row.getCell(1).getNumericCellValue() : 0.0;

                        String regex = "\\([^)]*\\)";
                        territorialidade = territorialidade.replaceAll(regex, "").trim();

                        // Adiciona os dados na lista
                        Map<String, Object> bairroData = new HashMap<>();
                        bairroData.put("bairro", territorialidade);
                        bairroData.put("densidade", populacaoTotal.intValue());  // Converte para inteiro

                        bairrosDensidadeList.add(bairroData);

                        System.out.println("Territorialidade: " + territorialidade + " | População Total 2010: " + populacaoTotal);
                    }

                }

                inserirDadosDensidadeDemografica(bairrosDensidadeList);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Erro ao ler o arquivo do S3.");
        }
    }

    private void inserirDadosDensidadeDemografica(List<Map<String, Object>> bairrosDensidadeList) {
        // Conecta ao banco de dados
        DBConnectionProvider dbConnectionProvider = new DBConnectionProvider();
        JdbcTemplate connection = dbConnectionProvider.getConnection();

        // Itera sobre a lista de bairros e densidades
        for (Map<String, Object> bairroData : bairrosDensidadeList) {
            String bairro = (String) bairroData.get("bairro");
            Integer densidade = (Integer) bairroData.get("densidade");

            // Verifica se o bairro já existe na tabela DadosInseridos
            String verificaBairroSql = "SELECT COUNT(1) FROM DadosInseridos WHERE LOWER(bairro) LIKE LOWER(?)";
            Integer bairroExistente = connection.queryForObject(verificaBairroSql, Integer.class, "%" + bairro + "%");

            if (bairroExistente > 0) {
                // O bairro já existe, então vamos atualizar os dados dessa linha
                String updateSql = "UPDATE DadosInseridos SET densidadeDemografica = ?, dtInsercao = NOW() WHERE bairro = ?";
                connection.update(updateSql, densidade, bairro);
                System.out.println("Dados atualizados para o bairro: " + bairro);
            } else {
                // O bairro não existe, vamos inserir uma nova linha para esse bairro
                String insertSql = "INSERT INTO DadosInseridos (bairro, densidadeDemografica, dtInsercap) VALUES (?, ?, NOW())";
                connection.update(insertSql, bairro, densidade);
                System.out.println("Dados inseridos para o bairro: " + bairro);
            }
        }
    }

    public void lerIDH(String bucketName, String archiveName) {
        InputStream arquivoS3 = lerArquivoS3(bucketName, archiveName);

        if(arquivoS3 != null) {
            List<Map<String, Object>> bairrosIDHList = new ArrayList<>();

            try (HSSFWorkbook workbook = new HSSFWorkbook(arquivoS3)) {
                HSSFSheet sheet = workbook.getSheetAt(0);

                //Itera sobre as linhas do arquivo
                for (int i = 7; sheet.getRow(i).getRowNum() <= sheet.getRow(37).getRowNum(); i++) {
                    String territorialidade = sheet.getRow(i).getCell(0) != null ? sheet.getRow(i).getCell(0).getStringCellValue() : "";
                    Double idh = sheet.getRow(i).getCell(5) != null ? sheet.getRow(i).getCell(5).getNumericCellValue() : 0.0;

                    String regex = "\\([^)]*\\)";
                    territorialidade = territorialidade.replaceAll(regex, "").trim();

                    System.out.println("Territorialidade: " + territorialidade + " | IDHM: " + idh);

                    // Adiciona o bairro e o IDH à lista
                    Map<String, Object> bairroData = new HashMap<>();
                    bairroData.put("bairro", territorialidade);
                    bairroData.put("idh", idh);
                    bairrosIDHList.add(bairroData);
                }

                for (Map<String, Object> bairroData : bairrosIDHList) {
                    String bairro = (String) bairroData.get("bairro");
                    Double idh = (Double) bairroData.get("idh");

                    inserirDadosIDH(bairro, idh);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("Erro ao ler o arquivo do S3.");
        }


    }

    public void inserirDadosIDH(String bairro, Double idh) {
        // Conecta ao banco de dados
        DBConnectionProvider dbConnectionProvider = new DBConnectionProvider();
        JdbcTemplate connection = dbConnectionProvider.getConnection();

        // Verifica se o bairro já existe na tabela DadosIDH
        String verificaBairroSql = "SELECT COUNT(1) FROM DadosInseridos WHERE LOWER(bairro) LIKE LOWER(?)";
        Integer bairroExistente = connection.queryForObject(verificaBairroSql, Integer.class, "%" + bairro + "%");

        if (bairroExistente > 0) {
            // O bairro já existe, então vamos atualizar o valor de IDH
            String updateSql = "UPDATE DadosInseridos SET idh = ?, dtInsercao = NOW() WHERE bairro = ?";
            connection.update(updateSql, idh, bairro);
            System.out.println("Dados do IDH atualizados para o bairro: " + bairro);
        } else {
            // O bairro não existe, vamos inserir uma nova linha para esse bairro
            String insertSql = "INSERT INTO DadosInseridos (bairro, idh, dtInsercao) VALUES (?, ?, NOW())";
            connection.update(insertSql, bairro, idh);
            System.out.println("Dados inseridos para o bairro: " + bairro);
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


