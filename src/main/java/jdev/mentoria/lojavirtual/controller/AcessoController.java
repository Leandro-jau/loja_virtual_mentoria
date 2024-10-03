package jdev.mentoria.lojavirtual.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import jdev.mentoria.lojavirtual.ExceptionMentoriaJava;
import jdev.mentoria.lojavirtual.model.Acesso;
import jdev.mentoria.lojavirtual.repository.AcessoRepository;
import jdev.mentoria.lojavirtual.service.AcessoService;

@Controller
@RestController //precisa disso para trabalhar com API, porque nosso backend vai ser todo em Rest (recebe json e retorna)
public class AcessoController {
	
	@Autowired
	private AcessoService acessoService;
	
	@Autowired
	private AcessoRepository acessoRepository;
	
	@ResponseBody /*Poder dar um retorno a API */
	//@RequestMapping em alguns lugares pode estar usando isso no lugar de PostMapping vai ser a mesma coisa
	@PostMapping(value = "**/salvarAcesso") /*Mapeando a url para receber JSON usamos ** para ignorar localhost:8080projeto etc... de qualquerlugar quevier osalvarAcesso ele vai interceptar*/
	public ResponseEntity<Acesso> salvarAcesso(@RequestBody Acesso acesso) throws ExceptionMentoriaJava { /*Recebe o JSON e converte pra Objeto, ResponseEntity<Acesso> = vai ser uma resposta do tipo acesso*/
		
		//se tiver salvando um novo registro agente vai consultar no banco para saber se ja existe algum acesso com a mesma descição e damos o alerta
		if (acesso.getId() == null) {
			  //buscamos no banco
			  List<Acesso> acessos = acessoRepository.buscarAcessoDesc(acesso.getDescricao().toUpperCase());
			  //se acessos diferente de vazio
			  if (!acessos.isEmpty()) {
				  throw new ExceptionMentoriaJava("Já existe Acesso com a descrição: " + acesso.getDescricao());
			  }
			}
		
		Acesso acessoSalvo = acessoService.save(acesso);
		
		return new ResponseEntity<Acesso>(acessoSalvo, HttpStatus.OK);
				
	}
	
	@ResponseBody /*Poder dar um retorno da API*/
	@PostMapping(value = "**/deleteAcesso") /*Mapeando a url para receber JSON*/
	//depois eu testo isso
	//public ResponseEntity<String> deleteAcesso(@RequestBody Acesso acesso) { 

	//    if(acessoRepository.existsById(acesso.getId())) {
	//        acessoRepository.deleteById(acesso.getId());
	 //       return new ResponseEntity<>("Acesso removido com sucesso.", HttpStatus.OK);
	 //   } else {
	 //       return new ResponseEntity<>("ID não encontrado.", HttpStatus.NOT_FOUND);
	 //   }
	//}
	public ResponseEntity<?> deleteAcesso(@RequestBody Acesso acesso) { /*Recebe o JSON e converte pra Objeto*/
		
		acessoRepository.deleteById(acesso.getId());
		
		return new ResponseEntity("Acesso Removido",HttpStatus.OK);
	}
	
	//@Secured({ "ROLE_GERENTE", "ROLE_ADMIN" })
	@ResponseBody
	@DeleteMapping(value = "**/deleteAcessoPorId/{id}") //@DeleteMapping isso chamamos de verbos, igual, post get etc...
	public ResponseEntity<?> deleteAcessoPorId(@PathVariable("id") Long id) { //agora estamos passando só o id não o objeto inteiro aqui não estamos passando mais em json aqui estamos passando por url
		
		acessoRepository.deleteById(id);
		
		return new ResponseEntity("Acesso Removido",HttpStatus.OK);
	}
	
	@ResponseBody
	@GetMapping(value = "**/obterAcesso/{id}")
	public ResponseEntity<Acesso> obterAcesso(@PathVariable("id") Long id) throws ExceptionMentoriaJava { 
		
		//Acesso acesso = acessoRepository.findById(id).get(); era ssim com o get depois que criamos a classe ExceptionMentoriaJava, ficou igual ao codigo abaixo
		Acesso acesso = acessoRepository.findById(id).orElse(null);//se ele nao encontrar vou passar nulo
		
		if (acesso == null) {
			//quando uso throw eu jogo o a mensagem de erro pra cima
			throw new ExceptionMentoriaJava("Não encontrou Acesso com código: " + id);
		}
		
		return new ResponseEntity<Acesso>(acesso,HttpStatus.OK);
		
		
	}
	
	@ResponseBody
	@GetMapping(value = "**/buscarPorDesc/{desc}")
	public ResponseEntity<List<Acesso>> buscarPorDesc(@PathVariable("desc") String desc) { 
		//para nao ter problema com caractere maiusculo/minusculo usamos o toUpperCase()
		List<Acesso> acesso = acessoRepository.buscarAcessoDesc(desc.toUpperCase());
		
		return new ResponseEntity<List<Acesso>>(acesso,HttpStatus.OK);
	}

}
