package jdev.mentoria.lojavirtual.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import jdev.mentoria.lojavirtual.model.StatusRastreio;

public interface StatusRastreioRepository extends JpaRepository<StatusRastreio, Long> {

}
