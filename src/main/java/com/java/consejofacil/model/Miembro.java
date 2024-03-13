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
    @Column(name = "foto")
    private byte[] foto;
    @Column(name = "telefono", length = 50)
    private String telefono;

    @ManyToOne
    @JoinColumn(name = "id_cargo")
    Cargo cargo;

    @ManyToOne
    @JoinColumn(name = "id_estado_miembro")
    EstadoMiembro estadoMiembro;

    // Constructor utilizado para insertar un nuevo miembro
    public Miembro(String nombre, String apellido, String clave, LocalDate fechaNac, String telefono, String direccion,
                   String correo, Cargo cargo, EstadoMiembro estadoMiembro) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.clave = clave;
        this.fechaNac = fechaNac;
        this.telefono = telefono;
        this.direccion = direccion;
        this.correo = correo;
        this.cargo = cargo;
        this.estadoMiembro = estadoMiembro;
    }

    // Constructor utilizado para modificar un nuevo miembro (se quita la clave)
    public Miembro(String nombre, String apellido, LocalDate fechaNac, String telefono, String direccion,
                   String correo, Cargo cargo, EstadoMiembro estadoMiembro) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechaNac = fechaNac;
        this.telefono = telefono;
        this.direccion = direccion;
        this.correo = correo;
        this.cargo = cargo;
        this.estadoMiembro = estadoMiembro;
    }

    @Override
    public String toString() {
        return nombre + " " + apellido;
    }
}