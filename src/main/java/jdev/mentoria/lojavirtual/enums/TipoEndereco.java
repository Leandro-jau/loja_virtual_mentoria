package jdev.mentoria.lojavirtual.enums;

public enum TipoEndereco {

	//Enums serve para especificar campos tipo sexo = masculino
	//Variaveis
	COBRANCA("Cobrança"),
	ENTREGA("Entrega");

	//Sempre que se trabalha com enum ele exige um campo para descrição
	private String descricao;

	//Construtor obrigatório
	private TipoEndereco(String descricao) {
	  this.descricao = descricao;
	}


	public String getDescricao() {
		return descricao;
	}

	//Esse tostring vai mostrar o que ele receber no constructor, se deixar sem essa definição
	//ele não vai saber o que mostrar.
	//Depois temos que mapear ele na classe endereço.java @Enumerated(EnumType.STRING)
	//private TipoEndereco tipoEndereco;


	@Override
	public String toString() {
		return this.descricao;
	}

}
