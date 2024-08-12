package jdev_mentoria.lojavirtual;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jdev.mentoria.lojavirtual.LojaVirtualMentoriaApplication;
import jdev.mentoria.lojavirtual.controller.AcessoController;
import jdev.mentoria.lojavirtual.model.Acesso;
import jdev.mentoria.lojavirtual.repository.AcessoRepository;
import jdev.mentoria.lojavirtual.service.AcessoService;
import junit.framework.TestCase;


@SpringBootTest(classes = LojaVirtualMentoriaApplication.class)
class LojaVirtualMentoriaApplicationTests extends TestCase { //TestCse é uma classe do jUnit
	
	@Autowired
	private AcessoService acessoService;
	
	@Autowired //**** com isso não estavamos usando o acessoService
	private AcessoRepository acessoRepository;
	
	@Autowired
	private AcessoController acessoController; 

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
