package jdev.mentoria.lojavirtual.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jdev.mentoria.lojavirtual.model.CategoriaProduto;

public interface CategoriaProdutoRepository extends JpaRepository<CategoriaProduto, Long> {
	
	//O atributo nativeQuery = true indica que a consulta é uma SQL nativa (não é uma JPQL - Java Persistence Query Language). Ou seja, você está 
	//escrevendo uma consulta SQL que será executada diretamente no banco de dados.	
	@Query(nativeQuery = true, value = "select count(1) > 0 from categoria_produto where upper(trim(nome_desc)) = upper(trim(?1))")
	public boolean existeCatehoria(String nomeCategoria);

	
	@Query("select a from CategoriaProduto a where upper(trim(a.nomeDesc)) like %?1%")
	public List<CategoriaProduto> buscarCategoriaDes(String nomeDesc);

}
