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

@Controller
public class FormularioCambioController implements Initializable {

    // Campos del formulario
    @FXML
    @Getter
    private TextArea txtDetalles;
    @FXML
    @Getter
    private DatePicker dtpFechaCambio;
    @FXML
    @Getter
    private ComboBox<TipoCambio> cmbTipo;
    @FXML
    @Getter
    private ComboBox<Miembro> cmbResponsable;

    // Listas utilizadas
    @Getter
    private ObservableList<Miembro> miembros = FXCollections.observableArrayList();
    @Getter
    private ObservableList<TipoCambio> tiposCambios = FXCollections.observableArrayList();

    // Cambio
    @Getter
    private HistorialCambio cambio;

    @Autowired
    @Lazy
    private HistorialCambioManager historialCambioManager;

    // Logger para gestionar informacion
    @Getter
    private final Logger log = LoggerFactory.getLogger(FormularioCambioController.class);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Validamos el acceso del miembro
        historialCambioManager.validarAccesoMiembro();

        // Inicializamos los combos del formulario
        historialCambioManager.inicializarCombosFormulario();

        // Estblecemos en nulo al cambio
        cambio = null;

        // Establecemos el dia de hoy
        dtpFechaCambio.setValue(LocalDate.now());
    }

    @FXML
    private void nuevoCambio() { historialCambioManager.limpiarFormulario(); }

    @FXML
    private void guardarCambio() {
        historialCambioManager.guardarCambio();
    }

    @FXML
    private void seleccionarResponsable() throws Exception { historialCambioManager.seleccionarMiembro(cmbResponsable); }

    public void establecerCambio(HistorialCambio cmb) {
        // Establecemos cambio
        this.cambio = cmb;

        if (cambio != null) {
            // Proporcionamos la información segun el cambio
            historialCambioManager.autocompletarFormulario(cambio);
        }
    }
}
