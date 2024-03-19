package com.java.consejofacil.repository;

import com.java.consejofacil.model.HistorialCambio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistorialCambioRepository extends JpaRepository<HistorialCambio, Integer> {

    @Query(value = " SELECT * FROM public.historial_cambios ORDER BY fecha_cambio DESC LIMIT :limite" , nativeQuery = true)
    List<HistorialCambio> encontrarUltimosCambios(@Param("limite") int limite);
}
