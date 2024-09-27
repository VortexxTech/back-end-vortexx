import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

public class LerArquivos {

    public LerArquivos() {};

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

            // Obtém a primeira planilha
            HSSFSheet sheet = workbook.getSheetAt(0);

            List linha = new ArrayList<>();

            // Itera sobre as linhas
            for (Row CurrentRow : sheet) {
                String NumRow = toString();
                    linha.add(CurrentRow.getRowNum());

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

}
