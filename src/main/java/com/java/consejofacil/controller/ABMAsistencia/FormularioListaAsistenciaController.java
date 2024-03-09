package com.java.consejofacil.controller.ABMAsistencia;

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
import java.util.*;

@SuppressWarnings("SpellCheckingInspection")
@Controller
public class FormularioListaAsistenciaController extends BaseFormularioAsistencia implements Initializable {

    // Columnas de la tabla
    @FXML
    @Getter
    private TableColumn<Asistencia, Integer> colDni;
    @FXML
    @Getter
    private TableColumn<Asistencia, String> colNombre;
    @FXML
    @Getter
    private TableColumn<Asistencia, Cargo> colCargo;
    @FXML
    @Getter
    private TableColumn<Asistencia, EstadoMiembro> colEstadoMiembro;
    @FXML
    @Getter
    private TableColumn<Asistencia, EstadoAsistencia> colEstadoAsistencia;
    @FXML
    @Getter
    private TableColumn<Asistencia, Boolean> colSeleccionar;

    // Tabla de asistencias
    @FXML
    @Getter
    private TableView<Asistencia> tblAsistencias;

    // TextField para filtrar miembros
    @FXML
    @Getter
    private TextField txtNombre;
    @FXML
    @Getter
    private ComboBox<Cargo> cmbCargo;
    @FXML
    @Getter
    private ComboBox<EstadoMiembro> cmbEstadoMiembro;
    @FXML
    @Getter
    private ComboBox<EstadoAsistencia> cmbEstadoAsistencia;

    // CheckBox para seleccionar todos y para mostrar las asistencias seleccionadas
    @FXML
    @Getter
    private CheckBox checkSeleccionarTodo;
    @FXML
    @Getter
    private CheckBox checkMostrarSeleccionados;
    @FXML
    @Getter
    private CheckBox checkMarcarPresentePorDefecto;

    // Listas utilizadas
    @Getter
    private final ObservableList<Asistencia> filtroAsistencias = FXCollections.observableArrayList();
    @Getter
    private final Map<Miembro, Integer> miembrosSeleccionados = new HashMap<>();
    @Getter
    private final ObservableList<Cargo> cargos = FXCollections.observableArrayList();
    @Getter
    private final ObservableList<EstadoMiembro> estadosMiembros = FXCollections.observableArrayList();

    // Cadenas de mensajes
    @Getter
    private final ArrayList<String> cadenaErrores = new ArrayList<>();
    @Getter
    private final ArrayList<String> cadenaInfos = new ArrayList<>();

    @Autowired
    @Lazy
    private AsistenciaManager asistenciaManager;

    // Logger para gestionar informacion
    @Getter
    private final Logger log = LoggerFactory.getLogger(FormularioListaAsistenciaController.class);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicializamos tabla de involucrados
        asistenciaManager.inicializarListaAsistencias();
        // Inicializamos combos
        asistenciaManager.inicializarCombosFormulario(this);
        // Inicializamos filtros de la tabla
        asistenciaManager.inicializarFiltrosListaAsistencias();
        // Inicializamos cadenas de strings
        asistenciaManager.inicializarCadenasTexto();
    }

    // Metodo para guardar las asistencias seleccionadas

    @FXML
    private void guardarAsistencias() { asistenciaManager.guardarListaAsistencias(); }

    // Buscamos involucrados por expediente

    @FXML
    private void buscarAsistenciasPorReunion() { asistenciaManager.buscarAsistenciasPorReunion(); }

    // Filtramos por la información seleccionada

    @FXML
    private void filtrarPorNombre() { asistenciaManager.filtrarListaAsistencias(); }

    @FXML
    private void filtrarPorCargo() { asistenciaManager.filtrarListaAsistencias(); }

    @FXML
    private void filtrarPorEstadoMiembro() { asistenciaManager.filtrarListaAsistencias(); }

    @FXML
    private void filtrarPorEstadoAsistencia() { asistenciaManager.filtrarListaAsistencias(); }

    // Metodos de los CheckBox

    @FXML
    private void seleccionarTodos() { asistenciaManager.seleccionarTodosLosMiembros(); }

    @FXML
    private void mostrarInvolucradosSeleccionados() { asistenciaManager.filtrarListaAsistencias(); }

    @FXML
    private void marcarPresentePorDefecto() { asistenciaManager.marcarPresentePorDefecto(); }

    // Metodos de busqueda avanzada de los combos

    @FXML
    private void seleccionarReunion() throws Exception { asistenciaManager.seleccionarReunion(getCmbReunion()); }

    // Metodos para limpiar campos

    @FXML
    private void nuevaReunion() { asistenciaManager.limpiarFormulario(this); }

    @FXML
    private void limpiarFiltro() {
        asistenciaManager.limpiarFiltrosListaAsistencias();
    }
}