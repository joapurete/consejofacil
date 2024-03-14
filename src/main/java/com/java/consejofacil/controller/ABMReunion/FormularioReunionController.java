package com.java.consejofacil.controller.ABMReunion;

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
import java.time.LocalDate;
import java.util.ResourceBundle;

@Controller
public class FormularioReunionController implements Initializable {

    // Campos del formulario
    @FXML
    @Getter
    private TextField txtAsunto;
    @FXML
    @Getter
    private DatePicker dtpFechaReunion;

    // Listas utilizadas
    @Getter
    private ObservableList<Reunion> reuniones = FXCollections.observableArrayList();

    // Reunión
    @Getter
    private Reunion reunion;

    @Autowired
    @Lazy
    private ReunionManager reunionManager;

    // Logger para gestionar informacion
    @Getter
    private final Logger log = LoggerFactory.getLogger(FormularioReunionController.class);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Validamos el acceso del miembro
        reunionManager.validarAccesoMiembro();

        // Estblecemos en nulo a la reunión
        reunion = null;

        // Establecemos el dia de hoy
        dtpFechaReunion.setValue(LocalDate.now());
    }

    @FXML
    private void nuevaReunion() { reunionManager.limpiarFormulario(); }

    @FXML
    private void guardarReunion() {
        reunionManager.guardarReunion();
    }

    public void establecerReunion(Reunion r) {
        // Establecemos reunión
        this.reunion = r;

        if (reunion != null) {
            // Proporcionamos la información segun la reunión
            reunionManager.autocompletarFormulario(reunion);
        }
    }
}