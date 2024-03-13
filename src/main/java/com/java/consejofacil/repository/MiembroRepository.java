package com.java.consejofacil.repository;

import com.java.consejofacil.model.Miembro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@SuppressWarnings("SpellCheckingInspection")
@Repository
public interface MiembroRepository extends JpaRepository<Miembro, Integer> {

    List<Miembro> findByestadoMiembro_estadoMiembro(String estadoMiembro);

    @Modifying
    @Query(value = "UPDATE public.miembros SET dni_miembro = :dni_nuevo " +
            "WHERE dni_miembro = :dni_actual", nativeQuery = true)
    int modificarDni(@Param("dni_nuevo") int dni_nuevo, @Param("dni_actual") int dni_actual);

    @Modifying
    @Query(value = "UPDATE public.miembros SET clave = :clave " +
            "WHERE dni_miembro = :dni_miembro", nativeQuery = true)
    int cambiarContrasena(@Param("clave") String clave, @Param("dni_miembro") int dni_miembro);
}
