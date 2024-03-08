package com.java.consejofacil.controller.ABMReunion;

import com.java.consejofacil.model.Reunion;
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
public class BaseTablaReuniones {

    // Columnas de la tabla
    @FXML
    private TableColumn<Reunion, Integer> colId;
    @FXML
    private TableColumn<Reunion, LocalDate> colFechaReunion;
    @FXML
    private TableColumn<Reunion, String> colAsunto;

    // Tabla de reuniones
    @FXML
    private TableView<Reunion> tblReuniones;

    // Filtros utilizados
    @FXML
    private DatePicker dtpFechaReunion;
    @FXML
    private TextField txtAsunto;

    // Listas utilizadas
    private final ObservableList<Reunion> reuniones = FXCollections.observableArrayList();
    private final ObservableList<Reunion> filtroReuniones = FXCollections.observableArrayList();

    // Manager
    @Autowired
    @Lazy
    private ReunionManager reunionManager;

    // Logger para gestionar informacion
    private final Logger log = LoggerFactory.getLogger(BaseTablaReuniones.class);
}
