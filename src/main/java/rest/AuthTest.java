package rest;
import io.restassured.http.ContentType;
import io.restassured.path.xml.XmlPath;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class AuthTest {

    @Test
    public void deveAcessarSWAPI(){
        given()
                .log().all()
                .when()
                .get("https://swapi.dev/api/people/1/")
                .then()
                .log().all()
                .statusCode(200)
                .body("name", is("Luke Skywalker"))
                ;
    }

    //5643587e835d360f952c2d3c81eff991
    //https://api.openweathermap.org/data/2.5/weather?q=Fortaleza&appid=5643587e835d360f952c2d3c81eff991&&units-metric

    @Test
    public void deveObterClima(){
        given()
                .log().all()
                .queryParam("q","Fortaleza, BR")
                .queryParam("appid", "5643587e835d360f952c2d3c81eff991")
                .queryParam("units" , "metric")
                .when()
                .get("https://api.openweathermap.org/data/2.5/weather")
                .then()
                .log().all()
                .statusCode(200)
                .body("name" , is("Fortaleza"))
                .body("id", is(6320062))
                .body("coord.lon" , is(-38.5247F))
                .body("coord.lat", is(-3.7227F))
                .body("main.temp", greaterThan(25F))
        ;
    }

    @Test
    public void naoDeveAcessarSemSenha(){
        given()
                .log().all()
                .when()
                .get("https://restapi.wcaquino.me/basicauth")
                .then()
                .log().all()
                .statusCode(401)
        ;
    }

    @Test
    public void deveFazerAutenticacaoBasic(){
        given()
                .log().all()
                .when()
                .get("https://admin:senha@restapi.wcaquino.me/basicauth")
                .then()
                .log().all()
                .statusCode(200)
                .body("status" , is("logado"))
        ;
    }
    @Test
    public void deveFazerAutenticacaoBasicDois(){
        given()
                .log().all()
                .auth().basic("admin", "senha")
                .when()
                .get("https://restapi.wcaquino.me/basicauth")
                .then()
                .log().all()
                .statusCode(200)
                .body("status" , is("logado"))
        ;
    }
    @Test
    public void deveFazerAutenticacaoBasicChallenge(){
        given()
                .log().all()
                .auth().preemptive().basic("admin", "senha")
                .when()
                .get("https://restapi.wcaquino.me/basicauth2")
                .then()
                .log().all()
                .statusCode(200)
                .body("status" , is("logado"))
        ;
    }

    @Test
    public void deveFazerAutenticacaoComTokenJWT(){
        Map<String, String> login = new HashMap<String,String>();
        login.put("email", "claudio@soares");
        login.put("senha", "Soares01");
        //Login na API
        String token = given()
                .log().all()
                .body(login)
                .contentType(ContentType.JSON)
                .when()
                .post("https://barrigarest.wcaquino.me/signin")
                .then()
                .log().all()
                .statusCode(200)
                //receber token
                .extract().path("token")
        ;

        //obter as contas
        given()
                .log().all()
                .header("Authorization", "JWT " + token)
                .when()
                .get("https://barrigarest.wcaquino.me/contas")
                .then()
                .log().all()
                .statusCode(200)
                .body("nome", hasItem("Conta de teste"))
                ;
    }

    @Test
    public void deveAcessarAplicacaoWeb(){
       //Login
       String cookie = given()
                .log().all()
                .formParam("email", "claudio@soares")
                .formParam("senha", "Soares01")
                .contentType(ContentType.URLENC.withCharset("UTF-8"))
                .when()
                .post("https://seubarriga.wcaquino.me/logar")
                .then()
                .log().all()
                .statusCode(200)
                .extract().header("set-cookie")
                ;
       cookie = cookie.split("=")[1].split(";")[0];
       System.out.println(cookie);

        //Obter conta

      String body = given()
              .log().all()
              .cookie("connect.sid", cookie)
              .when()
              .get(" https://seubarriga.wcaquino.me/contas")
              .then()
              .log().all()
              .statusCode(200)
              .body("html.body.table.tbody.tr[0].td[0]", is("Conta de teste"))
              .extract().body().asString();
      ;
      System.out.println("----------------");
        XmlPath xmlPath = new XmlPath(XmlPath.CompatibilityMode.HTML, body);
        System.out.println(xmlPath.getString("html.body.table.tbody.tr[0].td[0]"));
    }



}
