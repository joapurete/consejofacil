package com.java.consejofacil.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "minutas")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode

public class Minuta {

    @Id
    @Column(name = "id_minuta", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "tema_tratado", length = 150, nullable = false)
    private String temaTratado;
    @Column(name = "detalles_minuta", length = 500, nullable = false)
    private String detallesMinuta;

    @ManyToOne
    @JoinColumn(name = "id_reunion")
    Reunion reunion;

    public Minuta(String temaTratado, String detallesMinuta, Reunion reunion) {
        this.temaTratado = temaTratado;
        this.detallesMinuta = detallesMinuta;
        this.reunion = reunion;
    }
}
