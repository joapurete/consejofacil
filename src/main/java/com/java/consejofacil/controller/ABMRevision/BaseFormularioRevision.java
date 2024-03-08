package com.java.consejofacil.controller.ABMRevision;

import com.java.consejofacil.model.Expediente;
import com.java.consejofacil.model.Reunion;
import com.java.consejofacil.model.Revision;
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
public class BaseFormularioRevision {

    // Campos del formulario
    @FXML
    private ComboBox<Reunion> cmbReunion;
    @FXML
    private ComboBox<Expediente> cmbExpediente;
    @FXML
    private TextArea txtDetallesRevision;

    // Listas utilizadas
    private final ObservableList<Revision> revisiones = FXCollections.observableArrayList();
    private final ObservableList<Reunion> reuniones = FXCollections.observableArrayList();
    private final ObservableList<Expediente> expedientes = FXCollections.observableArrayList();

    // Revisión
    @Setter
    private Revision revision;

    // Administradores necesarios
    @Autowired
    @Lazy
    private RevisionManager revisionManager;

    // Logger para gestionar informacion
    @Getter
    private final Logger log = LoggerFactory.getLogger(BaseFormularioRevision.class);

    // Metodo para establecer una revision inicial
    public void establecerRevision(Revision rev, BaseFormularioRevision controlador) {
        // Establecemos revisión
        this.revision = rev;

        if (revision != null) {
            // Proporcionamos la información segun la revisión
            revisionManager.autocompletarFormulario(revision, controlador);
        }
    }
}
