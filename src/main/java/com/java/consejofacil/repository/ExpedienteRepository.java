package com.java.consejofacil.repository;

import com.java.consejofacil.model.Expediente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@SuppressWarnings("SpellCheckingInspection")
@Repository
public interface ExpedienteRepository extends JpaRepository<Expediente, Integer> {

    List<Expediente> findByestadoExpediente_estadoExpediente(String estadoExpediente);

    @Query(value = "SELECT e.estado_expediente, COUNT(ex.*) FROM public.expedientes ex RIGHT JOIN public.estados_expedientes e " +
            "on e.id_estado_expediente = ex.id_estado_expediente GROUP BY e.estado_expediente", nativeQuery = true)
    List<Object[]> contarCantidadExpedientesPorEstado();

    @Query(value = "SELECT * FROM public.expedientes ORDER BY fecha_ingreso DESC LIMIT :limite", nativeQuery = true)
    List<Expediente> encontrarUltimosExpedientes(@Param("limite") int limite);

    @Query(value = "SELECT a.cantidad_acciones, i.cantidad_involucrados FROM public.expedientes ex " +
            "LEFT JOIN (SELECT id_expediente, COUNT(*) AS cantidad_acciones FROM public.acciones GROUP BY id_expediente) " +
            "AS a ON a.id_expediente = ex.id_expediente " +
            "LEFT JOIN (SELECT id_expediente, COUNT(*) AS cantidad_involucrados FROM public.involucrados GROUP BY id_expediente) " +
            "AS i ON i.id_expediente = ex.id_expediente " +
            "ORDER BY ex.fecha_ingreso DESC LIMIT :limite", nativeQuery = true)
    List<Object[]> contarCantidadInvAccPorExpediente(@Param("limite") int limite);
}

