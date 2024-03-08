package com.java.consejofacil.controller.ABMAsistencia;


import com.java.consejofacil.model.Asistencia;
import com.java.consejofacil.model.EstadoAsistencia;
import com.java.consejofacil.model.Miembro;
import com.java.consejofacil.model.Reunion;
import com.java.consejofacil.view.FXMLView;
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
import java.util.ResourceBundle;

@Controller
public class ListaAsistenciaController implements Initializable {

    // Columnas de la tabla
    @FXML
    @Getter
    private TableColumn<Asistencia, Reunion> colReunion;
    @FXML
    @Getter
    private TableColumn<Asistencia, Miembro> colMiembro;
    @FXML
    @Getter
    private TableColumn<Asistencia, EstadoAsistencia> colEstado;

    // Tabla de involucrados
    @FXML
    @Getter
    private TableView<Asistencia> tblAsistencias;

    // Filtros adicionales
    @FXML
    @Getter
    private ComboBox<EstadoAsistencia> cmbEstado;
    @FXML
    @Getter
    private ComboBox<Reunion> cmbReunion;
    @FXML
    @Getter
    private ComboBox<Miembro> cmbMiembro;

    // CheckBox Autocompletado y lista
    @FXML
    @Getter
    private CheckBox checkAutocompletado;
    @FXML
    @Getter
    private CheckBox checkLista;

    // Listas utilizadas
    @Getter
    private ObservableList<Asistencia> asistencias = FXCollections.observableArrayList();
    @Getter
    private ObservableList<Asistencia> filtroAsistencias = FXCollections.observableArrayList();
    @Getter
    private ObservableList<Reunion> reuniones = FXCollections.observableArrayList();
    @Getter
    private ObservableList<Miembro> miembros = FXCollections.observableArrayList();

    @Autowired
    @Lazy
    private AsistenciaManager asistenciaManager;

    // Logger para gestionar informacion
    @Getter
    private final Logger log = LoggerFactory.getLogger(ListaAsistenciaController.class);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicializamos tabla de asistencias
        asistenciaManager.inicializarTablaAsistencias();
        // Inicializamos los filtros
        asistenciaManager.inicializarFiltros();
    }

    // Metodos para agregar, modificar y eliminar asistencias

    @FXML
    private void agregarAsistencia() throws Exception { asistenciaManager.cargarFormulario(checkLista.isSelected() ? FXMLView.FormularioListaAsistencia : FXMLView.FormularioAsistencia, null); }

    @FXML
    private void modificarAsistencia() throws Exception {
        // Obtenemos asistencia seleccionada
        Asistencia asis = tblAsistencias.getSelectionModel().getSelectedItem();

        // Obtenemos la selección de los controles
        boolean listaSeleccionada = checkLista.isSelected();

        // Verificamos que no sea nulo
        if (asis == null) {
            // Manejamos el caso cuando no se selecciona ninguna asistencia
            String errorMessage = listaSeleccionada ? "Debes seleccionar una asistencia para obtener la lista a la que pertenece!" : "Debes seleccionar una asistencia!";
            asistenciaManager.mostrarMensaje(Alert.AlertType.ERROR, "Error", errorMessage);
        } else {
            asistenciaManager.cargarFormulario(listaSeleccionada ? FXMLView.FormularioListaAsistencia : FXMLView.FormularioAsistencia, asis);
        }
    }

    @FXML
    private void eliminarAsistencia() {
        // Obtenemos asistencia seleccionada
        Asistencia asis = tblAsistencias.getSelectionModel().getSelectedItem();

        // Verificamos que no sea nulo
        if (asis == null) {
            asistenciaManager.mostrarMensaje(Alert.AlertType.ERROR, "Error", "Debes seleccionar una asistencia!"); // para obtener la lista al cual pertenece
        } else {

            // Eliminamos la asistencia
            if (asistenciaManager.eliminarAsistencia(asis, false)){
                // Mostramos un mensaje
                asistenciaManager.mostrarMensaje(Alert.AlertType.INFORMATION, "Info", "Se ha eliminado la asistencia correctamente!");
            } else {
                asistenciaManager.mostrarMensaje(Alert.AlertType.ERROR, "Error", "No se pudo eliminar la asistencia correctamente!");
            }

            // Actualizamos la tabla de asistencias
            tblAsistencias.refresh();
        }
    }

    // Metodos para filtrar las asistencias

    @FXML
    private void filtrarPorReunion() { asistenciaManager.filtrarAsistencias(); }

    @FXML
    private void filtrarPorMiembro() { asistenciaManager.filtrarAsistencias(); }

    @FXML
    private void filtrarPorEstado() { asistenciaManager.filtrarAsistencias(); }

    // Metodos para limpiar los campos

    @FXML
    private void limpiarFiltros() { asistenciaManager.limpiarFiltros(); }

    // Metodos para los selectores

    @FXML
    private void seleccionarReunion() throws Exception { asistenciaManager.seleccionarReunion(cmbReunion); }

    @FXML
    private void seleccionarMiembro() throws Exception { asistenciaManager.seleccionarMiembro(cmbMiembro); }
}
