package com.java.consejofacil.controller.ABMInvolucrado;


import com.java.consejofacil.model.Expediente;
import com.java.consejofacil.model.Involucrado;
import com.java.consejofacil.model.Miembro;
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
public class ListaInvolucradosController implements Initializable {

    // Columnas de la tabla
    @FXML
    @Getter
    private TableColumn<Involucrado, Expediente> colExpediente;
    @FXML
    @Getter
    private TableColumn<Involucrado, Miembro> colInvolucrado;
    @FXML
    @Getter
    private TableColumn<Involucrado, String> colDetallesInvolucrado;

    // Tabla de involucrados
    @FXML
    @Getter
    private TableView<Involucrado> tblInvolucrados;

    // Filtros adicionales
    @FXML
    @Getter
    private ComboBox<Expediente> cmbExpediente;
    @FXML
    @Getter
    private ComboBox<Miembro> cmbInvolucrado;
    @FXML
    @Getter
    private TextField txtDetallesInvolucrado;

    // CheckBox Autocompletado y lista
    @FXML
    @Getter
    private CheckBox checkAutocompletado;
    @FXML
    @Getter
    private CheckBox checkLista;

    // Listas utilizadas
    @Getter
    private ObservableList<Involucrado> involucrados = FXCollections.observableArrayList();
    @Getter
    private ObservableList<Involucrado> filtroInvolucrados = FXCollections.observableArrayList();
    @Getter
    private ObservableList<Expediente> expedientes = FXCollections.observableArrayList();
    @Getter
    private ObservableList<Miembro> miembros = FXCollections.observableArrayList();

    @Autowired
    @Lazy
    private InvolucradoManager involucradoManager;

    // Logger para gestionar informacion
    @Getter
    private final Logger log = LoggerFactory.getLogger(ListaInvolucradosController.class);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicializamos tabla de involucrados
        involucradoManager.inicializarTablaInvolucrados();
        // Inicializamos los filtros
        involucradoManager.inicializarFiltros();
    }

    // Metodos para agregar, modificar y eliminar revisiones

    @FXML
    private void agregarInvolucrado() throws Exception { involucradoManager.cargarFormulario(checkLista.isSelected() ? FXMLView.FormularioListaInvolucrado : FXMLView.FormularioInvolucrado, null); }

    @FXML
    private void modificarInvolucrado() throws Exception {
        // Obtenemos involucrado seleccionado
        Involucrado inv = tblInvolucrados.getSelectionModel().getSelectedItem();

        // Obtenemos la selección de los controles
        boolean listaSeleccionada = checkLista.isSelected();

        // Verificamos que no sea nulo
        if (inv == null) {
            // Manejamos el caso cuando no se selecciona ningún involucrado
            String errorMessage = listaSeleccionada ? "Debes seleccionar un involucrado para obtener la lista a la que pertenece!" : "Debes seleccionar un involucrado!";
            involucradoManager.mostrarMensaje(Alert.AlertType.ERROR, "Error", errorMessage);
        } else {
            involucradoManager.cargarFormulario(listaSeleccionada ? FXMLView.FormularioListaInvolucrado : FXMLView.FormularioInvolucrado, inv);
        }
    }

    @FXML
    private void eliminarInvolucrado() {
        // Obtenemos involucrado seleccionado
        Involucrado inv = tblInvolucrados.getSelectionModel().getSelectedItem();

        // Verificamos que no sea nulo
        if (inv == null) {
            involucradoManager.mostrarMensaje(Alert.AlertType.ERROR, "Error", "Debes seleccionar un involucrado!"); // para obtener la lista al cual pertenece
        } else {

            // Eliminamos el involucrado
            if (involucradoManager.eliminarInvolucrado(inv, false)){
                // Mostramos un mensaje
                involucradoManager.mostrarMensaje(Alert.AlertType.INFORMATION, "Info", "Se ha eliminado el involucrado correctamente!");
            } else {
                involucradoManager.mostrarMensaje(Alert.AlertType.ERROR, "Error", "No se pudo eliminar el involucrado correctamente!");
            }

            // Actualizamos la tabla de involucrados
            tblInvolucrados.refresh();
        }
    }

    // Metodos para filtrar las revisiones

    @FXML
    private void filtrarPorExpediente() { involucradoManager.filtrarInvolucrados(); }

    @FXML
    private void filtrarPorInvolucrado() { involucradoManager.filtrarInvolucrados(); }

    @FXML
    private void filtrarPorDetalles() { involucradoManager.filtrarInvolucrados(); }

    // Metodos para limpiar los campos

    @FXML
    private void limpiarFiltros() { involucradoManager.limpiarFiltros(); }

    // Metodos para los selectores

    @FXML
    private void seleccionarExpediente() throws Exception { involucradoManager.seleccionarExpediente(cmbExpediente); }

    @FXML
    private void seleccionarInvolucrado() throws Exception { involucradoManager.seleccionarMiembro(cmbInvolucrado); }
}