package com.java.consejofacil.controller.ABMAccion;

import com.java.consejofacil.config.StageManager;
import com.java.consejofacil.security.SessionManager;
import com.java.consejofacil.helper.Alertas.AlertHelper;
import com.java.consejofacil.helper.Componentes.ComponentHelper;
import com.java.consejofacil.controller.SelectorController;
import com.java.consejofacil.helper.Componentes.TableCellFactoryHelper;
import com.java.consejofacil.helper.Utilidades.DateFormatterHelper;
import com.java.consejofacil.model.Accion;
import com.java.consejofacil.model.Expediente;
import com.java.consejofacil.service.Accion.AccionServiceImpl;
import com.java.consejofacil.service.Expediente.ExpedienteServiceImpl;
import com.java.consejofacil.view.FXMLView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.ArrayList;

@Component
public class AccionManager {

    // Servicios utilizados
    @Autowired
    @Lazy
    private ExpedienteServiceImpl expedienteService;
    @Autowired
    @Lazy
    private AccionServiceImpl accionService;

    // Controladores de los fxml
    @Autowired
    @Lazy
    private ListaAccionesController listaAccionesControlador;
    @Autowired
    @Lazy
    private FormularioAccionController abmAccionControlador;
    @Autowired
    @Lazy
    private SelectorController selectorControlador;

    // Stage Manager
    @Autowired
    @Lazy
    private StageManager stageManager;

    // Componente para obtener información de sesion
    @Autowired
    @Lazy
    private SessionManager sessionManager;

    // Metodo para validar el acceso del miembro

    public void validarAccesoMiembro() {
        sessionManager.validarAccesoMiembro();
    }

    // Metodos de inicialización de componentes

    public void inicializarTablaAcciones() {
        try {
            // Asociamos columnas de la tabla
            listaAccionesControlador.getColId().setCellValueFactory(new PropertyValueFactory<>("id"));
            listaAccionesControlador.getColFechaAccion().setCellValueFactory(new PropertyValueFactory<>("fechaAccion"));
            listaAccionesControlador.getColDetallesAccion().setCellValueFactory(new PropertyValueFactory<>("detallesAccion"));
            listaAccionesControlador.getColExpediente().setCellValueFactory(new PropertyValueFactory<>("expediente"));

            // Formateamos la fecha de accion
            TableCellFactoryHelper.configurarCeldaFecha(listaAccionesControlador.getColFechaAccion());

            // Cargamos listas
            listaAccionesControlador.getAcciones().clear();
            listaAccionesControlador.getFiltroAcciones().clear();
            listaAccionesControlador.getAcciones().addAll(accionService.findAll());
            listaAccionesControlador.getFiltroAcciones().addAll(listaAccionesControlador.getAcciones());

            // Cargamos la tabla
            listaAccionesControlador.getTblAcciones().setItems(listaAccionesControlador.getFiltroAcciones());
        } catch (Exception e) {
            listaAccionesControlador.getLog().error(e.getMessage());
        }
    }

    public void inicializarFiltros() {
        // Cargamos lista y combo de expedientes
        listaAccionesControlador.getExpedientes().clear();
        listaAccionesControlador.getExpedientes().addAll(expedienteService.findAll());
        listaAccionesControlador.getCmbExpediente().setItems(listaAccionesControlador.getExpedientes());
        configurarComboEditable(listaAccionesControlador.getCmbExpediente());
    }

    public void inicializarCombosFormulario() {
        // Cargamos lista y combo de expedientes
        abmAccionControlador.getExpedientes().clear();
        abmAccionControlador.getExpedientes().addAll(expedienteService.findAll());
        abmAccionControlador.getCmbExpediente().setItems(abmAccionControlador.getExpedientes());
        configurarComboEditable(abmAccionControlador.getCmbExpediente());
    }

    // Metodo para cargar el ABM

    public void cargarFormulario(Accion accion) {
        try {
            boolean autocompletadoSeleccionado = listaAccionesControlador.getCheckAutocompletado().isSelected();
            LocalDate fechaAccionSeleccionada = listaAccionesControlador.getDtpFechaAccion().getValue();
            String txtDetalles = listaAccionesControlador.getTxtDetalles().getText().trim();
            Expediente expedienteSeleccionado = listaAccionesControlador.getCmbExpediente().getValue();

            // Cargamos FXML de ABMAccion
            Parent rootNode = stageManager.loadView(FXMLView.FormularioAccion.getFxmlFile());

            // Autocompletamos el formulario si el CheckBox está seleccionado
            if (accion != null) {
                // Establecemos accion
                abmAccionControlador.establecerAccion(accion);
            } else if (autocompletadoSeleccionado) {
                autocompletarFormulario(new Accion(fechaAccionSeleccionada, txtDetalles, expedienteSeleccionado));
            }

            // Abrimos modal
            stageManager.openModal(rootNode, FXMLView.FormularioAccion);

        } catch (Exception e) {
            listaAccionesControlador.getLog().error(e.getMessage());
        }
    }

    public void procesarAccion(Accion accion, boolean nuevoValor) {
        if (accion != null) {
            // Si la acción no está dentro de la lista, la agregamos
            if (nuevoValor) {
                // Lo agregamos a la lista
                listaAccionesControlador.getAcciones().add(accion);

                // Aplicamos el filtro si es necesario
                if (aplicarFiltro(accion)) {
                    listaAccionesControlador.getFiltroAcciones().add(accion);
                }
            } else {
                // Actualizamos la accion
                actualizarAccion(accion);

                // Quitamos el filtro si es necesario
                if (quitarFiltro(accion)) {
                    listaAccionesControlador.getFiltroAcciones().remove(accion);
                }
            }
        }
    }

    public void actualizarAccion(Accion accion) {
        listaAccionesControlador.getAcciones().stream()
                // Filtramos los elementos que coinciden con el ID de la acción
                .filter(a -> a.getId() == accion.getId())
                // Actualizamos los atributos del elemento
                .findFirst().ifPresent(a -> {
                    a.setFechaAccion(accion.getFechaAccion());
                    a.setDetallesAccion(accion.getDetallesAccion());
                    a.setExpediente(accion.getExpediente());
                });
    }

    // Metodos para agregar, modificar y eliminar acciones

    public Accion obtenerDatosFormulario(){
        // Obtenemos la información en los campos
        String detallesAccion = abmAccionControlador.getTxtDetallesAccion().getText().trim();
        LocalDate fechaAccion = abmAccionControlador.getDtpFechaAccion().getValue();
        Expediente expediente = abmAccionControlador.getCmbExpediente().getValue();

        // Creamos una nueva accion auxiliar
        return new Accion(fechaAccion, detallesAccion, expediente);
    }

    public void guardarAccion() {
        if (validarCamposFormulario()) {
            // Creamos una nueva accion auxiliar
            Accion aux = obtenerDatosFormulario();

            // Si la accion es diferente de nulo, queremos modificar
            if (abmAccionControlador.getAccion() != null) {

                // Establecemos el ID a la accion auxiliar
                aux.setId(abmAccionControlador.getAccion().getId());

                modificarAccion(aux);
            } else {
                // Si la accion es nulo, queremos agregar
                agregarAccion(aux);
            }

            // Actuailizamos la tabla de acciones
            listaAccionesControlador.getTblAcciones().refresh();

            // Salimos del modal
            stageManager.closeModal(FXMLView.FormularioAccion.getKey());
        }
    }

    public void agregarAccion(Accion accion) {
        try {
            // Guardamos la acción auxiliar
            accion = accionService.save(accion);

            // Agregamos acción a la tabla
            procesarAccion(accion, true);

            // Mostramos un mensaje
            mostrarMensaje(false, "Info", "Se ha agregado la acción correctamente!");

        } catch (Exception e) {
            abmAccionControlador.getLog().error(e.getMessage());
            mostrarMensaje(true, "Error", "No se pudo agregar la acción correctamente!");
        }
    }


    public void modificarAccion(Accion accion) {
        try {
            // Modificamos la acción
            accion = accionService.update(accion);

            // Agregamos acción modificada a la tabla
            procesarAccion(accion, false);

            // Mostramos un mensaje
            mostrarMensaje(false, "Info", "Se ha modificado la acción correctamente!");

        } catch (Exception e) {
            abmAccionControlador.getLog().error(e.getMessage());
            mostrarMensaje(true, "Error", "No se pudo modificar la acción correctamente!");
        }
    }

    public void eliminarAccion(Accion accion, boolean auto) {
        // Mostramos alerta de confirmacion
        if (auto || mostrarConfirmacion("Info", "¿Está seguro que desea eliminar la acción?")) {
            try {
                // Eliminamos la acción
                accionService.delete(accion);

                // Actualizamos las listas
                listaAccionesControlador.getAcciones().remove(accion);
                listaAccionesControlador.getFiltroAcciones().remove(accion);

                // Mostramos un mensaje
                mostrarMensaje(false, "Info", "Se ha eliminado la acción correctamente!");

            } catch (Exception e) {
                listaAccionesControlador.getLog().error(e.getMessage());
                mostrarMensaje(true, "Info", "No se pudo eliminar la acción correctamente!");
            }
        }
    }

    // Metodos para filtrar las acciones

    public void filtrarAcciones() {
        // Limpiamos la lista filtro de acciones
        listaAccionesControlador.getFiltroAcciones().clear();

        // Filtramos todas las acciones
        for (Accion accion : listaAccionesControlador.getAcciones()) {
            if (aplicarFiltro(accion)) {
                listaAccionesControlador.getFiltroAcciones().add(accion);
            }
        }

        // Actualizamos la tabla
        listaAccionesControlador.getTblAcciones().refresh();
    }

    private boolean aplicarFiltro(Accion accion) {
        // Obtenemos todos los filtros
        String detalles = listaAccionesControlador.getTxtDetalles().getText();
        LocalDate fechaAccion = listaAccionesControlador.getDtpFechaAccion().getValue();
        Expediente expediente = listaAccionesControlador.getCmbExpediente().getValue();

        // Filtramos con base en los filtros obtenidos
        return (fechaAccion == null || fechaAccion.equals(accion.getFechaAccion()))
                && (expediente == null || expediente.equals(accion.getExpediente()))
                && (detalles == null || accion.getDetallesAccion().toLowerCase().contains(detalles.toLowerCase()));
    }

    private boolean quitarFiltro(Accion accion) {
        // Obtenemos todos los filtros
        String detalles = listaAccionesControlador.getTxtDetalles().getText();
        LocalDate fechaAccion = listaAccionesControlador.getDtpFechaAccion().getValue();
        Expediente expediente = listaAccionesControlador.getCmbExpediente().getValue();

        // Quitamos del filtro con base en los filtros obtenidos
        return (fechaAccion != null && !fechaAccion.equals(accion.getFechaAccion()))
                || (expediente != null && !expediente.equals(accion.getExpediente()))
                || (detalles != null && !accion.getDetallesAccion().toLowerCase().contains(detalles.toLowerCase()));
    }

    // Metodos de autocarga del formulario

    public void autocompletarFormulario(Accion accion) {
        // Completamos el formulario a partir de la información proporcionada
        abmAccionControlador.getTxtDetallesAccion().setText(accion.getDetallesAccion());

        if (accion.getFechaAccion() != null) {
            abmAccionControlador.getDtpFechaAccion().setValue(accion.getFechaAccion());
        }

        if (accion.getExpediente() != null) {
            abmAccionControlador.getCmbExpediente().setValue(accion.getExpediente());
        }
    }

    // Metdodos para validar los campos del formulario

    public boolean validarCamposFormulario() {
        ArrayList<String> errores = new ArrayList<>();
        Accion accion = obtenerDatosFormulario();

        // Verificamos que los detalles no esten vacios y que no superen los 500 caracteres
        if (accion.getDetallesAccion().isEmpty()) {
            errores.add("Por favor, ingrese unos detalles de la acción.");
        } else if (accion.getDetallesAccion().length() > 500) {
            errores.add("Los detalles de la acción no pueden tener más de 500 caracteres.");
        }

        // Verificamos que la fecha no este vacia
        if (accion.getFechaAccion() == null) {
            errores.add("Por favor, ingrese una fecha de acción.");
        }

        // Verificamos que haya seleccionado un expediente
        if (accion.getExpediente() == null) {
            errores.add("Por favor, seleccione un expediente.");
        } else {
            Expediente expediente = expedienteService.findById(accion.getExpediente().getId());
            if (expediente == null) {
                errores.add("El expediente seleccionado no se encuentra en la base de datos.");
            } else {
                // Verificamos que la fecha de acción no sea anterior al día de ingreso del expediente en la facultad
                if (accion.getFechaAccion() != null && accion.getFechaAccion().isBefore(expediente.getFechaIngreso())) {
                    errores.add("Debe seleccionar una fecha de acción que sea igual o posterior al dia de ingreso del expediente " +
                            DateFormatterHelper.formatearFechaSimple(expediente.getFechaIngreso()) + ".");
                }
            }
        }

        // Verificamos si hay errores
        if (!errores.isEmpty()) {
            AlertHelper.mostrarCadenaMensajes(true, errores, "Se ha producido uno o varios errores:", "Error");
            return false;
        }

        return true;
    }

    // Metodos para limpiar campos

    public void limpiarFiltros(){
        // Guardamos los eventos de los filtros
        EventHandler<ActionEvent> handlerExpediente = listaAccionesControlador.getCmbExpediente().getOnAction();
        EventHandler<ActionEvent> handlerFechaAccion = listaAccionesControlador.getDtpFechaAccion().getOnAction();

        // Desactivamos los eventos asociados a los filtros
        listaAccionesControlador.getCmbExpediente().setOnAction(null);
        listaAccionesControlador.getDtpFechaAccion().setOnAction(null);

        // Limpiamos los filtros
        listaAccionesControlador.getTxtDetalles().clear();
        listaAccionesControlador.getCmbExpediente().setValue(null);
        listaAccionesControlador.getDtpFechaAccion().setValue(null);

        // Filtramos las acciones
        filtrarAcciones();

        // Restauramos los eventos asociados a los filtros
        listaAccionesControlador.getCmbExpediente().setOnAction(handlerExpediente);
        listaAccionesControlador.getDtpFechaAccion().setOnAction(handlerFechaAccion);
    }

    public void limpiarFormulario() {
        // Limpiamos el formulario
        abmAccionControlador.getDtpFechaAccion().setValue(null);
        abmAccionControlador.getCmbExpediente().setValue(null);
        abmAccionControlador.getTxtDetallesAccion().clear();
    }

    // Metodos para interactuar con los selectores

    public void seleccionarExpediente(ComboBox<Expediente> combo) throws Exception {
        selectorControlador.seleccionarExpediente(combo);
    }

    // Metodo para configurar un combo editable

    public <T> void configurarComboEditable(ComboBox<T> combo) {
        ComponentHelper.configurarComboEditable(combo);
    }

    // Metodos para mostrar mensajes en pantalla

    public boolean mostrarConfirmacion(String titulo, String contenido) {
        return AlertHelper.mostrarConfirmacion(titulo, contenido);
    }

    public void mostrarMensaje(boolean error, String titulo, String contenido) {
        AlertHelper.mostrarMensaje(error, titulo, contenido);
    }
}
