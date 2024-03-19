package com.java.consejofacil.security;

import com.java.consejofacil.config.StageManager;
import com.java.consejofacil.controller.MainLayoutController;
import com.java.consejofacil.controller.MenuController;
import com.java.consejofacil.helper.Alertas.AlertHelper;
import com.java.consejofacil.model.Miembro;
import com.java.consejofacil.view.FXMLView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

public class SessionManager {

    // Componente que contiene la información de sesion
    @Autowired
    @Lazy
    private SessionInfo sesion;

    // Controladores de los fxml
    @Autowired
    @Lazy
    private MainLayoutController mainLayoutControlador;
    @Autowired
    @Lazy
    private MenuController menuControlador;

    // Stage Manager
    @Autowired
    @Lazy
    private StageManager stageManager;

    public SessionManager() {
    }

    // Metodos para abrir, cerrar y modifidcar una sesion activa

    public void abrirSesion(Miembro miembro) {
        // Establecemos la información del usuario
        sesion.setUsuario(miembro);

        // Mostramos un mensaje de exito
        mostrarMensaje("Se ha iniciado sesión correctamente!");
    }

    public void cerrarSesion() {
        // Eliminamos la información del usuario
        sesion.setUsuario(null);

        // Mostramos un mensaje
        mostrarMensaje("Se ha cerrado sesión correctamente!");

        // Cambiamos al login
        stageManager.switchScene(FXMLView.Login);

    }

    public void modificarSesion(Miembro miembro) {
        if (validarSesion() && miembro != null) {
            // Modificamos la información del usuario
            sesion.setUsuario(miembro);

            // Actualizamos el menu
            menuControlador.actualizarMenu();

            // Validamos el aceso del miembro con la nueva información establecida
            validarAccesoMiembro();
        }
    }

    // Metodo Getter para obtener la información de sesion

    public Miembro getUsuario() {
        return sesion.getUsuario();
    }

    // Metodos para validar la sesion y el acceso del miembro

    public boolean validarSesion() {
        return sesion.inSession();
    }

    public boolean autenticarMiembro(Miembro miembro) {
        return validarSesion() && getUsuario().equals(miembro);
    }

    public boolean tieneCargoMiembro(){
        return validarSesion() && getUsuario().getCargo().getCargo().equals("Miembro del Consejo")
        && getUsuario().getCargo().getPrioridad() <= 1;
    }

    public void validarAccesoMiembro() {
        // Verificamos si está en sesion y está activo en el sistema
        if (validarSesion()) {
            if (sesion.getUsuario().getEstadoMiembro().getEstadoMiembro().equals("Activo")) {

                // Verificamos si se encuentra dentro de una de las vistas para gestionar los miembros y si tiene la prioridad necesaria
                if (mainLayoutControlador.getViewCentro().getTitle().contains("Miembro") &&
                        tieneCargoMiembro()) {

                    // Cambiamos de centro
                    mainLayoutControlador.cambiarCentro(FXMLView.Inicio);
                }

            } else {
                // Cerramos la sesión
                cerrarSesion();
            }
        } else {
            // Cerramos la sesión
            cerrarSesion();
        }
    }

    private void mostrarMensaje(String contenido) {
        AlertHelper.mostrarMensaje(false, "Info", contenido);
    }
}
