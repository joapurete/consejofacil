package com.java.consejofacil.controller.ABMRevision;

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
public class FormularioRevisionController extends BaseFormularioRevision implements Initializable {

    // Logger para gestionar informacion
    private final Logger log = LoggerFactory.getLogger(FormularioRevisionController.class);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Validamos el acceso del miembro
        getRevisionManager().validarAccesoMiembro();

        // Inicializamos combos
        getRevisionManager().inicializarCombosFormulario(this);

        // Estblecemos en nulo a la revisión
        setRevision(null);
    }

    // Limpiamos el fomulario

    @FXML
    private void nuevaRevision() { getRevisionManager().limpiarFormulario(this); }

    //

    @FXML
    private void guardarRevision() {
        getRevisionManager().guardarRevision();
    }

    @FXML
    private void seleccionarReunion() throws Exception { getRevisionManager().seleccionarReunion(getCmbReunion()); }

    @FXML
    private void seleccionarExpediente() throws Exception { getRevisionManager().seleccionarExpediente(getCmbExpediente()); }
}