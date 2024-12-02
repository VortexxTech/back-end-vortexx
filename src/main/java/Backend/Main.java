package Backend;

import Backend.ApiArquivos.LerArquivos;
import S3.S3Provider;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

public class Main {
    public static void main(String[] args) throws S3Exception {
        LerArquivos arquivos = new LerArquivos();

        String bucketName = System.getenv(("BUCKET_NAME"));

        arquivos.lerIDH(bucketName, "7_Indice_de_desenvolvimento_humano_municip_2000_10962.xls");
        arquivos.lerDensidadeDemografica(bucketName, "densidade_sao_paulo_bairros.xlsx");
        arquivos.lerCusto(bucketName, "Bairros_Sao_Paulo_Preco_m2_Corrigido.xlsx");
    }
}

