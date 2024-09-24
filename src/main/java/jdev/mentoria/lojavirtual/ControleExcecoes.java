package jdev.mentoria.lojavirtual;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jdev.mentoria.lojavirtual.model.dto.ObjetoErroDTO;

//Ele não é uma classe do banco, é usado só em tempo de execução por isso fica nesse pacote, então ele se chama DTO ou Bean
//depois que ele passa das validações/excessões das classes do pacote security, vamos precisamos ter uma classe que controla 
//as excessóes na nossa aplicação, depois que o spring security libera a validação antes de cair em algum controle ou regra nossa 
//ele vai passar para essa classe de excessões


@RestControllerAdvice
@ControllerAdvice
public class ControleExcecoes extends ResponseEntityExceptionHandler {
	
	/*Captura execeçoes do projeto*/
	@ExceptionHandler({Exception.class, RuntimeException.class, Throwable.class}) //estamos capturando essas exeções
	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		
		ObjetoErroDTO objetoErroDTO = new ObjetoErroDTO();
		
		String msg = "";
		
		//se passarmos algum argumento errado para a nossa API
		if (ex instanceof MethodArgumentNotValidException) {
			//criamos uma lista para tratar melhor o erro para tratarmos
			List<ObjectError> list = ((MethodArgumentNotValidException) ex).getBindingResult().getAllErrors();//fazemos uma conversão aqui ((MethodArgumentNotValidException) ex) vai retornar uma lista de object erro
			
			for (ObjectError objectError : list) {
				msg += objectError.getDefaultMessage() + "\n";
			}
		}else {
			//se não for uma instancia da classe: MethodArgumentNotValidException damos uma mensagem mais genérica
			msg = ex.getMessage();
		}
		
		//setamos o erro que esta na variavel mensagem
		objetoErroDTO.setError(msg);
		objetoErroDTO.setCode(status.value() + " ==> " + status.getReasonPhrase()); //retorna o codigo do status da solicitação esse status esta no inicio do metodo HttpStatus status
		
		ex.printStackTrace();
		
		return new ResponseEntity<Object>(objetoErroDTO, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	
	/*Captura erro na parte de banco, acontece muitas vezes da exceção não ser captrado pelo método acima ai precisamos de um método mais genérico*/
	//como parametro vou receber apenas esse tipo de exceção: Exception ex, DataIntegrityViolationException é uma excecao comum de BD E
	//violação de chave também ConstraintViolationException E ERROS DE AQL SQLException.class
	@ExceptionHandler({DataIntegrityViolationException.class, 
			ConstraintViolationException.class, SQLException.class})
	protected ResponseEntity<Object> handleExceptionDataIntegry(Exception ex){
		
		ObjetoErroDTO objetoErroDTO = new ObjetoErroDTO();
		
		String msg = "";
		
		if (ex instanceof DataIntegrityViolationException) {
			msg = "Erro de integridade no banco: " +  ((DataIntegrityViolationException) ex).getCause().getCause().getMessage(); //aqui tb fazemos a conversão
		}else
		if (ex instanceof ConstraintViolationException) {
			msg = "Erro de chave estrangeira: " + ((ConstraintViolationException) ex).getCause().getCause().getMessage();
		}else
		if (ex instanceof SQLException) {
			msg = "Erro de SQL do Banco: " + ((SQLException) ex).getCause().getCause().getMessage();
		}else {
			msg = ex.getMessage();
		}
		
		objetoErroDTO.setError(msg);
		objetoErroDTO.setCode(HttpStatus.INTERNAL_SERVER_ERROR.toString()); 
		
		ex.printStackTrace();
		
		return new ResponseEntity<Object>(objetoErroDTO, HttpStatus.INTERNAL_SERVER_ERROR);
		
	}
	

}
