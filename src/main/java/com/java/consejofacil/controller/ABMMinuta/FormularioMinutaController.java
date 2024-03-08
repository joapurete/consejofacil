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
public class FormularioMinutaController implements Initializable {

    // Campos del formulario
    @FXML
    @Getter
    private TextArea txtDetallesMinuta;
    @FXML
    @Getter
    private TextField txtTemaTratado;
    @FXML
    @Getter
    private ComboBox<Reunion> cmbReunion;

    // Listas utilizadas
    @Getter
    private ObservableList<Minuta> minutas = FXCollections.observableArrayList();
    @Getter
    private ObservableList<Reunion> reuniones = FXCollections.observableArrayList();

    // Minuta
    @Getter
    private Minuta minuta;

    @Autowired
    @Lazy
    private MinutaManager minutaManager;

    // Logger para gestionar informacion
    @Getter
    private final Logger log = LoggerFactory.getLogger(FormularioMinutaController.class);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicializamos los combos del formulario
        minutaManager.inicializarCombosFormulario();

        // Estblecemos en nulo a la minuta
        minuta = null;
    }

    @FXML
    private void nuevaMinuta() { minutaManager.limpiarFormulario(); }

    @FXML
    private void guardarMinuta() {
        minutaManager.guardarMinuta();
    }

    @FXML
    private void seleccionarReunion() throws Exception { minutaManager.seleccionarReunion(cmbReunion); }

    public void establecerMinuta(Minuta min) {
        // Establecemos minuta
        this.minuta = min;

        if (minuta != null) {
            // Proporcionamos la información segun la minuta
            minutaManager.autocompletarFormulario(minuta);
        }
    }
}