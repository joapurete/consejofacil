package com.java.consejofacil.repository;

import com.java.consejofacil.model.Miembro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@SuppressWarnings("SpellCheckingInspection")
@Repository
public interface MiembroRepository extends JpaRepository<Miembro, Integer> {

    List<Miembro> findByestadoMiembro_estadoMiembro(String estadoMiembro);
}
