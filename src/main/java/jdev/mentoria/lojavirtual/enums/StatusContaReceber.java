package jdev.mentoria.lojavirtual.enums;

public enum StatusContaReceber {
	COBRANCA("Pagar"),
	VENCIDA("Vencida"),
	ABERTA("Aberta"),
	QUITADA("Quitada");
	
	//obrigatorio ter a descrição	
	private String descricao;
	
	//obrigatorio ter o construtor
	private StatusContaReceber(String descricao) {
		this.descricao = descricao;
	}
	
	public String getDescricao() {
		return descricao;
	}
	
	@Override
	public String toString() {
		return this.descricao;
	}

}
