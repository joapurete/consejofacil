package com.java.consejofacil.controller;

import com.java.consejofacil.helper.Alertas.AlertHelper;
import com.java.consejofacil.model.*;
import com.java.consejofacil.security.SessionManager;
import com.java.consejofacil.service.Expediente.ExpedienteServiceImpl;
import com.java.consejofacil.service.HistorialCambio.HistorialCambioServiceImpl;
import com.java.consejofacil.service.Miembro.MiembroServiceImpl;
import com.java.consejofacil.service.Reunion.ReunionServiceImpl;
import com.java.consejofacil.view.FXMLView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

@Controller
public class InicioController implements Initializable {

    // Etiqueta para recibir al usuario
    @FXML
    private Label lblBienvenida;

    // Tablas utilizadas
    @FXML
    private TableView<Expediente> tblUltimosExpedientes;
    @FXML
    private TableView<Reunion> tblProximasReuniones;
    @FXML
    private TableView<HistorialCambio> tblUltimosCambios;
    @FXML
    private TableView<Reunion> tblUltimasReuniones;

    // Columnas de las tablas
    @FXML
    private TableColumn<Expediente, LocalDate> colIngreso;
    @FXML
    private TableColumn<Expediente, String> colNota;
    @FXML
    private TableColumn<Expediente, Miembro> colIniciante;
    @FXML
    private TableColumn<Expediente, Integer> colInvolucrados;
    @FXML
    private TableColumn<Expediente, Integer> colAcciones;

    @FXML
    private TableColumn<Reunion, LocalDate> colFechaReunion;
    @FXML
    private TableColumn<Reunion, String> colAsuntoReunion;

    @FXML
    private TableColumn<HistorialCambio, LocalDate> colFechaCambio;
    @FXML
    private TableColumn<HistorialCambio, String> colDetallesCambio;
    @FXML
    private TableColumn<HistorialCambio, TipoCambio> colTipoCambio;
    @FXML
    private TableColumn<HistorialCambio, Miembro> colResponsable;

    @FXML
    private TableColumn<Reunion, LocalDate> colFechaUR;
    @FXML
    private TableColumn<Reunion, String> colAsuntoUR;
    @FXML
    private TableColumn<Reunion, Integer> colRevisiones;
    @FXML
    private TableColumn<Reunion, Integer> colAsistencias;
    @FXML
    private TableColumn<Reunion, Integer> colMinutas;

    // Graficos utilizados
    @FXML
    private BarChart<String, Number> barChartEstadosMiembros;
    @FXML
    private PieChart pieChartEstadosExpedientes;

    // Servicios necesarios
    @Autowired
    @Lazy
    private MiembroServiceImpl miembroService;
    @Autowired
    @Lazy
    private ExpedienteServiceImpl expedienteService;
    @Autowired
    @Lazy
    private ReunionServiceImpl reunionService;
    @Autowired
    @Lazy
    private HistorialCambioServiceImpl historialCambioService;

    // Controlador del menu para redireccionamientos
    @Autowired
    @Lazy
    private MenuController menuControlador;

    // Información de inicio de sesion
    @Autowired
    @Lazy
    private SessionManager sessionManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Validamos el acceso del usuario
        sessionManager.validarAccesoMiembro();
        if (sessionManager.validarSesion()) {
            lblBienvenida.setText("-> Bienvenido, " + sessionManager.getUsuario().toString() + " <-");
        }

        // Cargamos los graficos
        cargarGraficoBarrasEstadosMiembros();
        cargarGraficoCircularEstadosExpedientes();

        // Inicializamos las tablas
        inicializarTablaUltimosExpedientes();
        inicializarTablaProximasReuniones();
        inicializarTablaUltimosCambios();
        inicializarTablaUltimasReuniones();
    }

    // Metodos para cargar los diferentes graficos

    private void cargarGraficoBarrasEstadosMiembros() {
        // Limpiamos la tabla de datos del gráfico de barras
        barChartEstadosMiembros.getData().clear();

        // Obtenemos las cantidades de miembros por estado
        List<Object[]> cantMiembrosPorEstado = miembroService.contarCantidadMiembrosPorEstado();

        // Creamos una serie de datos para el gráfico
        XYChart.Series<String, Number> serieDatos = new XYChart.Series<>();

        for (Object[] rs : cantMiembrosPorEstado) {
            // Obtememos los datos
            String estadoMiembro = String.valueOf(rs[0]);
            Long cantidadMiembros = (Long) rs[1];

            // Agregamos los datos a la serie
            serieDatos.getData().add(new XYChart.Data<>(estadoMiembro, cantidadMiembros));
        }

        // Agregamos la serie al gráfico
        barChartEstadosMiembros.getData().add(serieDatos);

        // Asignamos un color aleatorio a cada barra
        serieDatos.getData().forEach(data -> data.getNode().setStyle("-fx-bar-fill: -primary;"));
    }

    private void cargarGraficoCircularEstadosExpedientes() {
        // Limpiamos la tabla de datos del gráfico circular
        pieChartEstadosExpedientes.getData().clear();

        // Obtenemos las cantidades de expedientes por estado
        List<Object[]> cantExpedientesPorEstado = expedienteService.contarCantidadExpedientesPorEstado();

        for (Object[] rs : cantExpedientesPorEstado) {
            // Obtememos los datos
            String estadoExpediente = String.valueOf(rs[0]);
            Long cantidadExpedientes = (Long) rs[1];

            // Agregamos los datos al gráfico
            pieChartEstadosExpedientes.getData().add(new PieChart.Data(estadoExpediente, cantidadExpedientes));
        }
    }

    // Metodos para inicializar las diferentes tablas

    private void inicializarTablaUltimosExpedientes() {
        // Asociamos columnas de la tabla
        colIngreso.setCellValueFactory(new PropertyValueFactory<>("fechaIngreso"));
        colNota.setCellValueFactory(new PropertyValueFactory<>("textoNota"));
        colIniciante.setCellValueFactory(new PropertyValueFactory<>("iniciante"));

        // Ocultamos algunas columnas menos relevantes
        colIngreso.setVisible(false);
        colIniciante.setVisible(false);

        // Buscamos los ultimos 5 expedientes
        ObservableList<Expediente> ultimosExpedientes = FXCollections.observableArrayList();
        ultimosExpedientes.addAll(expedienteService.encontrarUltimosExpedientes(5));

        List<Object[]> cantInvAccPorExpediente = expedienteService.contarCantidadInvAccPorExpediente(5);

        // Establecemos algunas fábricas de celdas para las columnas restantes
        colAcciones.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer acciones, boolean empty) {
                // Llamamos a la clase padre para cualquier inicializacion necesaria
                super.updateItem(acciones, empty);
                // Verificamos si la celda esta vacia
                if (empty) {
                    setText(null);
                } else {
                    Object[] item = cantInvAccPorExpediente.get(getTableRow().getIndex());
                    int cantidad = item[0] == null ? 0 : Integer.parseInt(String.valueOf(item[0]));
                    setText(cantidad + " acciones");
                }
            }
        });
        colInvolucrados.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer involucrados, boolean empty) {
                // Llamamos a la clase padre para cualquier inicializacion necesaria
                super.updateItem(involucrados, empty);
                // Verificamos si la celda esta vacia
                if (empty) {
                    setText(null);
                } else {
                    Object[] item = cantInvAccPorExpediente.get(getTableRow().getIndex());
                    int cantidad = item[1] == null ? 0 : Integer.parseInt(String.valueOf(item[1]));
                    setText(cantidad + " miembros");
                }
            }
        });

        // Agregamos los expedientes a la tabla
        tblUltimosExpedientes.setItems(ultimosExpedientes);
    }

    private void inicializarTablaProximasReuniones() {
        // Asociamos columnas de la tabla
        colFechaReunion.setCellValueFactory(new PropertyValueFactory<>("fechaReunion"));
        colAsuntoReunion.setCellValueFactory(new PropertyValueFactory<>("asunto"));

        // Buscamos los ultimos 5 expedientes
        ObservableList<Reunion> proximasReuniones = FXCollections.observableArrayList();
        proximasReuniones.addAll(reunionService.encontrarProximasReuniones(5));

        // Agregamos los expedientes a la tabla
        tblProximasReuniones.setItems(proximasReuniones);
    }

    private void inicializarTablaUltimosCambios() {
        // Asociamos columnas de la tabla
        colFechaCambio.setCellValueFactory(new PropertyValueFactory<>("fechaCambio"));
        colDetallesCambio.setCellValueFactory(new PropertyValueFactory<>("detallesCambio"));
        colTipoCambio.setCellValueFactory(new PropertyValueFactory<>("tipoCambio"));
        colResponsable.setCellValueFactory(new PropertyValueFactory<>("responsable"));

        // Ocultamos algunas columnas menos relevantes
        colDetallesCambio.setVisible(false);

        // Buscamos los ultimos 5 expedientes
        ObservableList<HistorialCambio> ultimosCambios = FXCollections.observableArrayList();
        ultimosCambios.addAll(historialCambioService.encontrarUltimosCambios(5));

        // Agregamos los expedientes a la tabla
        tblUltimosCambios.setItems(ultimosCambios);
    }

    private void inicializarTablaUltimasReuniones() {
        // Asociamos columnas de la tabla
        colFechaUR.setCellValueFactory(new PropertyValueFactory<>("fechaReunion"));
        colAsuntoUR.setCellValueFactory(new PropertyValueFactory<>("asunto"));

        // Ocultamos algunas columnas menos relevantes
        colFechaUR.setVisible(false);

        // Buscamos las ultimos 5 reuniones
        ObservableList<Reunion> ultimasReuniones = FXCollections.observableArrayList();
        ultimasReuniones.addAll(reunionService.encontrarUltimasReuniones(5));

        List<Object[]> cantRevAsisMinPorReunion = reunionService.contarCantidadRevAsisMinPorExpediente(5);

        // Establecemos algunas fábricas de celdas para las columnas restantes
        colRevisiones.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer acciones, boolean empty) {
                // Llamamos a la clase padre para cualquier inicializacion necesaria
                super.updateItem(acciones, empty);
                // Verificamos si la celda esta vacia
                if (empty) {
                    setText(null);
                } else {
                    Object[] item = cantRevAsisMinPorReunion.get(getTableRow().getIndex());
                    int cantidad = item[0] == null ? 0 : Integer.parseInt(String.valueOf(item[0]));
                    setText(cantidad + " expedientes");
                }
            }
        });
        colAsistencias.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer involucrados, boolean empty) {
                // Llamamos a la clase padre para cualquier inicializacion necesaria
                super.updateItem(involucrados, empty);
                // Verificamos si la celda esta vacia
                if (empty) {
                    setText(null);
                } else {
                    Object[] item = cantRevAsisMinPorReunion.get(getTableRow().getIndex());
                    int cantidad = item[1] == null ? 0 : Integer.parseInt(String.valueOf(item[1]));
                    setText(cantidad + " presentes");
                }
            }
        });
        colMinutas.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer involucrados, boolean empty) {
                // Llamamos a la clase padre para cualquier inicializacion necesaria
                super.updateItem(involucrados, empty);
                // Verificamos si la celda esta vacia
                if (empty) {
                    setText(null);
                } else {
                    Object[] item = cantRevAsisMinPorReunion.get(getTableRow().getIndex());
                    int cantidad = item[2] == null ? 0 : Integer.parseInt(String.valueOf(item[2]));
                    setText(cantidad + " minutas");
                }
            }
        });

        // Agregamos las reuniones a la tabla
        tblUltimasReuniones.setItems(ultimasReuniones);
    }

    // Metodos para ver mas informacion

    @FXML
    private void verTodosLosExpedientes() {
        menuControlador.redireccionarMenu(FXMLView.ListaExpedientes, menuControlador.getBtnExpedientes());
    }
    @FXML
    private void verTodosLosMiembros() {
        if (sessionManager.validarSesion()) {
            if (!sessionManager.tieneCargoMiembro()) {
                menuControlador.redireccionarMenu(FXMLView.ListaMiembros, menuControlador.getBtnMiembros());
            } else {
                AlertHelper.mostrarMensaje(true, "Error", "Lo siento, solo el personal administrativo y los gerentes pueden acceder a la lista de miembros del sistema.");
            }
        }
    }
    @FXML
    private void verTodasLasReuniones() {
        menuControlador.redireccionarMenu(FXMLView.ListaReuniones, menuControlador.getBtnReuniones());
    }
    @FXML
    private void verTodosLosCambios() {
        menuControlador.redireccionarMenu(FXMLView.ListaHistorialCambios, menuControlador.getBtnHistorialCambios());
    }
}
