package com.java.consejofacil.repository;

import com.java.consejofacil.model.TipoCambio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoCambioRepository extends JpaRepository<TipoCambio, Integer> {
}
