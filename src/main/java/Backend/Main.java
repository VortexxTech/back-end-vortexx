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

        arquivos.lerDensidadeDemografica("s3-castro-lab", "densidade_sao_paulo_bairros.xlsx");
    }
}

