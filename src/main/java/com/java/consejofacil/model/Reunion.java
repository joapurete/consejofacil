package com.java.consejofacil.model;

import com.java.consejofacil.helpers.Helpers;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "reuniones")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode

public class Reunion {

    @Id
    @Column(name = "id_reunion", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "asunto", length = 150, nullable = false)
    private String asunto;
    @Column(name = "fecha_reunion", nullable = false)
    private LocalDate fechaReunion;

    public Reunion(String asunto, LocalDate fechaReunion) {
        this.asunto = asunto;
        this.fechaReunion = fechaReunion;
    }

    @Override
    public String toString() {
        // Formateamos la fecha de la reunion
        return "ID Reunión " + getId() + " | " + Helpers.formatearFecha(getFechaReunion());
    }
}