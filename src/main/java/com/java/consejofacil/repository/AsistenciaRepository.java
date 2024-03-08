package com.java.consejofacil.repository;

import com.java.consejofacil.model.Asistencia;
import com.java.consejofacil.model.Reunion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AsistenciaRepository extends JpaRepository<Asistencia, Integer> {

    List<Asistencia> findByReunion(Reunion reunion);

    @Query(value = "SELECT COUNT(a) FROM public.asistencias a WHERE a.id_reunion = :id_reunion " +
            "and a.id_miembro = :id_miembro" , nativeQuery = true)
    long existeDuplicado(@Param("id_reunion") int id_reunion, @Param("id_miembro") int id_miembro);
}
