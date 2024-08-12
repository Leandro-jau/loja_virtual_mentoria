package jdev.mentoria.lojavirtual.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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
	@PostMapping(value = "**/salvarAcesso") /*Mapeando a url para receber JSON usamos ** para ignorar localhost:8080projeto etc... de qualquerlugar quevier osalvarAcesso ele vai interceptar*/
	//public Acesso salvarAcesso(Acesso acesso) { xxxx
	//public Acesso salvarAcesso(Acesso acesso) {
	public ResponseEntity<Acesso> salvarAcesso(@RequestBody Acesso acesso) { /*Recebe o JSON e converte pra Objeto, ResponseEntity<Acesso> = vai ser uma resposta do tipo acesso*/
		
		Acesso acessoSalvo = acessoService.save(acesso);
		
		return new ResponseEntity<Acesso>(acessoSalvo, HttpStatus.OK);
		
		//return acessoService.save(acesso);
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

}
