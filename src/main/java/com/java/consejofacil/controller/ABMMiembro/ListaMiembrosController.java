package com.java.consejofacil.controller.ABMMiembro;

import com.java.consejofacil.model.Miembro;
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
public class ListaMiembrosController extends BaseTablaMiembros implements Initializable {

    // CheckBox Autocompletado
    @FXML
    private CheckBox checkAutocompletado;

    // Logger para gestionar informacion
    private final Logger log = LoggerFactory.getLogger(ListaMiembrosController.class);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Validamos el acceso del miembro
        getMiembroManager().validarAccesoMiembro();
        // Inicializamos la tabla de miembros
        getMiembroManager().inicializarTablaMiembros(this);
        // Inicializamos los filtros
        getMiembroManager().inicializarFiltros(this);
    }

    @FXML
    private void agregarMiembro() { getMiembroManager().cargarFormulario(null, this); }

    @FXML
    private void eliminarMiembro() {
        // Obtenemos miembro seleccionado
        Miembro miembro = getTblMiembros().getSelectionModel().getSelectedItem();

        // Verificamos que no sea nulo
        if (miembro == null) {
            getMiembroManager().mostrarMensaje(true, "Error", "Debes seleccionar un miembro!");
        } else {
            // Eliminamos el miembro
            getMiembroManager().eliminarMiembro(miembro, false);

            // Actualizamos la tabla de miembros
            getTblMiembros().refresh();
        }
    }

    @FXML
    private void modificarMiembro() {
        // Obtenemos miembro seleccionado
        Miembro miembro = getTblMiembros().getSelectionModel().getSelectedItem();

        // Verificamos que no sea nulo
        if (miembro == null) {
            getMiembroManager().mostrarMensaje(true, "Error", "Debes seleccionar un miembro!");
        } else {
            getMiembroManager().cargarFormulario(miembro, this);
        }
    }

    @FXML
    private void filtrarPorNombre() { getMiembroManager().filtrarMiembros(this); }

    @FXML
    private void filtrarPorCargo() { getMiembroManager().filtrarMiembros(this); }

    @FXML
    private void filtrarPorFechaNac() { getMiembroManager().filtrarMiembros(this); }

    @FXML
    private void filtrarPorEstado() { getMiembroManager().filtrarMiembros(this); }

    @FXML
    private void limpiarFiltro() { getMiembroManager().limpiarFiltros(this); }
}