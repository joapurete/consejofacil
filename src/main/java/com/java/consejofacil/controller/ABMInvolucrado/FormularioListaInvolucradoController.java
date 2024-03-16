package com.java.consejofacil.controller.ABMInvolucrado;

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
import java.util.*;

@Getter
@SuppressWarnings("SpellCheckingInspection")
@Controller
public class FormularioListaInvolucradoController extends BaseFormularioInvolucrado implements Initializable {

    // Columnas de la tabla
    @FXML
    private TableColumn<Involucrado, Integer> colDni;
    @FXML
    private TableColumn<Involucrado, String> colNombre;
    @FXML
    private TableColumn<Involucrado, Cargo> colCargo;
    @FXML
    private TableColumn<Involucrado, EstadoMiembro> colEstado;
    @FXML
    private TableColumn<Involucrado, String> colDetallesInvolucrado;
    @FXML
    private TableColumn<Involucrado, Boolean> colSeleccionar;

    // Tabla de miembros
    @FXML
    private TableView<Involucrado> tblInvolucrados;

    // TextField para filtrar miembros
    @FXML
    private TextField txtNombre;
    @FXML
    private ComboBox<Cargo> cmbCargo;
    @FXML
    private ComboBox<EstadoMiembro> cmbEstado;
    @FXML
    private TextField txtDetalles;

    // CheckBox para seleccionar todos y para mostrar los involucrados seleccionados
    @FXML
    private CheckBox checkSeleccionarTodo;
    @FXML
    private CheckBox checkMostrarSeleccionados;

    // Listas utilizadas
    private final ObservableList<Involucrado> filtroInvolucrados = FXCollections.observableArrayList();
    private final Map<Miembro, Integer> miembrosSeleccionados = new HashMap<>();
    private final ObservableList<Cargo> cargos = FXCollections.observableArrayList();
    private final ObservableList<EstadoMiembro> estadosMiembros = FXCollections.observableArrayList();

    // Cadenas de mensajes
    private final ArrayList<String> cadenaErrores = new ArrayList<>();
    private final ArrayList<String> cadenaInfos = new ArrayList<>();

    // Logger para gestionar informacion
    private final Logger log = LoggerFactory.getLogger(FormularioListaInvolucradoController.class);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Validamos el acceso del miembro
        getInvolucradoManager().validarAccesoMiembro();
        // Inicializamos tabla de involucrados
        getInvolucradoManager().inicializarListaInvolucrados();
        // Inicializamos combos
        getInvolucradoManager().inicializarCombosFormulario(this);
        // Inicializamos filtros de la tabla
        getInvolucradoManager().inicializarFiltrosListaInvolucrados();
        // Inicializamos cadenas de strings
        getInvolucradoManager().inicializarCadenasTexto();
    }

    // Metodo para guardar los involucrados seleccionados

    @FXML
    private void guardarInvolucrados() { getInvolucradoManager().guardarListaInvolucrados(); }

    // Buscamos involucrados por expediente

    @FXML
    private void buscarInvolucradosPorExpediente() { getInvolucradoManager().buscarInvolucradosPorExpediente(); }

    // Filtramos por la información seleccionada

    @FXML
    private void filtrarPorNombre() { getInvolucradoManager().filtrarListaInvolucrados(); }

    @FXML
    private void filtrarPorCargo() { getInvolucradoManager().filtrarListaInvolucrados(); }

    @FXML
    private void filtrarPorEstado() { getInvolucradoManager().filtrarListaInvolucrados(); }

    @FXML
    private void filtrarPorDetalles() { getInvolucradoManager().filtrarListaInvolucrados(); }

    // Metodos de los CheckBox

    @FXML
    private void seleccionarTodos() { getInvolucradoManager().seleccionarTodosLosMiembros(); }

    @FXML
    void mostrarInvolucradosSeleccionados() { getInvolucradoManager().filtrarListaInvolucrados(); }

    // Metodos de busqueda avanzada de los combos

    @FXML
    private void seleccionarExpediente() throws Exception { getInvolucradoManager().seleccionarExpediente(getCmbExpediente()); }

    // Metodos para limpiar campos

    @FXML
    private void nuevoExpediente() { getInvolucradoManager().limpiarFormulario(this); }

    @FXML
    private void limpiarFiltros() {
        getInvolucradoManager().limpiarFiltrosListaInvolucrados();
    }
}