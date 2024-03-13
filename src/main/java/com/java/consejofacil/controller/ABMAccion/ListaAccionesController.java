package com.java.consejofacil.controller.ABMAccion;

import com.java.consejofacil.model.Accion;
import com.java.consejofacil.model.Expediente;
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

@Controller
public class ListaAccionesController implements Initializable {

    // Columnas de la tabla
    @FXML
    @Getter
    private TableColumn<Accion, Integer> colId;
    @FXML
    @Getter
    private TableColumn<Accion, LocalDate> colFechaAccion;
    @FXML
    @Getter
    private TableColumn<Accion, String> colDetallesAccion;
    @FXML
    @Getter
    private TableColumn<Accion, Expediente> colExpediente;

    // TextField para filtrar acciones
    @FXML
    @Getter
    private TextField txtDetalles;

    // Tabla de acciones
    @FXML
    @Getter
    private TableView<Accion> tblAcciones;

    // Filtros adicionales
    @FXML
    @Getter
    private DatePicker dtpFechaAccion;
    @FXML
    @Getter
    private ComboBox<Expediente> cmbExpediente;

    // CheckBox Autocompletado
    @FXML
    @Getter
    private CheckBox checkAutocompletado;

    // Listas utilizadas
    @Getter
    private final ObservableList<Accion> acciones = FXCollections.observableArrayList();
    @Getter
    private final ObservableList<Accion> filtroAcciones = FXCollections.observableArrayList();
    @Getter
    private final ObservableList<Expediente> expedientes = FXCollections.observableArrayList();

    @Autowired
    @Lazy
    private AccionManager accionManager;

    // Logger para gestionar informacion
    @Getter
    private final Logger log = LoggerFactory.getLogger(ListaAccionesController.class);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicializamos la tabla de acciones
        accionManager.inicializarTablaAcciones();
        // Inicializamos los filtros
        accionManager.inicializarFiltros();
    }

    @FXML
    private void agregarAccion() { accionManager.cargarFormulario(null); }

    @FXML
    private void eliminarAccion() {
        // Obtenemos accion seleccionada
        Accion accion = tblAcciones.getSelectionModel().getSelectedItem();

        // Verificamos que no sea nulo
        if (accion == null) {
            accionManager.mostrarMensaje(true, "Error", "Debes seleccionar una acción!");
        } else {
            accionManager.eliminarAccion(accion, false);

            // Actuailizamos la tabla de acciones
            tblAcciones.refresh();
        }
    }

    @FXML
    private void modificarAccion() {
        // Obtenemos accion seleccionada
        Accion accion = tblAcciones.getSelectionModel().getSelectedItem();

        // Verificamos que no sea nulo
        if (accion == null) {
            accionManager.mostrarMensaje(true, "Error", "Debes seleccionar una acción!");
        } else {
            accionManager.cargarFormulario(accion);
        }
    }

    @FXML
    private void filtrarPorDetalles() { accionManager.filtrarAcciones(); }

    @FXML
    private void filtrarPorFechaAccion() { accionManager.filtrarAcciones(); }

    @FXML
    private void filtrarPorExpediente() { accionManager.filtrarAcciones(); }

    @FXML
    private void limpiarFiltro() { accionManager.limpiarFiltros(); }

    @FXML
    private void seleccionarExpediente() throws Exception { accionManager.seleccionarExpediente(cmbExpediente); }
}
