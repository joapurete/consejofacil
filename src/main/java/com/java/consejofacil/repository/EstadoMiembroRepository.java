package com.java.consejofacil.repository;

import com.java.consejofacil.model.EstadoMiembro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoMiembroRepository extends JpaRepository<EstadoMiembro, Integer> {
}
