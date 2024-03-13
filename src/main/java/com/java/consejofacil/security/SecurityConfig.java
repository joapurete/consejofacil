package com.java.consejofacil.security;

import com.java.consejofacil.controller.SessionController;
import com.java.consejofacil.helper.Alertas.AlertHelper;
import com.java.consejofacil.model.Miembro;
import com.java.consejofacil.service.Miembro.MiembroServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class SecurityConfig {

    @Autowired
    @Lazy
    private MiembroServiceImpl miembroService;

    @Autowired
    @Lazy
    private SessionController sessionControlador;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public SecurityConfig() {}

    // Metodo para validar las credenciales y autenticar la información del usuario

    public boolean validarCredenciales(int dni, String clave) {

        // Buscamos el miembro asociado al DNI especificado
        Miembro miembro = miembroService.findById(dni);

        // Verificamos si existe
        if (miembro != null) {

            String estadoMiembro = miembro.getEstadoMiembro().getEstadoMiembro();

            // Verificamos que el usuario este activo en el sistema
            if (estadoMiembro.equals("Activo")) {

                // Validamos la contrasena ingresada y si ambas coinciden
                if (validarContrasena(clave, miembro.getClave())) {

                    // Abrimos la sesion del usuario
                    sessionControlador.abrirSesion(miembro);

                    // Indicamos que el incio de sesion fue exitosa
                    return true;
                } else {
                    // Mostramos en un mensaje, en caso de validacion incorrecta
                    mostrarMensaje("La contraseña ingresada es incorrecta. Vuelva a intentarlo.");
                }
            } else {
                switch (estadoMiembro) {
                    case "Inactivo" -> mostrarMensaje("El miembro del consejo " + miembro + " se encuentra inactivo.");
                    case "En espera" -> mostrarMensaje("El miembro del consejo " + miembro + " se encuentra en espera de registración.");
                    default -> mostrarMensaje("El miembro del consejo " + miembro + " no se encuentra activo en el sistema.");
                }
            }
        } else {
            // Mostramos un mensaje, en caso de no encontrar el miembro
            mostrarMensaje("No se encontró ningún miembro con el DNI especificado.");
        }

        // Indicamos que hubo un error
        return false;
    }

    private void mostrarMensaje(String contenido) { AlertHelper.mostrarMensaje(true, "Error", contenido); }

    // Metodo para codificar y validar contrasenas

    public String codificarContrasena(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public boolean validarContrasena(String rawPassword, String encodedPassword) { return passwordEncoder.matches(rawPassword, encodedPassword); }
}
