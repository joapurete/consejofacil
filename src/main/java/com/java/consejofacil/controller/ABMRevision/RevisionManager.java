package com.java.consejofacil.controller.ABMRevision;

import com.java.consejofacil.config.StageManager;
import com.java.consejofacil.helpers.Helpers;
import com.java.consejofacil.model.*;
import com.java.consejofacil.service.EstadoExpediente.EstadoExpedienteServiceImpl;
import com.java.consejofacil.service.Expediente.ExpedienteServiceImpl;
import com.java.consejofacil.service.Reunion.ReunionServiceImpl;
import com.java.consejofacil.service.Revision.RevisionServiceImpl;
import com.java.consejofacil.view.FXMLView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class RevisionManager {

    // Variables de control
    private final DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Ayudas necesarias
    @Autowired
    @Lazy
    private Helpers helpers;

    // Servicios necesarios
    @Autowired
    @Lazy
    private ReunionServiceImpl reunionService;
    @Autowired
    @Lazy
    private ExpedienteServiceImpl expedienteService;
    @Autowired
    @Lazy
    private RevisionServiceImpl revisionService;
    @Autowired
    @Lazy
    private EstadoExpedienteServiceImpl estadoExpedienteService;

    // Controladores de los fxml
    @Autowired
    @Lazy
    private ListaRevisionesController listaRevisionesControlador;
    @Autowired
    @Lazy
    private FormularioRevisionController abmRevisionControlador;
    @Autowired
    @Lazy
    private FormularioListaRevisionController abmListaRevisionControlador;

    // Administradores necesarios
    @Autowired
    @Lazy
    private StageManager stageManager;

    // Metodos de inicialización de componentes

    public void inicializarTablaRevisiones() {
        try {
            // Asociamos columnas de la tabla
            listaRevisionesControlador.getColDetallesRevision().setCellValueFactory(new PropertyValueFactory<>("detallesRevision"));
            listaRevisionesControlador.getColReunion().setCellValueFactory(new PropertyValueFactory<>("reunion"));
            listaRevisionesControlador.getColExpediente().setCellValueFactory(new PropertyValueFactory<>("expediente"));

            // Cargamos listas
            listaRevisionesControlador.getRevisiones().clear();
            listaRevisionesControlador.getFiltroRevisiones().clear();
            listaRevisionesControlador.getRevisiones().addAll(revisionService.findAll());
            listaRevisionesControlador.getFiltroRevisiones().addAll(listaRevisionesControlador.getRevisiones());

            // Cargamos la tabla
            listaRevisionesControlador.getTblRevisiones().setItems(listaRevisionesControlador.getFiltroRevisiones());
        } catch (Exception e) {
            listaRevisionesControlador.getLog().error(e.getMessage());
        }
    }

    public void inicializarFiltros() {
        // Cargamos lista y combo de reuniones
        listaRevisionesControlador.getReuniones().clear();
        listaRevisionesControlador.getReuniones().addAll(reunionService.encontrarReunionesHastaHoy());
        listaRevisionesControlador.getCmbReunion().setItems(listaRevisionesControlador.getReuniones());
        configurarComboEditable(listaRevisionesControlador.getCmbReunion());

        // Cargamos lista y combo de expedientes
        listaRevisionesControlador.getExpedientes().clear();
        listaRevisionesControlador.getExpedientes().addAll(expedienteService.encontrarExpedientesAbiertos());
        listaRevisionesControlador.getCmbExpediente().setItems(listaRevisionesControlador.getExpedientes());
        configurarComboEditable(listaRevisionesControlador.getCmbExpediente());
    }

    public void inicializarCombosFormulario(BaseFormularioRevision controlador) {
        // Cargamos lista y combo de reuniones
        controlador.getReuniones().clear();
        controlador.getReuniones().addAll(reunionService.encontrarReunionesHastaHoy());
        controlador.getCmbReunion().setItems(controlador.getReuniones());
        configurarComboEditable(controlador.getCmbReunion());

        if (controlador instanceof FormularioRevisionController) {
            // Cargamos lista y combo de expedientes
            controlador.getExpedientes().clear();
            controlador.getExpedientes().addAll(expedienteService.encontrarExpedientesAbiertos());
            controlador.getCmbExpediente().setItems(controlador.getExpedientes());
            configurarComboEditable(controlador.getCmbExpediente());
        }
    }

    // Metodo para cargar el ABM

    public void cargarFormulario(FXMLView fxml, Revision revision) {
        try {
            boolean listaSeleccionada = listaRevisionesControlador.getCheckLista().isSelected();
            boolean autocompletadoSeleccionado = listaRevisionesControlador.getCheckAutocompletado().isSelected();
            Reunion reunionSeleccionada = listaRevisionesControlador.getCmbReunion().getValue();
            Expediente expedienteSeleccionado = listaRevisionesControlador.getCmbExpediente().getValue();
            String detallesRevision = listaRevisionesControlador.getTxtDetalles().getText().trim();

            // Cargamos FXML de ABMRevision
            Parent rootNode = stageManager.loadView(fxml.getFxmlFile());

            // Obtenemos el controlador a utilizar
            BaseFormularioRevision controlador = listaSeleccionada ? abmListaRevisionControlador : abmRevisionControlador;

            if (revision != null) {
                // Establecemos revision seleccionada
                controlador.establecerRevision(revision, controlador);
            } else if (autocompletadoSeleccionado) {
                // Autocompletamos el formulario si el CheckBox está seleccionado
                autocompletarFormulario(new Revision(reunionSeleccionada, expedienteSeleccionado, detallesRevision), controlador);
            }

            // Abrimos modal
            stageManager.openModal(rootNode, fxml);

        } catch (Exception e) {
            listaRevisionesControlador.getLog().error(e.getMessage());
        }
    }

    public void procesarRevision(Revision revision, boolean nuevoValor) {
        if (revision != null) {
            // Si la revisión no está dentro de la lista, lo agregamos
            if (nuevoValor) {
                // Lo agregamos a la lista
                listaRevisionesControlador.getRevisiones().add(revision);

                // Aplicamos el filtro si es necesario
                if (aplicarFiltro(revision)) {
                    listaRevisionesControlador.getFiltroRevisiones().add(revision);
                }
            } else {
                // Actualizamos la revision
                actualizarRevision(revision);

                // Quitamos el filtro si es necesario
                if (quitarFiltro(revision)) {
                    listaRevisionesControlador.getFiltroRevisiones().remove(revision);
                }
            }
        }
    }

    public void actualizarRevision(Revision revision) {
        listaRevisionesControlador.getRevisiones().stream()
                // Filtramos los elementos que coinciden con el ID de la revision
                .filter(rev -> rev.getId() == revision.getId())
                // Actualizamos los atributos del elemento
                .findFirst().ifPresent(rev -> {
                    rev.setReunion(revision.getReunion());
                    rev.setExpediente(revision.getExpediente());
                    rev.setDetallesRevision(revision.getDetallesRevision());
                });
    }

    // Metodos para agregar, modificar y eliminar revisiones

    public Revision obtenerDatosFormulario(){
        // Obtenemos la información en los campos
        Reunion reunion = abmRevisionControlador.getCmbReunion().getValue();
        Expediente expediente = abmRevisionControlador.getCmbExpediente().getValue();
        String detallesRevision = !abmRevisionControlador.getTxtDetallesRevision().getText().trim().isEmpty() ?
                abmRevisionControlador.getTxtDetallesRevision().getText().trim() : "No hay detalles para mostrar.";

        // Creamos una nueva revisión auxiliar
        return new Revision(reunion, expediente, detallesRevision);
    }

    public void guardarRevision() {
        if (validarCamposFormulario(abmRevisionControlador)) {
            // Creamos una nueva revisión auxiliar
            Revision aux = obtenerDatosFormulario();

            // Si la revisión es diferente de nulo, queremos modificar
            if (abmRevisionControlador.getRevision() != null) {

                // Establecemos el ID a la revisión auxiliar
                aux.setId(abmRevisionControlador.getRevision().getId());

                // Modificamos la revisión
                if (modificarRevision(aux)) {
                    mostrarMensaje(Alert.AlertType.INFORMATION, "Info", "Se ha modificado la revisión correctamente!");
                } else {
                    mostrarMensaje(Alert.AlertType.ERROR, "Error", "No se pudo modificar la revisión correctamente!");
                }

            } else {
                // Si la revisión es nulo, queremos agregar
                if (agregarRevision(aux)) {
                    mostrarMensaje(Alert.AlertType.INFORMATION, "Info", "Se ha agregado la revisión correctamente!");
                } else {
                    mostrarMensaje(Alert.AlertType.ERROR, "Error", "No se pudo agregar la revisión correctamente!");
                }
            }

            // Actuailizamos la tabla de revisiones
            listaRevisionesControlador.getTblRevisiones().refresh();

            // Salimos del modal
            stageManager.closeModal(FXMLView.FormularioRevision.getKey());
        }
    }

    public boolean agregarRevision(Revision revision) {
        try {
            // Guardamos la revisión auxiliar
            revision = revisionService.save(revision);

            // Agregamos revisión a la tabla
            procesarRevision(revision, true);

            // Indicamos que la operacion fue exitosa
            return true;

        } catch (Exception e) {
            abmRevisionControlador.getLog().error(e.getMessage());
        }
        // Indicamos que hubo un error
        return false;
    }


    public boolean modificarRevision(Revision revision) {
        try {
            // Modificamos la revisión
            revision = revisionService.update(revision);

            // Agregamos revisión modificada a la tabla
            procesarRevision(revision, false);

            // Indicamos que la operacion fue exitosa
            return true;

        } catch (Exception e) {
            abmRevisionControlador.getLog().error(e.getMessage());
        }
        // Indicamos que hubo un error
        return false;
    }

    public boolean eliminarRevision(Revision revision, boolean auto) {
        // Mostramos alerta de confirmacion
        if (auto || mostrarConfirmacion("Info", "¿Está seguro que desea eliminar la revisión?")) {
            try {
                // Eliminamos la revisión
                revisionService.delete(revision);

                // Actualizamos las listas
                listaRevisionesControlador.getRevisiones().remove(revision);
                listaRevisionesControlador.getFiltroRevisiones().remove(revision);

                // Indicamos que la operacion fue exitosa
                return true;

            } catch (Exception e) {
                listaRevisionesControlador.getLog().error(e.getMessage());
            }
        }
        // Indicamos que hubo un error
        return false;
    }

    // Metodos para filtrar las revisiones

    public void filtrarRevisiones() {
        // Limpiamos la lista filtro de revisiones
        listaRevisionesControlador.getFiltroRevisiones().clear();

        // Filtramos todas las revisiones
        for (Revision rev : listaRevisionesControlador.getRevisiones()) {
            if (aplicarFiltro(rev)) {
                listaRevisionesControlador.getFiltroRevisiones().add(rev);
            }
        }

        // Actualizamos la tabla
        listaRevisionesControlador.getTblRevisiones().refresh();
    }

    public boolean aplicarFiltro(Revision rev) {
        // Obtenemos todos los filtros
        Reunion reunion = listaRevisionesControlador.getCmbReunion().getValue();
        Expediente expediente = listaRevisionesControlador.getCmbExpediente().getValue();
        String detallesRevision = listaRevisionesControlador.getTxtDetalles().getText();

        // Filtramos con base en los filtros obtenidos
        return (reunion == null || reunion.equals(rev.getReunion()))
                && (expediente == null || expediente.equals(rev.getExpediente()))
                && (detallesRevision == null || rev.getDetallesRevision().toLowerCase().contains(detallesRevision.toLowerCase()));
    }

    public boolean quitarFiltro(Revision rev) {
        // Obtenemos todos los filtros
        Reunion reunion = listaRevisionesControlador.getCmbReunion().getValue();
        Expediente expediente = listaRevisionesControlador.getCmbExpediente().getValue();
        String detallesRevision = listaRevisionesControlador.getTxtDetalles().getText();

        // Quitamos del filtro con base en los filtros obtenidos
        return (reunion != null && !reunion.equals(rev.getReunion()))
                || (expediente != null && !expediente.equals(rev.getExpediente()))
                || (detallesRevision != null && !rev.getDetallesRevision().toLowerCase().contains(detallesRevision.toLowerCase()));
    }

    // Metodos para interactuar con los selectores

    public void seleccionarExpediente(ComboBox<Expediente> combo) throws Exception {
        helpers.seleccionarExpediente(combo);
    }

    public void seleccionarReunion(ComboBox<Reunion> combo) throws Exception {
        helpers.seleccionarReunion(combo);
    }

    // Metodos de autocarga del formulario

    public void autocompletarFormulario(Revision revision, BaseFormularioRevision controlador) {
        Reunion reunion = revision.getReunion();
        Expediente expediente = revision.getExpediente();
        String detallesRevision = revision.getDetallesRevision();

        // Establecemos la reunion seleccionada
        if (reunion != null) {
            controlador.getCmbReunion().setValue(reunion);
        }

        if (controlador instanceof FormularioRevisionController) {
            // Completamos el formulario a partir de la información proporcionada
            controlador.getTxtDetallesRevision().setText(detallesRevision);

            // Establecemos el expediente seleccionado
            if (expediente != null) {
                controlador.getCmbExpediente().setValue(expediente);
            }
        }

        if (controlador instanceof FormularioListaRevisionController) {
            // Agregamos los expedientes asociados a la reunión
            cargarListaRevisiones(reunion);
        }
    }

    // Metodos de validacion de los campos de los formularios

    public boolean validarCamposFormulario(BaseFormularioRevision controlador) {
        ArrayList<String> errores = new ArrayList<>();
        Revision aux = new Revision();

        // Verificamos que haya seleccionado una reunión
        if (controlador.getCmbReunion().getValue() == null) {
            errores.add("Por favor, seleccione una reunión.");
        } else {
            Reunion reunion = reunionService.findById(controlador.getCmbReunion().getValue().getId());
            if (reunion == null) {
                errores.add("La reunión seleccionada no se encuentra en la base de datos.");
            } else {
                // Verificamos que la reunion no sea posterior al día de hoy
                if (reunion.getFechaReunion().isAfter(LocalDate.now())) {
                    errores.add("Debe seleccionar una reunión previa o programada para el día de hoy " + LocalDate.now().format(formatoFecha) + ".");
                }
                if (controlador instanceof FormularioRevisionController) {
                    // Establecemos reunión al auxiliar
                    aux.setReunion(reunion);
                }
            }
        }

        // Validaciones específicas del formulario original

        if (controlador instanceof FormularioRevisionController) {

            // Verificamos que haya seleccionado un expediente
            if (controlador.getCmbExpediente().getValue() == null) {
                errores.add("Por favor, seleccione un expediente.");
            } else {
                Expediente expediente = expedienteService.findById(controlador.getCmbExpediente().getValue().getId());
                if (expediente == null) {
                    errores.add("El expediente seleccionado no se encuentra en la base de datos.");
                } else {
                    // Verificamos que el expediente seleccionado esté abierto
                    if (!expediente.getEstadoExpediente().getEstadoExpediente().equals("Abierto")) {
                        errores.add("Debe seleccionar un expediente que esté actualmente abierto.");
                    }
                    if (aux.getReunion() != null) {
                        // Verificamos que la fecha de ingreso del expediente no sea posterior al día de la reunion
                        if (expediente.getFechaIngreso().isAfter(aux.getReunion().getFechaReunion())) {
                            errores.add("La fecha de ingreso " + expediente.getFechaIngreso().format(formatoFecha) + " del expediente N°"
                                    + expediente.getId() + " es posterior al día de la reunión " + aux.getReunion().getFechaReunion().format(formatoFecha) + ".");
                            errores.add("Debe seleccionar un expediente cuya fecha de ingreso sea igual o anterior al día de la reunión.");
                        }
                    }
                    // Establecemos expediente al auxiliar
                    aux.setExpediente(expediente);
                }
            }

            // Verificamos que los detalles no supere los 500 caracteres
            if (controlador.getTxtDetallesRevision().getLength() > 500) {
                errores.add("Los detalles del involucrado no pueden tener más de 500 caracteres.");
            }

            // Verificamos que la revisión no esté repetida en el sistema
            if (aux.getReunion() != null && aux.getExpediente() != null) {
                boolean esNuevoInvolucrado = controlador.getRevision() == null;
                boolean esDiferente = esNuevoInvolucrado ||
                        !controlador.getRevision().getReunion().equals(aux.getReunion()) ||
                        !controlador.getRevision().getExpediente().equals(aux.getExpediente());
                if (esNuevoInvolucrado || esDiferente) {
                    if (revisionService.existeDuplicado(aux.getReunion().getId(), aux.getExpediente().getId()) >= 1) {
                        errores.add("La revisión ya está asociada a la reunión seleccionada.");
                    }
                }
            }
        }

        // Validaciones específicas del formulario por lista

        if (controlador instanceof FormularioListaRevisionController) {
            // Verificamos que haya agregado al menos una revisión a la lista
            if (abmListaRevisionControlador.getExpedientesSeleccionados().isEmpty()) {
                errores.add("Por favor, seleccione un expediente.");
            }
        }

        // Verificamos si hay errores
        if (!errores.isEmpty()) {
            helpers.mostrarCadenaMensajes(errores, "Se ha producido uno o varios errores:", Alert.AlertType.ERROR, "Error");
            return false;
        }

        return true;
    }

    public boolean validarRevision(Revision rev) {
        // Creamos un array de errores
        ArrayList<String> errores = new ArrayList<>();

        // Verificamos que haya seleccionado un expediente
        Expediente expediente = expedienteService.findById(rev.getExpediente().getId());
        if (expediente == null) {
            errores.add("El expediente N° " + rev.getExpediente().getId() + " no se encuentra en la base de datos.");
        } else {
            // Verificamos que el expediente no esté cerrado
            if (!expediente.getEstadoExpediente().getEstadoExpediente().equals("Abierto")) {
                errores.add("El expediente N° " + expediente.getId() + " se encuentra cerrado.");
                errores.add("Debe seleccionar un expediente que esté actualmente abierto.");
            }
            // Verificamos que el expediente no sea posterior al día de la reunion
            if (expediente.getFechaIngreso().isAfter(rev.getReunion().getFechaReunion())) {
                errores.add("La fecha de ingreso " + expediente.getFechaIngreso().format(formatoFecha) + " del expediente N°"
                        + expediente.getId() + " es posterior al día de la reunión " + rev.getReunion().getFechaReunion().format(formatoFecha) + ".");
                errores.add("Debe seleccionar un expediente cuya fecha de ingreso sea igual o anterior al día de la reunión.");
            }
        }

        // Verificamos que los detalles no supere los 500 caracteres
        if (rev.getDetallesRevision().length() > 500) {
            errores.add("Los detalles del involucrado no pueden tener más de 500 caracteres.");
        }

        // En caso de que haya errores, agregarlo a la lista de errores para mostrarlos en pantalla
        if (!errores.isEmpty()) {
            abmListaRevisionControlador.getCadenaErrores().addAll(errores);
            return false;
        }

        return true;
    }

    // Metodos para limpiar los campos

    public void limpiarFiltros() {
        // Guardamos los eventos de los filtros
        EventHandler<ActionEvent> handlerReunion = listaRevisionesControlador.getCmbReunion().getOnAction();
        EventHandler<ActionEvent> handlerExpediente = listaRevisionesControlador.getCmbExpediente().getOnAction();

        // Desactivamos los eventos asociados a los filtros
        listaRevisionesControlador.getCmbReunion().setOnAction(null);
        listaRevisionesControlador.getCmbExpediente().setOnAction(null);

        // Limpiamos los filtros
        listaRevisionesControlador.getCmbReunion().setValue(null);
        listaRevisionesControlador.getCmbExpediente().setValue(null);
        listaRevisionesControlador.getTxtDetalles().clear();

        // Filtramos las revisiones
        filtrarRevisiones();

        // Restauramos los eventos asociados a los filtros
        listaRevisionesControlador.getCmbReunion().setOnAction(handlerReunion);
        listaRevisionesControlador.getCmbExpediente().setOnAction(handlerExpediente);
    }

    public void limpiarFormulario(BaseFormularioRevision controlador) {
        // Limpiamos el formulario
        controlador.getCmbReunion().setValue(null);

        if (controlador instanceof FormularioRevisionController) {
            controlador.getCmbExpediente().setValue(null);
            controlador.getTxtDetallesRevision().clear();
        }

        if (controlador instanceof FormularioListaRevisionController) {
            // Limpiamos la tabla de revisiones
            abmListaRevisionControlador.getTblRevisiones().getItems().clear();
        }
    }

    // Metodos para mostrar mensajes en pantalla

    public boolean mostrarConfirmacion(String titulo, String contenido) {
        return helpers.mostrarConfirmacion(titulo, contenido);
    }

    public void mostrarCadenaMensajes(ArrayList<String> mensajes, String titulo, Alert.AlertType tipoAlerta, String tituloAlerta) {
        helpers.mostrarCadenaMensajes(mensajes, titulo, tipoAlerta, tituloAlerta);
    }

    public void mostrarMensaje(Alert.AlertType tipo, String titulo, String contenido) {
        helpers.mostrarMensaje(tipo, titulo, contenido);
    }

    // Metodo para configurar un combo editable

    public <T> void configurarComboEditable(ComboBox<T> combo) {
        helpers.configurarComboEditable(combo);
    }

    // Metodos adicionales para el formulario con lista

    public void inicializarListaRevisiones() {
        // Asociamos las Columnas de la tabla
        abmListaRevisionControlador.getColDetallesRevision().setCellValueFactory(new PropertyValueFactory<>("detallesRevision"));

        // Configuramos las columnas en relacion con el expediente
        helpers.configurarIdExpediente(abmListaRevisionControlador.getColId());
        helpers.configurarNotaExpediente(abmListaRevisionControlador.getColTextoNota());
        helpers.configurarFechaIngresoExpediente(abmListaRevisionControlador.getColFechaIngreso());
        helpers.configurarEstadoExpediente(abmListaRevisionControlador.getColEstado());

        // Configuramos el TextField para que pueda modificar en tiempo de ejecucion los detalles de la revision
        helpers.configurarTextFieldTableCell(abmListaRevisionControlador.getColDetallesRevision());

        // Configuramos el CheckBox para que pueda seleccionar nuevas revisiones a la lista
        helpers.configurarCheckTableCell(abmListaRevisionControlador.getColSeleccionar(),
                abmListaRevisionControlador.getExpedientesSeleccionados());
    }

    public void inicializarFiltrosListaRevisiones() {
        // Cargamos lista y combo de estados
        abmListaRevisionControlador.getEstadosExpedientes().clear();
        abmListaRevisionControlador.getEstadosExpedientes().addAll(estadoExpedienteService.findAll());
        abmListaRevisionControlador.getCmbEstado().setItems(abmListaRevisionControlador.getEstadosExpedientes());
    }

    public void inicializarCadenasTexto() {
        // Limpiamos cadenas de errores e infos
        abmListaRevisionControlador.getCadenaErrores().clear();
        abmListaRevisionControlador.getCadenaInfos().clear();
    }

    // Metodos para buscar y seleccionar nuevos expedientes

    public void buscarExpedientesPorReunion() {
        // Agregamos los expedientes asociados a la reunión seleccionada
        if (abmListaRevisionControlador.getCmbReunion().getValue() != null) {
            cargarListaRevisiones(abmListaRevisionControlador.getCmbReunion().getValue());
        }

        // Actualizamos la tabla de revisiones
        abmListaRevisionControlador.getTblRevisiones().refresh();
    }

    public Revision buscarRevisionPorExpediente(Expediente expediente) {
        // Filtramos la lista de revisiones por el expediente proporcionado
        Optional<Revision> optionalRevision = abmListaRevisionControlador.getRevisiones().stream()
                .filter(rev -> rev.getExpediente().equals(expediente))
                .findFirst();

        // Devolvemos la revision encontrada
        return optionalRevision.orElse(null);
    }

    // Metodos para cargar la lista de revisiones y expedientes seleccionados

    public void cargarListaRevisiones(Reunion reunion) {
        // Limpiamos las listas
        abmListaRevisionControlador.getRevisiones().clear();
        abmListaRevisionControlador.getFiltroRevisiones().clear();
        abmListaRevisionControlador.getExpedientesSeleccionados().clear();

        // Cargamos la lista de revisiones seleccionadas por reunion
        abmListaRevisionControlador.getRevisiones().addAll(revisionService.encontrarRevisionesPorReunion(reunion));

        // Cargamos la lista de expedientes seleccionados
        cargarExpedientesSeleccionados();

        // Obtenemos todos los expedientes
        List<Expediente> listaExpedientes = expedienteService.findAll();

        // Iteramos todos los expedientes para mostrar aquellos que no esten seleccionados en pantalla
        for (Expediente expediente : listaExpedientes) {
            // Verificamos si el expediente seleccionado ya se encuentra en la lista
            if (buscarRevisionPorExpediente(expediente) == null) {
                // Si no se encuentra presente, lo agregamos
                abmListaRevisionControlador.getRevisiones().add(new Revision(reunion, expediente, "--"));
            }
        }

        // Cargamos la lista que contiene el filtro de revisiones
        abmListaRevisionControlador.getFiltroRevisiones().addAll(abmListaRevisionControlador.getRevisiones());

        // Establecemos los elementos en la tabla de revisiones
        abmListaRevisionControlador.getTblRevisiones().setItems(abmListaRevisionControlador.getFiltroRevisiones());
    }

    public void cargarExpedientesSeleccionados() {
        // Cargamos la lista de epedientes seleccionados
        for (Revision revision : abmListaRevisionControlador.getRevisiones()) {
            abmListaRevisionControlador.getExpedientesSeleccionados().put(revision.getExpediente(), helpers.FLAG_MODIFICAR);
        }
    }

    // Metodos para filtrar las revisiones seleccionadas

    public boolean mostrarExpedientesSeleccionados(Revision rev) {
        // Verificamos si el CheckBox de mostrar seleccionados está presionado
        // En ese caso, mostrar solo aquellos que esten contenidos en la lista, y no los haya que eliminar
        return (abmListaRevisionControlador.getExpedientesSeleccionados().containsKey(rev.getExpediente())
                && abmListaRevisionControlador.getExpedientesSeleccionados().get(rev.getExpediente()) != helpers.FLAG_ELIMINAR)
                || !abmListaRevisionControlador.getCheckMostrarSeleccionados().isSelected();
    }

    public void filtrarListaRevisiones() {
        // Limpiamos la lista filtro de revisiones
        abmListaRevisionControlador.getFiltroRevisiones().clear();

        // Filtramos todas las revisiones según el criterio especificado
        for (Revision rev : abmListaRevisionControlador.getRevisiones()) {
            if (mostrarExpedientesSeleccionados(rev)) {
                if (aplicarFiltroListaRevisiones(rev)) {
                    abmListaRevisionControlador.getFiltroRevisiones().add(rev);
                }
            }
        }

        // Actualizamos la tabla
        abmListaRevisionControlador.getTblRevisiones().refresh();
    }

    public boolean aplicarFiltroListaRevisiones(Revision rev) {
        // Obtenemos todos los filtros
        String nota = abmListaRevisionControlador.getTxtNota().getText().trim();
        String detallesRevision = abmListaRevisionControlador.getTxtDetalles().getText();
        LocalDate fechaIngreso = abmListaRevisionControlador.getDtpFechaIngreso().getValue();
        EstadoExpediente estado = abmListaRevisionControlador.getCmbEstado().getValue();

        // Filtramos con base en los filtros obtenidos
        return (fechaIngreso == null || fechaIngreso.equals(rev.getExpediente().getFechaIngreso()))
                && (estado == null || estado.equals(rev.getExpediente().getEstadoExpediente()))
                && (rev.getExpediente().getIniciante().toString().toLowerCase().contains(nota.toLowerCase())
                || rev.getExpediente().getTextoNota().toLowerCase().contains(nota.toLowerCase()))
                && (detallesRevision == null || rev.getDetallesRevision().toLowerCase().contains(detallesRevision.toLowerCase()));
    }

    public void limpiarFiltrosListaExpedientes() {
        // Guardamos los eventos de los filtros
        EventHandler<ActionEvent> handlerEstado = abmListaRevisionControlador.getCmbEstado().getOnAction();
        EventHandler<ActionEvent> handlerFechaIngreso = abmListaRevisionControlador.getDtpFechaIngreso().getOnAction();

        // Desactivamos los eventos asociados a los filtros
        abmListaRevisionControlador.getCmbEstado().setOnAction(null);
        abmListaRevisionControlador.getDtpFechaIngreso().setOnAction(null);

        // Limpiamos los filtros
        abmListaRevisionControlador.getTxtNota().clear();
        abmListaRevisionControlador.getDtpFechaIngreso().setValue(null);
        abmListaRevisionControlador.getCmbEstado().setValue(null);
        abmListaRevisionControlador.getTxtDetalles().clear();

        // Filtramos las revisiones
        filtrarListaRevisiones();

        // Restauramos los eventos asociados a los filtros
        abmListaRevisionControlador.getCmbEstado().setOnAction(handlerEstado);
        abmListaRevisionControlador.getDtpFechaIngreso().setOnAction(handlerFechaIngreso);
    }

    // Metodos de los CheckBox del formulario con lista

    public void seleccionarTodosLosExpedientes() {
        // Guardamos el valor del CheckBox
        boolean valorActual = abmListaRevisionControlador.getCheckSeleccionarTodo().isSelected();
        // Creamos una copia de la lista de revisiones (para evitar bugs)
        List<Revision> copiaRevisiones = new ArrayList<>(abmListaRevisionControlador.getFiltroRevisiones());

        // Iteramos sobre la copia de la lista
        for (Revision revision : copiaRevisiones) {
            helpers.seleccionarItemCheckTableCell(revision.getExpediente(), abmListaRevisionControlador.getExpedientesSeleccionados(), valorActual);
        }

        // Actualizamos la tabla de revisiones
        abmListaRevisionControlador.getTblRevisiones().refresh();
    }

    // Metodos para guardar lista de revisiones

    public void guardarListaRevisiones() {
        // Validamos los campos y que la lista de revisiones seleccionadas no este vacia
        if (validarCamposFormulario(abmListaRevisionControlador)) {

            // Iteramos todos los expedientes seleccionados
            for (Map.Entry<Expediente, Integer> entry : abmListaRevisionControlador.getExpedientesSeleccionados().entrySet()) {

                // Obtenemos la revision seleccionada, junto con su flag
                Revision revision = buscarRevisionPorExpediente(entry.getKey());
                int flag = entry.getValue();

                // Verificamos si la revision es diferente de nulo
                if (revision != null) {

                    // Si esta seleccionado, queremos agregar o modificar
                    if (flag != helpers.FLAG_ELIMINAR) {

                        // Validamos la información de la revision
                        if (validarRevision(revision)) {

                            // Verificamos que los detalles no esten vacios
                            if (revision.getDetallesRevision().isEmpty()) {
                                revision.setDetallesRevision("No hay detalles para mostrar.");
                            }

                            // Si el ID es diferente de 0, quiere decir que ya existe
                            if (revision.getId() != 0 || flag == helpers.FLAG_MODIFICAR) {
                                // Modificamos la revisión
                                if (modificarRevision(revision)) {
                                    abmListaRevisionControlador.getCadenaInfos().add("Se ha modificado la revisión del expediente N° " + revision.getExpediente().getId() + " correctamente!");
                                } else {
                                    abmListaRevisionControlador.getCadenaErrores().add("No se pudo modificar la revisión del expediente N° " + revision.getExpediente().getId() + " correctamente!");
                                }
                            } else {
                                // Agregamos la revisión
                                if (agregarRevision(revision)) {
                                    abmListaRevisionControlador.getCadenaInfos().add("Se ha agregado la revisión del expediente N° " + revision.getExpediente().getId() + " correctamente!");
                                } else {
                                    abmListaRevisionControlador.getCadenaErrores().add("No se pudo agregar la revisión del expediente N° " + revision.getExpediente().getId() + " correctamente!");
                                }
                            }
                        }

                    } else {
                        // Si no está dentro de los expedientes seleccionados, queremos eliminar
                        // Eliminamos la revisión
                        if (eliminarRevision(revision, true)) {
                            abmListaRevisionControlador.getCadenaInfos().add("Se ha eliminado la revisión del expediente N° " + revision.getExpediente().getId() + " correctamente!");
                        } else {
                            abmListaRevisionControlador.getCadenaErrores().add("No se pudo eliminar la revisión del expediente N° " + revision.getExpediente().getId() + " correctamente!");

                        }
                    }
                }
            }

            // Mostramos cadenas de errores e infos en pantalla
            mostrarCadenaMensajes(abmListaRevisionControlador.getCadenaErrores(), "Se ha producido uno o varios errores:", Alert.AlertType.ERROR, "Error");
            mostrarCadenaMensajes(abmListaRevisionControlador.getCadenaInfos(), "Se ha producido una o varias modificaciones:", Alert.AlertType.INFORMATION, "Info");

            // Actualizamos la tabla de revisiones
            listaRevisionesControlador.getTblRevisiones().refresh();

            // Salimos del modal
            stageManager.closeModal(FXMLView.FormularioListaRevision.getKey());
        }
    }
}
