package com.java.consejofacil.repository;

import com.java.consejofacil.model.Minuta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MinutaRepository extends JpaRepository<Minuta, Integer> {
}
