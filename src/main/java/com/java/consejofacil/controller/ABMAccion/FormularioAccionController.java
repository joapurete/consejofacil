package com.java.consejofacil.controller.ABMAccion;

import com.java.consejofacil.model.Accion;
import com.java.consejofacil.model.Expediente;
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
public class FormularioAccionController implements Initializable {

    // Campos del formulario
    @FXML
    @Getter
    private TextArea txtDetallesAccion;
    @FXML
    @Getter
    private DatePicker dtpFechaAccion;
    @FXML
    @Getter
    private ComboBox<Expediente> cmbExpediente;

    // Listas utilizadas
    @Getter
    private ObservableList<Accion> acciones = FXCollections.observableArrayList();
    @Getter
    private ObservableList<Expediente> expedientes = FXCollections.observableArrayList();

    // Accion
    @Getter
    private Accion accion;

    @Autowired
    @Lazy
    private AccionManager accionManager;

    // Logger para gestionar informacion
    @Getter
    private final Logger log = LoggerFactory.getLogger(FormularioAccionController.class);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Validamos el acceso del miembro
        accionManager.validarAccesoMiembro();

        // Inicializamos los combos del formulario
        accionManager.inicializarCombosFormulario();

        // Estblecemos en nulo a la accion
        accion = null;

        // Establecemos el dia de hoy
        dtpFechaAccion.setValue(LocalDate.now());
    }

    @FXML
    private void nuevaAccion() { accionManager.limpiarFormulario(); }

    @FXML
    private void guardarAccion() {
        accionManager.guardarAccion();
    }

    @FXML
    private void seleccionarExpediente() { accionManager.seleccionarExpediente(cmbExpediente); }

    public void establecerAccion(Accion a) {
        // Establecemos accion
        this.accion = a;

        if (accion != null) {
            // Proporcionamos la información segun la accion
            accionManager.autocompletarFormulario(accion);
        }
    }
}