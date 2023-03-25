package rest;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.internal.path.xml.NodeImpl;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;

import static io.restassured.RestAssured.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.xml.HasXPath.hasXPath;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UserXMLTest {

    public static RequestSpecification requestSpec;
    public static ResponseSpecification responseSpec;

    @BeforeClass
    public static void steup(){
        RestAssured.baseURI = "https://restapi.wcaquino.me";
        //RestAssured.port = 443;
        //RestAssured.basePath = "/v2";
        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder.log(LogDetail.ALL);
        requestSpec = requestSpecBuilder.build();

        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder();
        responseSpecBuilder.expectStatusCode(200);
        responseSpec = responseSpecBuilder.build();

        requestSpecification = requestSpec;
        responseSpecification = responseSpec;

    }

@Test
    public void devoTrabalharComXML(){

        given()
                .when()
                .get("/usersXML/3")
                .then()
               // .statusCode(200)
                .rootPath("user")
                .body("name", Matchers.is("Ana Julia"))
                .body("@id", Matchers.is("3"))
                .rootPath("user.filhos")
                .body("name.size()", Matchers.is(2))
                .body("name[0]", Matchers.is("Zezinho"))
                .body("name[1]", Matchers.is("Luizinho"))
                .body("name", hasItem("Luizinho"))
                .body("name", hasItems("Luizinho", "Zezinho"))
                ;
}

    @Test
    public void devoFazerPesquisasAvancadasComXML() {
        given()
                .when()
                .get("/usersXML")
                .then()
               // .statusCode(200)
                .body("users.user.size()", Matchers.is(3))// Quantidade de usarios
                .body("users.user.findAll{it.age.toInteger() <=25}.size()", Matchers.is(2)) //Quantida de usuarios com idadade menor ou igual a 25.
                .body("users.user.@id", hasItems("1","2","3"))//quantidade de id de usuarios.
                .body("users.user.find{it.age == 25}.name", Matchers.is("Maria Joaquina")) // nomes que a ideade é igual a 25 anos.
                .body("users.user.findAll{it.name.toString().contains('n')}.name", hasItems("Maria Joaquina", "Ana Julia"))// Nomes que contem "n"
                .body("users.user.salary.find{it != null}.toDouble()", Matchers.is(1234.5678d)) // salario diferente de Nulo
                .body("users.user.age.collect{it.toInteger() * 2}", hasItems(40,50,60))// a idade de cada usuario multipliacado por 2
                .body("users.user.name.findAll{it.toString().startsWith('Maria')}.collect{it.toString().toUpperCase()}", Matchers.is("MARIA JOAQUINA"))
    ;
    }

    @Test
    public void devoFazerPesquisasAvancadasComXMLEJava() {
        ArrayList<NodeImpl> nomes = given()
                .when()
                    .get("/usersXML")
                .then()
                    .statusCode(200)
                .extract().path("users.user.name.findAll{it.toString().contains('n')}")
        ;
     //   assertEquals("Maria Joaquina".toUpperCase(), name.toUpperCase());
      //  System.out.println(nomes);
        assertEquals("Maria Joaquina".toUpperCase(), nomes.get(0).toString().toUpperCase());
        assertTrue("Ana Julia".equalsIgnoreCase(nomes.get(1).toString()));
    }

    @Test
    public void devoFazerPesquisasAvancadasComXPath() {
     given()
                .when()
                    .get("/usersXML")
                .then()
                .statusCode(200)
                .body(hasXPath("count(/users/user)", is("3")))
                .body(hasXPath("/users/user[@id= '1']"))
                .body(hasXPath("//user[@id= '2']"))
                .body(hasXPath("//name[text()= 'Luizinho']/../../name", is(("Ana Julia"))))
                .body(hasXPath("//name[text()= 'Ana Julia']/following-sibling::filhos", allOf(containsString("Zezinho"), containsString("Luizinho"))))
             .body(hasXPath("/users/user/name",is("João da Silva")))
             .body(hasXPath("//name",is("João da Silva")))
             .body(hasXPath("/users/user[2]/name",is("Maria Joaquina")))
             .body(hasXPath("/users/user[last()]/name",is("Ana Julia")))
             .body(hasXPath("count(/users/user/name[contains(.,'n')])",is("2")))
             .body(hasXPath("//user[age < 24]/name",is("Ana Julia")))
             .body(hasXPath("//user[age > 20 and age < 30]/name",is("Maria Joaquina")))
             .body(hasXPath("//user[age > 20][age < 30]/name",is("Maria Joaquina")))
                ;

    }

}
