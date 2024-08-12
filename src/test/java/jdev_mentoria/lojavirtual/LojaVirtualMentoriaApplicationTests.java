package jdev_mentoria.lojavirtual;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	    
	    acesso.setDescricao("ROLE_COMPRADOR");
	    
	    ObjectMapper objectMapper = new ObjectMapper();
	    
	    ResultActions retornoApi = mockMvc
	    						 .perform(MockMvcRequestBuilders.post("/salvarAcesso")  //Nós vamos fazer um Post então vamos na  classe AcessoController.java - @PostMapping(value = "**/salvarAcesso") e vimos que esta usando Post tb... então é Post mesmo
	    						 .content(objectMapper.writeValueAsString(acesso)) //esta mais abaixo no codigo objectMapper, aqui estamos passando o conteudo, acesso é o conteudo que vamos mandar salvar
	    						 .accept(MediaType.APPLICATION_JSON) //tipo de dados que nossa api trabalha
	    						 .contentType(MediaType.APPLICATION_JSON)); //tipo de conteudo
	    
	    //1System.out.println("Retorno da API: " + retornoApi.andReturn().getResponse().getContentAsString());
	    
	    /*Conveter o retorno da API para um obejto de acesso*/
	    
	    //1Acesso objetoRetorno = objectMapper.
	    //1					   readValue(retornoApi.andReturn().getResponse().getContentAsString(),
	    //1					   Acesso.class);  //<groupId>com.fasterxml.jackson.jaxrs</groupId> = pom.xml
	    
	    //1assertEquals(acesso.getDescricao(), objetoRetorno.getDescricao());
	    
	    
	    
	    
	}
	
	

	@Test
	public void testCadastroAcesso() {
		
		Acesso acesso = new Acesso();
		
		acesso.setDescricao("ROLE_ADMIN");

		/*Gravou no banco de dados*/
		acesso = acessoController.salvarAcesso(acesso).getBody(); 
		
		assertEquals(true, acesso.getId() > 0);
		
		/*Validar dados salvos da forma correta*/
		assertEquals("ROLE_ADMIN", acesso.getDescricao());
		
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
