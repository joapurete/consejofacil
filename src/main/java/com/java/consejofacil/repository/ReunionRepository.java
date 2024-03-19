package com.java.consejofacil.repository;

import com.java.consejofacil.model.Reunion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReunionRepository extends JpaRepository<Reunion, Integer> {

    @Query(value = "SELECT * FROM public.reuniones WHERE fecha_reunion <= CURRENT_DATE" , nativeQuery = true)
    List<Reunion> encontrarReunionesHastaHoy();

    @Query(value = "SELECT * FROM public.reuniones WHERE fecha_reunion >= CURRENT_DATE ORDER BY fecha_reunion LIMIT :limite" , nativeQuery = true)
    List<Reunion> encontrarProximasReuniones(@Param("limite") int limite);

    @Query(value = "SELECT * FROM public.reuniones WHERE fecha_reunion < CURRENT_DATE ORDER BY fecha_reunion DESC LIMIT :limite" , nativeQuery = true)
    List<Reunion> encontrarUltimasReuniones(@Param("limite") int limite);

    @Query(value = "SELECT rev.cantidad_revisiones, asis.cantidad_presentes, mi.cantidad_minutas  FROM public.reuniones r " +
            "LEFT JOIN (SELECT id_reunion, COUNT(*) AS cantidad_revisiones FROM public.revisiones GROUP BY id_reunion) " +
            "AS rev ON rev.id_reunion = r.id_reunion " +
            "LEFT JOIN (SELECT id_reunion, COUNT(*) AS cantidad_presentes FROM public.asistencias GROUP BY id_reunion) " +
            "AS asis ON asis.id_reunion = r.id_reunion " +
            "LEFT JOIN (SELECT id_reunion, COUNT(*) AS cantidad_minutas FROM public.minutas GROUP BY id_reunion) " +
            "AS mi ON mi.id_reunion = r.id_reunion " +
            "WHERE fecha_reunion < CURRENT_DATE ORDER BY fecha_reunion DESC LIMIT :limite", nativeQuery = true)
    List<Object[]> contarCantidadRevAsisMinPorExpediente(@Param("limite") int limite);
}
