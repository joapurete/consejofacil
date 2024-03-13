package com.java.consejofacil.controller;

import com.java.consejofacil.helper.Utilidades.ImageHelper;
import com.java.consejofacil.security.SessionInfo;
import com.java.consejofacil.view.FXMLView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
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

    // Informacion de inicio de sesion
    @Autowired
    @Lazy
    private SessionInfo sesion;

    // Botones del menu lateral
    @FXML
    private Button btnInicio, btnExpedientes, btnReuniones, btnInvolucrados, btnAcciones, btnMinutas, btnMiembros, btnAsistencias, btnRevisiones, btnHistorialCambios, btnLogout;
    // Boton para acceder al perfil
    @FXML
    private HBox btnPerfil;
    // Foto de perfil
    @FXML
    private ImageView fotoPerfil;

    // Controladores utilizados
    @Autowired
    @Lazy
    private MainLayoutController mainLayoutController;

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

        System.out.println(sesion.getUsuario().toString());
    }

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
}
