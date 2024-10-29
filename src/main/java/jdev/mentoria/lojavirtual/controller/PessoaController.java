package jdev.mentoria.lojavirtual.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import jdev.mentoria.lojavirtual.ExceptionMentoriaJava;
import jdev.mentoria.lojavirtual.model.PessoaFisica;
import jdev.mentoria.lojavirtual.model.PessoaJuridica;
import jdev.mentoria.lojavirtual.repository.PessoaRepository;
import jdev.mentoria.lojavirtual.service.PessoaUserService;
import jdev.mentoria.lojavirtual.util.ValidaCNPJ;
import jdev.mentoria.lojavirtual.util.ValidaCPF;

@RestController
public class PessoaController {
	
	@Autowired
	private PessoaRepository pesssoaRepository;
	
	@Autowired
	private PessoaUserService pessoaUserService;
	
	/*end-point é microsservicos é um API*/
	@ResponseBody
	@PostMapping(value = "**/salvarPj")     //(@RequestBody isso recebe um json
	public ResponseEntity<PessoaJuridica> salvarPj(@RequestBody @Valid PessoaJuridica pessoaJuridica) throws ExceptionMentoriaJava{
		
		if (pessoaJuridica == null) {
			throw new ExceptionMentoriaJava("Pessoa juridica nao pode ser NULL");
		}
		
		if (pessoaJuridica.getId() == null && pesssoaRepository.existeCnpjCadastrado(pessoaJuridica.getCnpj()) != null) {
			throw new ExceptionMentoriaJava("Já existe CNPJ cadastrado com o número: " + pessoaJuridica.getCnpj());
		}
		
		if (pessoaJuridica.getId() == null && pesssoaRepository.existeInsEstadualCadastrado(pessoaJuridica.getInscEstadual()) != null) {
			throw new ExceptionMentoriaJava("Já existe Inscrição estadula cadastrado com o número: " + pessoaJuridica.getInscEstadual());
		}
		
		if (!ValidaCNPJ.isCNPJ(pessoaJuridica.getCnpj())) {
			throw new ExceptionMentoriaJava("Cnpj : " + pessoaJuridica.getCnpj() + " está inválido.");
		}
		
		//não vamos salvar direto vamos usar antes a classe de serviço para fazer validações
		//pessoaJuridica = pessoaRepository.salvar
		pessoaJuridica = pessoaUserService.salvarPessoaJuridica(pessoaJuridica);
		
		//aqui quando ele retorna ele gera um json
		return new ResponseEntity<PessoaJuridica>(pessoaJuridica, HttpStatus.OK);
	}
	
	
	/*end-point é microsservicos é um API*/
	@ResponseBody
	@PostMapping(value = "**/salvarPf")
	public ResponseEntity<PessoaFisica> salvarPf(@RequestBody PessoaFisica pessoaFisica) throws ExceptionMentoriaJava{
		
		if (pessoaFisica == null) {
			throw new ExceptionMentoriaJava("Pessoa fisica não pode ser NULL");
		}
		
		if (pessoaFisica.getId() == null && pesssoaRepository.existeCpfCadastrado(pessoaFisica.getCpf()) != null) {
			throw new ExceptionMentoriaJava("Já existe CPF cadastrado com o número: " + pessoaFisica.getCpf());
		}
		
		
		if (!ValidaCPF.isCPF(pessoaFisica.getCpf())) {
			throw new ExceptionMentoriaJava("CPF : " + pessoaFisica.getCpf() + " está inválido.");
		}
		
		pessoaFisica = pessoaUserService.salvarPessoaFisica(pessoaFisica);
		
		return new ResponseEntity<PessoaFisica>(pessoaFisica, HttpStatus.OK);
	}


}
