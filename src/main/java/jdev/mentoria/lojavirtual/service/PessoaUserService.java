package jdev.mentoria.lojavirtual.service;




import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Service;

import jdev.mentoria.lojavirtual.model.PessoaJuridica;
import jdev.mentoria.lojavirtual.model.Usuario;
import jdev.mentoria.lojavirtual.repository.PessoaRepository;
import jdev.mentoria.lojavirtual.repository.UsuarioRepository;

@Service
public class PessoaUserService {
	
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private PessoaRepository pessoaRepository;
	
	//sql direto do spring
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public PessoaJuridica salvarPessoaJuridica(PessoaJuridica juridica) {
		
		juridica = pessoaRepository.save(juridica);
		
		for (int i = 0; i< juridica.getEnderecos().size(); i++) {
			juridica.getEnderecos().get(i).setPessoa(juridica);
			juridica.getEnderecos().get(i).setEmpresa(juridica);
		}
		
		juridica = pessoaRepository.save(juridica);
		
		Usuario usuarioPj = usuarioRepository.findUserByPessoa(juridica.getId(), juridica.getEmail());
		
		if (usuarioPj == null) {
			
			String constraint = usuarioRepository.consultaConstraintAcesso();
			if (constraint != null) { //jdbcTemplate sql direto no spring podemos manipular o BD
				jdbcTemplate.execute("begin; alter table usuarios_acesso drop constraint " + constraint +"; commit;");
			}
			
			usuarioPj = new Usuario();
			usuarioPj.setDataAtualSenha(Calendar.getInstance().getTime());
			usuarioPj.setEmpresa(juridica);
			usuarioPj.setPessoa(juridica);
			usuarioPj.setLogin(juridica.getEmail());
			
			String senha = "" + Calendar.getInstance().getTimeInMillis();
			String senhaCript = new BCryptPasswordEncoder().encode(senha);
			
			usuarioPj.setSenha(senhaCript);
			
			usuarioPj = usuarioRepository.save(usuarioPj);
			
			usuarioRepository.insereAcessoUserPj(usuarioPj.getId());
			
			/*Fazer o envio de e-mail do login e da senha*/
			
		}
		
		return juridica;
		
	}

}
