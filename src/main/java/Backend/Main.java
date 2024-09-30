package Backend;

import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        LerArquivos arquivos = new LerArquivos();

        // O parametro "csvFile" procura o arquivo csv que vai converter
        // E o parametro "pathXls" indica a pasta e o nome do arquivo que ele vai criar
        //arquivos.converterCsvToXls("./src/main/java/book.csv","./src/main/java/book.xls");

        arquivos.converterCsvToXls("C:\\SPTech\\Vortexx\\back-end-vortexx\\src\\main\\java\\20240916113529.csv", "C:\\SPTech\\Vortexx\\back-end-vortexx\\src\\main\\java");

        arquivos.lerXls("./src/main/java/s7t5102.xls");
    }
}

