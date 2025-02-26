package jdev.mentoria.lojavirtual.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import jdev.mentoria.lojavirtual.ExceptionMentoriaJava;
import jdev.mentoria.lojavirtual.model.CupDesc;
import jdev.mentoria.lojavirtual.repository.CupDescontoRepository;


@RestController
public class CupDescontoController {
	
	@Autowired
	private CupDescontoRepository cupDescontoRepository;
	
	@ResponseBody
	@DeleteMapping(value = "**/deleteCupomPorId/{id}")
	public ResponseEntity<String> deleteMarcaPorId(@PathVariable("id") Long id) { 
		
		cupDescontoRepository.deleteById(id);
		
		return new ResponseEntity<String>("Cupom Produto Removido",HttpStatus.OK);
	}
	
	@ResponseBody
	@GetMapping(value = "**/obterCupom/{id}")
	public ResponseEntity<CupDesc> obterMarca(@PathVariable("id") Long id) throws ExceptionMentoriaJava { 
		
		CupDesc cupDesc = cupDescontoRepository.findById(id).orElse(null);
		
		if (cupDesc == null) {
			throw new ExceptionMentoriaJava("Não encontrou Cupom Produto com código: " + id);
		}
		
		return new ResponseEntity<CupDesc>(cupDesc,HttpStatus.OK);
	}
	
	@ResponseBody 
	@PostMapping(value = "**/salvarCupDesc") 
	public ResponseEntity<CupDesc> salvarCupDesc(@RequestBody @Valid  CupDesc cupDes) throws ExceptionMentoriaJava { /*Recebe o JSON e converte pra Objeto*/
		
		CupDesc cupDesc2 = cupDescontoRepository.save(cupDes);
		return new ResponseEntity<CupDesc>(cupDesc2, HttpStatus.OK);
	}
	
	@ResponseBody
	@GetMapping(value = "**/listaCupomDesc/{idEmpresa}")
	public ResponseEntity<List<CupDesc>> listaCupomDesc(@PathVariable("idEmpresa") Long idEmpresa){
		
		return new ResponseEntity<List<CupDesc>>(cupDescontoRepository.cupDescontoPorEmpresa(idEmpresa), HttpStatus.OK);
	}
	
	@ResponseBody
	@GetMapping(value = "**/listaCupomDesc")
	public ResponseEntity<List<CupDesc>> listaCupomDesc(){
		
		return new ResponseEntity<List<CupDesc>>(cupDescontoRepository.findAll() , HttpStatus.OK);
	}




}
