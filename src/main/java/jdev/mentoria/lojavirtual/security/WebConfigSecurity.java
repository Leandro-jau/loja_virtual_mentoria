package jdev.mentoria.lojavirtual.security;

import javax.servlet.http.HttpSessionListener;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true) //prePostEnabled = true: Isso habilita o uso das anotações @PreAuthorize e @PostAuthorize, que permitem aplicar restrições de segurança antes ou depois que um método é invocado. securedEnabled = true: Isso permite o uso da anotação @Secured, que pode especificar uma lista de papéis que têm permissão para invocar um método.
public class WebConfigSecurity extends WebSecurityConfigurerAdapter implements HttpSessionListener { //extends WebSecurityConfigurerAdapter  = para usar recursos do spring secutiry  implements HttpSessionListener = para fazer logoff e login de usuario serve para implementa interceptadores
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		//web.ignoring().antMatchers(HttpMethod.GET, "/salvarAcesso", "/deleteAcesso")
		web.ignoring().antMatchers(HttpMethod.GET, "/salvarAcesso","/deleteAcesso") //salvarAcesso","/deleteAcesso" = esta ligado ao acessoController.java
		//*web.ignoring().antMatchers(HttpMethod.GET, "/salvarAcesso")
		//.antMatchers(HttpMethod.POST, "/salvarAcesso", "/deleteAcesso");
		.antMatchers(HttpMethod.POST, "/salvarAcesso","/deleteAcesso");	
		//*.antMatchers(HttpMethod.POST, "/salvarAcesso");	
		/*Ingnorando URL no momento para nao autenticar senão não vamos conseguir testar*/
	}
	
}
