package com.java.consejofacil.repository;

import com.java.consejofacil.model.EstadoExpediente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoExpedienteRepository extends JpaRepository<EstadoExpediente, Integer> {
}
