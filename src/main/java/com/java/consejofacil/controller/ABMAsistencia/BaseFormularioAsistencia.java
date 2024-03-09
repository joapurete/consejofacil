package com.java.consejofacil.controller.ABMAsistencia;

import com.java.consejofacil.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

@Getter
public class BaseFormularioAsistencia {

    // Campos del formulario
    @FXML
    private ComboBox<Reunion> cmbReunion;
    @FXML
    private ComboBox<Miembro> cmbMiembro;
    @FXML
    private ComboBox<EstadoAsistencia> cmbEstado;

    // Listas utilizadas
    private final ObservableList<Asistencia> asistencias = FXCollections.observableArrayList();
    private final ObservableList<Reunion> reuniones = FXCollections.observableArrayList();
    private final ObservableList<Miembro> miembros = FXCollections.observableArrayList();
    private final ObservableList<EstadoAsistencia> estadosAsistencias = FXCollections.observableArrayList();

    // Asistencia
    @Setter
    private Asistencia asistencia;

    // Manager
    @Autowired
    @Lazy
    private AsistenciaManager asistenciaManager;

    // Logger para gestionar informacion
    private final Logger log =  LoggerFactory.getLogger(BaseFormularioAsistencia.class);

    // Metodo para establecer una asistencia inicial
    public void establecerAsistencia(Asistencia asis, BaseFormularioAsistencia controlador) {
        // Establecemos la asistencia
        this.asistencia = asis;

        if (asistencia != null) {
            // Proporcionamos la información segun la asitencia
            asistenciaManager.autocompletarFormulario(asistencia, controlador);
        }
    }

}

