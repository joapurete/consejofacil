package com.java.consejofacil.controller.ABMReunion;

import com.java.consejofacil.model.Reunion;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import java.net.URL;
import java.util.ResourceBundle;

@Getter
@Controller
public class ListaReunionesController extends BaseTablaReuniones implements Initializable {

    // CheckBox Autocompletado
    @FXML
    private CheckBox checkAutocompletado;

    // Logger para gestionar informacion
    private final Logger log = LoggerFactory.getLogger(ListaReunionesController.class);

    @Override
    public void initialize(URL location, ResourceBundle resources) { getReunionManager().inicializarTablaReuniones(this); }

    @FXML
    private void agregarReunion() { getReunionManager().cargarFormulario(null); }

    @FXML
    private void eliminarReunion() {
        // Obtenemos reunión seleccionada
        Reunion reunion = getTblReuniones().getSelectionModel().getSelectedItem();

        // Verificamos que no sea nulo
        if (reunion == null) {
            getReunionManager().mostrarMensaje(true, "Error", "Debes seleccionar una reunión!");
        } else {
            // Eliminamos la reunión
            getReunionManager().eliminarReunion(reunion, false);

            // Actualizamos la tabla de reuniones
            getTblReuniones().refresh();
        }
    }

    @FXML
    private void modificarReunion() {
        // Obtenemos reunión seleccionada
        Reunion reunion = getTblReuniones().getSelectionModel().getSelectedItem();

        // Verificamos que no sea nulo
        if (reunion == null) {
            getReunionManager().mostrarMensaje(true, "Error", "Debes seleccionar una reunión!");
        } else {
            // Cargamos el formulario para modificar la reunión
            getReunionManager().cargarFormulario(reunion);
        }
    }

    @FXML
    private void filtrarPorAsunto() { getReunionManager().filtrarReuniones(this); }

    @FXML
    private void filtrarPorFechaReunion() { getReunionManager().filtrarReuniones(this); }

    @FXML
    private void limpiarFiltros() {
        getReunionManager().limpiarFiltros(this);
    }
}
