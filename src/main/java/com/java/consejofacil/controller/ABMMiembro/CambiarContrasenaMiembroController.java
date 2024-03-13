package com.java.consejofacil.controller.ABMMiembro;

import com.java.consejofacil.model.Miembro;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class CambiarContrasenaMiembroController implements Initializable {

    // Campos del formulario
    @FXML
    @Getter
    private PasswordField txtContrasenaActual;
    @FXML
    @Getter
    private PasswordField txtContrasenaNueva;
    @FXML
    @Getter
    private PasswordField txtRepetirContrasenaNueva;


    // Reunión
    @Getter @Setter
    private Miembro miembro;

    @Autowired
    @Lazy
    private MiembroManager miembroManager;

    // Logger para gestionar informacion
    @Getter
    private final Logger log = LoggerFactory.getLogger(CambiarContrasenaMiembroController.class);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Estblecemos en nulo aL miembro
        miembro = null;
    }

    @FXML
    private void nuevaContrasena() { miembroManager.limpiarFormulario(this); }

    @FXML
    private void cambiarContrasena() {
        miembroManager.guardarContrasenaNueva();
    }
}