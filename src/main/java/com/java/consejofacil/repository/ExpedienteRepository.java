package com.java.consejofacil.repository;

import com.java.consejofacil.model.Expediente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@SuppressWarnings("SpellCheckingInspection")
@Repository
public interface ExpedienteRepository extends JpaRepository<Expediente, Integer> {

    List<Expediente> findByestadoExpediente_estadoExpediente(String estadoExpediente);
}
