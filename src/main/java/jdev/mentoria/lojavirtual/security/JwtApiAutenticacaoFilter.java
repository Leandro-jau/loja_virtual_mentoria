package jdev.mentoria.lojavirtual.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

/*Filtro onde todas as requisicoes serão capturadas para autenticar, ess classe junta tudo que vir e retornar vai passar por esse filtro*/
public class JwtApiAutenticacaoFilter extends GenericFilterBean {

	//Esse filter intercepta todas as requisições
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		try { //é o unico lugar que vamos usar o try catch
		
		/*Estabele a autenticao do user, aqui ele vai tentar obter a autenticação se esta ou nao*/		
		Authentication authentication = new JWTTokenAutenticacaoService().
				getAuthetication((HttpServletRequest) request, (HttpServletResponse) response); //agente adicionou agora estamos recuperando a autenticação do usuario
		                         //isso (HttpServletRequest) request é uma conversão para a classe concreta esse também (HttpServletResponse) ** essa conversão é necessário porque isso public void doFilter(ServletRequest request é uma interface
		
		
		
		/*Coloca o processo de autenticacao para o spring secutiry, vamos usar as classes internas do spring security para ele ativar as classes 
		 * do pacote security que estamos implementando*/
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		//chain é para ele continuar a requisição para chamar a api ou bloquear
		chain.doFilter(request, response);
		
		} catch (Exception e) { //qualquer exceção que não conseguimos tratar vai pegar aqui
			
			e.printStackTrace();
			//esse erro vai acontecer por exemplo se alterarmos o nome da coluna do banco ou algum outro erro de sql de banco de dados
			response.getWriter().write("Ocorreu um erro no sistema, avise o administrador: \n" + e.getMessage());
		}
		
		
	}
	
	

}
