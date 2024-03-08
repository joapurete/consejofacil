package com.java.consejofacil.controller.ABMExpediente;

import com.java.consejofacil.model.EstadoExpediente;
import com.java.consejofacil.model.Expediente;
import com.java.consejofacil.model.Miembro;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.time.LocalDate;

@Getter
public class BaseTablaExpedientes {

    // Columnas de la tabla
    @FXML
    private TableColumn<Expediente, Integer> colId;
    @FXML
    private TableColumn<Expediente, String> colTextoNota;
    @FXML
    private TableColumn<Expediente, LocalDate> colFechaIngreso;
    @FXML
    private TableColumn<Expediente, Miembro> colIniciante;
    @FXML
    private TableColumn<Expediente, EstadoExpediente> colEstado;

    // TextField para filtrar expedientes
    @FXML
    private TextField txtNota;

    // Tabla de expedientes
    @FXML
    private TableView<Expediente> tblExpedientes;

    // Filtros adicionales
    @FXML
    private DatePicker dtpFechaIngreso;
    @FXML
    private ComboBox<Miembro> cmbIniciante;
    @FXML
    private ComboBox<EstadoExpediente> cmbEstado;

    // Listas utilizadas
    private final ObservableList<Expediente> expedientes = FXCollections.observableArrayList();
    private final ObservableList<Expediente> filtroExpedientes = FXCollections.observableArrayList();
    private final ObservableList<Miembro> miembros = FXCollections.observableArrayList();
    private final ObservableList<EstadoExpediente> estadosExpedientes = FXCollections.observableArrayList();

    // Manager
    @Autowired
    @Lazy
    private ExpedienteManager expedienteManager;

    // Logger para gestionar informacion
    private final Logger log =  LoggerFactory.getLogger(BaseTablaExpedientes.class);

}
