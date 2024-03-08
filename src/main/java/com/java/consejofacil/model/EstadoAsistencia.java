package com.java.consejofacil.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "estados_asistencias")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode

public class EstadoAsistencia {

    @Id
    @Column(name = "id_estado_asistencia", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "estado_asistencia", length = 25, nullable = false)
    private String estadoAsistencia;

    public EstadoAsistencia(String estadoAsistencia) {
        this.estadoAsistencia = estadoAsistencia;
    }

    @Override
    public String toString(){
        return estadoAsistencia;
    }
}