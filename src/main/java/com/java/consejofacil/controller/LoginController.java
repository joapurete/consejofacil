package com.java.consejofacil.controller;

import com.java.consejofacil.controller.ABMMiembro.MiembroManager;
import com.java.consejofacil.security.SecurityConfig;
import com.java.consejofacil.config.StageManager;
import com.java.consejofacil.helper.Alertas.AlertHelper;
import com.java.consejofacil.helper.Validaciones.DataValidatorHelper;
import com.java.consejofacil.view.FXMLView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    // CheckBox para recordar los credenciales
    @FXML
    private CheckBox checkRecordar;

    // Controladores de los fxml
    @Autowired
    @Lazy
    private MiembroManager miembroManager;

    // Stage Manager
    @Autowired
    @Lazy
    private StageManager stageManager;

    // Componente para validar las credenciales del usuario automaticamente
    @Autowired
    @Lazy
    private SecurityConfig securityConfig;

    // Logger para mostrar informacion
    @Getter
    private final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // Cargar las credenciales si existen
        securityConfig.autocargarCredenciales(txtDni, txtContrasena);
    }

    @FXML
    void iniciarSesion() {

        if (validarCamposFormulario()) {

            // Obtenemos los datos del formulario
            int dni = Integer.parseInt(txtDni.getText().trim());
            String contrasena = txtContrasena.getText().trim();

            if (securityConfig.validarCredenciales(dni, contrasena)) {

                if (checkRecordar.isSelected()) {
                    // Guardar las credenciales en el archivo properties
                    securityConfig.guardarCredenciales(String.valueOf(dni), contrasena);
                }

                // Si el inicio de sesion fue exitoso, cambiamos al inicio
                stageManager.switchScene(FXMLView.MainLayout);
            }
        }
    }

    @FXML
    void crearCuenta() {
        miembroManager.cargarFormulario(null, this);
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
        AlertHelper.mostrarMensaje(false, "Info", "Por favor, comunícate con el personal administrativo para obtener ayuda y recuperar el acceso a tu cuenta.");
    }
}
