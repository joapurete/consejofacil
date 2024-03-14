package com.java.consejofacil.controller.ABMAsistencia;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import java.net.URL;
import java.util.ResourceBundle;

@Getter
@Controller
public class FormularioAsistenciaController extends BaseFormularioAsistencia implements Initializable {

    // Logger para gestionar informacion
    private final Logger log = LoggerFactory.getLogger(FormularioAsistenciaController.class);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Validamos el acceso del miembro
        getAsistenciaManager().validarAccesoMiembro();

        // Inicializamos combos del formulario
        getAsistenciaManager().inicializarCombosFormulario(this);

        // Estblecemos en nulo la asistencia
        setAsistencia(null);
    }

    @FXML
    private void nuevaAsistencia() { getAsistenciaManager().limpiarFormulario(this); }

    @FXML
    private void guardarAsistencia() {
        getAsistenciaManager().guardarAsistencia();
    }

    @FXML
    private void seleccionarReunion() throws Exception { getAsistenciaManager().seleccionarReunion(getCmbReunion()); }

    @FXML
    private void seleccionarMiembro() throws Exception { getAsistenciaManager().seleccionarMiembro(getCmbMiembro()); }
}