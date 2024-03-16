package com.java.consejofacil.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tipos_cambios")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode

public class TipoCambio {

    @Id
    @Column(name = "id_tipo_cambio", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "tipo_cambio", length = 25, nullable = false)
    private String tipoCambio;

    public TipoCambio(String tipoCambio) {
        this.tipoCambio = tipoCambio;
    }

    @Override
    public String toString(){
        return tipoCambio;
    }
}