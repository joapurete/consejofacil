package com.java.consejofacil.controller.ABMMiembro;

import com.java.consejofacil.model.Cargo;
import com.java.consejofacil.model.EstadoMiembro;
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
public class BaseTablaMiembros {

    // Columnas de la tabla
    @FXML
    private TableColumn<Miembro, Integer> colDni;
    @FXML
    private TableColumn<Miembro, String> colNombre;
    @FXML
    private TableColumn<Miembro, LocalDate> colFechaNac;
    @FXML
    private TableColumn<Miembro, Cargo> colCargo;
    @FXML
    private TableColumn<Miembro, EstadoMiembro> colEstado;

    // TextField para filtrar miembros
    @FXML
    private TextField txtNombre;

    // Tabla de miembros
    @FXML
    private TableView<Miembro> tblMiembros;

    // Filtros adicionales
    @FXML
    private DatePicker dtpFechaNac;
    @FXML
    private ComboBox<Cargo> cmbCargo;
    @FXML
    private ComboBox<EstadoMiembro> cmbEstado;

    // Listas utilizadas
    private final ObservableList<Miembro> miembros = FXCollections.observableArrayList();
    private final ObservableList<Miembro> filtroMiembros = FXCollections.observableArrayList();
    private final ObservableList<Cargo> cargos = FXCollections.observableArrayList();
    private final ObservableList<EstadoMiembro> estadosMiembros = FXCollections.observableArrayList();

    // Manager
    @Autowired
    @Lazy
    private MiembroManager miembroManager;

    // Logger para gestionar informacion
    private final Logger log =  LoggerFactory.getLogger(BaseTablaMiembros.class);

}