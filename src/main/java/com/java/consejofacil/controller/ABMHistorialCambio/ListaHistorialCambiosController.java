package com.java.consejofacil.controller.ABMHistorialCambio;

import com.java.consejofacil.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

@Getter
@Controller
public class ListaHistorialCambiosController implements Initializable {

    // Columnas de la tabla
    @FXML
    private TableColumn<HistorialCambio, Integer> colId;
    @FXML
    private TableColumn<HistorialCambio, String> colDetalles;
    @FXML
    private TableColumn<HistorialCambio, LocalDate> colFechaCambio;
    @FXML
    private TableColumn<HistorialCambio, Miembro> colResponsable;
    @FXML
    private TableColumn<HistorialCambio, EstadoExpediente> colTipo;


    // Tabla de expedientes
    @FXML
    private TableView<HistorialCambio> tblCambios;

    // Filtros adicionales
    @FXML
    private DatePicker dtpFechaCambio;
    @FXML
    private ComboBox<Miembro> cmbResponsable;
    @FXML
    private ComboBox<TipoCambio> cmbTipo;
    @FXML
    private TextField txtDetalles;

    // CheckBox Autocompletado
    @FXML
    private CheckBox checkAutocompletado;

    // Listas utilizadas
    private final ObservableList<HistorialCambio> cambios = FXCollections.observableArrayList();
    private final ObservableList<HistorialCambio> filtroCambios = FXCollections.observableArrayList();
    private final ObservableList<Miembro> miembros = FXCollections.observableArrayList();
    private final ObservableList<TipoCambio> tiposCambios = FXCollections.observableArrayList();

    // Manager
    @Autowired
    @Lazy
    private HistorialCambioManager historialCambioManager;

    // Logger para gestionar informacion
    private final Logger log =  LoggerFactory.getLogger(ListaHistorialCambiosController.class);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Validamos el acceso del miembro
        historialCambioManager.validarAccesoMiembro();
        // Inicializamos la tabla de miembros
        historialCambioManager.inicializarTablaHistorialCambios();
        // Inicializamos los filtros
        historialCambioManager.inicializarFiltros();
    }

    @FXML
    private void agregarCambio() { historialCambioManager.cargarFormulario(null); }

    @FXML
    private void eliminarCambio() {
        // Obtenemos cambio seleccionado
        HistorialCambio cmb = tblCambios.getSelectionModel().getSelectedItem();

        // Verificamos que no sea nulo
        if (cmb == null) {
            historialCambioManager.mostrarMensaje(true, "Error", "Debes seleccionar un cambio!");
        } else {
            // Eliminamos el cambio
            historialCambioManager.eliminarCambio(cmb, false);

            // Actualizamos la tabla de cambios
            tblCambios.refresh();
        }
    }

    @FXML
    private void modificarCambio() {
        // Obtenemos cambio seleccionado
        HistorialCambio cmb = tblCambios.getSelectionModel().getSelectedItem();

        // Verificamos que no sea nulo
        if (cmb == null) {
            historialCambioManager.mostrarMensaje(true, "Error", "Debes seleccionar un cambio!");
        } else {
            historialCambioManager.cargarFormulario(cmb);
        }
    }

    @FXML
    private void filtrarPorDetalles() { historialCambioManager.filtrarCambios(); }

    @FXML
    private void filtrarPorResponsable() { historialCambioManager.filtrarCambios(); }

    @FXML
    private void filtrarPorFechaCambio() { historialCambioManager.filtrarCambios(); }

    @FXML
    private void filtrarPorTipo() { historialCambioManager.filtrarCambios(); }

    @FXML
    private void limpiarFiltro() {
        historialCambioManager.limpiarFiltros();
    }

    @FXML
    private void seleccionarResponsable() throws Exception {historialCambioManager.seleccionarMiembro(cmbResponsable); }
}
