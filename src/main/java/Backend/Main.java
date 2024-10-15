package Backend;

import org.springframework.jdbc.core.JdbcTemplate;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import S3.S3Provider;


import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        LerArquivos arquivos = new LerArquivos();

        // O parametro "csvFile" procura o arquivo csv que vai converter
        // E o parametro "pathXls" indica a pasta e o nome do arquivo que ele vai criar
        //arquivos.converterCsvToXls("./src/main/java/book.csv","./src/main/java/book.xls");

//        arquivos.converterCsvToXls("src/main/java/20240916113529.csv", "src/main/java");

        //arquivos.lerXls("/home/ubuntu/JAR/s7t5102.xls");

        S3Client s3Client = new S3Provider().getS3Client();

        // aqui lista todos os buckets
        List<Bucket> buckets = s3Client.listBuckets().buckets();
        for (Bucket bucket : buckets) {
            System.out.println("Bucket: " + bucket.name());
        }

        // aqui lista todos os arquivos de um bucket
        ListObjectsRequest listObjects = ListObjectsRequest.builder()
                .bucket("") // aqui vai o nome do bucket
                .build();
        List<S3Object> objects = s3Client.listObjects(listObjects).contents();
        for (S3Object object : objects) {
            System.out.println("Objeto: " + object.key());
        }
    }
}

