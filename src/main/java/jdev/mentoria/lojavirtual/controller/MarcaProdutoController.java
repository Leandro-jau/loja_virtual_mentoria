package jdev.mentoria.lojavirtual.controller;

import java.util.List;

import javax.validation.Valid;

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
import jdev.mentoria.lojavirtual.model.MarcaProduto;
import jdev.mentoria.lojavirtual.repository.MarcaRepository;


@Controller
@RestController
public class MarcaProdutoController {
	
	@Autowired
	private MarcaRepository marcaRepository;
	
	@ResponseBody 
	@PostMapping(value = "**/salvarMarca") 
	public ResponseEntity<MarcaProduto> salvarMarca(@RequestBody @Valid MarcaProduto marcaProduto) throws ExceptionMentoriaJava { /*Recebe o JSON e converte pra Objeto*/
		
		if (marcaProduto.getId() == null) {
		  List<MarcaProduto> acessos = marcaRepository.buscarMarcaDesc(marcaProduto.getNomeDesc().toUpperCase());
		  
		  if (!acessos.isEmpty()) {
			  throw new ExceptionMentoriaJava("Já existe Marca com a descrição: " + marcaProduto.getNomeDesc());
		  }
		}
		
		
		MarcaProduto marcaProdutoSalvo = marcaRepository.save(marcaProduto);
		
		return new ResponseEntity<MarcaProduto>(marcaProdutoSalvo, HttpStatus.OK);
	}
	
	
	
	@ResponseBody /*Poder dar um retorno da API*/
	@PostMapping(value = "**/deleteMarca") /*Mapeando a url para receber JSON*/
	public ResponseEntity<String> deleteMarca(@RequestBody MarcaProduto marcaProduto) { /*Recebe o JSON e converte pra Objeto*/
		
		marcaRepository.deleteById(marcaProduto.getId());
		
		return new ResponseEntity<String>("Marca Removida",HttpStatus.OK);
	}
	

	@ResponseBody
	@DeleteMapping(value = "**/deleteMarcaPorId/{id}")
	public ResponseEntity<String> deleteMarcaPorId(@PathVariable("id") Long id) { 
		
		marcaRepository.deleteById(id);
		
		return new ResponseEntity<String>("Marca Removid",HttpStatus.OK);
	}
	
	
	
	@ResponseBody
	@GetMapping(value = "**/obterMarca/{id}")
	public ResponseEntity<MarcaProduto> obterMarca(@PathVariable("id") Long id) throws ExceptionMentoriaJava { 
		
		MarcaProduto marcaProduto = marcaRepository.findById(id).orElse(null);
		
		if (marcaProduto == null) {
			throw new ExceptionMentoriaJava("Não encontrou a Marca com código: " + id);
		}
		
		return new ResponseEntity<MarcaProduto>(marcaProduto,HttpStatus.OK);
	}
	
	
	
	@ResponseBody
	@GetMapping(value = "**/buscarMarcaPorDesc/{desc}")
	public ResponseEntity<List<MarcaProduto>> buscarMarcaPorDesc(@PathVariable("desc") String desc) { 
		
		List<MarcaProduto> acesso = marcaRepository.buscarMarcaDesc(desc.toUpperCase());
		
		return new ResponseEntity<List<MarcaProduto>>(acesso,HttpStatus.OK);
	}

}
