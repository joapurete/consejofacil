package com.java.consejofacil.controller.ABMInvolucrado;

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
public class FormularioInvolucradoController extends BaseFormularioInvolucrado implements Initializable {

    // Logger para gestionar informacion
    private final Logger log = LoggerFactory.getLogger(FormularioInvolucradoController.class);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Validamos el acceso del miembro
        getInvolucradoManager().validarAccesoMiembro();
        // Inicializamos combos del formulario
        getInvolucradoManager().inicializarCombosFormulario(this);

        // Estblecemos en nulo al involucrado
        setInvolucrado(null);
    }

    @FXML
    private void nuevoInvolucrado() { getInvolucradoManager().limpiarFormulario(this); }

    @FXML
    private void guardarInvolucrado() {
        getInvolucradoManager().guardarInvolucrado();
    }

    @FXML
    private void seleccionarExpediente() throws Exception { getInvolucradoManager().seleccionarExpediente(getCmbExpediente()); }

    @FXML
    private void seleccionarInvolucrado() throws Exception { getInvolucradoManager().seleccionarMiembro(getCmbInvolucrado()); }
}