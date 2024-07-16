package jdev.mentoria.lojavirtual.model;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "usuario")
@SequenceGenerator(name = "seq_usuario", sequenceName = "seq_usuario", allocationSize = 1, initialValue = 1)
public class Usuario implements UserDetails {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_usuario")
	private Long id;
	
	private String login;
	
	private String senha;
	
	@Temporal(TemporalType.DATE)//@Temporal é do pacote javax que é uma evolução do java
	private Date dataAtualSenha;
	
	//O usuario tem uma lista de acessos se vermos o diagrama de classes conseguimos ver 3 tabelas
	//Usuario, Usuario_Acesso e Acesso, conseguimos ver a cardinalidade de muitos para muitos
	//por isso gerou "3 tabelas" mas não vamos criar a tabela do meio  Usuario_Acesso, o spring 
	//vai criar essa tabela associativa através das anotações, quando o usuario fazer o login
	//ele vai usar essa lista que estamos criando.
	//private List<Acesso> acessos; esse <Acesso> é a classe Acesso.java
	@OneToMany(fetch = FetchType.LAZY) //daqui para baixo é a parte complexa onde criamos a terceira tabela, @OneToMany é do pacote javax.persistence quando falamos javax é springdata e jpa, FetchType.LAZY para carregar os acessos só para quando precisar
	@JoinTable(name = "usuarios_acesso", uniqueConstraints = @UniqueConstraint (columnNames = {"usuario_id", "acesso_id"} , //é aqui que criamos a terceira tabela, aqui criamos ela name = "usuarios_acesso" isso porque temos muitos para muitos então criamos uma tabela associativa, criamos as colunas com uniqueConstraints, com isso @UniqueConstraint (columnNames = {"usuario_id", "acesso_id"} não vamos ter cadastro repetido
	 name = "unique_acesso_user"), //é um nome para esse conjunto columnNames = {"usuario_id", "acesso_id"}
	joinColumns = @JoinColumn(name = "usuario_id", referencedColumnName = "id", table = "usuario", //apontamos as colunas para suas tabelas, o campo usuario_id eu posso repetir por isso que é unique = false pois eu posso cadastrar varios acessos para o mesmo usuario eu posso repetir o codigo de usuario nessa tabela usuario_acesso pois posso ter 5 tipos de acesso para o usuario 1 então por isso que é false o parametro unique
	unique = false, foreignKey = @ForeignKey(name = "usuario_fk", value = ConstraintMode.CONSTRAINT)), //foreignKey = @ForeignKey colocamos o nome da chave estrangeira pois senão ele vai criar uma nome muito estranho
	//até agora a parte de cima é da tabela usuario, usuario_acesso apontando para usuario
	inverseJoinColumns = @JoinColumn(name = "acesso_id", unique = false, referencedColumnName = "id", table = "acesso", //acesso_id é como se fosse da tabela usuario_acesso o unique ta como fasle senão vai restringir, referenciamos a tabela acesso campo id
	foreignKey = @ForeignKey(name = "aesso_fk", value = ConstraintMode.CONSTRAINT))) //foreignKey é um atributo do javax
	private List<Acesso> acessos;
	

	/*Autoridades = São os acesso, ou seja ROLE_ADMIN, ROLE_SECRETARIO, ROLE_FINACEIRO*/
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {//ele retorna uma coleção de classes que implementam essa interface de segurança GrantedAuthority de autoridade do spring security 
		
		return this.acessos; //como ele entende isso, através disso: public class Acesso implements GrantedAuthority da classe Acesso.java
	}                        //GrantedAuthority é uma interface

	@Override
	public String getPassword() {
		return this.senha; //depois o spring security vai fazer a magica
	}

	@Override
	public String getUsername() {
		return this.login; //depois o spring security vai fazer a magica
	}

	@Override
	public boolean isAccountNonExpired() {
		return true; //se tiver falso o spring vai dar acesso não autorizado
	}

	@Override
	public boolean isAccountNonLocked() {
		return true; //se tiver falso o spring vai dar acesso não autorizado
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true; //se tiver falso o spring vai dar acesso não autorizado
	}

	@Override
	public boolean isEnabled() {
		return true; //se tiver falso o spring vai dar acesso não autorizado
	}

}
