package jdev.mentoria.lojavirtual.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

//Essa classe pessoa é uma abstração, nós nunca vamos trabalhar direto com o objeto pessoa,
//nós vamos trabalhar com clases concretas como pessoa juridica e pessoa fisica, conforme
//esta no diagrama de classes //não vamos ter a tabela fisicamente de pessoa no banco de dados
//quem vai cuidar disso pra gente é o framework usando a anotação: @inheritance

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS) //TABLE_PER_CLASS porque não vamos ter a tabela de pessoa fisicamente, vamos acessar tudo pela pessoa juridica e fisica
@SequenceGenerator(name = "seq_pessoa", sequenceName = "seq_pessoa", initialValue = 1, allocationSize = 1)
public abstract class Pessoa implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_pessoa")
	private Long id;

	@Column(nullable = false)
	private String nome;

	@Column(nullable = false)
	private String email;

	@Column(nullable = false)
	private String telefone;
	
	@Column
	private String tipoPessoa;

	//esse "pessoa" que esta aqui: mappedBy = "pessoa" se refere a esse codigo: private Pessoa pessoa que esta na classe Endereço.java
	//se apagar uma pessoa vai apagar os endereços
	//fetch = FetchType.LAZY só vai carregar os endereço se nós dermos um getendereço
	@OneToMany(mappedBy = "pessoa", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Endereco> enderecos = new ArrayList<>(); //agente ja instancia para não gerar o erro de nullpointexception
	
	public void setTipoPessoa(String tipoPessoa) {
		this.tipoPessoa = tipoPessoa;
	}
	
	public String getTipoPessoa() {
		return tipoPessoa;
	}
	

	public void setEnderecos(List<Endereco> enderecos) {
		this.enderecos = enderecos;	}

	public List<Endereco> getEnderecos() {
		return enderecos;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}
		Pessoa other = (Pessoa) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

}
