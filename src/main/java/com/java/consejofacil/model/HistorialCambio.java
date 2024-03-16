package com.java.consejofacil.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "historial_cambios")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode

public class HistorialCambio {

    @Id
    @Column(name = "id_cambio", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "fecha_cambio", nullable = false)
    private LocalDate fechaCambio;
    @Column(name = "detalles_cambio", length = 500, nullable = false)
    private String detallesCambio;

    @ManyToOne
    @JoinColumn(name = "id_miembro")
    Miembro responsable;

    @ManyToOne
    @JoinColumn(name = "id_tipo_cambio")
    TipoCambio tipoCambio;

    public HistorialCambio(LocalDate fechaCambio, Miembro responsable, TipoCambio tipoCambio, String detallesCambio) {
        this.fechaCambio = fechaCambio;
        this.responsable = responsable;
        this.tipoCambio = tipoCambio;
        this.detallesCambio = detallesCambio;
    }
}