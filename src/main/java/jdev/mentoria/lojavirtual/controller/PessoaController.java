package jdev.mentoria.lojavirtual.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import jdev.mentoria.lojavirtual.ExceptionMentoriaJava;
import jdev.mentoria.lojavirtual.enums.TipoPessoa;
import jdev.mentoria.lojavirtual.model.Endereco;
import jdev.mentoria.lojavirtual.model.PessoaFisica;
import jdev.mentoria.lojavirtual.model.PessoaJuridica;
import jdev.mentoria.lojavirtual.model.dto.CepDTO;
import jdev.mentoria.lojavirtual.model.dto.ConsultaCnpjDto;
import jdev.mentoria.lojavirtual.repository.EnderecoRepository;
import jdev.mentoria.lojavirtual.repository.PessoaFisicaRepository;
import jdev.mentoria.lojavirtual.repository.PessoaRepository;
import jdev.mentoria.lojavirtual.service.PessoaUserService;
import jdev.mentoria.lojavirtual.service.ServiceContagemAcessoApi;
import jdev.mentoria.lojavirtual.util.ValidaCNPJ;
import jdev.mentoria.lojavirtual.util.ValidaCPF;

@RestController
public class PessoaController {
	
	@Autowired
	private PessoaRepository pesssoaRepository;
	
	@Autowired
	private PessoaUserService pessoaUserService;
	
	@Autowired
	private EnderecoRepository enderecoRepository;
	
	@Autowired
	private PessoaFisicaRepository pesssoaFisicaRepository;
	
	@Autowired
	private ServiceContagemAcessoApi serviceContagemAcessoApi;
	
	
	
	//Isso é uma API
	@ResponseBody
	@GetMapping(value = "**/consultaPfNome/{nome}")
	public ResponseEntity<List<PessoaFisica>> consultaPfNome(@PathVariable("nome") String nome) {
		
		List<PessoaFisica> fisicas = pesssoaFisicaRepository.pesquisaPorNomePF(nome.trim().toUpperCase());
		
		//jdbcTemplate.execute("begin; update tabela_acesso_end_potin set qtd_acesso_end_point = qtd_acesso_end_point + 1 where nome_end_point = 'END-POINT-NOME-PESSOA-FISICA'; commit;");
		serviceContagemAcessoApi.atualizaAcessoEndPointPF();
		return new ResponseEntity<List<PessoaFisica>>(fisicas, HttpStatus.OK);
	}
	
	@ResponseBody
	@GetMapping(value = "**/consultaNomePJ/{nome}")
	public ResponseEntity<List<PessoaJuridica>> consultaNomePJ(@PathVariable("nome") String nome) {
		
		List<PessoaJuridica> fisicas = pesssoaRepository.pesquisaPorNomePJ(nome.trim().toUpperCase());
		
		return new ResponseEntity<List<PessoaJuridica>>(fisicas, HttpStatus.OK);
	}
	
	@ResponseBody
	@GetMapping(value = "**/consultaCnpjPJ/{cnpj}")
	public ResponseEntity<List<PessoaJuridica>> consultaCnpjPJ(@PathVariable("cnpj") String cnpj) {
		
		List<PessoaJuridica> fisicas = pesssoaRepository.existeCnpjCadastradoList(cnpj.trim().toUpperCase());
		
		return new ResponseEntity<List<PessoaJuridica>>(fisicas, HttpStatus.OK);
	}
	
	
	@ResponseBody
	@GetMapping(value = "**/consultaPfCpf/{cpf}")
	public ResponseEntity<List<PessoaFisica>> consultaPfCpf(@PathVariable("cpf") String cpf) {
		
		List<PessoaFisica> fisicas = pesssoaFisicaRepository.pesquisaPorCpfPF(cpf);
		
		return new ResponseEntity<List<PessoaFisica>>(fisicas, HttpStatus.OK);
	}
	
	@ResponseBody
	@GetMapping(value = "**/consultaCep/{cep}")
	public ResponseEntity<CepDTO> consultaCep(@PathVariable("cep") String cep){
		
	  return new ResponseEntity<CepDTO>(pessoaUserService.consultaCep(cep), HttpStatus.OK);
		
	}
	
	@ResponseBody
	@GetMapping(value = "**/consultaCnpjReceitaWs/{cnpj}")
	public ResponseEntity<ConsultaCnpjDto> consultaCnpjReceitaWs(@PathVariable("cnpj") String cnpj){
		
	  return new ResponseEntity<ConsultaCnpjDto>(pessoaUserService.consultaCnpjReceitaWS(cnpj), HttpStatus.OK);
		
	}
	
	/*end-point é microsservicos é um API*/
	@ResponseBody
	@PostMapping(value = "**/salvarPj")     //(@RequestBody isso recebe um json
	public ResponseEntity<PessoaJuridica> salvarPj(@RequestBody @Valid PessoaJuridica pessoaJuridica) throws ExceptionMentoriaJava{
		
		if (pessoaJuridica == null) {
			throw new ExceptionMentoriaJava("Pessoa juridica nao pode ser NULL");
		}
		
		if (pessoaJuridica.getTipoPessoa() == null) {
			throw new ExceptionMentoriaJava("Informe o tipo Jurídico ou Fornecedor da Loja");
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
		
		if (pessoaJuridica.getId() == null || pessoaJuridica.getId() <= 0) {
			
			for (int p = 0; p < pessoaJuridica.getEnderecos().size(); p++) {
				
				CepDTO cepDTO = pessoaUserService.consultaCep(pessoaJuridica.getEnderecos().get(p).getCep());
				
				//cep não precisa setar que ja vai estar vindo por json atraves do end point
				//o webservice não retorna o numero da casa, mas agente passa no json do postman então nao substituimos ele
				pessoaJuridica.getEnderecos().get(p).setBairro(cepDTO.getBairro());
				pessoaJuridica.getEnderecos().get(p).setCidade(cepDTO.getLocalidade());
				pessoaJuridica.getEnderecos().get(p).setComplemento(cepDTO.getComplemento());
				pessoaJuridica.getEnderecos().get(p).setRuaLogra(cepDTO.getLogradouro());
				pessoaJuridica.getEnderecos().get(p).setUf(cepDTO.getUf());
				
			}
		}else {
			
			for (int p = 0; p < pessoaJuridica.getEnderecos().size(); p++) {
				
				Endereco enderecoTemp =  enderecoRepository.findById(pessoaJuridica.getEnderecos().get(p).getId()).get();
				
				if (!enderecoTemp.getCep().equals(pessoaJuridica.getEnderecos().get(p).getCep())) {
					
					CepDTO cepDTO = pessoaUserService.consultaCep(pessoaJuridica.getEnderecos().get(p).getCep());
					
					pessoaJuridica.getEnderecos().get(p).setBairro(cepDTO.getBairro());
					pessoaJuridica.getEnderecos().get(p).setCidade(cepDTO.getLocalidade());
					pessoaJuridica.getEnderecos().get(p).setComplemento(cepDTO.getComplemento());
					pessoaJuridica.getEnderecos().get(p).setRuaLogra(cepDTO.getLogradouro());
					pessoaJuridica.getEnderecos().get(p).setUf(cepDTO.getUf());
				}
			}
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
		
		if (pessoaFisica.getTipoPessoa() == null) {
			pessoaFisica.setTipoPessoa(TipoPessoa.FISICA.name());
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
