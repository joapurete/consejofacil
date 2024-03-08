package com.java.consejofacil.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "estados_expedientes")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode

public class EstadoExpediente {

    @Id
    @Column(name = "id_estado_expediente", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "estado_expediente", length = 25, nullable = false)
    private String estadoExpediente;

    public EstadoExpediente(String estadoExpediente) {
        this.estadoExpediente = estadoExpediente;
    }

    @Override
    public String toString(){
        return estadoExpediente;
    }
}
