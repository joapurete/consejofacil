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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import java.net.URL;
import java.util.*;

@SuppressWarnings("SpellCheckingInspection")
@Controller
public class FormularioListaInvolucradoController extends BaseFormularioInvolucrado implements Initializable {

    // Columnas de la tabla
    @FXML
    @Getter
    private TableColumn<Involucrado, Integer> colDni;
    @FXML
    @Getter
    private TableColumn<Involucrado, String> colNombre;
    @FXML
    @Getter
    private TableColumn<Involucrado, Cargo> colCargo;
    @FXML
    @Getter
    private TableColumn<Involucrado, EstadoMiembro> colEstado;
    @FXML
    @Getter
    private TableColumn<Involucrado, String> colDetallesInvolucrado;
    @FXML
    @Getter
    private TableColumn<Involucrado, Boolean> colSeleccionar;

    // Tabla de miembros
    @FXML
    @Getter
    private TableView<Involucrado> tblInvolucrados;

    // TextField para filtrar miembros
    @FXML
    @Getter
    private TextField txtNombre;
    @FXML
    @Getter
    private ComboBox<Cargo> cmbCargo;
    @FXML
    @Getter
    private ComboBox<EstadoMiembro> cmbEstado;
    @FXML
    @Getter
    private TextField txtDetalles;

    // CheckBox para seleccionar todos y para mostrar los involucrados seleccionados
    @FXML
    @Getter
    private CheckBox checkSeleccionarTodo;
    @FXML
    @Getter
    private CheckBox checkMostrarSeleccionados;

    // Listas utilizadas
    @Getter
    private final ObservableList<Involucrado> filtroInvolucrados = FXCollections.observableArrayList();
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

    // Logger para gestionar informacion
    @Getter
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