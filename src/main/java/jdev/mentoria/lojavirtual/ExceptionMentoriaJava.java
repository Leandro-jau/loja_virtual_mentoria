package jdev.mentoria.lojavirtual;

public class ExceptionMentoriaJava extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	//o construtor recebe uma string de mensagem, e no super passamos msgErro
	public ExceptionMentoriaJava(String msgErro) {
		super(msgErro);
	}

}
