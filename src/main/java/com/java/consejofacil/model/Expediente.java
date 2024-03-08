package com.java.consejofacil.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "expedientes")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode

public class Expediente {

    @Id
    @Column(name = "id_expediente", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "texto_nota", length = 500, nullable = false)
    private String textoNota;
    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDate fechaIngreso;

    @ManyToOne
    @JoinColumn(name = "id_miembro")
    Miembro iniciante;

    @ManyToOne
    @JoinColumn(name = "id_estado_expediente")
    EstadoExpediente estadoExpediente;

    public Expediente(String textoNota, LocalDate fechaIngreso, Miembro iniciante, EstadoExpediente estadoExpediente) {
        this.textoNota = textoNota;
        this.fechaIngreso = fechaIngreso;
        this.iniciante = iniciante;
        this.estadoExpediente = estadoExpediente;
    }

    @Override
    public String toString() {
        return "Expediente N° " + getId();
    }
}
