package com.java.consejofacil.controller.ABMRevision;

import com.java.consejofacil.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

@Getter
@SuppressWarnings("SpellCheckingInspection")
@Controller
public class FormularioListaRevisionController extends BaseFormularioRevision implements Initializable {

    // Columnas de la tabla
    @FXML
    private TableColumn<Revision, Integer> colId;
    @FXML
    private TableColumn<Revision, String> colTextoNota;
    @FXML
    private TableColumn<Revision, LocalDate> colFechaIngreso;
    @FXML
    private TableColumn<Revision, EstadoExpediente> colEstado;
    @FXML
    private TableColumn<Revision, String> colDetallesRevision;
    @FXML
    private TableColumn<Revision, Boolean> colSeleccionar;

    // Tabla de expedientes
    @FXML
    private TableView<Revision> tblRevisiones;

    // TextField para filtrar expedientes
    @FXML
    private TextField txtNota;
    @FXML
    private TextField txtDetalles;
    @FXML
    private DatePicker dtpFechaIngreso;
    @FXML
    private ComboBox<EstadoExpediente> cmbEstado;

    // CheckBox para seleccionar todos y para mostrar las revisiones seleccionadas
    @FXML
    private CheckBox checkSeleccionarTodo;
    @FXML
    private CheckBox checkMostrarSeleccionados;

    // Listas utilizadas
    private final ObservableList<Revision> filtroRevisiones = FXCollections.observableArrayList();
    private final Map<Expediente, Integer> expedientesSeleccionados = new HashMap<>();
    private final ObservableList<Miembro> miembros = FXCollections.observableArrayList();
    private final ObservableList<EstadoExpediente> estadosExpedientes = FXCollections.observableArrayList();

    // Cadenas de mensajes
    private final ArrayList<String> cadenaErrores = new ArrayList<>();
    private final ArrayList<String> cadenaInfos = new ArrayList<>();

    // Logger para gestionar informacion
    private final Logger log = LoggerFactory.getLogger(FormularioListaRevisionController.class);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Validamos el acceso del miembro
        getRevisionManager().validarAccesoMiembro();
        // Inicializamos lista de expedientes
        getRevisionManager().inicializarListaRevisiones();
        // Inicializamos combos
        getRevisionManager().inicializarCombosFormulario(this);
        // Inicializamos filtros de la tabla
        getRevisionManager().inicializarFiltrosListaRevisiones();
        // Inicializamos cadenas de strings
        getRevisionManager().inicializarCadenasTexto();
    }

    // Metodo para guardar las revisiones seleccionadas

    @FXML
    private void guardarRevisiones() {
        getRevisionManager().guardarListaRevisiones();
    }

    // Buscamos nuevos expedientes seleccionados

    @FXML
    private void buscarExpedientesPorReunion() {
        getRevisionManager().buscarExpedientesPorReunion();
    }

    // Filtramos por la información seleccionada

    @FXML
    private void filtrarPorNota() { getRevisionManager().filtrarListaRevisiones(); }

    @FXML
    private void filtrarPorDetalles() { getRevisionManager().filtrarListaRevisiones(); }

    @FXML
    private void filtrarPorFechaIngreso() { getRevisionManager().filtrarListaRevisiones(); }

    @FXML
    private void filtrarPorEstado() { getRevisionManager().filtrarListaRevisiones(); }

    // Metodos de los CheckBox

    @FXML
    private void seleccionarTodos() {
        getRevisionManager().seleccionarTodosLosExpedientes();
    }

    // Filtramos los expedientes para mostrar unicamente los seleccionados
    @FXML
    void mostrarInvolucradosSeleccionados() { getRevisionManager().filtrarListaRevisiones(); }

    // Metodos de busqueda avanzada de los combos

    @FXML
    private void seleccionarReunion() throws Exception { getRevisionManager().seleccionarReunion(getCmbReunion()); }

    // Metodos para limpiar campos

    @FXML
    private void nuevaReunion() { getRevisionManager().limpiarFormulario(this); }

    @FXML
    private void limpiarFiltro() { getRevisionManager().limpiarFiltrosListaRevisiones(); }
}