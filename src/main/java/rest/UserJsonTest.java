package rest;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.hamcrest.core.StringContains;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.patch;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;

public class UserJsonTest {

    @Test

    public void deveVerificarPrimeiroNivel(){//Primeiro nivel do Json
        given()
                .when()
                    .get("https://restapi.wcaquino.me/users/1")
                .then()
                .statusCode(200)
                .body("id", is(1))
                .body("name", containsString("Silva")) //Contem o textp "Silva"
                .body("age", greaterThan(18)) // idade maior que 18.
           //     .body("salary", is(1234.5678))
        ;
    }

    @Test
    public void deveVerificarPrimeiroNivelOutrasFormas(){
        Response response = RestAssured.request(Method.GET,"https://restapi.wcaquino.me/users/1" ); // Extrair as informa��o do Json para trabalhar

        //path
        response.path("id"); // sem "exibicao na tela
        System.out.println((Integer) response.path("id")); //Exibindo na tela o resultado

        assertEquals(new Integer(1), response.path(("id"))); //compara��o com Junit
        assertEquals(new Integer(1), response.path("%s","id")); //compara��o com Junit

        //Jsonpatch

        JsonPath jsonPath = new JsonPath(response.asString());
        assertEquals(1, jsonPath.getInt("id"));

        //From

        int id = JsonPath.from(response.asString()).getInt("id");
        assertEquals(1, id);

    }
@Test
    public void deveVerificarSegundoNivel(){//Segundo nivel do Json
        given()
                .when()
                .get("https://restapi.wcaquino.me/users/2")
                .then()
                .statusCode(200)
                .body("name", containsString("Joaquina")) //Contem o textp "Joaquina"
                .body("endereco.rua", is("Rua dos bobos"))
        ;
    }

    @Test
    public void deveVerificarLista(){//Lista
        given()
                .when()
                .get("https://restapi.wcaquino.me/users/3")
                .then()
                .statusCode(200)
                .body("name", containsString("Ana")) //Contem o textp "Joaquina"
                .body("filhos", hasSize(2))
                .body("filhos[0].name", is("Zezinho"))
                .body("filhos[1].name", is("Luizinho"))
                .body("filhos.name", hasItem("Zezinho"))
                .body("filhos.name", hasItems("Luizinho", "Luizinho"))
        ;
    }

    @Test
    public void deveRetornarErroUsuarioInexistente(){
        given()
                .when()
                .get("https://restapi.wcaquino.me/users/4")
                .then()
                .statusCode(404)
                .body("error", is("Usuário inexistente"))
        ;
    }

    @Test
    public void deveVerificarListaRaiz(){
        given()
                .when()
                .get("https://restapi.wcaquino.me/users")
                .then()
                .statusCode(200)
                .body("$", hasSize(3))// busca quantas listas possui
                .body("name", hasItems("João da Silva", "Maria Joaquina", "Ana Júlia")) //busca os nomes da lista raiz
                .body("age[1]", is(25)) // valida se na lida 1 a idade e igual a 25
                .body("filhos.name", hasItems(Arrays.asList("Zezinho", "Luizinho")))//busca nomes na lista dentro da lista.
                .body("salary", contains(1234.5678f,2500, null))
        ;
    }

    @Test
    public void deveFazerVerificacoesAvancadas(){
        given()
                .when()
                .get("https://restapi.wcaquino.me/users")
                .then()
                .statusCode(200)
                .body("$", hasSize(3))// busca quantas listas possui
                .body("age.findAll{it <= 25}.size()", is(2) ) //busca na lista a idade menor que 25
                .body("age.findAll{it <= 25 && it > 20 }.size()", is(1) )// busca a idade menor ou igual 25 e maior que 20
                .body("findAll{it.age <= 25 && it.age > 20 }.name", hasItem("Maria Joaquina") )// busca a idade menor que 25 e maior que 20 e compara o nome.
                .body("findAll{it.age <= 25}[0].name", is("Maria Joaquina") )// localiza se o primeiro registro menor/igual a 25 é maria joaquina
                .body("findAll{it.age <= 25}[-1].name", is("Ana Júlia") )//localiza o ultimo Registro da lista e compara se e o nome
                .body("find{it.age <= 25}.name", is("Maria Joaquina") )// vai busca apenas o primeiro da lista e comparar o nome.
                .body("findAll{it.name.contains('n')}.name", hasItems("Maria Joaquina", "Ana Júlia"))
                .body("name.collect{it.toUpperCase()}", hasItem("MARIA JOAQUINA"))//valida se existe o nome e considera o maisculo
                .body("age.collect{it * 2}", hasItems(60,50,40))//pegou a idade e meultiplicou por 2
                .body("id.max()", is(3))//buscar maior id e validar se e 3
                .body("salary.min()", is(1234.5678f))// menor salario da lista
                .body("salary.findAll{it != null}.sum()", is(closeTo(3734.5678f, 0.001)))
       ;
    }

    @Test
    public void devoUnirJsonPathComJava(){
        ArrayList<String> names =
        given()
                .when()
                .get("https://restapi.wcaquino.me/users")
                .then()
                .statusCode(200)
                .extract().path("name.findAll{it.startsWith('Maria')}")
        ;
        Assert.assertEquals(1, names.size());//validar que so existe um nome Maria
        Assert.assertTrue(names.get(0).equalsIgnoreCase("Maria Joaquina"));
        Assert.assertEquals(names.get(0).toUpperCase(), "Maria joaquina".toUpperCase());
    }




}