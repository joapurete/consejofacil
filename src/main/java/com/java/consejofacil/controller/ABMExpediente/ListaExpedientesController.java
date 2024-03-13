package com.java.consejofacil.controller.ABMExpediente;

import com.java.consejofacil.model.Expediente;
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
public class ListaExpedientesController extends BaseTablaExpedientes implements Initializable {

    // CheckBox Autocompletado
    @FXML
    private CheckBox checkAutocompletado;

    // Logger para gestionar informacion
    private final Logger log = LoggerFactory.getLogger(ListaExpedientesController.class);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicializamos la tabla de expedientes
        getExpedienteManager().inicializarTablaExpedientes(this);
        // Inicializamos los filtros
        getExpedienteManager().inicializarFiltros(this);
    }

    @FXML
    private void agregarExpediente() { getExpedienteManager().cargarFormulario(null); }

    @FXML
    private void eliminarExpediente() {
        // Obtenemos expediente seleccionado
        Expediente exp = getTblExpedientes().getSelectionModel().getSelectedItem();

        // Verificamos que no sea nulo
        if (exp == null) {
            getExpedienteManager().mostrarMensaje(true, "Error", "Debes seleccionar un expediente!");
        } else {
            // Eliminamos el expediente
            getExpedienteManager().eliminarExpediente(exp, false);

            // Actualizamos la tabla de expediente
            getTblExpedientes().refresh();
        }
    }

    @FXML
    private void modificarExpediente() {
        // Obtenemos expediente seleccionado
        Expediente exp = getTblExpedientes().getSelectionModel().getSelectedItem();

        // Verificamos que no sea nulo
        if (exp == null) {
            getExpedienteManager().mostrarMensaje(true, "Error", "Debes seleccionar un expediente!");
        } else {
            getExpedienteManager().cargarFormulario(exp);
        }
    }

    @FXML
    private void filtrarPorNota() { getExpedienteManager().filtrarExpedientes(this); }

    @FXML
    private void filtrarPorIniciante() { getExpedienteManager().filtrarExpedientes(this); }

    @FXML
    private void filtrarPorFechaIngreso() { getExpedienteManager().filtrarExpedientes(this); }

    @FXML
    private void filtrarPorEstado() { getExpedienteManager().filtrarExpedientes(this); }

    @FXML
    private void limpiarFiltro() {
        getExpedienteManager().limpiarFiltros(this);
    }

    @FXML
    private void seleccionarIniciante() throws Exception { getExpedienteManager().seleccionarMiembro(getCmbIniciante()); }
}
