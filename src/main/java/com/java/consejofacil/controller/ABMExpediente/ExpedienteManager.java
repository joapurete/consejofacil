package com.java.consejofacil.controller.ABMExpediente;

import com.java.consejofacil.config.StageManager;
import com.java.consejofacil.security.SessionManager;
import com.java.consejofacil.helper.Alertas.AlertHelper;
import com.java.consejofacil.helper.Componentes.ComponentHelper;
import com.java.consejofacil.controller.SelectorController;
import com.java.consejofacil.helper.Componentes.TableCellFactoryHelper;
import com.java.consejofacil.helper.Utilidades.DateFormatterHelper;
import com.java.consejofacil.model.EstadoExpediente;
import com.java.consejofacil.model.Expediente;
import com.java.consejofacil.model.Miembro;
import com.java.consejofacil.service.EstadoExpediente.EstadoExpedienteServiceImpl;
import com.java.consejofacil.service.Expediente.ExpedienteServiceImpl;
import com.java.consejofacil.service.Miembro.MiembroServiceImpl;
import com.java.consejofacil.view.FXMLView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.ArrayList;

@Component
public class ExpedienteManager {

    // Servicios utilizados
    @Autowired
    @Lazy
    private ExpedienteServiceImpl expedienteService;
    @Autowired
    @Lazy
    private MiembroServiceImpl miembroService;
    @Autowired
    @Lazy
    private EstadoExpedienteServiceImpl estadoExpedienteService;

    // Controladores de los fxml
    @Autowired
    @Lazy
    private ListaExpedientesController listaExpedientesControlador;
    @Autowired
    @Lazy
    private FormularioExpedienteController abmExpedienteControlador;
    @Autowired
    @Lazy
    private SelectorExpedienteController selectorExpedienteControlador;
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

    public void inicializarTablaExpedientes(BaseTablaExpedientes controlador) {
        try {
            // Asociamos columnas de la tabla
            controlador.getColId().setCellValueFactory(new PropertyValueFactory<>("id"));
            controlador.getColTextoNota().setCellValueFactory(new PropertyValueFactory<>("textoNota"));
            controlador.getColFechaIngreso().setCellValueFactory(new PropertyValueFactory<>("fechaIngreso"));
            controlador.getColIniciante().setCellValueFactory(new PropertyValueFactory<>("iniciante"));
            controlador.getColEstado().setCellValueFactory(new PropertyValueFactory<>("estadoExpediente"));

            // Formateamos la fecha de ingreso
            TableCellFactoryHelper.configurarCeldaFecha(controlador.getColFechaIngreso());

            // Cargamos listas
            controlador.getExpedientes().clear();
            controlador.getFiltroExpedientes().clear();
            controlador.getExpedientes().addAll(expedienteService.findAll());
            controlador.getFiltroExpedientes().addAll(controlador.getExpedientes());

            // Cargamos la tabla
            controlador.getTblExpedientes().setItems(controlador.getFiltroExpedientes());
        } catch (Exception e) {
            controlador.getLog().error(e.getMessage());
        }
    }

    public void inicializarFiltros(BaseTablaExpedientes controlador) {
        // Cargamos lista y combo de miembros
        controlador.getMiembros().clear();
        controlador.getMiembros().addAll(miembroService.encontrarMiembrosActivos());
        controlador.getCmbIniciante().setItems(controlador.getMiembros());
        configurarComboEditable(controlador.getCmbIniciante());

        // Cargamos lista y combo de estados
        controlador.getEstadosExpedientes().clear();
        controlador.getEstadosExpedientes().addAll(estadoExpedienteService.findAll());
        controlador.getCmbEstado().setItems(controlador.getEstadosExpedientes());
    }

    public void inicializarCombosFormulario() {
        // Cargamos lista y combo de miembros
        abmExpedienteControlador.getMiembros().clear();
        abmExpedienteControlador.getMiembros().addAll(miembroService.encontrarMiembrosActivos());
        abmExpedienteControlador.getCmbIniciante().setItems(abmExpedienteControlador.getMiembros());
        configurarComboEditable(abmExpedienteControlador.getCmbIniciante());

        // Cargamos lista y combo de estados
        abmExpedienteControlador.getEstadosExpedientes().clear();
        abmExpedienteControlador.getEstadosExpedientes().addAll(estadoExpedienteService.findAll());
        abmExpedienteControlador.getCmbEstadoExpediente().setItems(abmExpedienteControlador.getEstadosExpedientes());
    }

    // Metodo para cargar el ABM

    public void cargarFormulario(Expediente expediente) {
        try {
            boolean autocompletadoSeleccionado = listaExpedientesControlador.getCheckAutocompletado().isSelected();
            String textoNota = listaExpedientesControlador.getTxtNota().getText().trim();
            LocalDate fechaIngresoSeleccionada = listaExpedientesControlador.getDtpFechaIngreso().getValue();
            Miembro inicianteSeleccionado = listaExpedientesControlador.getCmbIniciante().getValue();
            EstadoExpediente estadoExpedienteSeleccionado = listaExpedientesControlador.getCmbEstado().getValue();

            // Cargamos FXML de ABMExpediente
            Parent rootNode = stageManager.loadView(FXMLView.FormularioExpediente.getFxmlFile());

            if (expediente != null) {
                abmExpedienteControlador.establecerExpediente(expediente);
            } else if (autocompletadoSeleccionado) {
                // Autocompletamos el formulario si el CheckBox está seleccionado
                autocompletarFormulario(new Expediente(textoNota, fechaIngresoSeleccionada, inicianteSeleccionado,
                        estadoExpedienteSeleccionado));
            }

            // Abrimos modal
            stageManager.openModal(rootNode, FXMLView.FormularioExpediente);

        } catch (Exception e) {
            listaExpedientesControlador.getLog().error(e.getMessage());
        }
    }

    public void procesarExpediente(Expediente expediente, boolean nuevoValor) {
        if (expediente != null) {
            // Si el expediente no está dentro de la lista, la agregamos
            if (nuevoValor) {
                // Lo agregamos a la lista
                listaExpedientesControlador.getExpedientes().add(expediente);

                // Aplicamos el filtro si es necesario
                if (aplicarFiltro(expediente, listaExpedientesControlador)) {
                    listaExpedientesControlador.getFiltroExpedientes().add(expediente);
                }
            } else {
                // Actualizamos el expediente
                actualizarExpediente(expediente);

                // Quitamos el filtro si es necesario
                if (quitarFiltro(expediente, listaExpedientesControlador)) {
                    listaExpedientesControlador.getFiltroExpedientes().remove(expediente);
                }
            }
        }
    }

    public void actualizarExpediente(Expediente expediente) {
        listaExpedientesControlador.getExpedientes().stream()
                // Filtramos los elementos que coinciden con el ID del expediente
                .filter(exp -> exp.getId() == expediente.getId())
                // Actualizamos los atributos del elemento
                .findFirst().ifPresent(exp -> {
                    exp.setTextoNota(expediente.getTextoNota());
                    exp.setFechaIngreso(expediente.getFechaIngreso());
                    exp.setIniciante(expediente.getIniciante());
                    exp.setEstadoExpediente(expediente.getEstadoExpediente());
                });
    }

    // Metodos para agregar, modificar y eliminar expedientes

    public Expediente obtenerDatosFormulario(){
        // Obtenemos la información proporcionada
        String textoNota = abmExpedienteControlador.getTxtTextoNota().getText().trim();
        LocalDate fechaIngresoSeleccionada = abmExpedienteControlador.getDtpFechaIngreso().getValue();
        Miembro inicianteSeleccionado = abmExpedienteControlador.getCmbIniciante().getValue();
        EstadoExpediente estadoExpedienteSeleccionado = abmExpedienteControlador.getCmbEstadoExpediente().getValue();

        // Creamos un nuevo expediente auxiliar
        return new Expediente(textoNota, fechaIngresoSeleccionada, inicianteSeleccionado,
                estadoExpedienteSeleccionado);
    }

    public void guardarExpediente() {
        if (validarCamposFormulario()) {
            // Creamos un nuevo expediente auxiliar
            Expediente aux = obtenerDatosFormulario();

            // Si el expediente es diferente de nulo, queremos modificar
            if (abmExpedienteControlador.getExpediente() != null) {

                // Establecemos el ID al expediente auxiliar
                aux.setId(abmExpedienteControlador.getExpediente().getId());

                // Modificamos el expediente
                modificarExpediente(aux);
            } else {
                // Si el expediente es nulo, queremos agregar
                agregarExpediente(aux);
            }

            // Actuailizamos la tabla de acciones
            listaExpedientesControlador.getTblExpedientes().refresh();

            // Salimos del modal
            stageManager.closeModal(FXMLView.FormularioExpediente.getKey());
        }
    }

    public void agregarExpediente(Expediente expediente) {
        try {
            // Guardamos el expediente auxiliar
            expediente = expedienteService.save(expediente);

            // Agregamos expediente a la tabla
            procesarExpediente(expediente, true);

            // Mostramos un mensaje
            mostrarMensaje(false, "Info", "Se ha agregado el expediente correctamente!");
            
        } catch (Exception e) {
            abmExpedienteControlador.getLog().error(e.getMessage());
            mostrarMensaje(true, "Error", "No se pudo agregar el expediente correctamente!");
        }
    }


    public void modificarExpediente(Expediente expediente) {
        try {
            // Modificamos el expediente
            expediente = expedienteService.update(expediente);

            // Agregamos expediente modificado a la tabla
            procesarExpediente(expediente, false);

            // Mostramos un mensaje
            mostrarMensaje(false, "Info", "Se ha modificado el expediente correctamente!");
            
        } catch (Exception e) {
            abmExpedienteControlador.getLog().error(e.getMessage());
            mostrarMensaje(true, "Error", "No se pudo modificar el expediente correctamente!");
        }
    }

    public void eliminarExpediente(Expediente expediente, boolean auto) {
        // Mostramos alerta de confirmacion
        if (auto || mostrarConfirmacion("Info", "¿Está seguro que desea eliminar el expediente?")) {
            try {
                // Eliminamos el expediente
                expedienteService.delete(expediente);

                // Actualizamos las listas
                listaExpedientesControlador.getExpedientes().remove(expediente);
                listaExpedientesControlador.getFiltroExpedientes().remove(expediente);

                // Mostramos un mensaje
                mostrarMensaje(false, "Info", "Se ha eliminado el expediente correctamente!");
                
            } catch (Exception e) {
                listaExpedientesControlador.getLog().error(e.getMessage());
                mostrarMensaje(true, "Info", "No se pudo eliminar el expediente correctamente!");
            }
        }
    }

    // Metodos para filtrar los expedientes

    public void filtrarExpedientes(BaseTablaExpedientes controlador) {
        // Limpiamos la lista filtro de expedientes
        controlador.getFiltroExpedientes().clear();

        // Filtramos todos los expedientes
        for (Expediente exp : controlador.getExpedientes()) {
            if (aplicarFiltro(exp, controlador)) {
                controlador.getFiltroExpedientes().add(exp);
            }
        }

        // Actualizamos la tabla
        controlador.getTblExpedientes().refresh();
    }

    public boolean aplicarFiltro(Expediente exp, BaseTablaExpedientes controlador) {
        // Obtenemos todos los filtros
        String nota = controlador.getTxtNota().getText();
        Miembro iniciante = controlador.getCmbIniciante().getValue();
        LocalDate fechaIngreso = controlador.getDtpFechaIngreso().getValue();
        EstadoExpediente estado = controlador.getCmbEstado().getValue();

        // Filtramos con base en los filtros obtenidos
        return (nota == null || exp.getIniciante().toString().toLowerCase().contains(nota.toLowerCase())
                || exp.getTextoNota().toLowerCase().contains(nota.toLowerCase()))
                && (iniciante == null || iniciante.equals(exp.getIniciante()))
                && (fechaIngreso == null || fechaIngreso.equals(exp.getFechaIngreso()))
                && (estado == null || estado.equals(exp.getEstadoExpediente()));
    }

    public boolean quitarFiltro(Expediente exp, BaseTablaExpedientes controlador) {
        // Obtenemos todos los filtros
        String nota = controlador.getTxtNota().getText();
        Miembro iniciante = controlador.getCmbIniciante().getValue();
        LocalDate fechaIngreso = (controlador.getDtpFechaIngreso().getValue() != null)
                ? controlador.getDtpFechaIngreso().getValue() : null;
        EstadoExpediente estado = controlador.getCmbEstado().getValue();

        // Quitamos del filtro con base en los filtros obtenidos
        return (nota != null && !exp.getIniciante().toString().toLowerCase().contains(nota.toLowerCase())
                && !exp.getTextoNota().toLowerCase().contains(nota.toLowerCase()))
                || (iniciante != null && !iniciante.equals(exp.getIniciante()))
                || (fechaIngreso != null && !fechaIngreso.equals(exp.getFechaIngreso()))
                || (estado != null && !estado.equals(exp.getEstadoExpediente()));
    }

    // Metodos de autocarga del formulario

    public void autocompletarFormulario(Expediente expediente) {
        // Completamos el formulario a partir de la información proporcionada
        abmExpedienteControlador.getTxtTextoNota().setText(expediente.getTextoNota());

        if (expediente.getFechaIngreso() != null) {
            abmExpedienteControlador.getDtpFechaIngreso().setValue(expediente.getFechaIngreso());
        }

        if (expediente.getIniciante() != null) {
            abmExpedienteControlador.getCmbIniciante().setValue(expediente.getIniciante());
        }

        if (expediente.getEstadoExpediente() != null) {
            abmExpedienteControlador.getCmbEstadoExpediente().setValue(expediente.getEstadoExpediente());
        }
    }

    // Metdodos para validar los campos del formulario

    public boolean validarCamposFormulario() {
        ArrayList<String> errores = new ArrayList<>();
        Expediente expediente = obtenerDatosFormulario();

        // Verificamos que la nota no este vacia y que no supere los 500 caracteres
        if (expediente.getTextoNota().isEmpty()) {
            errores.add("Por favor, ingrese una nota.");
        } else if (expediente.getTextoNota().length() > 500) {
            errores.add("La nota no puede tener más de 500 caracteres.");
        }

        // Verificamos que la fecha de ingreso no este vacia y que no sea posterior al día de hoy
        if (expediente.getFechaIngreso() == null) {
            errores.add("Por favor, ingrese una fecha de ingreso.");
        } else if (expediente.getFechaIngreso().isAfter(LocalDate.now())) {
            errores.add("Debe seleccionar una fecha de ingreso que sea igual o anterior al dia de hoy " + DateFormatterHelper.fechaHoy() + ".");
        }

        // Verificamos que haya seleccionado un iniciante
        if (expediente.getIniciante() == null) {
            errores.add("Por favor, seleccione un iniciante.");
        } else {
            Miembro iniciante = miembroService.findById(expediente.getIniciante().getDni());
            if (iniciante == null) {
                errores.add("El iniciante seleccionado no se encuentra en la base de datos.");
            } else if (!iniciante.getEstadoMiembro().getEstadoMiembro().equals("Activo")) {
                errores.add("Debe seleccionar un iniciante activo en el sistema.");
            }
        }

        // Verificamos que haya seleccionado un estado de expediente
        if (expediente.getEstadoExpediente() == null) {
            errores.add("Por favor, seleccione un estado de expediente.");
        } else {
            EstadoExpediente estado = estadoExpedienteService.findById(expediente.getEstadoExpediente().getId());
            if (estado == null) {
                errores.add("El estado de expediente seleccionado no se encuentra en la base de datos.");
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

    public void limpiarFiltros(BaseTablaExpedientes controlador) {
        // Guardamos los eventos de los filtros
        EventHandler<ActionEvent> handlerIniciante = controlador.getCmbIniciante().getOnAction();
        EventHandler<ActionEvent> handlerEstado = controlador.getCmbEstado().getOnAction();
        EventHandler<ActionEvent> handlerFechaIngreso = controlador.getDtpFechaIngreso().getOnAction();

        // Desactivamos los eventos asociados a los filtros
        controlador.getCmbIniciante().setOnAction(null);
        controlador.getCmbEstado().setOnAction(null);
        controlador.getDtpFechaIngreso().setOnAction(null);

        // Limpiamos los filtros
        controlador.getTxtNota().clear();
        controlador.getCmbIniciante().setValue(null);
        controlador.getDtpFechaIngreso().setValue(null);
        controlador.getCmbEstado().setValue(null);

        // Filtramos los expedientes
        filtrarExpedientes(controlador);

        // Restauramos los eventos asociados a los filtros
        controlador.getCmbIniciante().setOnAction(handlerIniciante);
        controlador.getDtpFechaIngreso().setOnAction(handlerFechaIngreso);
        controlador.getCmbEstado().setOnAction(handlerEstado);
    }

    public void limpiarFormulario() {
        // Limpiamos el formulario
        abmExpedienteControlador.getCmbIniciante().setValue(null);
        abmExpedienteControlador.getCmbEstadoExpediente().setValue(null);
        abmExpedienteControlador.getTxtTextoNota().clear();
        abmExpedienteControlador.getDtpFechaIngreso().setValue(null);
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

    // Metodos adicionales para el selector

    public void agregarExpedienteSelector() {
        // Obtenemos el expediente seleccionado
        selectorExpedienteControlador.setExpediente(selectorExpedienteControlador.getTblExpedientes().getSelectionModel().getSelectedItem());

        // Verificamos que sea diferente de nulo
        if (selectorExpedienteControlador.getExpediente() == null) {
            mostrarMensaje(true, "Error", "Debes seleccionar un expediente!");
        } else {
            // Salimos del modal
            stageManager.closeModal(FXMLView.SelectorExpediente.getKey());
        }
    }

    public void autoseleccionarExpediente() {
        // Seleccionamos el expediente en cuestion en la tabla
        TableView.TableViewSelectionModel<Expediente> selectionModel = selectorExpedienteControlador.getTblExpedientes().getSelectionModel();
        selectionModel.select(selectorExpedienteControlador.getExpediente());
    }

}
