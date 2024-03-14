package com.java.consejofacil.controller.ABMAsistencia;

import com.java.consejofacil.config.StageManager;
import com.java.consejofacil.security.SessionManager;
import com.java.consejofacil.helper.Alertas.AlertHelper;
import com.java.consejofacil.helper.Componentes.ComponentHelper;
import com.java.consejofacil.controller.SelectorController;
import com.java.consejofacil.helper.Componentes.TableCellFactoryHelper;
import com.java.consejofacil.helper.Utilidades.DateFormatterHelper;
import com.java.consejofacil.helper.Utilidades.ListHelper;
import com.java.consejofacil.model.*;
import com.java.consejofacil.service.Asistencia.AsistenciaServiceImpl;
import com.java.consejofacil.service.Cargo.CargoServiceImpl;
import com.java.consejofacil.service.EstadoAsistencia.EstadoAsistenciaServiceImpl;
import com.java.consejofacil.service.EstadoMiembro.EstadoMiembroServiceImpl;
import com.java.consejofacil.service.Miembro.MiembroServiceImpl;
import com.java.consejofacil.service.Reunion.ReunionServiceImpl;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class AsistenciaManager {

    // Servicios necesarios
    @Autowired
    @Lazy
    private ReunionServiceImpl reunionService;
    @Autowired
    @Lazy
    private MiembroServiceImpl miembroService;
    @Autowired
    @Lazy
    private AsistenciaServiceImpl asistenciaService;
    @Autowired
    @Lazy
    private EstadoAsistenciaServiceImpl estadoAsistenciaService;
    @Autowired
    @Lazy
    private CargoServiceImpl cargoService;
    @Autowired
    @Lazy
    private EstadoMiembroServiceImpl estadoMiembroService;

    // Controladores de los fxml
    @Autowired
    @Lazy
    private ListaAsistenciaController listaAsistenciasControlador;
    @Autowired
    @Lazy
    private FormularioAsistenciaController abmAsistenciaControlador;
    @Autowired
    @Lazy
    private FormularioListaAsistenciaController abmListaAsistenciaControlador;
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

    public void inicializarTablaAsistencias() {
        try {
            // Asociamos columnas de la tabla
            listaAsistenciasControlador.getColReunion().setCellValueFactory(new PropertyValueFactory<>("reunion"));
            listaAsistenciasControlador.getColMiembro().setCellValueFactory(new PropertyValueFactory<>("miembro"));
            listaAsistenciasControlador.getColEstado().setCellValueFactory(new PropertyValueFactory<>("estadoAsistencia"));

            // Cargamos listas
            listaAsistenciasControlador.getAsistencias().clear();
            listaAsistenciasControlador.getFiltroAsistencias().clear();
            listaAsistenciasControlador.getAsistencias().addAll(asistenciaService.findAll());
            listaAsistenciasControlador.getFiltroAsistencias().addAll(listaAsistenciasControlador.getAsistencias());

            // Cargamos la tabla
            listaAsistenciasControlador.getTblAsistencias().setItems(listaAsistenciasControlador.getFiltroAsistencias());
        } catch (Exception e) {
            listaAsistenciasControlador.getLog().error(e.getMessage());
        }
    }

    public void inicializarFiltros() {
        // Cargamos lista y combo de reuniones
        listaAsistenciasControlador.getReuniones().clear();
        listaAsistenciasControlador.getReuniones().addAll(reunionService.findAll());
        listaAsistenciasControlador.getCmbReunion().setItems(listaAsistenciasControlador.getReuniones());
        ComponentHelper.configurarComboEditable(listaAsistenciasControlador.getCmbReunion());

        // Cargamos lista y combo de miembros
        listaAsistenciasControlador.getMiembros().clear();
        listaAsistenciasControlador.getMiembros().addAll(miembroService.encontrarMiembrosActivos());
        listaAsistenciasControlador.getCmbMiembro().setItems(listaAsistenciasControlador.getMiembros());
        ComponentHelper.configurarComboEditable(listaAsistenciasControlador.getCmbMiembro());

        // Cargamos lista y combo de estados de asistencias
        listaAsistenciasControlador.getEstadosAsistencias().clear();
        listaAsistenciasControlador.getEstadosAsistencias().addAll(estadoAsistenciaService.findAll());
        listaAsistenciasControlador.getCmbEstado().setItems(listaAsistenciasControlador.getEstadosAsistencias());
    }

    public void inicializarCombosFormulario(BaseFormularioAsistencia controlador) {
        // Cargamos lista y combo de reuniones
        controlador.getReuniones().clear();
        controlador.getReuniones().addAll(reunionService.findAll());
        controlador.getCmbReunion().setItems(controlador.getReuniones());
        ComponentHelper.configurarComboEditable(controlador.getCmbReunion());

        if (controlador instanceof FormularioAsistenciaController) {
            // Cargamos lista y combo de miembros
            controlador.getMiembros().clear();
            controlador.getMiembros().addAll(miembroService.encontrarMiembrosActivos());
            controlador.getCmbMiembro().setItems(controlador.getMiembros());
            ComponentHelper.configurarComboEditable(controlador.getCmbMiembro());

            // Cargamos lista y combos de estados de asistencias
            controlador.getEstadosAsistencias().clear();
            controlador.getEstadosAsistencias().addAll(estadoAsistenciaService.findAll());
            controlador.getCmbEstado().setItems(controlador.getEstadosAsistencias());
        }
    }

    // Metodo para cargar el ABM

    public void cargarFormulario(FXMLView fxml, Asistencia asistencia) throws Exception {
        try {
            // Obtenemos la selección de los controles
            boolean listaSeleccionada = listaAsistenciasControlador.getCheckLista().isSelected();
            boolean autocompletadoSeleccionado = listaAsistenciasControlador.getCheckAutocompletado().isSelected();
            Reunion reunionSeleccionada = listaAsistenciasControlador.getCmbReunion().getValue();
            Miembro miembroSeleccionado = listaAsistenciasControlador.getCmbMiembro().getValue();
            EstadoAsistencia estadoAsistenciaSeleccionado = listaAsistenciasControlador.getCmbEstado().getValue();

            // Cargamos FXML de ABMAsistencia
            Parent rootNode = stageManager.loadView(fxml.getFxmlFile());

            // Obtenemos el controlador a utilizar
            BaseFormularioAsistencia controlador = listaSeleccionada ? abmListaAsistenciaControlador : abmAsistenciaControlador;

            if (asistencia != null) {
                // Establecemos asistencia seleccionada
                controlador.establecerAsistencia(asistencia, controlador);
            } else if (autocompletadoSeleccionado) {
                // Autocompletamos el formulario si el CheckBox está seleccionado
                autocompletarFormulario(new Asistencia(reunionSeleccionada, miembroSeleccionado, estadoAsistenciaSeleccionado), controlador);
            }

            // Abrimos modal
            stageManager.openModal(rootNode, fxml);

        } catch (Exception e) {
            listaAsistenciasControlador.getLog().error(e.getMessage());
        }
    }

    public void procesarAsistencia(Asistencia asistencia, boolean nuevoValor) {
        if (asistencia != null) {
            // Si la asistencia no está dentro de la lista, la agregamos
            if (nuevoValor) {
                // La agregamos a la lista
                listaAsistenciasControlador.getAsistencias().add(asistencia);

                // Aplicamos el filtro si es necesario
                if (aplicarFiltro(asistencia)) {
                    listaAsistenciasControlador.getFiltroAsistencias().add(asistencia);
                }
            } else {
                // Actualizamos la asistencia
                actualizarAsistencia(asistencia);

                // Quitamos el filtro si es necesario
                if (quitarFiltro(asistencia)) {
                    listaAsistenciasControlador.getFiltroAsistencias().remove(asistencia);
                }
            }
        }
    }

    public void actualizarAsistencia(Asistencia asistencia) {
        listaAsistenciasControlador.getAsistencias().stream()
                // Filtramos los elementos que coinciden con el ID de la asistencia
                .filter(asis -> asis.getId() == asistencia.getId())
                // Actualizamos los atributos del elemento
                .findFirst().ifPresent(asis -> {
                    asis.setReunion(asistencia.getReunion());
                    asis.setMiembro(asistencia.getMiembro());
                    asis.setEstadoAsistencia(asistencia.getEstadoAsistencia());
                });
    }

    // Metodos para agregar, modificar y eliminar asistencias

    public Asistencia obtenerDatosFormulario(BaseFormularioAsistencia controlador) {
        // Obtenemos la información de los campos
        Reunion reunion = controlador.getCmbReunion().getValue();

        // Creamos una nueva asistencia auxiliar
        Asistencia asistencia = new Asistencia(reunion, null, null);

        // Completamos con los datos del formulario original
        if (controlador instanceof FormularioAsistenciaController) {
           asistencia.setMiembro(controlador.getCmbMiembro().getValue());
           asistencia.setEstadoAsistencia(controlador.getCmbEstado().getValue());
        }

        // Devolvemos la asistencia creada
        return asistencia;
    }

    public void guardarAsistencia() {
        if (validarCamposFormulario(abmAsistenciaControlador)) {
            // Creamos una nueva asistencia auxiliar
            Asistencia aux = obtenerDatosFormulario(abmAsistenciaControlador);

            // Si la asistencia es diferente de nulo, queremos modificar
            if (abmAsistenciaControlador.getAsistencia() != null) {

                // Establecemos el ID a la asistencia auxiliar
                aux.setId(abmAsistenciaControlador.getAsistencia().getId());

                // Modificamos la asistencia
                if (modificarAsistencia(aux)) {
                    mostrarMensaje(false, "Info", "Se ha modificado la asistencia correctamente!");
                } else {
                    mostrarMensaje(true, "Error", "No se pudo modificar la asistencia correctamente!");
                }

            } else {
                // Si la asistencia es nulo, queremos agregar
                if (agregarAsistencia(aux)) {
                    mostrarMensaje(false, "Info", "Se ha agregado la asistencia correctamente!");
                } else {
                    mostrarMensaje(true, "Error", "No se pudo agregar la asistencia correctamente!");
                }
            }

            // Actuailizamos la tabla de asistencias
            listaAsistenciasControlador.getTblAsistencias().refresh();

            // Salimos del modal
            stageManager.closeModal(FXMLView.FormularioAsistencia.getKey());
        }
    }

    public boolean agregarAsistencia(Asistencia asistencia) {
        try {
            // Guardamos la asistencia auxiliar
            asistencia = asistenciaService.save(asistencia);

            // Agregamos asistencia a la tabla
            procesarAsistencia(asistencia, true);

            // Indicamos que la operacion fue exitosa
            return true;

        } catch (Exception e) {
            abmAsistenciaControlador.getLog().error(e.getMessage());
        }
        // Indicamos que hubo un error
        return false;
    }


    public boolean modificarAsistencia(Asistencia asistencia) {
        try {
            // Modificamos la asistencia
            asistencia = asistenciaService.update(asistencia);

            // Agregamos asistencia modificada a la tabla
            procesarAsistencia(asistencia, false);

            // Indicamos que la operacion fue exitosa
            return true;

        } catch (Exception e) {
            abmAsistenciaControlador.getLog().error(e.getMessage());
        }
        // Indicamos que hubo un error
        return false;
    }

    public boolean eliminarAsistencia(Asistencia asistencia, boolean auto) {
        // Mostramos alerta de confirmacion
        if (auto || mostrarConfirmacion("Info", "¿Está seguro que desea eliminar la asistencia?")) {
            try {
                // Eliminamos la asistencia
                asistenciaService.delete(asistencia);

                // Actualizamos las listas
                listaAsistenciasControlador.getAsistencias().remove(asistencia);
                listaAsistenciasControlador.getFiltroAsistencias().remove(asistencia);

                // Indicamos que la operacion fue exitosa
                return true;

            } catch (Exception e) {
                listaAsistenciasControlador.getLog().error(e.getMessage());
            }
        }
        // Indicamos que hubo un error
        return false;
    }

    // Metodos para filtrar los involucrados

    public void filtrarAsistencias() {
        // Limpiamos la lista filtro de asistencias
        listaAsistenciasControlador.getFiltroAsistencias().clear();

        // Filtramos todas las asistencias
        for (Asistencia asis : listaAsistenciasControlador.getAsistencias()) {
            if (aplicarFiltro(asis)) {
                listaAsistenciasControlador.getFiltroAsistencias().add(asis);
            }
        }

        // Actualizamos la tabla
        listaAsistenciasControlador.getTblAsistencias().refresh();
    }

    public boolean aplicarFiltro(Asistencia asis) {
        // Obtenemos todos los filtros
        Reunion reunion = listaAsistenciasControlador.getCmbReunion().getValue();
        Miembro miembro = listaAsistenciasControlador.getCmbMiembro().getValue();
        EstadoAsistencia estadoAsistencia = listaAsistenciasControlador.getCmbEstado().getValue();

        // Filtramos con base en los filtros obtenidos
        return (reunion == null || reunion.equals(asis.getReunion()))
                && (miembro == null || miembro.equals(asis.getMiembro()))
                && (estadoAsistencia == null || estadoAsistencia.equals(asis.getEstadoAsistencia()));
    }

    public boolean quitarFiltro(Asistencia asis) {
        // Obtenemos todos los filtros
        Reunion reunion = listaAsistenciasControlador.getCmbReunion().getValue();
        Miembro miembro = listaAsistenciasControlador.getCmbMiembro().getValue();
        EstadoAsistencia estadoAsistencia = listaAsistenciasControlador.getCmbEstado().getValue();

        // Quitamos del filtro con base en los filtros obtenidos
        return (reunion != null && !reunion.equals(asis.getReunion()))
                || (miembro != null && !miembro.equals(asis.getMiembro()))
                || (estadoAsistencia != null && !estadoAsistencia.equals(asis.getEstadoAsistencia()));
    }

    // Metodos de autocarga de los formularios

    public void autocompletarFormulario(Asistencia asis, BaseFormularioAsistencia controlador) {
        Reunion reunion = asis.getReunion();
        Miembro miembro = asis.getMiembro();
        EstadoAsistencia estadoAsistencia = asis.getEstadoAsistencia();

        // Establecemos la reunion seleccionada
        if (reunion != null) {
            controlador.getCmbReunion().setValue(reunion);
        }

        if (controlador instanceof FormularioAsistenciaController) {
            // Establecemos el miembro seleccionado
            if (miembro != null) {
                controlador.getCmbMiembro().setValue(miembro);
            }

            // Establecemos el estado seleccionado
            if (estadoAsistencia != null) {
                controlador.getCmbEstado().setValue(estadoAsistencia);
            }
        }

        if (controlador instanceof FormularioListaAsistenciaController) {
            // Agregamos las asistencias asociadas a la reunion
            cargarListaAsistencias(reunion);
        }
    }

    // Metdodos para validar los campos de los formularios

    private boolean validarCamposFormulario(BaseFormularioAsistencia controlador) {
        ArrayList<String> errores = new ArrayList<>();

        // Creamos una asistencia auxiliar y otra para almacenar los datos del formulario
        Asistencia aux = new Asistencia();
        Asistencia asistencia = obtenerDatosFormulario(controlador);

        // Verificamos que haya seleccionado una reunión
        if (asistencia.getReunion() == null) {
            errores.add("Por favor, seleccione un reunión.");
        } else {
            Reunion reunion = reunionService.findById(asistencia.getReunion().getId());
            if (reunion == null) {
                errores.add("La reunión seleccionada no se encuentra en la base de datos.");
            } else if (controlador instanceof FormularioAsistenciaController) {
                // Establecemos reunión al auxiliar
                aux.setReunion(reunion);
            }
        }

        // Validaciones específicas del formulario original

        if (controlador instanceof FormularioAsistenciaController) {

            // Verificamos que haya seleccionado un miembro y que no esté inactivo en el sistema
            if (asistencia.getMiembro() == null) {
                errores.add("Por favor, seleccione un miembro del consejo.");
            } else {
                Miembro miembro = miembroService.findById(asistencia.getMiembro().getDni());
                if (miembro == null) {
                    errores.add("El miembro del consejo seleccionado no se encuentra en la base de datos.");
                } else {
                    if (!miembro.getEstadoMiembro().getEstadoMiembro().equals("Activo")) {
                        errores.add("Debe seleccionar un miembro del consejo activo en el sistema.");
                    }
                    // Establecemos miembro al auxiliar
                    aux.setMiembro(miembro);
                }
            }

            // Verificamos que haya seleccionado un estado de asistencia
            if (asistencia.getEstadoAsistencia() == null) {
                errores.add("Por favor, seleccione un estado de asistencia.");
            } else {
                EstadoAsistencia estadoAsistencia = estadoAsistenciaService.findById(asistencia.getEstadoAsistencia().getId());
                if (estadoAsistencia == null) {
                    errores.add("El estado de asistencia seleccionado no se encuentra en la base de datos.");
                } else {
                    // Verificamos que la asistencia no se marcada como presente en reuniones que aun no han ocurrido
                    if (aux.getReunion() != null) {
                        if (aux.getReunion().getFechaReunion().isAfter(LocalDate.now()) &&
                                estadoAsistencia.getEstadoAsistencia().equals("Presente")) {
                            errores.add("La asistencia no puede ser marcada como presente en reuniones " +
                                        "futuras al día de hoy " + DateFormatterHelper.fechaHoy() + ".");
                        }
                    }
                }
            }

            // Verificamos que la asistencia no esté repetida en el sistema
            if (aux.getReunion() != null && aux.getMiembro() != null) {
                boolean esNuevaAsistencia = controlador.getAsistencia() == null;
                boolean esDiferente = esNuevaAsistencia ||
                        !controlador.getAsistencia().getReunion().equals(aux.getReunion()) ||
                        !controlador.getAsistencia().getMiembro().equals(aux.getMiembro());
                if (esNuevaAsistencia || esDiferente) {
                    if (asistenciaService.existeDuplicado(aux.getReunion().getId(), aux.getMiembro().getDni()) >= 1) {
                        errores.add("La asistencia ya está asociada a la reunión seleccionada.");
                    }
                }
            }
        }

        // Validaciones específicas del formulario por lista

        if (controlador instanceof FormularioListaAsistenciaController) {
            // Verificamos que haya agregado al menos un miembro a la lista
            if (abmListaAsistenciaControlador.getMiembrosSeleccionados().isEmpty()) {
                errores.add("Por favor, seleccione un miembro del consejo.");
            }
        }

        // Verificamos si hay errores
        if (!errores.isEmpty()) {
            mostrarCadenaMensajes(true, errores, "Se ha producido uno o varios errores:", "Error");
            return false;
        }

        return true;
    }

    private boolean validarAsistencia(Asistencia asis) {
        // Creamos un array de errores
        ArrayList<String> errores = new ArrayList<>();

        // Verificamos que haya seleccionado un miembro y que no esté inactivo en el sistema
        Miembro miembro = miembroService.findById(asis.getMiembro().getDni());
        if (miembro == null) {
            errores.add("El miembro del consejo " + asis.getMiembro().toString() + " no se encuentra en la base de datos.");
        } else {
            if (!miembro.getEstadoMiembro().getEstadoMiembro().equals("Activo")) {
                errores.add("El miembro del consejo " + asis.getMiembro().toString() + " se encuentra inactivo o en espera.\n" +
                        "Debe seleccionar un miembro del consejo activo en el sistema.");
            }
        }

        // Verificamos que haya seleccionado un estado de asistencia
        if (asis.getEstadoAsistencia() == null) {
            errores.add("Debe seleccionar un estado de asistencia para el miembro del consejo " + asis.getMiembro().toString() + ".");
        } else {
            EstadoAsistencia estadoAsistencia = estadoAsistenciaService.findById(asis.getEstadoAsistencia().getId());
            if (estadoAsistencia == null) {
                errores.add("El estado de asistencia seleccionado para el miembro del consejo " + asis.getMiembro().toString() + "  no se encuentra en la base de datos.");
            }
        }

        // En caso de que haya errores, agregarlo a la lista de errores para mostrarlos en pantalla
        if (!errores.isEmpty()) {
            abmListaAsistenciaControlador.getCadenaErrores().addAll(errores);
            return false;
        }

        return true;
    }

    // Metodos para limpiar campos

    public void limpiarFiltros() {
        // Guardamos los eventos de los filtros
        EventHandler<ActionEvent> handlerEstado = listaAsistenciasControlador.getCmbEstado().getOnAction();
        EventHandler<ActionEvent> handlerReunion = listaAsistenciasControlador.getCmbReunion().getOnAction();
        EventHandler<ActionEvent> handlerMiembro = listaAsistenciasControlador.getCmbMiembro().getOnAction();

        // Desactivamos los eventos asociados a los filtros
        listaAsistenciasControlador.getCmbEstado().setOnAction(null);
        listaAsistenciasControlador.getCmbReunion().setOnAction(null);
        listaAsistenciasControlador.getCmbMiembro().setOnAction(null);

        // Limpiamos los filtros
        listaAsistenciasControlador.getCmbEstado().setValue(null);
        listaAsistenciasControlador.getCmbReunion().setValue(null);
        listaAsistenciasControlador.getCmbMiembro().setValue(null);

        // Filtramos los involucrados
        filtrarAsistencias();

        // Restauramos los eventos asociados a los filtros
        listaAsistenciasControlador.getCmbEstado().setOnAction(handlerEstado);
        listaAsistenciasControlador.getCmbReunion().setOnAction(handlerReunion);
        listaAsistenciasControlador.getCmbMiembro().setOnAction(handlerMiembro);
    }

    public void limpiarFormulario(BaseFormularioAsistencia controlador) {
        // Limpiamos el formulario
        controlador.getCmbReunion().setValue(null);

        if (controlador instanceof FormularioAsistenciaController) {
            controlador.getCmbMiembro().setValue(null);
            controlador.getCmbEstado().setValue(null);
        }

        if (controlador instanceof FormularioListaAsistenciaController) {
            // Cargamos la lista de asistencias sin una reunion seleccionada
            cargarListaAsistencias(null);
        }
    }

    // Metodos para interactuar con los selectores

    public void seleccionarReunion(ComboBox<Reunion> combo) throws Exception {
        selectorControlador.seleccionarReunion(combo);
    }

    public void seleccionarMiembro(ComboBox<Miembro> combo) throws Exception {
        selectorControlador.seleccionarMiembro(combo);
    }

    // Metodos para mostrar mensajes en pantalla

    public boolean mostrarConfirmacion(String titulo, String contenido) {
        return AlertHelper.mostrarConfirmacion(titulo, contenido);
    }

    public void mostrarCadenaMensajes(boolean error, ArrayList<String> mensajes, String titulo, String tituloAlerta) {
        AlertHelper.mostrarCadenaMensajes(error, mensajes, titulo, tituloAlerta);
    }

    public void mostrarMensaje(boolean error, String titulo, String contenido) {
        AlertHelper.mostrarMensaje(error, titulo, contenido);
    }

    // Metodos adicionales para el formulario con lista

    public void inicializarListaAsistencias() {
        // Asociamos las columnas de la tabla
        abmListaAsistenciaControlador.getColNombre().setCellValueFactory(new PropertyValueFactory<>("miembro"));
        abmListaAsistenciaControlador.getColEstadoAsistencia().setCellValueFactory(new PropertyValueFactory<>("estadoAsistencia"));

        // Configuramos las columnas en relacion con el miembro
        TableCellFactoryHelper.configurarCeldaDniMiembro(abmListaAsistenciaControlador.getColDni());
        TableCellFactoryHelper.configurarCeldaCargoMiembro(abmListaAsistenciaControlador.getColCargo());
        TableCellFactoryHelper.configurarCeldaEstadoMiembro(abmListaAsistenciaControlador.getColEstadoMiembro());

        // Configuramos el ComboBox para que pueda modificar en tiempo de ejecucion el estado de asistencia
        cargarListaEstadosAsistencias();
        TableCellFactoryHelper.configurarCeldaComboEstadoAsistencia(abmListaAsistenciaControlador.getColEstadoAsistencia(),
                abmListaAsistenciaControlador.getEstadosAsistencias());

        // Configuramos el CheckBox para que pueda seleccionar nuevas asistencias a la lista
        TableCellFactoryHelper.configurarCeldaCheck(abmListaAsistenciaControlador.getColSeleccionar(),
                abmListaAsistenciaControlador.getMiembrosSeleccionados());

        // Cargamos la lista de asistencias
        cargarListaAsistencias(null);
    }

    public void cargarListaEstadosAsistencias() {
        abmListaAsistenciaControlador.getEstadosAsistencias().clear();
        abmListaAsistenciaControlador.getEstadosAsistencias().addAll(estadoAsistenciaService.findAll());
    }

    public void inicializarFiltrosListaAsistencias() {
        // Cargamos lista y combo de cargos
        abmListaAsistenciaControlador.getCargos().clear();
        abmListaAsistenciaControlador.getCargos().addAll(cargoService.findAll());
        abmListaAsistenciaControlador.getCmbCargo().setItems(abmListaAsistenciaControlador.getCargos());

        // Cargamos lista y combo de estados de miembros
        abmListaAsistenciaControlador.getEstadosMiembros().clear();
        abmListaAsistenciaControlador.getEstadosMiembros().addAll(estadoMiembroService.findAll());
        abmListaAsistenciaControlador.getCmbEstadoMiembro().setItems(abmListaAsistenciaControlador.getEstadosMiembros());

        // Cargamos lista y combo de estados de asistencias
        abmListaAsistenciaControlador.getCmbEstadoAsistencia().setItems(abmListaAsistenciaControlador.getEstadosAsistencias());
    }

    public void inicializarCadenasTexto() {
        // Limpiamos cadenas de errores e infos
        abmListaAsistenciaControlador.getCadenaErrores().clear();
        abmListaAsistenciaControlador.getCadenaInfos().clear();
    }

    // Metodo para guardar lista de asistencias

    public void guardarListaAsistencias() {
        // Validamos los campos y que la lista de asistencias seleccionadas no este vacia
        if (validarCamposFormulario(abmListaAsistenciaControlador)) {

            // Iteramos todos los miembros seleccionados
            for (Map.Entry<Miembro, Integer> entry : abmListaAsistenciaControlador.getMiembrosSeleccionados().entrySet()) {

                // Obtenemos la asistencia seleccionada, junto con su flag
                Asistencia asistencia = buscarAsistenciaPorMiembro(entry.getKey());
                int flag = entry.getValue();

                // Verificamos si la asistencia es diferente de nulo
                if (asistencia != null) {

                    // Si esta seleccionado, queremos agregar o modificar
                    if (flag != ListHelper.FLAG_ELIMINAR) {
                        // Validamos la información de la asistencia
                        if (validarAsistencia(asistencia)) {

                            // Verificamos que la asistencia no se marcada como presente en reuniones que aun no han ocurrido
                            if (asistencia.getReunion().getFechaReunion().isAfter(LocalDate.now()) && asistencia.getEstadoAsistencia().getEstadoAsistencia().equals("Presente")) {
                                abmListaAsistenciaControlador.getCadenaErrores().add("La asistencia del miembro del consejo " + asistencia.getMiembro().toString()
                                        + " no puede ser marcada como presente en reuniones futuras al día de hoy " + DateFormatterHelper.fechaHoy() + ".");
                                asistencia.setEstadoAsistencia(abmListaAsistenciaControlador.getEstadosAsistencias().getLast());
                            }

                            // Si el ID es diferente de 0, quiere decir que ya existe
                                if (asistencia.getId() != 0 || flag == ListHelper.FLAG_MODIFICAR) {
                                // Modificamos la asistencia
                                if (modificarAsistencia(asistencia)) {
                                    abmListaAsistenciaControlador.getCadenaInfos().add("Se ha modificado la asistencia del miembro del consejo " + asistencia.getMiembro().toString() + " correctamente!");
                                } else {
                                    abmListaAsistenciaControlador.getCadenaErrores().add("No se pudo modificar la asistencia del miembro del consejo " + asistencia.getMiembro().toString() + " correctamente!");
                                }
                            } else {
                                // Agregamos la asistencia
                                if (agregarAsistencia(asistencia)) {
                                    abmListaAsistenciaControlador.getCadenaInfos().add("Se ha agregado la asistencia del miembro del consejo " + asistencia.getMiembro().toString() + " correctamente!");
                                } else {
                                    abmListaAsistenciaControlador.getCadenaErrores().add("No se pudo agregar la asistencia del miembro del consejo " + asistencia.getMiembro().toString() + " correctamente!");
                                }
                            }
                        }
                    } else {
                        // Si no está dentro de los miembros seleccionados, queremos eliminar
                        // Eliminamos la asistencia
                        if (eliminarAsistencia(asistencia, true)) {
                            abmListaAsistenciaControlador.getCadenaInfos().add("Se ha eliminado la asistencia del miembro del consejo " + asistencia.getMiembro().toString() + " correctamente!");
                        } else {
                            abmListaAsistenciaControlador.getCadenaErrores().add("No se pudo eliminar la asistencia del miembro del consejo " + asistencia.getMiembro().toString() + " correctamente!");

                        }
                    }
                }
            }

            // Mostramos cadenas de errores e infos en pantalla
            mostrarCadenaMensajes(true, abmListaAsistenciaControlador.getCadenaErrores(), "Se ha producido uno o varios errores:", "Error");
            mostrarCadenaMensajes(false, abmListaAsistenciaControlador.getCadenaInfos(), "Se ha producido una o varias modificaciones:", "Info");

            // Actualizamos la tabla de involucrados
            listaAsistenciasControlador.getTblAsistencias().refresh();

            // Salimos del modal
            stageManager.closeModal(FXMLView.FormularioListaAsistencia.getKey());
        }
    }

    // Metodos para buscar y seleccionar nuevos miembros

    public void buscarAsistenciasPorReunion() {
        // Cargamos los involucrados asociados al expediente seleccionado
        cargarListaAsistencias(abmListaAsistenciaControlador.getCmbReunion().getValue());

        // Actualizamos la tabla
        abmListaAsistenciaControlador.getTblAsistencias().refresh();
    }

    public Asistencia buscarAsistenciaPorMiembro(Miembro miembro) {
        // Filtramos la lista de asistencias por el miembro proporcionado
        Optional<Asistencia> optionalAsistencia = abmListaAsistenciaControlador.getAsistencias().stream()
                .filter(asis -> asis.getMiembro().equals(miembro))
                .findFirst();

        // Devolvemos el involucrado encontrado
        return optionalAsistencia.orElse(null);
    }

    // Metodos para cargar la lista de asistencias y miembros seleccionados

    public void cargarListaAsistencias(Reunion reunion) {
        // Limpiamos las listas
        abmListaAsistenciaControlador.getAsistencias().clear();
        abmListaAsistenciaControlador.getFiltroAsistencias().clear();
        abmListaAsistenciaControlador.getMiembrosSeleccionados().clear();

        if (reunion != null) {
            // Cargamos la lista de asistencias seleccionadas por reunion
            abmListaAsistenciaControlador.getAsistencias().addAll(asistenciaService.encontrarAsistenciasPorReunion(reunion));

            // Cargamos la lista de miembros seleccionados a partir de las asistencias
            cargarMiembrosSeleccionados();
        }

        // Obtenemos todos los miembros
        List<Miembro> listaMiembros = miembroService.findAll();

        // Iteramos todos los miembros para mostrar aquellos que no esten seleccionados en pantalla
        for (Miembro miembro : listaMiembros) {
            // Verificamos si el miembro seleccionado ya se encuentra en la lista
            if (buscarAsistenciaPorMiembro(miembro) == null) {
                // Si no se encuentra presente, lo agregamos
                abmListaAsistenciaControlador.getAsistencias().add(new Asistencia(reunion, miembro, abmListaAsistenciaControlador.getEstadosAsistencias().getLast()));
            }
        }

        // Cargamos la lista que contiene el filtro de asistencias
        abmListaAsistenciaControlador.getFiltroAsistencias().addAll(abmListaAsistenciaControlador.getAsistencias());

        // Establecemos los elementos en la tabla de asistencias
        abmListaAsistenciaControlador.getTblAsistencias().setItems(abmListaAsistenciaControlador.getFiltroAsistencias());
    }

    public void cargarMiembrosSeleccionados() {
        // Cargamos la lista de miembros seleccionados
        for (Asistencia asistencia : abmListaAsistenciaControlador.getAsistencias()) {
            abmListaAsistenciaControlador.getMiembrosSeleccionados().put(asistencia.getMiembro(), ListHelper.FLAG_MODIFICAR);
        }
    }

    // Metodos para filtrar los involucrados seleccionados

    public boolean mostrarMiembroSeleccionado(Asistencia asis) {
        // Verificamos si el CheckBox de mostrar seleccionados está presionado
        // En ese caso, mostrar solo aquellos que esten contenidos en la lista, y no los haya que eliminar
        return (abmListaAsistenciaControlador.getMiembrosSeleccionados().containsKey(asis.getMiembro())
                && abmListaAsistenciaControlador.getMiembrosSeleccionados().get(asis.getMiembro()) != ListHelper.FLAG_ELIMINAR);
    }

    public void filtrarListaAsistencias() {
        // Limpiamos la lista filtro de asistencias
        abmListaAsistenciaControlador.getFiltroAsistencias().clear();

        // Filtramos todos los miembros según el criterio especificado
        for (Asistencia asis : abmListaAsistenciaControlador.getAsistencias()) {
            if (mostrarMiembroSeleccionado(asis) || !abmListaAsistenciaControlador.getCheckMostrarSeleccionados().isSelected()) {
                if (aplicarFiltroListaAsistencias(asis)) {
                    abmListaAsistenciaControlador.getFiltroAsistencias().add(asis);
                }
            }
        }

        // Actualizamos la tabla
        abmListaAsistenciaControlador.getTblAsistencias().refresh();
    }

    public boolean aplicarFiltroListaAsistencias(Asistencia asis) {
        // Obtenemos los filtros
        String nombre = abmListaAsistenciaControlador.getTxtNombre().getText();
        Cargo cargo = abmListaAsistenciaControlador.getCmbCargo().getValue();
        EstadoMiembro estadoMiembro = abmListaAsistenciaControlador.getCmbEstadoMiembro().getValue();
        EstadoAsistencia estadoAsistencia = abmListaAsistenciaControlador.getCmbEstadoAsistencia().getValue();

        // Filtramos basándonos en el filtro obtenido
        return (nombre == null || String.valueOf(asis.getMiembro().getDni()).toLowerCase().contains(nombre.toLowerCase())
                || asis.getMiembro().toString().toLowerCase().contains(nombre.toLowerCase()))
                && (cargo == null || cargo.equals(asis.getMiembro().getCargo()))
                && (estadoMiembro == null || estadoMiembro.equals(asis.getMiembro().getEstadoMiembro()))
                && (estadoAsistencia == null || estadoAsistencia.equals(asis.getEstadoAsistencia()));
    }

    public void limpiarFiltrosListaAsistencias() {
        // Guardamos los eventos de los filtros
        EventHandler<ActionEvent> handlerCargo = abmListaAsistenciaControlador.getCmbCargo().getOnAction();
        EventHandler<ActionEvent> handlerEstadoMiembro = abmListaAsistenciaControlador.getCmbEstadoMiembro().getOnAction();
        EventHandler<ActionEvent> handlerEstadoAsistencia = abmListaAsistenciaControlador.getCmbEstadoAsistencia().getOnAction();

        // Desactivamos los eventos asociados a los filtros
        abmListaAsistenciaControlador.getCmbCargo().setOnAction(null);
        abmListaAsistenciaControlador.getCmbEstadoMiembro().setOnAction(null);
        abmListaAsistenciaControlador.getCmbEstadoAsistencia().setOnAction(null);

        // Limpiamos los filtros
        abmListaAsistenciaControlador.getTxtNombre().clear();
        abmListaAsistenciaControlador.getCmbCargo().setValue(null);
        abmListaAsistenciaControlador.getCmbEstadoMiembro().setValue(null);
        abmListaAsistenciaControlador.getCmbEstadoAsistencia().setValue(null);

        // Filtramos asistencias
        filtrarListaAsistencias();

        // Restauramos los eventos asociados a los filtros
        abmListaAsistenciaControlador.getCmbCargo().setOnAction(handlerCargo);
        abmListaAsistenciaControlador.getCmbEstadoMiembro().setOnAction(handlerEstadoMiembro);
        abmListaAsistenciaControlador.getCmbEstadoAsistencia().setOnAction(handlerEstadoAsistencia);
    }

    // Metodos de los CheckBox del formulario con lista

    public void seleccionarTodosLosMiembros() {
        // Guardamos el valor del CheckBox
        boolean valorActual = abmListaAsistenciaControlador.getCheckSeleccionarTodo().isSelected();
        // Creamos una copia de la lista de asistencias (para evitar bugs)
        List<Asistencia> copiaAsistencias = new ArrayList<>(abmListaAsistenciaControlador.getFiltroAsistencias());

        // Iteramos sobre la copia de la lista
        for (Asistencia asistencia : copiaAsistencias) {
            ListHelper.seleccionarItem(asistencia.getMiembro(), abmListaAsistenciaControlador.getMiembrosSeleccionados(), valorActual);
        }

        // Actualizamos la tabla
        abmListaAsistenciaControlador.getTblAsistencias().refresh();
    }

    public void marcarPresentePorDefecto() {
        // Iteramos la lista de asistencias para buscar aquellas que no esten seleccionadas y cambiarles el estado de asistencia por defecto
        for (Asistencia asistencia : abmListaAsistenciaControlador.getAsistencias()) {
            if (!mostrarMiembroSeleccionado(asistencia)) {
                if (abmListaAsistenciaControlador.getCheckMarcarPresentePorDefecto().isSelected()) {
                    asistencia.setEstadoAsistencia(abmListaAsistenciaControlador.getEstadosAsistencias().getFirst());
                } else {
                    asistencia.setEstadoAsistencia(abmListaAsistenciaControlador.getEstadosAsistencias().getLast());
                }
            }
        }

        // Actualizamos la tabla de asistencias
        abmListaAsistenciaControlador.getTblAsistencias().refresh();
    }
}