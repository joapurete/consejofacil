package com.java.consejofacil.repository;

import com.java.consejofacil.model.Expediente;
import com.java.consejofacil.model.Involucrado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvolucradoRepository extends JpaRepository<Involucrado, Integer> {

    List<Involucrado> findByExpediente(Expediente expediente);

    @Query(value = "SELECT COUNT(i) FROM public.involucrados i WHERE i.id_expediente = :id_expediente " +
            "and i.id_miembro = :id_miembro" , nativeQuery = true)
    long existeDuplicado(@Param("id_expediente") int id_expediente, @Param("id_miembro") int id_miembro);
}
