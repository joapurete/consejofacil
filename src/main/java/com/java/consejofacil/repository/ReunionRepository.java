package com.java.consejofacil.repository;

import com.java.consejofacil.model.Reunion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReunionRepository extends JpaRepository<Reunion, Integer> {

    @Query(value = "SELECT * FROM public.reuniones WHERE fecha_reunion <= CURRENT_DATE" , nativeQuery = true)
    List<Reunion> encontrarReunionesHastaHoy();
}
