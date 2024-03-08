package com.java.consejofacil.repository;

import com.java.consejofacil.model.Reunion;
import com.java.consejofacil.model.Revision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RevisionRepository extends JpaRepository<Revision, Integer> {

    List<Revision> findByReunion(Reunion reunion);

    @Query(value = "SELECT COUNT(r) FROM public.revisiones r WHERE r.id_reunion = :id_reunion " +
            "and r.id_expediente = :id_expediente" , nativeQuery = true)
    long existeDuplicado(@Param("id_reunion") int id_expediente, @Param("id_expediente") int id_miembro);
}
