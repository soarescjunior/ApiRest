package rest;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class FileTest {

    @Test
    public void deveObrigarEnviarArquivo(){
        given()
                .log().all()
                .when()
                .post("http://restapi.wcaquino.me/upload")
                .then()
                .log().all()
                .statusCode(404)
                .body("error:", is("Arquivo não enviado"))
                ;

    }

    @Test
    public void deveFazerUploadDoArquivo(){
        given()
                .log().all()
                .multiPart("arquivo", new File("src/main/resources/chroe - Pesquisa Google.pdf"))
                .when()
                .post("http://restapi.wcaquino.me/upload")
                .then()
                .log().all()
                .statusCode(200)
                .body("name" , is("chroe - Pesquisa Google.pdf"))
        ;

    }

    @Test
    public void naoDeveFazerUploadDoArquivoGrande(){
        given()
                .log().all()
                .multiPart("arquivo", new File("src/main/resources/arquivoGrande.exe"))
                .when()
                .post("http://restapi.wcaquino.me/upload")
                .then()
                .log().all()
                .time(lessThan(3000L))
                .statusCode(413)
        ;
    }

    @Test
    public void DeveBaixarArquivo() throws IOException {
        byte[] image = given()
                .log().all()
                .when()
                .get("http://restapi.wcaquino.me/download")
                .then()
              //  .log().all()
                .statusCode(200)
                .extract().asByteArray()
        ;
        File imagem = new File("src/main/resources/file.jpg");
        OutputStream out = new FileOutputStream(imagem);
        out.write(image);
        out.close();

        System.out.println(imagem.length());
        Assert.assertThat(imagem.length(), lessThan(1000L));
    }

}
