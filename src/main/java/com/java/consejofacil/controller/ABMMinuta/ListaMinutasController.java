package com.java.consejofacil.controller.ABMMinuta;

import com.java.consejofacil.model.Minuta;
import com.java.consejofacil.model.Reunion;
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
public class ListaMinutasController implements Initializable {

    // Columnas de la tabla
    @FXML
    @Getter
    private TableColumn<Minuta, Integer> colId;
    @FXML
    @Getter
    private TableColumn<Minuta, String> colTemaTratado;
    @FXML
    @Getter
    private TableColumn<Minuta, Reunion> colReunion;
    @FXML
    @Getter
    private TableColumn<Minuta, String> colDetallesMinuta;

    // TextField para filtrar minutas
    @FXML
    @Getter
    private TextField txtTemaTratado;
    @FXML
    @Getter
    private TextField txtDetallesMinuta;

    // Tabla de minutaa
    @FXML
    @Getter
    private TableView<Minuta> tblMinutas;

    // Filtros adicionales
    @FXML
    @Getter
    private ComboBox<Reunion> cmbReunion;

    // CheckBox Autocompletado
    @FXML
    @Getter
    private CheckBox checkAutocompletado;

    // Listas utilizadas
    @Getter
    private ObservableList<Minuta> minutas = FXCollections.observableArrayList();
    @Getter
    private ObservableList<Minuta> filtroMinutas = FXCollections.observableArrayList();
    @Getter
    private ObservableList<Reunion> reuniones = FXCollections.observableArrayList();

    @Autowired
    @Lazy
    private MinutaManager minutaManager;

    // Logger para gestionar informacion
    @Getter
    private final Logger log = LoggerFactory.getLogger(ListaMinutasController.class);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Validamos el acceso del miembro
        minutaManager.validarAccesoMiembro();
        // Inicializamos la tabla de minutas
        minutaManager.inicializarTablaMinutas();
        // Inicializamos los filtros
        minutaManager.inicializarFiltros();
    }

    @FXML
    private void agregarMinuta() { minutaManager.cargarFormulario(null); }

    @FXML
    private void eliminarMinuta() {
        // Obtenemos minuta seleccionada
        Minuta minuta = tblMinutas.getSelectionModel().getSelectedItem();

        // Verificamos que no sea nulo
        if (minuta == null) {
            minutaManager.mostrarMensaje(true, "Error", "Debes seleccionar una minuta!");
        } else {
            // Eliminamos la minuta
            minutaManager.eliminarMinuta(minuta, false);

            // Actualizamos la tabla de minutas
            tblMinutas.refresh();
        }
    }

    @FXML
    private void modificarMinuta() {
        // Obtenemos minuta seleccionada
        Minuta minuta = tblMinutas.getSelectionModel().getSelectedItem();

        // Verificamos que no sea nulo
        if (minuta == null) {
            minutaManager.mostrarMensaje(true, "Error", "Debes seleccionar una minuta!");
        } else {
            minutaManager.cargarFormulario(minuta);
        }
    }

    @FXML
    private void filtrarPorTema() { minutaManager.filtrarMinutas(); }

    @FXML
    private void filtrarPorReunion() { minutaManager.filtrarMinutas(); }

    @FXML
    private void filtrarPorDetalles() { minutaManager.filtrarMinutas(); }

    @FXML
    private void limpiarFiltros() {
        minutaManager.limpiarFiltros();
    }

    @FXML
    private void seleccionarReunion() throws Exception { minutaManager.seleccionarReunion(cmbReunion); }
}