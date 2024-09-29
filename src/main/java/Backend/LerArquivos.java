package Backend;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import Backend.DBConnectionProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

public class LerArquivos {
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
            System.out.println("Conversão concluída!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void lerXls(String caminhoArquivo){
        try (FileInputStream fis = new FileInputStream(caminhoArquivo);
             HSSFWorkbook workbook = new HSSFWorkbook(fis)) {
//             criarTabela();

            // Obtém a primeira planilha
            HSSFSheet sheet = workbook.getSheetAt(0);

            List linha = new ArrayList<>();

            // Itera sobre as linhas
            for (Row CurrentRow : sheet) {
                    linha.add(String.valueOf(CurrentRow.getRowNum()));

                    CurrentRow.forEach(cell -> {
                        if(cell != null && cell.getCellType() == CellType.NUMERIC){
                            linha.add(cell.toString());
                        }
                    });

                System.out.println(linha);

                

                    linha.clear();
                }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void criarTabela() {
        DBConnectionProvider dbConnectionProvider = new DBConnectionProvider();
        JdbcTemplate connection = dbConnectionProvider.getConnection();

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

    public void inserirDados() {
        DBConnectionProvider dbConnectionProvider = new DBConnectionProvider();
        JdbcTemplate connection = dbConnectionProvider.getConnection();

        connection.update("INSERT INTO filme (nome, ano, genero, diretor) VALUES (?, ?, ?, ?)\",\n" +
                "        \"Matrix\", 1999, \"Ficção Científica\", \"Lana Wachowski, Lilly Wachowski");
    }

}
