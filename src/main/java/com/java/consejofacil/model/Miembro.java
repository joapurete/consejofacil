package com.java.consejofacil.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "miembros")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode

public class Miembro {

    @Id
    @Column(name = "dni_miembro", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int dni;
    @Column(name = "nombre", length = 50, nullable = false)
    private String nombre;
    @Column(name = "apellido", length = 50, nullable = false)
    private String apellido;
    @Column(name = "clave", length = 500, nullable = false)
    private String clave;
    @Column(name = "fecha_nac")
    private LocalDate fechaNac;
    @Column(name = "correo", length = 100)
    private String correo;
    @Column(name = "direccion", length = 100)
    private String direccion;
    @Column(name = "telefono", length = 50)
    private String telefono;
    @Column(name = "foto", length = 150)
    private String foto;

    @ManyToOne
    @JoinColumn(name = "id_cargo")
    Cargo cargo;

    @ManyToOne
    @JoinColumn(name = "id_estado_miembro")
    EstadoMiembro estadoMiembro;

    public Miembro(String nombre, String apellido, String clave, LocalDate fechaNac, String correo, String direccion, String telefono, String foto, Cargo cargo, EstadoMiembro estadoMiembro) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.clave = clave;
        this.fechaNac = fechaNac;
        this.correo = correo;
        this.direccion = direccion;
        this.telefono = telefono;
        this.foto = foto;
        this.cargo = cargo;
        this.estadoMiembro = estadoMiembro;
    }

    @Override
    public String toString() {
        return nombre + " " + apellido;
    }
}