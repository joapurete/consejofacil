package com.java.consejofacil.controller.ABMMiembro;

import com.java.consejofacil.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;

import java.net.URL;

import javafx.scene.image.ImageView;

import java.util.ResourceBundle;

@Controller
public class FormularioMiembroController implements Initializable {

    // Campos del formulario
    @FXML
    @Getter
    private TextField txtDni;
    @FXML
    @Getter
    private TextField txtNombre;
    @FXML
    @Getter
    private TextField txtApellido;
    @FXML
    @Getter
    private DatePicker dtpFechaNac;
    @FXML
    @Getter
    private TextField txtTelefono;
    @FXML
    @Getter
    private TextField txtDireccion;
    @FXML
    @Getter
    private TextField txtCorreo;
    @FXML
    @Getter
    private PasswordField txtContrasena;
    @FXML
    @Getter
    private ComboBox<Cargo> cmbCargo;
    @FXML
    @Getter
    private ComboBox<EstadoMiembro> cmbEstado;
    @FXML
    @Getter
    private ImageView imgFotoPerfil;

    // Botones y Labels que se deben mostrar / ocultar
    @FXML
    @Getter
    private Button btnCambiarContrasena;
    @FXML
    @Getter
    private Label lblContrasena;
    @FXML
    @Getter
    private Label lblCargo;
    @FXML
    @Getter
    private Label lblEstado;

    // Variables relacionadas con la foto de perfil
    @Getter
    @Setter
    private Image nuevaFotoPerfil;
    @Getter
    @Setter
    private Boolean seEliminoFotoPerfil;

    // Listas utilizadas
    @Getter
    private ObservableList<Cargo> cargos = FXCollections.observableArrayList();
    @Getter
    private ObservableList<EstadoMiembro> estadosMiembros = FXCollections.observableArrayList();

    // Miembro
    @Getter @Setter
    private Miembro miembro;

    @Autowired
    @Lazy
    private MiembroManager miembroManager;

    // Logger para gestionar informacion
    @Getter
    private final Logger log = LoggerFactory.getLogger(FormularioMiembroController.class);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicializamos los combos del formulario
        miembroManager.inicializarCombosFormulario();

        // Limpiamos las variables relacionadas con la foto de perfil
        // Inicialmente, no queremos eliminar la foto de perfil
        nuevaFotoPerfil = null;
        seEliminoFotoPerfil = false;

        // Estblecemos en nulo al miembro
        miembro = null;
    }

    @FXML
    private void nuevoMiembro() {
        miembroManager.limpiarFormulario(this);
    }

    @FXML
    private void guardarMiembro() {
        miembroManager.guardarMiembro();
    }

    @FXML
    private void seleccionarFotoPerfil() {
        miembroManager.seleccionarFotoPerfil();
    }

    @FXML
    private void eliminarFotoPerfil() { miembroManager.eliminarFotoPerfil(); }

    @FXML
    private void cambiarContrasena() {
        miembroManager.cambiarContrasena();
    }

    // Metodo para determinar si se está agregando un nuevo miembro

    public boolean esNuevoMiembro() {
        return miembro == null;
    }

    public void establecerMiembro(Miembro m) {
        // Establecemos miembro
        this.miembro = m;

        if (miembro != null) {
            // Proporcionamos la información segun el miembro
            miembroManager.autocompletarFormulario(miembro);
        }
    }
}
