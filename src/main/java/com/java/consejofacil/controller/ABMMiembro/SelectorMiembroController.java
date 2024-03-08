package com.java.consejofacil.controller.ABMMiembro;

import com.java.consejofacil.config.StageManager;
import com.java.consejofacil.model.Cargo;
import com.java.consejofacil.model.EstadoExpediente;
import com.java.consejofacil.model.EstadoMiembro;
import com.java.consejofacil.model.Miembro;
import com.java.consejofacil.service.Cargo.CargoServiceImpl;
import com.java.consejofacil.service.EstadoMiembro.EstadoMiembroServiceImpl;
import com.java.consejofacil.service.Miembro.MiembroServiceImpl;
import com.java.consejofacil.view.FXMLView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

@SuppressWarnings("SpellCheckingInspection")
@Controller
public class SelectorMiembroController implements Initializable {

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
    @FXML
    private ComboBox<Cargo> cmbCargo;
    @FXML
    private ComboBox<EstadoMiembro> cmbEstado;

    // Tabla de miembros
    @FXML
    private TableView<Miembro> tblMiembros;

    // Servicios utilizados
    @Autowired
    @Lazy
    private MiembroServiceImpl miembroService;
    @Autowired
    @Lazy
    private CargoServiceImpl cargoService;
    @Autowired
    @Lazy
    private EstadoMiembroServiceImpl estadoMiembroService;

    // Stage Manager
    @Autowired
    @Lazy
    private StageManager stageManager;

    // Listas utilizadas
    private ObservableList<Miembro> miembros = FXCollections.observableArrayList();
    private ObservableList<Miembro> filtroMiembros = FXCollections.observableArrayList();
    private ObservableList<Cargo> cargos = FXCollections.observableArrayList();
    private ObservableList<EstadoMiembro> estadosMiembros = FXCollections.observableArrayList();

    // Miembro
    @Getter
    private Miembro miembro;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        inicializarTablaMiembros();
        inicializarFiltros();

        // Establecemos en nulo al miembro
        miembro = null;
    }

    private void inicializarTablaMiembros() {
        // Asociamos las Columnas de la tabla
        colDni.setCellValueFactory(new PropertyValueFactory<>("dni"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colFechaNac.setCellValueFactory(new PropertyValueFactory<>("fechaNac"));
        colCargo.setCellValueFactory(new PropertyValueFactory<>("cargo"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estadoMiembro"));

        colocarNombreCompleto(colNombre);

        // Cargamos las listas
        miembros.clear();
        filtroMiembros.clear();
        miembros.addAll(miembroService.findAll());
        filtroMiembros.addAll(miembros);

        // Cargamos las tablas
        tblMiembros.setItems(filtroMiembros);
    }

    private void inicializarFiltros() {
        // Cargamos lista y combo de cargos
        cargos.clear();
        cargos.addAll(cargoService.findAll());
        cmbCargo.setItems(cargos);

        // Cargamos lista y combo de estados
        estadosMiembros.clear();
        estadosMiembros.addAll(estadoMiembroService.findAll());
        cmbEstado.setItems(estadosMiembros);
    }

    private void colocarNombreCompleto(TableColumn<Miembro, String> columna) {
        // Establecemos una fábrica de celdas para la columna
        // Devolvemos una nueva celda para la visualizacion de datos
        columna.setCellFactory(col -> new TableCell<Miembro, String>() {
            @Override
            protected void updateItem(String nombre, boolean empty) {
                // Llamamos a la clase padre para cualquier inicializacion necesaria
                super.updateItem(nombre, empty);
                // Verificamos si el nombre es nulo
                if (nombre == null || empty) {
                    setText(null);
                } else {
                    // Si es diferente de nulo, concatenamos el nombre y apellido del miembro correspondiente
                    setText(nombre + " " + getTableView().getItems().get(getIndex()).getApellido());
                }
            }
        });
    }

    @FXML
    private void agregarMiembro(ActionEvent event) {
        // Obtenemos el miembro seleccionado
        miembro = tblMiembros.getSelectionModel().getSelectedItem();

        // Verificamos que sea diferente de nulo
        if (miembro == null) {
            mostrarMensaje(Alert.AlertType.ERROR, "Error", "Debes seleccionar un miembro del consejo!");
        } else {
            // Salimos del modal
            stageManager.closeModal(FXMLView.SelectorMiembro.getKey());
        }
    }

    @FXML
    private void filtrarPorNombre(KeyEvent event) {
        // Filtramos por ID, nombre y apellido
        filtrarMiembros();
    }

    @FXML
    private void filtrarPorCargo(ActionEvent event) {
        // Filtramos por cargo
        filtrarMiembros();
    }

    @FXML
    private void filtrarPorEstado(ActionEvent event) {
        // Filtramos por estado
        filtrarMiembros();
    }

    @FXML
    private void filtrarMiembros() {
        // Limpiamos la lista filtro de miembros
        filtroMiembros.clear();

        // Filtramos todos los miembros
        for (Miembro m : miembros) {
            if (aplicarFiltro(m)) {
                filtroMiembros.add(m);
            }
        }

        // Actualizamos la tabla
        tblMiembros.setItems(filtroMiembros);
    }

    private boolean aplicarFiltro(Miembro m) {
        // Obtenemos el filtro (ID, Nombre, Apellido)
        String nombre = txtNombre.getText().trim();
        Cargo cargo = cmbCargo.getValue();
        EstadoMiembro estado = cmbEstado.getValue();

        // Filtramos en base al filtro obtenido
        return (String.valueOf(m.getDni()).toLowerCase().contains(nombre.toLowerCase())
                || m.getNombre().toLowerCase().contains(nombre.toLowerCase())
                || m.getApellido().toLowerCase().contains(nombre.toLowerCase()))
                && (cargo == null || cargo.equals(m.getCargo()))
                && (estado == null || estado.equals(m.getEstadoMiembro()));
    }

    @FXML
    private void limpiarFiltros(ActionEvent event) {
        // Limpiamos los filtros
        txtNombre.clear();
        cmbCargo.setValue(null);
        cmbEstado.setValue(null);

        // Filtramos miembros
        filtrarMiembros();
    }

    public void establecerMiembro(Miembro m) {
        // Establecemos el miembro
        this.miembro = m;

        if (miembro != null) {
            // Seleccionamos el miembro en cuestion en la tabla
            TableView.TableViewSelectionModel<Miembro> selectionModel = tblMiembros.getSelectionModel();
            selectionModel.select(miembro);
        }
    }

    private void mostrarMensaje(Alert.AlertType tipo, String titulo, String contenido) {
        // Funcion para mostrar alerta
        Alert alert = new Alert(tipo);
        alert.setHeaderText(null);
        alert.setTitle(titulo);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}
