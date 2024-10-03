package jdev_mentoria.lojavirtual;

import java.util.Calendar;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jdev.mentoria.lojavirtual.ExceptionMentoriaJava;
import jdev.mentoria.lojavirtual.LojaVirtualMentoriaApplication;
import jdev.mentoria.lojavirtual.controller.AcessoController;
import jdev.mentoria.lojavirtual.model.Acesso;
import jdev.mentoria.lojavirtual.repository.AcessoRepository;
import jdev.mentoria.lojavirtual.service.AcessoService;
import junit.framework.TestCase;


@SpringBootTest(classes = LojaVirtualMentoriaApplication.class)
class LojaVirtualMentoriaApplicationTests extends TestCase { //TestCse é uma classe do jUnit
	
	//@Autowired
	//private AcessoService acessoService;
	
	@Autowired //**** com isso não estavamos usando o acessoService
	private AcessoRepository acessoRepository;
	
	@Autowired
	private AcessoController acessoController; 
	
	//precisamos simular todo esse ambiente precisamos pegar um objeto do spring onde ele pega as informações da aplicação
	//que esta rodando no nosso projeto
	@Autowired
	private WebApplicationContext wac;
	
	/*Teste do end-point de salvar*/
	@Test
	public void testRestApiCadastroAcesso() throws JsonProcessingException, Exception {
		
	    DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(this.wac); //this.wac = pegamos o contexto da aplicação
	    MockMvc mockMvc = builder.build(); //builder.build() = vai trazer todas as informações da aplicação para gente;
	    									//esses objetos acima que vão ser os responsavei por fazer os testes trazer requisição e vão dar o retorno para nós
	    Acesso acesso = new Acesso();
	    
	    acesso.setDescricao("ROLE_COMPRADOR" + Calendar.getInstance().getTimeInMillis());
	    
	    ObjectMapper objectMapper = new ObjectMapper();
	    
	    ResultActions retornoApi = mockMvc
	    						 .perform(MockMvcRequestBuilders.post("/salvarAcesso")  //Nós vamos fazer um Post então vamos na  classe AcessoController.java - @PostMapping(value = "**/salvarAcesso") e vimos que esta usando Post tb... então é Post mesmo
	    						 .content(objectMapper.writeValueAsString(acesso)) //esta mais abaixo no codigo objectMapper, aqui estamos passando o conteudo, acesso é o conteudo que vamos mandar salvar
	    						 .accept(MediaType.APPLICATION_JSON) //tipo de dados que nossa api trabalha
	    						 .contentType(MediaType.APPLICATION_JSON)); //tipo de conteudo
	    
	    System.out.println("Retorno da API: " + retornoApi.andReturn().getResponse().getContentAsString()); //só para ver o json é o retorno da resposta em string
	    
	    /*vamos trabalhar o retornoAPI etõ precisamos Conveter o retorno da API para um obejto de acesso porque ele salva e retorna um objeto, isso porque na classe acessocontroller o metodo public ResponseEntity<Acesso> retorna um json return new ResponseEntity<Acesso>(acessoSalvo, HttpStatus.OK);*/
	    
	    Acesso objetoRetorno = objectMapper.
	    					   readValue(retornoApi.andReturn().getResponse().getContentAsString(), //essa é a mesma linha de codigo acima explicada
	    					   Acesso.class);  //<groupId>com.fasterxml.jackson.jaxrs</groupId> = pom.xml  Acesso.class é o tipo da classe
	    
	    assertEquals(acesso.getDescricao(), objetoRetorno.getDescricao()); //agora esses dois devem ter o mesmo valor
	    
	    
	    
	    
	}
	
	@Test
	public void testRestApiDeleteAcesso() throws JsonProcessingException, Exception {
		
	    DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(this.wac);
	    MockMvc mockMvc = builder.build();
	    
	    Acesso acesso = new Acesso();
	    
	    acesso.setDescricao("ROLE_TESTE_DELETE");
	    
	    acesso = acessoRepository.save(acesso);
	    
	    ObjectMapper objectMapper = new ObjectMapper();
	    
	    ResultActions retornoApi = mockMvc
	    						 .perform(MockMvcRequestBuilders.post("/deleteAcesso")
	    						 .content(objectMapper.writeValueAsString(acesso))
	    						 .accept(MediaType.APPLICATION_JSON)
	    						 .contentType(MediaType.APPLICATION_JSON));
	    //ele vai trazer esse retorno da classe acessocontroller.java return new ResponseEntity("Acesso Removido",HttpStatus.OK);
	    System.out.println("Retorno da API: " + retornoApi.andReturn().getResponse().getContentAsString());// esse é isso HttpStatus.OK da classse acessocontroller
	    System.out.println("Status de retorno: " + retornoApi.andReturn().getResponse().getStatus());
	    
	    assertEquals("Acesso Removido", retornoApi.andReturn().getResponse().getContentAsString());
	    assertEquals(200, retornoApi.andReturn().getResponse().getStatus());//outro teste codigo 200 é quando a resposta é feita com sucesso
	    
	    
	}
	
	@Test
	public void testRestApiDeletePorIDAcesso() throws JsonProcessingException, Exception {
		
	    DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(this.wac);
	    MockMvc mockMvc = builder.build();
	    
	    Acesso acesso = new Acesso();
	    
	    acesso.setDescricao("ROLE_TESTE_DELETE_ID");
	    
	    acesso = acessoRepository.save(acesso);
	    
	    ObjectMapper objectMapper = new ObjectMapper();
	    
	    ResultActions retornoApi = mockMvc
	    						 .perform(MockMvcRequestBuilders.delete("/deleteAcessoPorId/" + acesso.getId())
	    						 .content(objectMapper.writeValueAsString(acesso))
	    						 .accept(MediaType.APPLICATION_JSON)
	    						 .contentType(MediaType.APPLICATION_JSON));
	    
	    System.out.println("Retorno da API: " + retornoApi.andReturn().getResponse().getContentAsString());
	    System.out.println("Status de retorno: " + retornoApi.andReturn().getResponse().getStatus());
	    
	    assertEquals("Acesso Removido", retornoApi.andReturn().getResponse().getContentAsString());
	    assertEquals(200, retornoApi.andReturn().getResponse().getStatus());
	    
	    
	}
	
	@Test
	public void testRestApiObterAcessoID() throws JsonProcessingException, Exception {
		
	    DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(this.wac);
	    MockMvc mockMvc = builder.build();
	    
	    Acesso acesso = new Acesso();
	    
	    acesso.setDescricao("ROLE_OBTER_ID"); //mudamos os nomes só para ver no console a descrição do que estou testando
	    
	    acesso = acessoRepository.save(acesso);
	    
	    ObjectMapper objectMapper = new ObjectMapper();
	    
	    ResultActions retornoApi = mockMvc
	    						 .perform(MockMvcRequestBuilders.get("/obterAcesso/" + acesso.getId()) //agora mudamos o verbo para get
	    						 .content(objectMapper.writeValueAsString(acesso))
	    						 .accept(MediaType.APPLICATION_JSON)
	    						 .contentType(MediaType.APPLICATION_JSON));
	    
	    assertEquals(200, retornoApi.andReturn().getResponse().getStatus());
	    
	    //getResponse() = pegamos a resposta da API -- readValue RETORNA UM OBJETO GENERICO por isso passamos o tipo dele  Acesso.class
	    Acesso acessoRetorno = objectMapper.readValue(retornoApi.andReturn().getResponse().getContentAsString(), Acesso.class); //Acesso.class = ele retorna um tipo generico por isso passamos esse tipo transformamos ele em uma classe de acesso
	    
	    assertEquals(acesso.getDescricao(), acessoRetorno.getDescricao());
	    
	    assertEquals(acesso.getId(), acessoRetorno.getId());
	    
	}
	
	@Test
	public void testRestApiObterAcessoDesc() throws JsonProcessingException, Exception {
		
	    DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(this.wac);
	    MockMvc mockMvc = builder.build();
	    
	    Acesso acesso = new Acesso();
	    
	    acesso.setDescricao("ROLE_TESTE_OBTER_LIST");
	    
	    acesso = acessoRepository.save(acesso);
	    
	    ObjectMapper objectMapper = new ObjectMapper();
	    
	    ResultActions retornoApi = mockMvc //**aqui estamos buscando 
	    						 .perform(MockMvcRequestBuilders.get("/buscarPorDesc/OBTER_LIST") //aqui estamos passando uma parte do que esta sendo salvo
	    						 .content(objectMapper.writeValueAsString(acesso))
	    						 .accept(MediaType.APPLICATION_JSON)
	    						 .contentType(MediaType.APPLICATION_JSON));
	    
	    assertEquals(200, retornoApi.andReturn().getResponse().getStatus());
	    
	    
	    List<Acesso> retornoApiList = objectMapper. //**aqui estamos convertendo para uma lista
	    							     readValue(retornoApi.andReturn()
	    									.getResponse().getContentAsString(),
	    									 new TypeReference<List<Acesso>> () {});
	    //nesse teste é 1 porque sabemos que estamos setando apenas 1 no banco
	    assertEquals(1, retornoApiList.size());
	    
	    assertEquals(acesso.getDescricao(), retornoApiList.get(0).getDescricao()); //pegamos na posição 0;
	    
	    
	    acessoRepository.deleteById(acesso.getId());
	    
	}
	

	@Test
	public void testCadastroAcesso() throws ExceptionMentoriaJava {
		
		String descacesso = "ROLE_ADMIN" +  Calendar.getInstance().getTimeInMillis();
		
		Acesso acesso = new Acesso();			
		
		acesso.setDescricao(descacesso);

		/*Gravou no banco de dados*/
		acesso = acessoController.salvarAcesso(acesso).getBody(); 
		
		assertEquals(true, acesso.getId() > 0);
		
		/*Validar dados salvos da forma correta*/
		assertEquals(descacesso, acesso.getDescricao());
		
		/*Teste de carregamento*/		
		Acesso acesso2 = acessoRepository.findById(acesso.getId()).get();
		
		assertEquals(acesso.getId(), acesso2.getId());
		
		/*Teste de delete*/
		
		acessoRepository.deleteById(acesso2.getId());
		
		acessoRepository.flush(); /*Roda esse SQL de delete no banco de dados*/
		
		Acesso acesso3 = acessoRepository.findById(acesso2.getId()).orElse(null); //findById(acesso2.getId()) = vamos tentar buscar um codigo que foi apagado do banco e para não dar excessão o spring usa "orElse" = seão tiver me retorna nulo
		
		assertEquals(true, acesso3 == null); //se eu pedi para apagar ele tem que estar nulo
		
        /*Teste de query*/
		
		acesso = new Acesso();
		
		acesso.setDescricao("ROLE_ALUNO");
		
		acesso = acessoController.salvarAcesso(acesso).getBody();
		
		List<Acesso> acessos = acessoRepository.buscarAcessoDesc("ALUNO".trim().toUpperCase());
		
		assertEquals(1, acessos.size());
		
		acessoRepository.deleteById(acesso.getId());
		
		
	}

}
