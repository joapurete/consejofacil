package com.java.consejofacil.security;

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
    private SessionInfo sessionInfo;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public SecurityConfig() {}

    // Metodo para validar las credenciales y autenticar la información del usuario

    public boolean validarCredenciales(int dni, String clave) {

        // Buscamos el miembro asociado al DNI especificado
        Miembro miembro = miembroService.findById(dni);

        // Verificamos si existe
        if (miembro != null) {

            // Validamos la contrasena ingresada y si ambas coinciden
            if (validarContrasena(clave, miembro.getClave())) {
                // Mostramos un mensaje de exito
                AlertHelper.mostrarMensaje(false, "Info", "Se ha iniciado sesión correctamente!");

                // Guardamos la información del usuario
                sessionInfo.setUsuario(miembro);

                // Indicamos que el incio de sesion fue exitosa
                return true;
            } else {
                // Mostramos en un mensaje, en caso de validacion incorrecta
                AlertHelper.mostrarMensaje(true, "Error", "La contraseña ingresada es incorrecta. Vuelva a intentarlo.");
            }
        } else {
            // Mostramos un mensaje, en caso de no encontrar el miembro
            AlertHelper.mostrarMensaje(true, "Error", "No se encontró ningún miembro con el DNI especificado.");
        }

        // Indicamos que hubo un error
        return false;
    }

    // Metodo para codificar y validar contrasenas

    public String codificarContrasena(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public boolean validarContrasena(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
