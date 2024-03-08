package com.java.consejofacil.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "involucrados")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode

public class Involucrado {

    @Id
    @Column(name = "id_involucrado", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "detalles_involucrado", length = 500)
    private String detallesInvolucrado;

    @ManyToOne
    @JoinColumn(name = "id_expediente")
    Expediente expediente;

    @ManyToOne
    @JoinColumn(name = "id_miembro")
    Miembro miembro;

    public Involucrado(Expediente expediente, Miembro miembro, String detallesInvolucrado) {
        this.expediente = expediente;
        this.miembro = miembro;
        this.detallesInvolucrado = detallesInvolucrado;
    }
}
