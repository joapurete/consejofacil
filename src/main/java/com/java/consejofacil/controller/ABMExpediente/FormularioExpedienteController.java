package com.java.consejofacil.controller.ABMExpediente;

import com.java.consejofacil.model.EstadoExpediente;
import com.java.consejofacil.model.Expediente;
import com.java.consejofacil.model.Miembro;
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
public class FormularioExpedienteController implements Initializable {

    // Campos del formulario
    @FXML
    @Getter
    private TextArea txtTextoNota;
    @FXML
    @Getter
    private DatePicker dtpFechaIngreso;
    @FXML
    @Getter
    private ComboBox<EstadoExpediente> cmbEstadoExpediente;
    @FXML
    @Getter
    private ComboBox<Miembro> cmbIniciante;

    // Listas utilizadas
    @Getter
    private ObservableList<Miembro> miembros = FXCollections.observableArrayList();
    @Getter
    private ObservableList<EstadoExpediente> estadosExpedientes = FXCollections.observableArrayList();

    // Expediente
    @Getter
    private Expediente expediente;

    @Autowired
    @Lazy
    private ExpedienteManager expedienteManager;

    // Logger para gestionar informacion
    @Getter
    private final Logger log = LoggerFactory.getLogger(FormularioExpedienteController.class);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Validamos el acceso del miembro
        expedienteManager.validarAccesoMiembro();

        // Inicializamos los combos del formulario
        expedienteManager.inicializarCombosFormulario();

        // Estblecemos en nulo al expediente
        expediente = null;

        // Establecemos el dia de hoy
        dtpFechaIngreso.setValue(LocalDate.now());
    }

    @FXML
    private void nuevoExpediente() { expedienteManager.limpiarFormulario(); }

    @FXML
    private void guardarExpediente() {
        expedienteManager.guardarExpediente();
    }

    @FXML
    private void seleccionarIniciante() throws Exception { expedienteManager.seleccionarMiembro(cmbIniciante); }

    public void establecerExpediente(Expediente exp) {
        // Establecemos expediente
        this.expediente = exp;

        if (expediente != null) {
            // Proporcionamos la información segun el expediente
            expedienteManager.autocompletarFormulario(expediente);
        }
    }
}
