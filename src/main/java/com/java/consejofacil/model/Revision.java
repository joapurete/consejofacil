package com.java.consejofacil.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "revisiones")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode

public class Revision {

    @Id
    @Column(name = "id_revision", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "detalles_revision", length = 500)
    private String detallesRevision;

    @ManyToOne
    @JoinColumn(name = "id_reunion")
    Reunion reunion;

    @ManyToOne
    @JoinColumn(name = "id_expediente")
    Expediente expediente;

    public Revision(Reunion reunion, Expediente expediente, String detallesRevision) {
        this.reunion = reunion;
        this.expediente = expediente;
        this.detallesRevision = detallesRevision;
    }
}
