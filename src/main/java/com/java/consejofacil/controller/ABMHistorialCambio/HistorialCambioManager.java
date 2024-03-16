package com.java.consejofacil.controller.ABMHistorialCambio;

import com.java.consejofacil.config.StageManager;
import com.java.consejofacil.model.*;
import com.java.consejofacil.security.SessionManager;
import com.java.consejofacil.helper.Alertas.AlertHelper;
import com.java.consejofacil.helper.Componentes.ComponentHelper;
import com.java.consejofacil.controller.SelectorController;
import com.java.consejofacil.helper.Componentes.TableCellFactoryHelper;
import com.java.consejofacil.helper.Utilidades.DateFormatterHelper;
import com.java.consejofacil.service.HistorialCambio.HistorialCambioServiceImpl;
import com.java.consejofacil.service.Miembro.MiembroServiceImpl;
import com.java.consejofacil.service.TipoCambio.TipoCambioServiceImpl;
import com.java.consejofacil.view.FXMLView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Pair;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;

@Component
public class HistorialCambioManager {

    // Servicios utilizados
    @Autowired
    @Lazy
    private HistorialCambioServiceImpl historialCambioService;
    @Autowired
    @Lazy
    private MiembroServiceImpl miembroService;
    @Autowired
    @Lazy
    private TipoCambioServiceImpl tipoCambioService;

    // Controladores de los fxml
    @Autowired
    @Lazy
    private ListaHistorialCambiosController listaHistorialCambiosControlador;
    @Autowired
    @Lazy
    private FormularioCambioController abmCambioControlador;
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

    // Identificadores para cada tipo de cambio
    @Getter
    private Pair<Integer, String> tipoIns = new Pair<>(1, "Inserción");
    @Getter
    private final Map.Entry<Integer, String> tipoMod = Map.entry(2, "Modificación");
    @Getter
    private final Map.Entry<Integer, String> tipoEli = Map.entry(3, "Eliminación");

    // Metodo para validar el acceso del miembro

    public void validarAccesoMiembro() {
        sessionManager.validarAccesoMiembro();
    }

    // Metodos de inicialización de componentes

    public void inicializarTablaHistorialCambios() {
        try {
            // Asociamos columnas de la tabla
            listaHistorialCambiosControlador.getColId().setCellValueFactory(new PropertyValueFactory<>("id"));
            listaHistorialCambiosControlador.getColDetalles().setCellValueFactory(new PropertyValueFactory<>("detallesCambio"));
            listaHistorialCambiosControlador.getColFechaCambio().setCellValueFactory(new PropertyValueFactory<>("fechaCambio"));
            listaHistorialCambiosControlador.getColResponsable().setCellValueFactory(new PropertyValueFactory<>("responsable"));
            listaHistorialCambiosControlador.getColTipo().setCellValueFactory(new PropertyValueFactory<>("tipoCambio"));

            // Formateamos la fecha de cambio
            TableCellFactoryHelper.configurarCeldaFecha(listaHistorialCambiosControlador.getColFechaCambio());

            // Cargamos listas
            listaHistorialCambiosControlador.getCambios().clear();
            listaHistorialCambiosControlador.getFiltroCambios().clear();
            listaHistorialCambiosControlador.getCambios().addAll(historialCambioService.findAll());
            listaHistorialCambiosControlador.getFiltroCambios().addAll(listaHistorialCambiosControlador.getCambios());

            // Cargamos la tabla
            listaHistorialCambiosControlador.getTblCambios().setItems(listaHistorialCambiosControlador.getFiltroCambios());
        } catch (Exception e) {
            listaHistorialCambiosControlador.getLog().error(e.getMessage());
        }
    }

    public void inicializarFiltros() {
        // Cargamos lista y combo de miembros
        listaHistorialCambiosControlador.getMiembros().clear();
        listaHistorialCambiosControlador.getMiembros().addAll(miembroService.encontrarMiembrosActivos());
        listaHistorialCambiosControlador.getCmbResponsable().setItems(listaHistorialCambiosControlador.getMiembros());
        configurarComboEditable(listaHistorialCambiosControlador.getCmbResponsable());

        // Cargamos lista y combo de tipos
        listaHistorialCambiosControlador.getTiposCambios().clear();
        listaHistorialCambiosControlador.getTiposCambios().addAll(tipoCambioService.findAll());
        listaHistorialCambiosControlador.getCmbTipo().setItems(listaHistorialCambiosControlador.getTiposCambios());
    }

    public void inicializarCombosFormulario() {
        // Cargamos lista y combo de miembros
        abmCambioControlador.getMiembros().clear();
        abmCambioControlador.getMiembros().addAll(miembroService.encontrarMiembrosActivos());
        abmCambioControlador.getCmbResponsable().setItems(abmCambioControlador.getMiembros());
        configurarComboEditable(abmCambioControlador.getCmbResponsable());

        // Cargamos lista y combo de tipos
        abmCambioControlador.getTiposCambios().clear();
        abmCambioControlador.getTiposCambios().addAll(tipoCambioService.findAll());
        abmCambioControlador.getCmbTipo().setItems(abmCambioControlador.getTiposCambios());
    }

    // Metodo para cargar el ABM

    public void cargarFormulario(HistorialCambio cambio) {
        try {
            boolean autocompletadoSeleccionado = listaHistorialCambiosControlador.getCheckAutocompletado().isSelected();
            LocalDate fechaCambioSeleccionada = listaHistorialCambiosControlador.getDtpFechaCambio().getValue();
            Miembro responsableSeleccionado = listaHistorialCambiosControlador.getCmbResponsable().getValue();
            TipoCambio tipoCambioSeleccionado = listaHistorialCambiosControlador.getCmbTipo().getValue();
            String detallesCambio = listaHistorialCambiosControlador.getTxtDetalles().getText().trim();

            // Cargamos FXML de ABMHistorialCambio
            Parent rootNode = stageManager.loadView(FXMLView.FormularioCambio.getFxmlFile());

            if (cambio != null) {
                abmCambioControlador.establecerCambio(cambio);
            } else if (autocompletadoSeleccionado) {
                // Autocompletamos el formulario si el CheckBox está seleccionado
                autocompletarFormulario(new HistorialCambio(fechaCambioSeleccionada, responsableSeleccionado,
                        tipoCambioSeleccionado, detallesCambio));
            }

            // Abrimos modal
            stageManager.openModal(rootNode, FXMLView.FormularioCambio);

        } catch (Exception e) {
            listaHistorialCambiosControlador.getLog().error(e.getMessage());
        }
    }

    public void procesarCambio(HistorialCambio cambio, boolean nuevoValor) {
        if (cambio != null) {
            // Si el cambio no está dentro de la lista, la agregamos
            if (nuevoValor) {
                // Lo agregamos a la lista
                listaHistorialCambiosControlador.getCambios().add(cambio);

                // Aplicamos el filtro si es necesario
                if (aplicarFiltro(cambio)) {
                    listaHistorialCambiosControlador.getFiltroCambios().add(cambio);
                }
            } else {
                // Actualizamos el cambio
                actualizarCambio(cambio);

                // Quitamos el filtro si es necesario
                if (quitarFiltro(cambio)) {
                    listaHistorialCambiosControlador.getFiltroCambios().remove(cambio);
                }
            }
        }
    }

    public void actualizarCambio(HistorialCambio cambio) {
        listaHistorialCambiosControlador.getCambios().stream()
                // Filtramos los elementos que coinciden con el ID del cambio
                .filter(cmb -> cmb.getId() == cambio.getId())
                // Actualizamos los atributos del elemento
                .findFirst().ifPresent(cmb -> {
                    cmb.setFechaCambio(cambio.getFechaCambio());
                    cmb.setResponsable(cambio.getResponsable());
                    cmb.setTipoCambio(cambio.getTipoCambio());
                    cmb.setDetallesCambio(cambio.getDetallesCambio());
                });
    }

    // Metodos para agregar, modificar y eliminar expedientes

    public HistorialCambio obtenerDatosFormulario() {
        // Obtenemos la información proporcionada
        LocalDate fechaCambioSeleccionada = abmCambioControlador.getDtpFechaCambio().getValue();
        Miembro responsableSeleccionado = abmCambioControlador.getCmbResponsable().getValue();
        TipoCambio tipoCambioSeleccionado = abmCambioControlador.getCmbTipo().getValue();
        String detallesCambio = abmCambioControlador.getTxtDetalles().getText().trim();

        // Creamos un nuevo cambio auxiliar
        return new HistorialCambio(fechaCambioSeleccionada, responsableSeleccionado, tipoCambioSeleccionado, detallesCambio);
    }

    public void guardarCambio() {
        if (validarCamposFormulario()) {
            // Creamos un nuevo cambio auxiliar
            HistorialCambio aux = obtenerDatosFormulario();

            // Si el cambio es diferente de nulo, queremos modificar
            if (abmCambioControlador.getCambio() != null) {

                // Establecemos el ID al cambio auxiliar
                aux.setId(abmCambioControlador.getCambio().getId());

                // Modificamos el cambio
                modificarCambio(aux);
            } else {
                // Si el cambio es nulo, queremos agregar
                agregarCambio(aux);
            }

            // Actuailizamos la tabla de cambios
            listaHistorialCambiosControlador.getTblCambios().refresh();

            // Salimos del modal
            stageManager.closeModal(FXMLView.FormularioCambio.getKey());
        }
    }

    public void agregarCambio(HistorialCambio cambio) {
        try {
            // Guardamos el cambio auxiliar
            cambio = historialCambioService.save(cambio);

            // Agregamos cambio a la tabla
            procesarCambio(cambio, true);

            // Mostramos un mensaje
            mostrarMensaje(false, "Info", "Se ha agregado el cambio correctamente!");

        } catch (Exception e) {
            abmCambioControlador.getLog().error(e.getMessage());
            mostrarMensaje(true, "Error", "No se pudo agregar el cambio correctamente!");
        }
    }


    public void modificarCambio(HistorialCambio cambio) {
        try {
            // Modificamos el cambio
            cambio = historialCambioService.update(cambio);

            // Agregamos cambio modificado a la tabla
            procesarCambio(cambio, false);

            // Mostramos un mensaje
            mostrarMensaje(false, "Info", "Se ha modificado el cambio correctamente!");

        } catch (Exception e) {
            abmCambioControlador.getLog().error(e.getMessage());
            mostrarMensaje(true, "Error", "No se pudo modificar el cambio correctamente!");
        }
    }

    public void eliminarCambio(HistorialCambio cambio, boolean auto) {
        // Mostramos alerta de confirmacion
        if (auto || mostrarConfirmacion("Info", "¿Está seguro que desea eliminar el cambio?")) {
            try {
                // Eliminamos el cambio
                historialCambioService.delete(cambio);

                // Actualizamos las listas
                listaHistorialCambiosControlador.getCambios().remove(cambio);
                listaHistorialCambiosControlador.getFiltroCambios().remove(cambio);

                // Mostramos un mensaje
                mostrarMensaje(false, "Info", "Se ha eliminado el cambio correctamente!");

            } catch (Exception e) {
                listaHistorialCambiosControlador.getLog().error(e.getMessage());
                mostrarMensaje(true, "Info", "No se pudo eliminar el cambio correctamente!");
            }
        }
    }

    // Metodo para registrar un nuevo cambio

    public void registrarCambio(int tipoCambio, String detallesCambio) {
        if (sessionManager.validarSesion()) {
            try {
                // Obtenemos la iformacion
                HistorialCambio cambio = new HistorialCambio();
                cambio.setFechaCambio(LocalDate.now());
                cambio.setResponsable(sessionManager.getUsuario());
                cambio.setTipoCambio(tipoCambioService.findById(tipoCambio));
                cambio.setDetallesCambio(detallesCambio);

                // Guardamos el cambio auxiliar
                historialCambioService.save(cambio);

                // Mostramos un mensaje
                System.out.println("Info: Se ha agregado el cambio correctamente!");

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    // Metodos para filtrar los cambios

    public void filtrarCambios() {
        // Limpiamos la lista filtro de cambios
        listaHistorialCambiosControlador.getFiltroCambios().clear();

        // Filtramos todos los cambios
        for (HistorialCambio cmb : listaHistorialCambiosControlador.getCambios()) {
            if (aplicarFiltro(cmb)) {
                listaHistorialCambiosControlador.getFiltroCambios().add(cmb);
            }
        }

        // Actualizamos la tabla
        listaHistorialCambiosControlador.getTblCambios().refresh();
    }

    public boolean aplicarFiltro(HistorialCambio cmb) {
        // Obtenemos todos los filtros
        Miembro responsable = listaHistorialCambiosControlador.getCmbResponsable().getValue();
        LocalDate fechaCambio = listaHistorialCambiosControlador.getDtpFechaCambio().getValue();
        TipoCambio tipoCambio = listaHistorialCambiosControlador.getCmbTipo().getValue();
        String detallesCambio = listaHistorialCambiosControlador.getTxtDetalles().getText();

        // Filtramos con base en los filtros obtenidos
        return (responsable == null || responsable.equals(cmb.getResponsable()))
                && (fechaCambio == null || fechaCambio.equals(cmb.getFechaCambio()))
                && (tipoCambio == null || tipoCambio.equals(cmb.getTipoCambio()))
                && (detallesCambio == null || cmb.getDetallesCambio().toLowerCase().contains(detallesCambio.toLowerCase()));
    }

    public boolean quitarFiltro(HistorialCambio cmb) {
        // Obtenemos todos los filtros
        Miembro responsable = listaHistorialCambiosControlador.getCmbResponsable().getValue();
        LocalDate fechaCambio = listaHistorialCambiosControlador.getDtpFechaCambio().getValue() != null ?
                listaHistorialCambiosControlador.getDtpFechaCambio().getValue() : null;
        TipoCambio tipoCambio = listaHistorialCambiosControlador.getCmbTipo().getValue();
        String detallesCambio = listaHistorialCambiosControlador.getTxtDetalles().getText();

        // Quitamos del filtro con base en los filtros obtenidos
        return (responsable != null && !responsable.equals(cmb.getResponsable()))
                || (fechaCambio != null && !fechaCambio.equals(cmb.getFechaCambio()))
                || (tipoCambio != null && !tipoCambio.equals(cmb.getTipoCambio()))
                || (detallesCambio != null && !cmb.getDetallesCambio().toLowerCase().contains(detallesCambio.toLowerCase()));
    }

    // Metodos de autocarga del formulario

    public void autocompletarFormulario(HistorialCambio cambio) {
        // Completamos el formulario a partir de la información proporcionada
        abmCambioControlador.getTxtDetalles().setText(cambio.getDetallesCambio());

        if (cambio.getFechaCambio() != null) {
            abmCambioControlador.getDtpFechaCambio().setValue(cambio.getFechaCambio());
        }

        if (cambio.getResponsable() != null) {
            abmCambioControlador.getCmbResponsable().setValue(cambio.getResponsable());
        }

        if (cambio.getTipoCambio() != null) {
            abmCambioControlador.getCmbTipo().setValue(cambio.getTipoCambio());
        }
    }

    // Metdodos para validar los campos del formulario

    public boolean validarCamposFormulario() {
        ArrayList<String> errores = new ArrayList<>();
        HistorialCambio cambio = obtenerDatosFormulario();

        // Verificamos que los detalles no esten vacios y que no superen los 500 caracteres
        if (cambio.getDetallesCambio().isEmpty()) {
            errores.add("Por favor, ingrese unos detalles del cambio.");
        } else if (cambio.getDetallesCambio().length() > 500) {
            errores.add("Los detalles del cambio no pueden tener más de 500 caracteres.");
        }

        // Verificamos que la fecha del cambio no este vacia y que no sea posterior al día de hoy
        if (cambio.getFechaCambio() == null) {
            errores.add("Por favor, ingrese una fecha del cambio.");
        } else if (cambio.getFechaCambio().isAfter(LocalDate.now())) {
            errores.add("Debe seleccionar una fecha del cambio que sea igual o anterior al dia de hoy " + DateFormatterHelper.fechaHoy() + ".");
        }

        // Verificamos que haya seleccionado un responsable
        if (cambio.getResponsable() == null) {
            errores.add("Por favor, seleccione un responsable.");
        } else {
            Miembro responsable = miembroService.findById(cambio.getResponsable().getDni());
            if (responsable == null) {
                errores.add("El responsable seleccionado no se encuentra en la base de datos.");
            } else if (!responsable.getEstadoMiembro().getEstadoMiembro().equals("Activo")) {
                errores.add("Debe seleccionar un responsable activo en el sistema.");
            }
        }

        // Verificamos que haya seleccionado un tipo de cambio
        if (cambio.getTipoCambio() == null) {
            errores.add("Por favor, seleccione un tipo de cambio.");
        } else {
            TipoCambio tipo = tipoCambioService.findById(cambio.getTipoCambio().getId());
            if (tipo == null) {
                errores.add("El tipo de cambio seleccionado no se encuentra en la base de datos.");
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

    public void limpiarFiltros() {
        // Guardamos los eventos de los filtros
        EventHandler<ActionEvent> handlerFechaCambio = listaHistorialCambiosControlador.getDtpFechaCambio().getOnAction();
        EventHandler<ActionEvent> handlerResponsable = listaHistorialCambiosControlador.getCmbResponsable().getOnAction();
        EventHandler<ActionEvent> handlerTipo = listaHistorialCambiosControlador.getCmbTipo().getOnAction();

        // Desactivamos los eventos asociados a los filtros
        listaHistorialCambiosControlador.getDtpFechaCambio().setOnAction(null);
        listaHistorialCambiosControlador.getCmbResponsable().setOnAction(null);
        listaHistorialCambiosControlador.getCmbTipo().setOnAction(null);

        // Limpiamos los filtros
        listaHistorialCambiosControlador.getDtpFechaCambio().setValue(null);
        listaHistorialCambiosControlador.getCmbResponsable().setValue(null);
        listaHistorialCambiosControlador.getCmbTipo().setValue(null);
        listaHistorialCambiosControlador.getTxtDetalles().clear();

        // Filtramos los cambios
        filtrarCambios();

        // Restauramos los eventos asociados a los filtros
        listaHistorialCambiosControlador.getCmbResponsable().setOnAction(handlerResponsable);
        listaHistorialCambiosControlador.getDtpFechaCambio().setOnAction(handlerFechaCambio);
        listaHistorialCambiosControlador.getCmbTipo().setOnAction(handlerTipo);
    }

    public void limpiarFormulario() {
        // Limpiamos el formulario
        abmCambioControlador.getDtpFechaCambio().setValue(null);
        abmCambioControlador.getCmbResponsable().setValue(null);
        abmCambioControlador.getCmbTipo().setValue(null);
        abmCambioControlador.getTxtDetalles().clear();
    }

    // Metodos para interactuar con los selectores

    public void seleccionarMiembro(ComboBox<Miembro> combo) throws Exception {
        selectorControlador.seleccionarMiembro(combo);
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