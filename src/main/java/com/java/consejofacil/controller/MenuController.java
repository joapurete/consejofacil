package com.java.consejofacil.controller;

import com.java.consejofacil.controller.ABMMiembro.MiembroManager;
import com.java.consejofacil.helper.Alertas.AlertHelper;
import com.java.consejofacil.helper.Utilidades.ImageHelper;
import com.java.consejofacil.model.Cargo;
import com.java.consejofacil.model.Miembro;
import com.java.consejofacil.security.SessionManager;
import com.java.consejofacil.view.FXMLView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import lombok.Getter;
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
    @Getter
    private Button btnInicio, btnExpedientes, btnReuniones, btnInvolucrados, btnAcciones, btnMinutas, btnMiembros,
            btnAsistencias, btnRevisiones, btnHistorialCambios;
    @FXML
    private HBox hboxPerfil;

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
    private SessionManager sessionManager;

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

        // Establecemos un handler cuando se presiona Enter en el Vbox del perfil
        // Establecemos foco en boton de inicio
        hboxPerfil.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                // Llamar a la función que deseas ejecutar aquí
                modificarPerfil();
            }
        });
        Platform.runLater(() -> btnInicio.requestFocus());

    }

    // Listeners de los items del menu

    private void addListeners() {
        // Establecemos los handlers correspondientes a cada boton
        btnInicio.setOnAction(event -> redireccionarMenu(FXMLView.Inicio, (Button) event.getSource()));
        btnExpedientes.setOnAction(event -> redireccionarMenu(FXMLView.ListaExpedientes, (Button) event.getSource()));
        btnReuniones.setOnAction(event -> redireccionarMenu(FXMLView.ListaReuniones, (Button) event.getSource()));
        btnMiembros.setOnAction(event -> redireccionarMenu(FXMLView.ListaMiembros, (Button) event.getSource()));
        btnAcciones.setOnAction(event -> redireccionarMenu(FXMLView.ListaAcciones, (Button) event.getSource()));
        btnMinutas.setOnAction(event -> redireccionarMenu(FXMLView.ListaMinutas, (Button) event.getSource()));
        btnInvolucrados.setOnAction(event -> redireccionarMenu(FXMLView.ListaInvolucrados, (Button) event.getSource()));
        btnAsistencias.setOnAction(event -> redireccionarMenu(FXMLView.ListaAsistencias, (Button) event.getSource()));
        btnRevisiones.setOnAction(event -> redireccionarMenu(FXMLView.ListaRevisiones, (Button) event.getSource()));
        btnHistorialCambios.setOnAction(event -> redireccionarMenu(FXMLView.ListaHistorialCambios, (Button) event.getSource()));
    }

    // Meotodo para cambiar el contenido central del main

    public void redireccionarMenu(FXMLView view, Button btn) {
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
            sessionManager.cerrarSesion();
        }
    }

    // Metodo para modificar el perfil del usuario en sesion

    @FXML
    private void modificarPerfil() {
        if (sessionManager.validarSesion()) {
            miembroManager.cargarFormulario(sessionManager.getUsuario(), this);
        }
    }

    // Metodo para establecer la información del usuario en el menu

    public void actualizarMenu() {
        if (sessionManager.validarSesion()) {

            Miembro miembro = sessionManager.getUsuario();

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
                } else {
                    // Colocamos la foto de perfil por defecto
                    ImageHelper.colocarImagenPorDefecto(fotoPerfil);
                }
            } else {
                // Colocamos la foto de perfil por defecto
                ImageHelper.colocarImagenPorDefecto(fotoPerfil);
            }

            // Redondeamos la imagen
            ImageHelper.redondearImagen(fotoPerfil);

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

    public void habilitarBtnMiembros(boolean tieneAcceso) {
        btnMiembros.setDisable(!tieneAcceso);
        btnMiembros.setVisible(tieneAcceso);
    }
}
