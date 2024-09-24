package jdev.mentoria.lojavirtual.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import jdev.mentoria.lojavirtual.model.Usuario;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuario, Long> {
	
	@Query(value = "select u from Usuario u where u.login = ?1") //aqui trabalhamos com q query do springdata essa consulta é na classe usuario.java
	Usuario findUserByLogin(String login);

}
