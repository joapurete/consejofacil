package com.java.consejofacil.controller;

import com.java.consejofacil.controller.ABMMiembro.MiembroManager;
import com.java.consejofacil.helper.Alertas.AlertHelper;
import com.java.consejofacil.helper.Utilidades.ImageHelper;
import com.java.consejofacil.model.Cargo;
import com.java.consejofacil.model.Miembro;
import com.java.consejofacil.view.FXMLView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

@Controller
public class MenuController implements Initializable {

    // Componentes para mostrar la información del usuario
    @FXML
    private ImageView fotoPerfil;
    @FXML
    private Label lblNombre;
    @FXML
    private Label lblCargo;

    // Botones del menu lateral
    @FXML
    private Button btnInicio, btnExpedientes, btnReuniones, btnInvolucrados, btnAcciones, btnMinutas, btnMiembros,
    btnAsistencias, btnRevisiones, btnHistorialCambios;

    // Controladores de los fxml
    @Autowired
    @Lazy
    private MiembroManager miembroManager;
    @Autowired
    @Lazy
    private MainLayoutController mainLayoutController;

    // Información de inicio de sesion
    @Autowired
    @Lazy
    private SessionController sessionControlador;

    // Lista de items (botones) del menu
    private List<Button> items;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Redondeamos la foto de perfil
        ImageHelper.redondearImagen(fotoPerfil);

        // Agregamos los listeners a los botones
        addListeners();

        // Inicializamos la lista de items con los botones correspondientes
        items = Arrays.asList(btnInicio, btnExpedientes, btnReuniones, btnInvolucrados, btnAcciones, btnMinutas,
                btnMiembros, btnAsistencias, btnRevisiones, btnHistorialCambios);

        // Actualizamos la información del usuario
        actualizarMenu();
    }

    // Listeners de los items del menu

    private void addListeners() {
        // Establecemos los handlers correspondientes a cada boton
        btnInicio.setOnAction(event -> onMenuItemSelected(FXMLView.Inicio, event));
        btnExpedientes.setOnAction(event -> onMenuItemSelected(FXMLView.ListaExpedientes, event));
        btnReuniones.setOnAction(event -> onMenuItemSelected(FXMLView.ListaReuniones, event));
        btnMiembros.setOnAction(event -> onMenuItemSelected(FXMLView.ListaMiembros, event));
        btnAcciones.setOnAction(event -> onMenuItemSelected(FXMLView.ListaAcciones, event));
        btnMinutas.setOnAction(event -> onMenuItemSelected(FXMLView.ListaMinutas, event));
        btnInvolucrados.setOnAction(event -> onMenuItemSelected(FXMLView.ListaInvolucrados, event));
        btnAsistencias.setOnAction(event -> onMenuItemSelected(FXMLView.ListaAsistencias, event));
        btnRevisiones.setOnAction(event -> onMenuItemSelected(FXMLView.ListaRevisiones, event));
    }

    // Meotodo para cambiar el contenido central del main

    private void onMenuItemSelected(FXMLView view, ActionEvent event) {
        // Obtenemos el boton que disparo el evento
        Button btn = (Button) event.getSource();

        // Verificamos si el boton no tiene la clase actual, lo que significa que no está actualmente seleccionado
        if (!btn.getStyleClass().contains("actual")) {
            // Itera sobre la lista de items y remueve la clase actual de cada boton, si la tienen
            items.forEach(button -> button.getStyleClass().removeIf(style -> style.equals("actual")));
            // Agregamos la clase actual al boton que disparo el evento
            btn.getStyleClass().add("actual");

            // Cambiamos el contenido del centro del contenedor principal
            mainLayoutController.cambiarCentro(view);
        }
    }

    // Metodo para cerrar sesion

    @FXML
    private void cerrarSesion() {
        if (AlertHelper.mostrarConfirmacion("Info", "¿Está seguro que desea cerrar sesión?")) {
            // Cerramos la sesion
            sessionControlador.cerrarSesion();
        }
    }

    // Metodo para modificar el perfil del usuario en sesion

    @FXML
    private void modificarPerfil() {
        if (sessionControlador.validarSesion()) {
            miembroManager.cargarFormulario(sessionControlador.getUsuario(), this);
        }
    }

    // Metodo para establecer la información del usuario en el menu

    public void actualizarMenu() {
        if (sessionControlador.validarSesion()) {

            Miembro miembro = sessionControlador.getUsuario();

            // Obtenemos la información del usuario en sesion
            byte[] imagenBytes = miembro.getFoto();
            String nombre = miembro.toString();
            Cargo cargo = miembro.getCargo() != null ? miembro.getCargo() : null;

            // Verificamos si tiene foto de perfil
            if (imagenBytes != null) {

                // Convertimos los bytes en un Image
                Image imagen = ImageHelper.convertirBytesAImage(imagenBytes);

                // Si la conversion fue exitosa, establecemos la foto de perfil
                if (imagen != null) {
                    fotoPerfil.setImage(imagen);
                }
            }

            // Establecemos el nombre y cargo del miembro
            if (nombre != null) {
               lblNombre.setText(nombre);
            }
            if (cargo != null) {
                lblCargo.setText(cargo.toString());

                // Mostramos / ocultamos el boton de miembros basandonos en la proridad
                habilitarBtnMiembros(cargo.getPrioridad() >= 2);
            }
        }
    }

    // Metodo para ocultar / mostrar btn de miembros segun la prioridad del miembro

    public void habilitarBtnMiembros(boolean tieneAcceso){
        btnMiembros.setDisable(!tieneAcceso);
        btnMiembros.setVisible(tieneAcceso);
    }
}
