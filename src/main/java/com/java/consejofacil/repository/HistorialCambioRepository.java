package com.java.consejofacil.repository;

import com.java.consejofacil.model.HistorialCambio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistorialCambioRepository extends JpaRepository<HistorialCambio, Integer> {

}
