package com.java.consejofacil.controller;

import com.java.consejofacil.security.SecurityConfig;
import com.java.consejofacil.config.StageManager;
import com.java.consejofacil.helper.Alertas.AlertHelper;
import com.java.consejofacil.helper.Validaciones.DataValidatorHelper;
import com.java.consejofacil.view.FXMLView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

@Controller
public class LoginController implements Initializable {

    // Campos del formulario
    @FXML
    private TextField txtDni;
    @FXML
    private PasswordField txtContrasena;

    // Stage Manager
    @Autowired
    @Lazy
    private StageManager stageManager;

    // Componente para validar las credenciales del usuario automaticamente
    @Autowired
    @Lazy
    private SecurityConfig securityConfig;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    void iniciarSesion() {

        if (validarCamposFormulario()) {

            // Obtenemos los datos del formulario
            int dni = Integer.parseInt(txtDni.getText().trim());
            String contrasena = txtContrasena.getText().trim();

            if (securityConfig.validarCredenciales(dni, contrasena)) {
                // Si el inicio de sesion fue exitoso, cambiamos al inicio
                stageManager.switchScene(FXMLView.MainLayout);
            }
        }
    }

    @FXML
    void crearCuenta() {
    }

    private boolean validarCamposFormulario() {
        ArrayList<String> errores = new ArrayList<>();

        // Obtenemos los datos del formulario
        String dni = txtDni.getText().trim();
        String contrasena = txtContrasena.getText().trim();

        //Verificamos que el DNI no este vacio, que solo contenga digitos, y que no esté en uso
        if (dni.isEmpty()) {
            errores.add("Por favor, ingrese un DNI.");
        } else if (DataValidatorHelper.validarDni(dni)) {
            errores.add("El DNI debe contener entre 7 u 8 dígitos numéricos.");
        }

        if (contrasena.isEmpty()) {
            errores.add("Por favor, ingrese un contraseña.");
        }

        // Verificamos si hay errores
        if (!errores.isEmpty()) {
            AlertHelper.mostrarCadenaMensajes(true, errores, "Se ha producido uno o varios errores:", "Error");
            return false;
        }

        return true;
    }

    @FXML
    private void olvidasteContrasena() {


    }
}
