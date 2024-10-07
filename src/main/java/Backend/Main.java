package Backend;

import org.springframework.jdbc.core.JdbcTemplate;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import S3.S3Provider;
import software.amazon.awssdk.services.s3.model.S3Exception;


import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        LerArquivos arquivos = new LerArquivos();

        // O parametro "csvFile" procura o arquivo csv que vai converter
        // E o parametro "pathXls" indica a pasta e o nome do arquivo que ele vai criar
        //arquivos.converterCsvToXls("./src/main/java/book.csv","./src/main/java/book.xls");

        arquivos.converterCsvToXls("src/main/java/20240916113529.csv", "src/main/java");

        arquivos.lerXls("src/main/java/s7t5102.xls");

//        S3Client s3Client = new S3Provider().getS3Client();
//
//        List<Bucket> buckets = s3Client.listBuckets().buckets();
//        for (Bucket bucket : buckets) {
//            System.out.println("Bucket: " + bucket.name());
//        }
    }
}

