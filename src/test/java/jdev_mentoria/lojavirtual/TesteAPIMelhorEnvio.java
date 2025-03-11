package jdev_mentoria.lojavirtual;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jdev.mentoria.lojavirtual.enums.ApiTokenIntegracao;
import jdev.mentoria.lojavirtual.model.dto.EmpresaTransporteDTO;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TesteAPIMelhorEnvio {
	
	public static void main(String[] args) throws Exception {

		/*
		 * OkHttpClient client = new OkHttpClient().newBuilder().build(); //Instanceia o
		 * objeto de requisiçao MediaType mediaType =
		 * MediaType.parse("application/json"); ////tipo do dados em json RequestBody
		 * body = RequestBody.create(mediaType, //passamos os dados, é o json com todas
		 * as informações
		 * "{\n    \"service\": 3,\n    \"agency\": 49,\n    \"from\": {\n        \"name\": \"Nome do remetente\",\n        \"phone\": \"53984470102\",\n        \"email\": \"contato@melhorenvio.com.br\",\n        \"document\": \"16571478358\",\n        \"company_document\": \"89794131000100\",\n        \"state_register\": \"123456\",\n        \"address\": \"Endereço do remetente\",\n        \"complement\": \"Complemento\",\n        \"number\": \"1\",\n        \"district\": \"Bairro\",\n        \"city\": \"São Paulo\",\n        \"country_id\": \"BR\",\n        \"postal_code\": \"01002001\",\n        \"note\": \"observação\"\n    },\n    \"to\": {\n        \"name\": \"Nome do destinatário\",\n        \"phone\": \"53984470102\",\n        \"email\": \"contato@melhorenvio.com.br\",\n        \"document\": \"25404918047\",\n        \"company_document\": \"07595604000177\",\n        \"state_register\": \"123456\",\n        \"address\": \"Endereço do destinatário\",\n        \"complement\": \"Complemento\",\n        \"number\": \"2\",\n        \"district\": \"Bairro\",\n        \"city\": \"Porto Alegre\",\n        \"state_abbr\": \"RS\",\n        \"country_id\": \"BR\",\n        \"postal_code\": \"90570020\",\n        \"note\": \"observação\"\n    },\n    \"products\": [\n        {\n            \"name\": \"Papel adesivo para etiquetas 1\",\n            \"quantity\": 3,\n            \"unitary_value\": 100.00\n        },\n        {\n            \"name\": \"Papel adesivo para etiquetas 2\",\n            \"quantity\": 1,\n            \"unitary_value\": 100.00\n        }\n    ],\n    \"volumes\": [\n        {\n            \"height\": 15,\n            \"width\": 20,\n            \"length\": 10,\n            \"weight\": 3.5\n        }\n    ],\n    \"options\": {\n        \"insurance_value\": 10.00,\n        \"receipt\": false,\n        \"own_hand\": false,\n        \"reverse\": false,\n        \"non_commercial\": false,\n        \"invoice\": {\n            \"key\": \"31190307586261000184550010000092481404848162\"\n        },\n        \"platform\": \"Nome da Plataforma\",\n        \"tags\": [\n            {\n                \"tag\": \"Identificação do pedido na plataforma, exemplo: 1000007\",\n                \"url\": \"Link direto para o pedido na plataforma, se possível, caso contrário pode ser passado o valor null\"\n            }\n        ]\n    }\n}"
		 * ); Request request = new Request.Builder().
		 * url(ApiTokenIntegracao.URL_MELHOR_ENVIO_SAND_BOX +
		 * "api/v2/me/cart")//passamos a url .method("POST", body).addHeader("Accept",
		 * "application/json") //aqui é o tipo que ele vai aceitar e responder passamos
		 * o copo .addHeader("Content-Type", "application/json")
		 * .addHeader("Authorization", "Bearer " +
		 * ApiTokenIntegracao.TOKEN_MELHOR_ENVIO_SAND_BOX) //aqui passamos o token
		 * .addHeader("User-Agent", "suporte@jdevtreinamento.com.br").build();// é um
		 * parametro que enviamos o cabeçãlho para o servidor do melhor envio Response
		 * response = client.newCall(request).execute();//aqui compila faz todo o
		 * processo de transformação do objeto, isso na linha de cima essa aqui só
		 * executa e da a resposta //nessa linha de sima ele bate la no nosso servidor
		 * de email System.out.println(response.body().string());
		 */
		
		OkHttpClient client = new OkHttpClient().newBuilder() .build();
		MediaType mediaType = MediaType.parse("application/json");
		RequestBody body = RequestBody.create(mediaType, "{ \"from\": { \"postal_code\": \"96020360\" }, \"to\": { \"postal_code\": \"01018020\" }, \"products\": [ { \"id\": \"x\", \"width\": 11, \"height\": 17, \"length\": 11, \"weight\": 0.3, \"insurance_value\": 10.1, \"quantity\": 1 }, { \"id\": \"y\", \"width\": 16, \"height\": 25, \"length\": 11, \"weight\": 0.3, \"insurance_value\": 55.05, \"quantity\": 2 }, { \"id\": \"z\", \"width\": 22, \"height\": 30, \"length\": 11, \"weight\": 1, \"insurance_value\": 30, \"quantity\": 1 } ] }");
		Request request = new Request.Builder()
		  .url(ApiTokenIntegracao.URL_MELHOR_ENVIO_SAND_BOX +"api/v2/me/shipment/calculate")
		  .method("POST", body)
		  .addHeader("Accept", "application/json")
		  .addHeader("Content-Type", "application/json")
		  .addHeader("Authorization", "Bearer " + ApiTokenIntegracao.TOKEN_MELHOR_ENVIO_SAND_BOX)
		  .addHeader("User-Agent", "suporte@jdevtreinamento.com.br")
		  .build();
		
		Response response = client.newCall(request).execute();
		//System.out.println(response.body().string()); 
		

	}

}
