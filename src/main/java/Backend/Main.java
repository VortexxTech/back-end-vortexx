package Backend;

import org.springframework.jdbc.core.JdbcTemplate;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import S3.S3Provider;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

public class Main {
    public static void main(String[] args) throws S3Exception {
        LerArquivos arquivos = new LerArquivos();

        // O parametro "csvFile" procura o arquivo csv que vai converter
        // E o parametro "pathXls" indica a pasta e o nome do arquivo que ele vai criar
        //arquivos.converterCsvToXls("./src/main/java/book.csv","./src/main/java/book.xls");

        //arquivos.lerXls("C:/SPTech/Vortexx/back-end-vortexx/src/main/java/s7t5102.xls");

        try{
//            S3Client s3Client = new S3Provider().getS3Client();

            // aqui lista todos os buckets
//            List<Bucket> buckets = s3Client.listBuckets().buckets();
//            System.out.println(buckets);
//            for (Bucket bucket : buckets) {
//                arquivos.fazerLog("Lendo o Bucket: " + bucket.name());
//
//                // aqui lista todos os arquivos de um bucket
//                ListObjectsRequest listObjects = ListObjectsRequest.builder()
//                        .bucket(bucket.name()) // aqui vai o nome do bucket
//                        .build();
//
//                List<S3Object> objects = s3Client.listObjects(listObjects).contents();
//                for (S3Object object : objects) {
//
//                    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
//                            .bucket(bucket.name())
//                            .key(object.key())
//                            .build();
//
//
//                    if(object.key().endsWith(".csv")) {
//                        arquivos.fazerLog("Lendo o arquivo: " + object.key());
//                        InputStream objectContent = s3Client.getObject(getObjectRequest, ResponseTransformer.toInputStream());
//                        Files.copy(objectContent, new File(object.key()).toPath());
//
//                        arquivos.fazerLog("Convertendo arquivo csv");
//                        arquivos.converterCsvToXls(object.key(), "convertido.xls");
//                    }
//
//                }
//            }

//            File arquivoConvertido = new File("convertido.xls");
//            File arquivoConvertido = new File();

            arquivos.lerXls("convertido.xls");
//            arquivos.fazerLog("Dados adicionados no banco!");
        } catch (S3Exception error) {
            System.out.println(error);
        }

    }
}

