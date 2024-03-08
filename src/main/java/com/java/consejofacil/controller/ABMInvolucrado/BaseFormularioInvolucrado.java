package com.java.consejofacil.controller.ABMInvolucrado;

import com.java.consejofacil.model.Expediente;
import com.java.consejofacil.model.Involucrado;
import com.java.consejofacil.model.Miembro;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

@Getter
public class BaseFormularioInvolucrado {

    // Campos del formulario
    @FXML
    private ComboBox<Expediente> cmbExpediente;
    @FXML
    private ComboBox<Miembro> cmbInvolucrado;
    @FXML
    private TextArea txtDetallesInvolucrado;

    // Listas utilizadas
    private final ObservableList<Involucrado> involucrados = FXCollections.observableArrayList();
    private final ObservableList<Expediente> expedientes = FXCollections.observableArrayList();
    private final ObservableList<Miembro> miembros = FXCollections.observableArrayList();

    // Involucrado
    @Setter
    private Involucrado involucrado;

    // Manager
    @Autowired
    @Lazy
    private InvolucradoManager involucradoManager;

    // Logger para gestionar informacion
    private final Logger log =  LoggerFactory.getLogger(BaseFormularioInvolucrado.class);

    // Metodo para establecer un involucrado inicial
    public void establecerInvolucrado(Involucrado inv, BaseFormularioInvolucrado controlador) {
        // Establecemos involucrado
        this.involucrado = inv;

        if (involucrado != null) {
            // Proporcionamos la información segun el involucrado
            involucradoManager.autocompletarFormulario(involucrado, controlador);
        }
    }

}
