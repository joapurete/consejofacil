package com.java.consejofacil.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cargos")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode

public class Cargo {

    @Id
    @Column(name = "id_cargo", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "cargo", length = 50, nullable = false)
    private String cargo;
    @Column(name = "prioridad", nullable = false)
    private int prioridad;

    public Cargo(String cargo, int prioridad) {
        this.cargo = cargo;
        this.prioridad = prioridad;
    }

    @Override
    public String toString(){
        return cargo;
    }
}
