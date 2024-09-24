package jdev.mentoria.lojavirtual.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;

import jdev.mentoria.lojavirtual.model.Usuario;

//Essa classe precisamos extender ela para pegar informações do nucleo do spring security do token
public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter {
	
	/*Confgurando o gerenciado de autenticacao, outro metodo que somos obrigados a implementar e sobrescrever isso é m construtor */
	public JWTLoginFilter(String url, AuthenticationManager authenticationManager) { //essa url vem da requisição
	
		/*Ibriga a autenticat a url, faz a conversão para passar a url*/
		super(new AntPathRequestMatcher(url));
		
		/*Gerenciador de autenticao*/
		setAuthenticationManager(authenticationManager);
		
	}

	
	/*Retorna o usuário ao processr a autenticacao, são metodos que temos que implementar eles*/
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {
		/*Obter o usuário, pegamos esse usuario da requisição*/
		Usuario user = new ObjectMapper().readValue(request.getInputStream(), Usuario.class); //Usuario.class convertemos para uma classe de usuario nosso
		                                             //pegamos da requisição pegamos os dados com request.getInputStream()
		/*Retorna o user com login e senha*/
		return getAuthenticationManager().
				authenticate(new UsernamePasswordAuthenticationToken(user.getLogin(), user.getSenha()));
	}
	
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {

		try {
			new JWTTokenAutenticacaoService().addAuthentication(response, authResult.getName()); //o login ta dentro do getName
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//Aqui nós temos a requisição = HttpServletRequest request e a resposta = HttpServletResponse response
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		
		//não podemos deixar esse método super continuar, pois como estamos sobreescrevendo ele, não podemos deixar ele chamar a
		//parte abstrata, pois devemos usar a nossa implementação
		//super.unsuccessfulAuthentication(request, response, failed);
		
		//se a classe de falaha é uma instancia de BadCredentialException
		if (failed instanceof BadCredentialsException) {
			response.getWriter().write("User e senha não encontrado");
		}else {
			//se for um outro erro qualquer pegamos a mensagem aqui 
			response.getWriter().write("Falha ao logar: " + failed.getMessage());
		}
	}

}
