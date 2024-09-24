package jdev.mentoria.lojavirtual.security;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import jdev.mentoria.lojavirtual.ApplicationContextLoad;
import jdev.mentoria.lojavirtual.model.Usuario;
import jdev.mentoria.lojavirtual.repository.UsuarioRepository;


/*(3)Criar a autenticação e retonar também a autenticação JWT gera o token recupera e valida*/
@Service
@Component
public class JWTTokenAutenticacaoService {
	/*Token de validade de 11 dias*/
	private static final long EXPIRATION_TIME = 959990000;
	
	/*Chave de senha para juntar com o JWT*/
	private static final String SECRET = "ss/-*-*sds565dsd-s/d-s*dsds";
	
	private static final String TOKEN_PREFIX = "Bearer";
	
	private static final String HEADER_STRING = "Authorization";
	
	/*Gera o token e da a responsta para o cliente o com JWT*/
	public void addAuthentication(HttpServletResponse response, String username) throws Exception {
		
		/*Montagem do Token*/
		
		String JWT = Jwts.builder()./*Chama o gerador de token*/
				setSubject(username) /*Adiciona o user*/
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
				.signWith(SignatureAlgorithm.HS512, SECRET).compact(); /*Temp de expiração*/
		
		/*Exe: Bearer *-/a*dad9s5d6as5d4s5d4s45dsd54s.sd4s4d45s45d4sd54d45s4d5s.ds5d5s5d5s65d6s6d*/
		String token = TOKEN_PREFIX + " " + JWT;
		
		/*Dá a resposta pra tela e para o cliente, outra API, navegador, aplicativo, javascript, outra chamadajava*/
		response.addHeader(HEADER_STRING, token);
		
		liberacaoCors(response);
		
		/*Usado para ver no Postman para teste*/
		response.getWriter().write("{\"Authorization\": \"" + token + "\"}");
		
	}
	
	/*(4)Retorna o usuário validado com token ou caso nao seja valido retona null, ele recebe uma requisição aqui HttpServletRequest request*/
	public Authentication getAuthetication(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		//estamos pegando o token para validar
		String token = request.getHeader(HEADER_STRING);
		
		try {
		
		if (token != null) {
			
			//precisamos pegar o token limpo pois ele ta vindo com esse prefixo private static final String TOKEN_PREFIX = "Bearer" o trim é para tirar espaço;
			String tokenLimpo = token.replace(TOKEN_PREFIX, "").trim();
			
			/*Faz a validacao do token do usuário na requisicao e obtem o USER*/
			String user = Jwts.parser().
					setSigningKey(SECRET) //estamos setando uma assinatura para testar esse SECRET é a senha que estamos usando
					.parseClaimsJws(tokenLimpo)
					.getBody().getSubject(); /*ADMIN ou Alex*/
			
			if (user != null) { //se o usuario for encontrado vamos carregar o usuario no banco para obter as credenciais dele para poder passar para o spring
				//aqui usamos a classe ApplicationContextLoad para pegar o contexto de dependencia do spring, fazemos isso pois não conseguimos injetar a dependencia aqui dentro
				Usuario usuario = ApplicationContextLoad.
						getApplicationContext().
						getBean(UsuarioRepository.class).findUserByLogin(user); //com o getBean pegamos qualquer classe ou controle que o spring esta gerenciando para nós isso getBean(UsuarioRepository.class) é uma conversão
				
				if (usuario != null) {
					return new UsernamePasswordAuthenticationToken(
							usuario.getLogin(),
							usuario.getSenha(), 
							usuario.getAuthorities());
				}
				
			}
			
		}
		
		}catch (SignatureException e) {
			response.getWriter().write("Token está inválido.");

		}catch (ExpiredJwtException e) {
			response.getWriter().write("Token está expirado, efetue o login novamente.");
		}
		finally {
			liberacaoCors(response);
		}
		return null;
	}
	
	/*Fazendo liberação contra erro de COrs no navegador*/
	private void liberacaoCors(HttpServletResponse response) {
		
		if (response.getHeader("Access-Control-Allow-Origin") == null) {
			response.addHeader("Access-Control-Allow-Origin", "*");
		}
		
		
		if (response.getHeader("Access-Control-Allow-Headers") == null) {
			response.addHeader("Access-Control-Allow-Headers", "*");
		}
		
		
		if (response.getHeader("Access-Control-Request-Headers") == null) {
			response.addHeader("Access-Control-Request-Headers", "*");
		}
		
		if (response.getHeader("Access-Control-Allow-Methods") == null) {
			response.addHeader("Access-Control-Allow-Methods", "*");
		}
		
	}
	


}
