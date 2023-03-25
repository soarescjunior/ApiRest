package rest;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

import org.hamcrest.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;


import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class OlaMundoTest {

    @Test
        public void testOlaMundo(){
        Response response = request(Method.GET, "https://restapi.wcaquino.me/ola");
        Assert.assertEquals(response.getBody().asString(), ("Ola Mundo!"));
        Assert.assertTrue(response.statusCode() == 200);
        Assert.assertEquals(200, response.statusCode());

        ValidatableResponse validacao = response.then();
        validacao.statusCode(200);
    }

    @Test

    public void devoConhecerOutrasFormasRestAssured(){
        Response response = request(Method.GET, "https://restapi.wcaquino.me/ola");
        ValidatableResponse validacao = response.then();
        validacao.statusCode(200);

        get("https://restapi.wcaquino.me/ola").then().statusCode(200);

        given()//Pre Condições
             .when() // ação
                .get("https://restapi.wcaquino.me/ola")
             .then() //Asertiva
                .statusCode(200);
    }

    @Test

    public void devoConhecerMatchersHamcrest(){
        assertThat("Maria", Matchers.is("Maria"));
        assertThat(128, Matchers.is(128));
        assertThat(128, Matchers.isA(Integer.class));
        assertThat(128.2, Matchers.isA(double.class));
        assertThat(128.2, Matchers.greaterThan(120.0));
        assertThat(128.2, Matchers.lessThan(130.0));

        List<Integer> impares = Arrays.asList(1,3,5,7,9);
        assertThat(impares, hasSize(5)); // valida a quantidade de ites.
        assertThat(impares, contains(1,3,5,7,9)); // valida todos os itens na mesma hordem
        assertThat(impares, containsInAnyOrder(1,3,5,7,9)); // valida todos independete da ordem
        assertThat(impares, hasItem(1)); // valida se tem um item da lista
        assertThat(impares, hasItems(1,2)); // valida mais de um item da lisata

        assertThat("Maria", is(not("Joao"))); // compara igualdade de itens
        assertThat("Maria", not("Joao")); // compara igualdade de itens sem os "is"
        assertThat("Maria", anyOf(is("Joaao"), is("Joaquina"))); // compara se alguns dos itens é igual ao atual
        assertThat("Maria", allOf(startsWith("Ma"), endsWith("ia"), containsString("r")));// compara o inicio, fim e se contem no texto.
    }

    @Test
    public void devoValidarOBody(){
        given()//Pre Condições
        .when() // ação
                .get("https://restapi.wcaquino.me/ola")
        .then() //Asertiva
                .statusCode(200)
                .body(is("Ola Mundo!")) //validar o corpo do texto.
                .body(containsString("Mundo")) //valida se o corpo do texto contem a String
                .body(is(not("Teste"))) //valida se o corpo do texto não existe
                .body(is(notNullValue())); // valida se o corpo do texto esta não vazio.

    }

}
