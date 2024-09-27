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
                    linha.add("Linha: " + CurrentRow.getRowNum());

                    for(int i = CurrentRow.getFirstCellNum(); i < CurrentRow.getLastCellNum(); i++){
                        Cell CurrentCell = CurrentRow.getCell(i);

                        if(CurrentCell != null && CurrentCell.getCellType() == CellType.NUMERIC){
                            System.out.println();

                            linha.add("Célula: " + i + " " + CurrentRow.getCell(i));
                        }
                    }

                    ModeloLinha linhaObj = new ModeloLinha();

                    linhaObj.setNumLinha(linha.get(0));
                    linhaObj.setAno(linha.get(1));
                    linhaObj.setPrecosCorrentes(linha.get(2));
                    linhaObj.setVariacaoRealAnual(linha.get(3));

                    linhaObj.printarLinha();

                    linha.clear();

                    // Itera sobre as células da linha
//                CurrentRow.forEach( cell -> {
//                    System.out.print("cell: " + cell + "\t");
//                });
                    System.out.println();
                }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
