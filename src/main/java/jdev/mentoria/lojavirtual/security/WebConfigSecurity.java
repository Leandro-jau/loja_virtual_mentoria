package jdev.mentoria.lojavirtual.security;

import javax.servlet.http.HttpSessionListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import jdev.mentoria.lojavirtual.service.ImplementacaoUserDetailsService;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true) //prePostEnabled = true: Isso habilita o uso das anotações @PreAuthorize e @PostAuthorize, que permitem aplicar restrições de segurança antes ou depois que um método é invocado. securedEnabled = true: Isso permite o uso da anotação @Secured, que pode especificar uma lista de papéis que têm permissão para invocar um método.
public class WebConfigSecurity extends WebSecurityConfigurerAdapter implements HttpSessionListener { //extends WebSecurityConfigurerAdapter  = para usar recursos do spring secutiry  implements HttpSessionListener = para fazer logoff e login de usuario serve para implementa interceptadores
	
	@Autowired
	private ImplementacaoUserDetailsService implementacaoUserDetailsService;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		//colocamos o nivel de autenticação por token aqui ativa a proteção por token
		http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
		//ativamos o contexto livre a parte principal do sistema para pelo menos poder entrar na tela de login do sistema
		.disable().authorizeRequests().antMatchers("/").permitAll()
		.antMatchers("/index").permitAll()
		.antMatchers(HttpMethod.OPTIONS, "/**").permitAll()//evita bloqueio de cors
		
		/* redireciona ou da um retorno para index quando desloga, para quando o usuario deslogar do sistema precisamos dar um retorno
		 * essa é uma parte engessada do security é copiar e colar*/
		.anyRequest().authenticated().and().logout().logoutSuccessUrl("/index")
		
		/*mapeia o logout do sistema*/
		.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
		
		/*Filtra as requisicoes para login de JWT*/
		.and().addFilterAfter(new JWTLoginFilter("/login", authenticationManager()),
				UsernamePasswordAuthenticationFilter.class)
		
		.addFilterBefore(new JwtApiAutenticacaoFilter(), UsernamePasswordAuthenticationFilter.class);
		
	}
	
	
	/*(1)Irá consultar o user no banco com Spring Security metodo que configura a implementação da configuração
	  precisao implantar o implementaçãoUserDetailService e o metodo protected void confiugre(AuthenticationManagerBuilder auth)
	  auth é um objeto de autenticação que vem por parametro do spring security
	  Nós estamos falando para o springsecurity usar esse nosso serviço implementacaoUserDetailsService para fazer essa consulta no usuario no banco
	  o resto o springsecutity se vira chama tudo sózinho
	  */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(implementacaoUserDetailsService).passwordEncoder(new BCryptPasswordEncoder()); //passwordEncoder(new BCryptPasswordEncoder()) setamos o padrao de codificação
		
	}
	
	/*Infnora algumas url de autenticação*/
	@Override
	public void configure(WebSecurity web) throws Exception {
		
		//web.ignoring().antMatchers(HttpMethod.GET, "/salvarAcesso","/deleteAcesso") //salvarAcesso","/deleteAcesso" = esta ligado ao acessoController.java
		//.antMatchers(HttpMethod.POST, "/salvarAcesso","/deleteAcesso");	
		/*Ingnorando URL no momento para nao autenticar senão não vamos conseguir testar*/
	}
	
}
