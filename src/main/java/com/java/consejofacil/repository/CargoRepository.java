package com.java.consejofacil.repository;

import com.java.consejofacil.model.Cargo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CargoRepository extends JpaRepository<Cargo, Integer> {

    @Query(value = "SELECT * FROM public.cargos WHERE prioridad <= :prioridad" , nativeQuery = true)
    List<Cargo> findByPrioridad(@Param("prioridad") int prioridad);
}
