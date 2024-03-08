package com.java.consejofacil.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "asistencias")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode

public class Asistencia {

    @Id
    @Column(name = "id_asistencia", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "id_reunion")
    Reunion reunion;

    @ManyToOne
    @JoinColumn(name = "id_miembro")
    Miembro miembro;

    @ManyToOne
    @JoinColumn(name = "id_estado_asistencia")
    EstadoAsistencia estadoAsistencia;

    public Asistencia(Reunion reunion, Miembro miembro, EstadoAsistencia estadoAsistencia) {
        this.reunion = reunion;
        this.miembro = miembro;
        this.estadoAsistencia = estadoAsistencia;
    }
}
