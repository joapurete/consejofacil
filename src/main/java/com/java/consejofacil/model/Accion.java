package com.java.consejofacil.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "acciones")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode

public class Accion {

    @Id
    @Column(name = "id_accion", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "fecha_accion", nullable = false)
    private LocalDate fechaAccion;
    @Column(name = "detalles_accion", length = 500, nullable = false)
    private String detallesAccion;

    @ManyToOne
    @JoinColumn(name = "id_expediente")
    Expediente expediente;

    public Accion(LocalDate fechaAccion, String detallesAccion, Expediente expediente) {
        this.fechaAccion = fechaAccion;
        this.detallesAccion = detallesAccion;
        this.expediente = expediente;
    }
}