package com.java.consejofacil.controller.ABMRevision;

import com.java.consejofacil.model.Expediente;
import com.java.consejofacil.model.Reunion;
import com.java.consejofacil.model.Revision;
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

@Getter
@Controller
public class ListaRevisionesController implements Initializable {

    // Columnas de la tabla
    @FXML
    private TableColumn<Revision, Reunion> colReunion;
    @FXML
    private TableColumn<Revision, Expediente> colExpediente;
    @FXML
    private TableColumn<Revision, String> colDetallesRevision;

    // Tabla de revisiones
    @FXML
    private TableView<Revision> tblRevisiones;

    // Filtros adicionales
    @FXML
    private TextField txtDetalles;
    @FXML
    private ComboBox<Reunion> cmbReunion;
    @FXML
    private ComboBox<Expediente> cmbExpediente;

    // CheckBox Autocompletado y lista
    @FXML
    private CheckBox checkAutocompletado;
    @FXML
    private CheckBox checkLista;

    // Listas utilizadas
    private final ObservableList<Revision> revisiones = FXCollections.observableArrayList();
    private final ObservableList<Revision> filtroRevisiones = FXCollections.observableArrayList();
    private final ObservableList<Reunion> reuniones = FXCollections.observableArrayList();
    private final ObservableList<Expediente> expedientes = FXCollections.observableArrayList();

    // Administradores necesarios
    @Autowired
    @Lazy
    private RevisionManager revisionManager;

    // Logger para gestionar informacion
    private final Logger log = LoggerFactory.getLogger(ListaRevisionesController.class);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Validamos el acceso del miembro
        revisionManager.validarAccesoMiembro();
        // Inicializamos la tabla de revisiones
        revisionManager.inicializarTablaRevisiones();
        // Inicializamos los filtros
        revisionManager.inicializarFiltros();
    }

    // Metodos para agregar, modificar y eliminar revisiones

    @FXML
    private void agregarRevision() { revisionManager.cargarFormulario(checkLista.isSelected() ? FXMLView.FormularioListaRevision : FXMLView.FormularioRevision, null); }

    @FXML
    private void modificarRevision() {
        // Obtenemos revisión seleccionada
        Revision rev = tblRevisiones.getSelectionModel().getSelectedItem();

        // Obtenemos la selección de los controles
        boolean listaSeleccionada = checkLista.isSelected();

        // Verificamos que no sea nulo
        if (rev == null) {
            // Manejamos el caso cuando no se selecciona ninguna revisión
            String errorMessage = listaSeleccionada ? "Debes seleccionar una revisión para obtener la lista a la que pertenece!" : "Debes seleccionar una revisión!";
            revisionManager.mostrarMensaje(true, "Error", errorMessage);
        } else {
            revisionManager.cargarFormulario(checkLista.isSelected() ? FXMLView.FormularioListaRevision : FXMLView.FormularioRevision, rev);
        }
    }

    @FXML
    private void eliminarRevision() {
        // Obtenemos revisión seleccionada
        Revision rev = tblRevisiones.getSelectionModel().getSelectedItem();
        // Verificamos que no sea nulo
        if (rev == null) {
            revisionManager.mostrarMensaje(true, "Error", "Debes seleccionar una revisión!");
        } else {
            // Eliminamos la revisión
            if (revisionManager.eliminarRevision(rev, false)) {
                // Mostramos un mensaje
                revisionManager.mostrarMensaje(false, "Info", "Se ha eliminado la revisión correctamente!");
            } else {
                revisionManager.mostrarMensaje(true, "Error", "No se pudo eliminar la revisión correctamente!");
            }

            // Actualizamos la tabla de revisiones
            tblRevisiones.refresh();
        }
    }

    // Metodos para filtrar las revisiones

    @FXML
    private void filtrarPorDetalles() { revisionManager.filtrarRevisiones(); }

    @FXML
    private void filtrarPorReunion() { revisionManager.filtrarRevisiones(); }

    @FXML
    private void filtrarPorExpediente() { revisionManager.filtrarRevisiones(); }

    // Metodos para limpiar los campos

    @FXML
    private void limpiarFiltros() { revisionManager.limpiarFiltros(); }

    // Metodos para los selectores

    @FXML
    private void seleccionarReunion() throws Exception { revisionManager.seleccionarReunion(cmbReunion); }

    @FXML
    private void seleccionarExpediente() throws Exception { revisionManager.seleccionarExpediente(cmbExpediente); }

}
