package jdev_mentoria.lojavirtual;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jdev.mentoria.lojavirtual.LojaVirtualMentoriaApplication;
import jdev.mentoria.lojavirtual.controller.AcessoController;
import jdev.mentoria.lojavirtual.model.Acesso;
import jdev.mentoria.lojavirtual.repository.AcessoRepository;
import jdev.mentoria.lojavirtual.service.AcessoService;


@SpringBootTest(classes = LojaVirtualMentoriaApplication.class)
class LojaVirtualMentoriaApplicationTests {
	
	@Autowired
	private AcessoService acessoService;
	
	//@Autowired **** com isso não estavamos usando o acessoService
	//private AcessoRepository acessoRepository; ****
	
	@Autowired
	private AcessoController acessoController; 

	@Test
	public void testCadastroAcesso() {
		
		Acesso acesso = new Acesso();
		
		acesso.setDescricao("ROLE_ADMIN");

		//acessoRepository.save(acesso); ****
		//acessoService.save(acesso); xxxx
		acessoController.salvarAcesso(acesso); 
		
	}

}
