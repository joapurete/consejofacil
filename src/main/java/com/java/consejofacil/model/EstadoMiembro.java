package com.java.consejofacil.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "estados_miembros")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode

public class EstadoMiembro {

    @Id
    @Column(name = "id_estado_miembro", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "estado_miembro", length = 25, nullable = false)
    private String estadoMiembro;

    public EstadoMiembro(String estadoMiembro) {
        this.estadoMiembro = estadoMiembro;
    }

    @Override
    public String toString(){
        return estadoMiembro;
    }
}