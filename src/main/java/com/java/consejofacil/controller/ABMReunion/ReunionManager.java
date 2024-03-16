package com.java.consejofacil.controller.ABMReunion;

import com.java.consejofacil.config.StageManager;
import com.java.consejofacil.controller.ABMHistorialCambio.HistorialCambioManager;
import com.java.consejofacil.security.SessionManager;
import com.java.consejofacil.helper.Alertas.AlertHelper;
import com.java.consejofacil.helper.Componentes.TableCellFactoryHelper;
import com.java.consejofacil.helper.Utilidades.DateFormatterHelper;
import com.java.consejofacil.model.Reunion;
import com.java.consejofacil.service.Reunion.ReunionServiceImpl;
import com.java.consejofacil.view.FXMLView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.ArrayList;

@Component
public class ReunionManager {

    // Servicios necesarios
    @Autowired
    @Lazy
    private ReunionServiceImpl reunionService;

    // Controladores de los fxml
    @Autowired
    @Lazy
    private ListaReunionesController listaReunionesControlador;
    @Autowired
    @Lazy
    private FormularioReunionController abmReunionControlador;
    @Autowired
    @Lazy
    private SelectorReunionController selectorReunionControlador;

    // Stage Manager
    @Autowired
    @Lazy
    private StageManager stageManager;

    // Componente para obtener información de sesion
    @Autowired
    @Lazy
    private SessionManager sessionManager;

    // Componente para registrar los cambios realizados
    @Autowired
    @Lazy
    private HistorialCambioManager historialCambioManager;

    // Metodo para validar el acceso del miembro

    public void validarAccesoMiembro() {
        sessionManager.validarAccesoMiembro();
    }

    // Metodos de inicialización de componentes

    public void inicializarTablaReuniones(BaseTablaReuniones controlador) {
        try {
            // Asociamos columnas de la tabla
            controlador.getColId().setCellValueFactory(new PropertyValueFactory<>("id"));
            controlador.getColFechaReunion().setCellValueFactory(new PropertyValueFactory<>("fechaReunion"));
            controlador.getColAsunto().setCellValueFactory(new PropertyValueFactory<>("asunto"));

            // Formateamos la fecha de ingreso
            TableCellFactoryHelper.configurarCeldaFecha(controlador.getColFechaReunion());

            // Cargamos listas
            controlador.getReuniones().clear();
            controlador.getFiltroReuniones().clear();
            controlador.getReuniones().addAll(reunionService.findAll());
            controlador.getFiltroReuniones().addAll(controlador.getReuniones());

            // Cargamos la tabla
            controlador.getTblReuniones().setItems(controlador.getFiltroReuniones());
        } catch (Exception e) {
            controlador.getLog().error(e.getMessage());
        }
    }

    // Metodo para cargar el ABM

    public void cargarFormulario(Reunion reunion) {
        try {
            boolean autocompletadoSeleccionado = listaReunionesControlador.getCheckAutocompletado().isSelected();
            String asunto = listaReunionesControlador.getTxtAsunto().getText().trim();
            LocalDate fechaReunionSeleccionada = listaReunionesControlador.getDtpFechaReunion().getValue();

            // Cargamos FXML de ABMReunion
            Parent rootNode = stageManager.loadView(FXMLView.FormularioReunion.getFxmlFile());

            if (reunion != null) {
                abmReunionControlador.establecerReunion(reunion);
            } else if (autocompletadoSeleccionado) {
                // Autocompletamos el formulario si el CheckBox está seleccionado
                autocompletarFormulario(new Reunion(asunto, fechaReunionSeleccionada));
            }

            // Abrimos modal
            stageManager.openModal(rootNode, FXMLView.FormularioReunion);

        } catch (Exception e) {
            listaReunionesControlador.getLog().error(e.getMessage());
        }
    }

    public void procesarReunion(Reunion reunion, boolean nuevoValor) {
        if (reunion != null) {
            // Si la reunión no está dentro de la lista, la agregamos
            if (nuevoValor) {
                // La agregamos a la lista
                listaReunionesControlador.getReuniones().add(reunion);

                // Aplicamos el filtro si es necesario
                if (aplicarFiltro(reunion, listaReunionesControlador)) {
                    listaReunionesControlador.getFiltroReuniones().add(reunion);
                }
            } else {
                // Actualizamos la reunion
                actualizarReunion(reunion);

                // Quitamos el filtro si es necesario
                if (quitarFiltro(reunion, listaReunionesControlador)) {
                    listaReunionesControlador.getFiltroReuniones().remove(reunion);
                }
            }
        }
    }

    public void actualizarReunion(Reunion reunion) {
        listaReunionesControlador.getReuniones().stream()
                // Filtramos los elementos que coinciden con el ID de la reunión
                .filter(r -> r.getId() == reunion.getId())
                // Actualizamos los atributos del elemento
                .findFirst().ifPresent(r -> {
                    r.setAsunto(reunion.getAsunto());
                    r.setFechaReunion(reunion.getFechaReunion());
                });
    }

    // Metodos para agregar, modificar y eliminar reuniones

    public Reunion obtenerDatosFormulario(){
        // Obtenemos la información en los campos
        String asunto = abmReunionControlador.getTxtAsunto().getText().trim();
        LocalDate fechaReunion = abmReunionControlador.getDtpFechaReunion().getValue();

        // Creamos una reunion auxiliar
        return new Reunion(asunto, fechaReunion);
    }

    public void guardarReunion() {
        if (validarCamposFormulario()) {
            // Creamos una nueva reunión auxiliar
            Reunion aux = obtenerDatosFormulario();

            // Si la reunión es diferente de nulo, queremos modificar
            if (abmReunionControlador.getReunion() != null) {

                // Establecemos el ID a la reunión auxiliar
                aux.setId(abmReunionControlador.getReunion().getId());

                // Modificamos la reunión
                modificarReunion(aux);
            } else {
                // Si la reunión es nulo, queremos agregar
                agregarReunion(aux);
            }

            // Actualizamos la tabla de reuniones
            listaReunionesControlador.getTblReuniones().refresh();

            // Salimos del modal
            stageManager.closeModal(FXMLView.FormularioReunion.getKey());
        }
    }

    public void agregarReunion(Reunion reunion) {
        try {
            // Guardamos la reunión auxiliar
            reunion = reunionService.save(reunion);

            // Agregamos reunión a la tabla
            procesarReunion(reunion, true);

            // Agregamos cambio a la base de datos
            historialCambioManager.registrarCambio(historialCambioManager.getTipoIns().getKey(),
                    obtenerDetallesCambioReunion(reunion, historialCambioManager.getTipoIns().getValue()));

            // Mostramos un mensaje
            mostrarMensaje(false, "Info", "Se ha agregado la reunión correctamente!");

        } catch (Exception e) {
            abmReunionControlador.getLog().error(e.getMessage());
            mostrarMensaje(true, "Error", "No se pudo agregar la reunión correctamente!");
        }
    }


    public void modificarReunion(Reunion reunion) {
        try {
            // Modificamos la reunión
            reunion = reunionService.update(reunion);

            // Agregamos reunión modificada a la tabla
            procesarReunion(reunion, false);

            // Agregamos cambio a la base de datos
            historialCambioManager.registrarCambio(historialCambioManager.getTipoMod().getKey(),
                    obtenerDetallesCambioReunion(reunion, historialCambioManager.getTipoMod().getValue()));

            // Mostramos un mensaje
            mostrarMensaje(false, "Info", "Se ha modificado la reunión correctamente!");

        } catch (Exception e) {
            abmReunionControlador.getLog().error(e.getMessage());
            mostrarMensaje(true, "Error", "No se pudo modificar la reunión correctamente!");
        }
    }

    public void eliminarReunion(Reunion reunion, boolean auto) {
        // Mostramos alerta de confirmacion
        if (auto || mostrarConfirmacion("Info", "¿Está seguro que desea eliminar la reunión?")) {
            try {
                // Eliminamos la reunión
                reunionService.delete(reunion);

                // Actualizamos las listas
                listaReunionesControlador.getReuniones().remove(reunion);
                listaReunionesControlador.getFiltroReuniones().remove(reunion);

                // Agregamos cambio a la base de datos
                historialCambioManager.registrarCambio(historialCambioManager.getTipoEli().getKey(),
                        obtenerDetallesCambioReunion(reunion, historialCambioManager.getTipoEli().getValue()));

                // Mostramos un mensaje
                mostrarMensaje(false, "Info", "Se ha eliminado la reunión correctamente!");

            } catch (Exception e) {
                listaReunionesControlador.getLog().error(e.getMessage());
                mostrarMensaje(true, "Info", "No se pudo eliminar la reunión correctamente!\n" +
                        "Es posible que esté vinculado a otros registros en el sistema.");
            }
        }
    }

    // Metodo para construir texto para los detalles del cambio realizado

    public String obtenerDetallesCambioReunion(Reunion reunion, String tipoCambio){
        return "Se ha registrado la " + tipoCambio.toLowerCase() + " de la reunión ID " + reunion.getId() + ". " +
                "Asunto de la reunión: [" + reunion.getAsunto() + "]. " +
                "Fecha de la reunión: " + DateFormatterHelper.formatearFechaSimple(reunion.getFechaReunion()) + ".";
    }

    // Metodos para filtrar las reuniones

    public void filtrarReuniones(BaseTablaReuniones controlador) {
        // Limpiamos la lista filtro de reuniones
        controlador.getFiltroReuniones().clear();

        // Filtramos todas las reuniones
        for (Reunion reunion : controlador.getReuniones()) {
            if (aplicarFiltro(reunion, controlador)) {
                controlador.getFiltroReuniones().add(reunion);
            }
        }

        // Actualizamos la tabla
        controlador.getTblReuniones().refresh();
    }

    public boolean aplicarFiltro(Reunion reunion, BaseTablaReuniones controlador) {
        // Obtenemos todos los filtros
        String asunto = controlador.getTxtAsunto().getText();
        LocalDate fechaReunion = controlador.getDtpFechaReunion().getValue();

        // Filtramos con base en los filtros obtenidos
        return (asunto == null || reunion.getAsunto().toLowerCase().contains(asunto.toLowerCase()))
                && (fechaReunion == null || fechaReunion.equals(reunion.getFechaReunion()));
    }

    private boolean quitarFiltro(Reunion reunion, BaseTablaReuniones controlador) {
        // Obtenemos todos los filtros
        String asunto = controlador.getTxtAsunto().getText();
        LocalDate fechaReunion = (controlador.getDtpFechaReunion().getValue() != null) ?
                controlador.getDtpFechaReunion().getValue() : null;

        // Quitamos del filtro con base en los filtros obtenidos
        return (asunto != null && !reunion.getAsunto().toLowerCase().startsWith(asunto.toLowerCase())
                || (fechaReunion != null && !fechaReunion.equals(reunion.getFechaReunion())));
    }

    // Metodos de autocarga del formulario

    public void autocompletarFormulario(Reunion reunion) {
        // Completamos el formulario a partir de la información proporcionada
        abmReunionControlador.getTxtAsunto().setText(reunion.getAsunto());

        if (reunion.getFechaReunion() != null) {
            abmReunionControlador.getDtpFechaReunion().setValue(reunion.getFechaReunion());
        }
    }

    // Metdodos para validar los campos del formulario

    public boolean validarCamposFormulario() {
        ArrayList<String> errores = new ArrayList<>();
        Reunion reunion = obtenerDatosFormulario();

        // Verificamos que el asunto no este vacio y que no supere los 150 caracteres
        if (reunion.getAsunto().isEmpty()) {
            errores.add("Por favor, ingrese un asunto.");
        } else if (reunion.getAsunto().length() > 150) {
            errores.add("El asunto no puede tener más de 150 caracteres.");
        }

        // Verificamos que la fecha de reunión no este vacia y que no sea anterior al día de hoy
        if (reunion.getFechaReunion() == null) {
            errores.add("Por favor, ingrese una fecha de reunión.");
        } else if (reunion.getFechaReunion().isBefore(LocalDate.now())) {
            errores.add("Debe seleccionar una fecha de reunión que sea igual o posterior al dia de hoy " + DateFormatterHelper.fechaHoy() + ".");
        }

        // Verificamos si hay errores
        if (!errores.isEmpty()) {
            AlertHelper.mostrarCadenaMensajes(true, errores, "Se ha producido uno o varios errores:", "Error");
            return false;
        }

        return true;
    }

    // Metodos para limpiar campos

    public void limpiarFiltros(BaseTablaReuniones controlador) {
        // Guardamos los eventos de los filtros
        EventHandler<ActionEvent> handlerFechaReunion = controlador.getDtpFechaReunion().getOnAction();

        // Desactivamos los eventos asociados a los filtros
        controlador.getDtpFechaReunion().setOnAction(null);

        // Limpiamos los filtros
        controlador.getTxtAsunto().clear();
        controlador.getDtpFechaReunion().setValue(null);

        // Filtramos reuniones
        filtrarReuniones(controlador);

        // Restauramos los eventos asociados a los filtros
        controlador.getDtpFechaReunion().setOnAction(handlerFechaReunion);
    }

    public void limpiarFormulario() {
        // Limpiamos el formulario
        abmReunionControlador.getDtpFechaReunion().setValue(null);
        abmReunionControlador.getTxtAsunto().clear();
    }

    // Metodos para mostrar mensajes en pantalla

    public boolean mostrarConfirmacion(String titulo, String contenido) {
        return AlertHelper.mostrarConfirmacion(titulo, contenido);
    }

    public void mostrarMensaje(boolean error, String titulo, String contenido) {
        AlertHelper.mostrarMensaje(error, titulo, contenido);
    }

    // Metodos adicionales para el selector

    public void agregarReunionSelector() {
        // Obtenemos la reunión seleccionada
        selectorReunionControlador.setReunion(selectorReunionControlador.getTblReuniones().getSelectionModel().getSelectedItem());

        // Verificamos que sea diferente de nulo
        if (selectorReunionControlador.getReunion() == null) {
            mostrarMensaje(true, "Error", "Debes seleccionar una reunión!");
        } else {
            // Salimos del modal
            stageManager.closeModal(FXMLView.SelectorReunion.getKey());
        }
    }

    public void autoseleccionarReunion(){
        // Seleccionamos la reunión en cuestion en la tabla
        TableView.TableViewSelectionModel<Reunion> selectionModel = selectorReunionControlador.getTblReuniones().getSelectionModel();
        selectionModel.select(selectorReunionControlador.getReunion());
    }
}
