package com.java.consejofacil.controller.ABMAsistencia;

import com.java.consejofacil.config.StageManager;
import com.java.consejofacil.controller.ABMAccion.FormularioAccionController;
import com.java.consejofacil.helpers.Helpers;
import com.java.consejofacil.model.*;
import com.java.consejofacil.service.Asistencia.AsistenciaServiceImpl;
import com.java.consejofacil.service.Cargo.CargoServiceImpl;
import com.java.consejofacil.service.EstadoAsistencia.EstadoAsistenciaService;
import com.java.consejofacil.service.EstadoAsistencia.EstadoAsistenciaServiceImpl;
import com.java.consejofacil.service.EstadoMiembro.EstadoMiembroServiceImpl;
import com.java.consejofacil.service.Expediente.ExpedienteServiceImpl;
import com.java.consejofacil.service.Involucrado.InvolucradoServiceImpl;
import com.java.consejofacil.service.Miembro.MiembroServiceImpl;
import com.java.consejofacil.service.Reunion.ReunionServiceImpl;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class AsistenciaManager {

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

    // Stage Manager
    @Autowired
    @Lazy
    private StageManager stageManager;

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
        helpers.configurarComboEditable(listaAsistenciasControlador.getCmbReunion());

        // Cargamos lista y combo de miembros
        listaAsistenciasControlador.getMiembros().clear();
        listaAsistenciasControlador.getMiembros().addAll(miembroService.encontrarMiembrosActivos());
        listaAsistenciasControlador.getCmbMiembro().setItems(listaAsistenciasControlador.getMiembros());
        helpers.configurarComboEditable(listaAsistenciasControlador.getCmbMiembro());
    }

    public void inicializarCombosFormulario(BaseFormularioAsistencia controlador) {
        // Cargamos lista y combo de reuniones
        controlador.getReuniones().clear();
        controlador.getReuniones().addAll(reunionService.findAll());
        controlador.getCmbReunion().setItems(controlador.getReuniones());
        helpers.configurarComboEditable(controlador.getCmbReunion());

        if (controlador instanceof FormularioAsistenciaController) {
            // Cargamos lista y combo de miembros
            controlador.getMiembros().clear();
            controlador.getMiembros().addAll(miembroService.encontrarMiembrosActivos());
            controlador.getCmbMiembro().setItems(controlador.getMiembros());
            helpers.configurarComboEditable(controlador.getCmbMiembro());
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

    public Asistencia obtenerDatosFormulario() {
        // Obtenemos la información de los campos
        Reunion reunion = abmAsistenciaControlador.getCmbReunion().getValue();
        Miembro miembro = abmAsistenciaControlador.getCmbMiembro().getValue();
        EstadoAsistencia estadoAsistencia = abmAsistenciaControlador.getCmbEstado().getValue();

        // Creamos una nueva asistencia auxiliar
        return new Asistencia(reunion, miembro, estadoAsistencia);
    }

    public void guardarAsistencia() {
        if (validarCamposFormulario(abmAsistenciaControlador)) {
            // Creamos una nueva asistencia auxiliar
            Asistencia aux = obtenerDatosFormulario();

            // Si la asistencia es diferente de nulo, queremos modificar
            if (abmAsistenciaControlador.getAsistencia() != null) {

                // Establecemos el ID a la asistencia auxiliar
                aux.setId(abmAsistenciaControlador.getAsistencia().getId());

                // Modificamos la asistencia
                if (modificarAsistencia(aux)) {
                    mostrarMensaje(Alert.AlertType.INFORMATION, "Info", "Se ha modificado la asistencia correctamente!");
                } else {
                    mostrarMensaje(Alert.AlertType.ERROR, "Error", "No se pudo modificar la asistencia correctamente!");
                }

            } else {
                // Si la asistencia es nulo, queremos agregar
                if (agregarAsistencia(aux)) {
                    mostrarMensaje(Alert.AlertType.INFORMATION, "Info", "Se ha agregado la asistencia correctamente!");
                } else {
                    mostrarMensaje(Alert.AlertType.ERROR, "Error", "No se pudo agregar la asistencia correctamente!");
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

    private boolean validarCamposFormulario(BaseFormularioInvolucrado controlador) {
        ArrayList<String> errores = new ArrayList<>();
        Involucrado aux = new Involucrado();

        // Verificamos que haya seleccionado un expediente
        if (controlador.getCmbExpediente().getValue() == null) {
            errores.add("Por favor, seleccione un expediente.");
        } else {
            Expediente expediente = expedienteService.findById(controlador.getCmbExpediente().getValue().getId());
            if (expediente == null) {
                errores.add("El expediente seleccionado no se encuentra en la base de datos.");
            } else if (controlador instanceof FormularioInvolucradoController) {
                // Establecemos expediente al auxiliar
                aux.setExpediente(expediente);
            }
        }

        // Validaciones específicas del formulario original

        if (controlador instanceof FormularioInvolucradoController) {

            // Verificamos que haya seleccionado un involucrado y que no esté inactivo en el sistema
            if (controlador.getCmbInvolucrado().getValue() == null) {
                errores.add("Por favor, seleccione un involucrado.");
            } else {
                Miembro involucrado = miembroService.findById(controlador.getCmbInvolucrado().getValue().getDni());
                if (involucrado == null) {
                    errores.add("El involucrado seleccionado no se encuentra en la base de datos.");
                } else {
                    if (!involucrado.getEstadoMiembro().getEstadoMiembro().equals("Activo")) {
                        errores.add("Debe seleccionar un involucrado activo en el sistema.");
                    }
                    // Establecemos involucrado al auxiliar
                    aux.setMiembro(involucrado);
                }
            }

            // Verificamos que los detalles no supere los 500 caracteres
            if (controlador.getTxtDetallesInvolucrado().getLength() > 500) {
                errores.add("Los detalles del involucrado no pueden tener más de 500 caracteres.");
            }

            // Verificamos que el involucrado no esté repetido en el sistema
            if (aux.getExpediente() != null && aux.getMiembro() != null) {
                boolean esNuevoInvolucrado = controlador.getInvolucrado() == null;
                boolean esDiferente = esNuevoInvolucrado ||
                        !controlador.getInvolucrado().getExpediente().equals(aux.getExpediente()) ||
                        !controlador.getInvolucrado().getMiembro().equals(aux.getMiembro());
                if (esNuevoInvolucrado || esDiferente) {
                    if (involucradoService.existeDuplicado(aux.getExpediente().getId(), aux.getMiembro().getDni()) >= 1) {
                        errores.add("El involucrado ya está asociado al expediente seleccionado.");
                    }
                }
            }
        }

        // Validaciones específicas del formulario por lista

        if (controlador instanceof FormularioListaInvolucradoController) {
            // Verificamos que haya agregado al menos un involucrado a la lista
            if (abmListaInvolucradoControlador.getMiembrosSeleccionados().isEmpty()) {
                errores.add("Por favor, seleccione un involucrado.");
            }
        }

        // Verificamos si hay errores
        if (!errores.isEmpty()) {
            mostrarCadenaMensajes(errores, "Se ha producido uno o varios errores:", Alert.AlertType.ERROR, "Error");
            return false;
        }

        return true;
    }

    private boolean validarInvolucrado(Involucrado inv) {
        // Creamos un array de errores
        ArrayList<String> errores = new ArrayList<>();

        // Verificamos que haya seleccionado un involucrado y que no esté inactivo en el sistema
        Miembro involucrado = miembroService.findById(inv.getMiembro().getDni());
        if (involucrado == null) {
            errores.add("El involucrado " + inv.getMiembro().toString() + " no se encuentra en la base de datos.");
        } else {
            if (!involucrado.getEstadoMiembro().getEstadoMiembro().equals("Activo")) {
                errores.add("El involucrado " + inv.getMiembro().toString() + " se encuentra inactivo.");
                errores.add("Debe seleccionar un involucrado activo en el sistema.");
            }
        }

        // Verificamos que los detalles no supere los 500 caracteres
        if (inv.getDetallesInvolucrado().length() > 500) {
            errores.add("Los detalles del involucrado no pueden tener más de 500 caracteres.");
        }

        // En caso de que haya errores, agregarlo a la lista de errores para mostrarlos en pantalla
        if (!errores.isEmpty()) {
            abmListaInvolucradoControlador.getCadenaErrores().addAll(errores);
            return false;
        }

        return true;
    }

    // Metodos para limpiar campos

    public void limpiarFiltros() {
        // Guardamos los eventos de los filtros
        EventHandler<ActionEvent> handlerExpediente = listaInvolucradosControlador.getCmbExpediente().getOnAction();
        EventHandler<ActionEvent> handlerInvolucrado = listaInvolucradosControlador.getCmbInvolucrado().getOnAction();

        // Desactivamos los eventos asociados a los filtros
        listaInvolucradosControlador.getCmbExpediente().setOnAction(null);
        listaInvolucradosControlador.getCmbInvolucrado().setOnAction(null);

        // Limpiamos los filtros
        listaInvolucradosControlador.getCmbExpediente().setValue(null);
        listaInvolucradosControlador.getCmbInvolucrado().setValue(null);
        listaInvolucradosControlador.getTxtDetallesInvolucrado().clear();

        // Filtramos los involucrados
        filtrarInvolucrados();

        // Restauramos los eventos asociados a los filtros
        listaInvolucradosControlador.getCmbExpediente().setOnAction(handlerExpediente);
        listaInvolucradosControlador.getCmbInvolucrado().setOnAction(handlerInvolucrado);
    }

    public void limpiarFormulario(BaseFormularioInvolucrado controlador) {
        // Limpiamos el formulario
        controlador.getCmbExpediente().setValue(null);

        if (controlador instanceof FormularioInvolucradoController) {
            controlador.getCmbInvolucrado().setValue(null);
            controlador.getTxtDetallesInvolucrado().clear();
        }

        if (controlador instanceof FormularioListaInvolucradoController) {
            // Limpiamos la tabla de involucrados
            abmListaInvolucradoControlador.getTblInvolucrados().getItems().clear();
        }
    }

    // Metodos para interactuar con los selectores

    public void seleccionarExpediente(ComboBox<Expediente> combo) throws Exception {
        helpers.seleccionarExpediente(combo);
    }

    public void seleccionarMiembro(ComboBox<Miembro> combo) throws Exception {
        helpers.seleccionarMiembro(combo);
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

    // Metodos adicionales para el formulario con lista

    public void inicializarListaInvolucrados() {
        // Asociamos las columnas de la tabla
        abmListaInvolucradoControlador.getColNombre().setCellValueFactory(new PropertyValueFactory<>("miembro"));
        abmListaInvolucradoControlador.getColDetallesInvolucrado().setCellValueFactory(new PropertyValueFactory<>("detallesInvolucrado"));

        // Configuramos las columnas en relacion con el miembro
        helpers.configurarDniMiembro(abmListaInvolucradoControlador.getColDni());
        helpers.configurarCargoMiembro(abmListaInvolucradoControlador.getColCargo());
        helpers.configurarEstadoMiembro(abmListaInvolucradoControlador.getColEstado());

        // Configuramos el TextField para que pueda modificar en tiempo de ejecucion los detalles del involucrado
        helpers.configurarTextFieldTableCell(abmListaInvolucradoControlador.getColDetallesInvolucrado());

        // Configuramos el CheckBox para que pueda seleccionar nuevos involucrados a la lista
        helpers.configurarCheckTableCell(abmListaInvolucradoControlador.getColSeleccionar(),
                abmListaInvolucradoControlador.getMiembrosSeleccionados());
    }

    public void inicializarFiltrosListaMiembros() {
        // Cargamos lista y combo de cargos
        abmListaInvolucradoControlador.getCargos().clear();
        abmListaInvolucradoControlador.getCargos().addAll(cargoService.findAll());
        abmListaInvolucradoControlador.getCmbCargo().setItems(abmListaInvolucradoControlador.getCargos());

        // Cargamos lista y combo de estados
        abmListaInvolucradoControlador.getEstadosMiembros().clear();
        abmListaInvolucradoControlador.getEstadosMiembros().addAll(estadoMiembroService.findAll());
        abmListaInvolucradoControlador.getCmbEstado().setItems(abmListaInvolucradoControlador.getEstadosMiembros());
    }

    public void inicializarCadenasTexto() {
        // Limpiamos cadenas de errores e infos
        abmListaInvolucradoControlador.getCadenaErrores().clear();
        abmListaInvolucradoControlador.getCadenaInfos().clear();
    }

    // Metodo para guardar lista de involucrados

    public void guardarListaInvolucrados() {
        // Validamos los campos y que la lista de involucrados seleccionados no este vacia
        if (validarCamposFormulario(abmListaInvolucradoControlador)) {

            // Iteramos todos los miembros seleccionados
            for (Map.Entry<Miembro, Integer> entry : abmListaInvolucradoControlador.getMiembrosSeleccionados().entrySet()) {

                // Obtenemos el involucrado seleccionado, junto con su flag
                Involucrado involucrado = buscarInvolucradoPorMiembro(entry.getKey());
                int flag = entry.getValue();

                // Verificamos si el involucrado es diferente de nulo
                if (involucrado != null) {

                    // Si esta seleccionado, queremos agregar o modificar
                    if (flag != helpers.FLAG_ELIMINAR) {
                        // Validamos la información del involucrado
                        if (validarInvolucrado(involucrado)) {

                            // Verificamos que los detalles no esten vacios
                            if (involucrado.getDetallesInvolucrado().isEmpty()) {
                                involucrado.setDetallesInvolucrado("No hay detalles para mostrar.");
                            }

                            // Si el ID es diferente de 0, quiere decir que ya existe
                            if (involucrado.getId() != 0 || flag == helpers.FLAG_MODIFICAR) {
                                // Modificamos el involucrado
                                if (modificarInvolucrado(involucrado)) {
                                    abmListaInvolucradoControlador.getCadenaInfos().add("Se ha modificado el involucrado " + involucrado.getMiembro().toString() + " correctamente!");
                                } else {
                                    abmListaInvolucradoControlador.getCadenaErrores().add("No se pudo modificar el involucrado " + involucrado.getMiembro().toString() + " correctamente!");
                                }
                            } else {
                                // Agregamos el involucrado
                                if (agregarInvolucrado(involucrado)) {
                                    abmListaInvolucradoControlador.getCadenaInfos().add("Se ha agregado el involucrado " + involucrado.getMiembro().toString() + " correctamente!");
                                } else {
                                    abmListaInvolucradoControlador.getCadenaErrores().add("No se pudo agregar el involucrado " + involucrado.getMiembro().toString() + " correctamente!");
                                }
                            }
                        }
                    } else {
                        // Si no está dentro de los involucrados seleccionados, queremos eliminar
                        // Eliminamos el involucrado
                        if (eliminarInvolucrado(involucrado, true)) {
                            abmListaInvolucradoControlador.getCadenaInfos().add("Se ha eliminado el involucrado " + involucrado.getMiembro().toString() + " correctamente!");
                        } else {
                            abmListaInvolucradoControlador.getCadenaErrores().add("No se pudo eliminar el involucrado " + involucrado.getMiembro().toString() + " correctamente!");

                        }
                    }
                }
            }

            // Mostramos cadenas de errores e infos en pantalla
            mostrarCadenaMensajes(abmListaInvolucradoControlador.getCadenaErrores(), "Se ha producido uno o varios errores:", Alert.AlertType.ERROR, "Error");
            mostrarCadenaMensajes(abmListaInvolucradoControlador.getCadenaInfos(), "Se ha producido una o varias modificaciones:", Alert.AlertType.INFORMATION, "Info");

            // Actualizamos la tabla de involucrados
            listaInvolucradosControlador.getTblInvolucrados().refresh();

            // Salimos del modal
            stageManager.closeModal(FXMLView.FormularioListaInvolucrado.getKey());
        }
    }

    // Metodos para buscar y seleccionar nuevos expedientes

    public void buscarInvolucradosPorExpediente() {
        // Cargamos los involucrados asociados al expediente seleccionado
        if (abmListaInvolucradoControlador.getCmbExpediente().getValue() != null) {
            cargarListaInvolucrados(abmListaInvolucradoControlador.getCmbExpediente().getValue());
        }

        // Actualizamos la tabla
        abmListaInvolucradoControlador.getTblInvolucrados().refresh();
    }

    public Involucrado buscarInvolucradoPorMiembro(Miembro miembro) {
        // Filtramos la lista de involucrados por el miembro proporcionado
        Optional<Involucrado> optionalInvolucrado = abmListaInvolucradoControlador.getInvolucrados().stream()
                .filter(inv -> inv.getMiembro().equals(miembro))
                .findFirst();

        // Devolvemos el involucrado encontrado
        return optionalInvolucrado.orElse(null);
    }

    // Metodos para cargar la lista de involucrados y miembros seleccionados

    public void cargarListaInvolucrados(Expediente expediente) {
        // Limpiamos las listas
        abmListaInvolucradoControlador.getInvolucrados().clear();
        abmListaInvolucradoControlador.getFiltroInvolucrados().clear();
        abmListaInvolucradoControlador.getMiembrosSeleccionados().clear();

        // Cargamos la lista de involucrados seleccionados por expedientes
        abmListaInvolucradoControlador.getInvolucrados().addAll(involucradoService.encontrarInvolucradosPorExpediente(expediente));

        // Cargamos la lista de miembros seleccionados a partir de los involucrados
        cargarMiembrosSeleccionados();

        // Obtenemos todos los miembros
        List<Miembro> listaMiembros = miembroService.findAll();

        // Iteramos todos los miembros para mostrar aquellos que no esten seleccionados en pantalla
        for (Miembro miembro : listaMiembros) {
            // Verificamos si el miembro seleccionado ya se encuentra en la lista
            if (buscarInvolucradoPorMiembro(miembro) == null) {
                // Si no se encuentra presente, lo agregamos
                abmListaInvolucradoControlador.getInvolucrados().add(new Involucrado(expediente, miembro, "--"));
            }
        }

        // Cargamos la lista que contiene el filtro de involucrados
        abmListaInvolucradoControlador.getFiltroInvolucrados().addAll(abmListaInvolucradoControlador.getInvolucrados());

        // Establecemos los elementos en la tabla de involucrados
        abmListaInvolucradoControlador.getTblInvolucrados().setItems(abmListaInvolucradoControlador.getFiltroInvolucrados());
    }

    public void cargarMiembrosSeleccionados() {
        // Cargamos la lista de miembros seleccionados
        for (Involucrado involucrado : abmListaInvolucradoControlador.getInvolucrados()) {
            abmListaInvolucradoControlador.getMiembrosSeleccionados().put(involucrado.getMiembro(), helpers.FLAG_MODIFICAR);
        }
    }

    // Metodos para filtrar los involucrados seleccionados

    public boolean mostrarMiembrosSeleccionados(Involucrado inv) {
        // Verificamos si el CheckBox de mostrar seleccionados está presionado
        // En ese caso, mostrar solo aquellos que esten contenidos en la lista, y no los haya que eliminar
        return (abmListaInvolucradoControlador.getMiembrosSeleccionados().containsKey(inv.getMiembro())
                && abmListaInvolucradoControlador.getMiembrosSeleccionados().get(inv.getMiembro()) != helpers.FLAG_ELIMINAR)
                || !abmListaInvolucradoControlador.getCheckMostrarSeleccionados().isSelected();
    }

    public void filtrarListaMiembros() {
        // Limpiamos la lista filtro de miembros
        abmListaInvolucradoControlador.getFiltroInvolucrados().clear();

        // Filtramos todos los miembros según el criterio especificado
        for (Involucrado inv : abmListaInvolucradoControlador.getInvolucrados()) {
            if (mostrarMiembrosSeleccionados(inv)) {
                if (aplicarFiltroListaMiembros(inv)) {
                    abmListaInvolucradoControlador.getFiltroInvolucrados().add(inv);
                }
            }
        }

        // Actualizamos la tabla
        abmListaInvolucradoControlador.getTblInvolucrados().refresh();
    }

    public boolean aplicarFiltroListaMiembros(Involucrado inv) {
        // Obtenemos los filtros
        String nombre = abmListaInvolucradoControlador.getTxtNombre().getText();
        Cargo cargo = abmListaInvolucradoControlador.getCmbCargo().getValue();
        EstadoMiembro estado = abmListaInvolucradoControlador.getCmbEstado().getValue();
        String detallesInvolucrado = abmListaInvolucradoControlador.getTxtDetalles().getText();

        // Filtramos basándonos en el filtro obtenido
        return (nombre == null || String.valueOf(inv.getMiembro().getDni()).toLowerCase().contains(nombre.toLowerCase())
                || inv.getMiembro().toString().toLowerCase().contains(nombre.toLowerCase()))
                && (cargo == null || cargo.equals(inv.getMiembro().getCargo()))
                && (estado == null || estado.equals(inv.getMiembro().getEstadoMiembro()))
                && (detallesInvolucrado == null || inv.getDetallesInvolucrado().toLowerCase().contains(detallesInvolucrado.toLowerCase()));
    }

    public void limpiarFiltrosListaMiembros() {
        // Guardamos los eventos de los filtros
        EventHandler<ActionEvent> handlerCargo = abmListaInvolucradoControlador.getCmbCargo().getOnAction();
        EventHandler<ActionEvent> handlerEstado = abmListaInvolucradoControlador.getCmbEstado().getOnAction();

        // Desactivamos los eventos asociados a los filtros
        abmListaInvolucradoControlador.getCmbCargo().setOnAction(null);
        abmListaInvolucradoControlador.getCmbEstado().setOnAction(null);

        // Limpiamos los filtros
        abmListaInvolucradoControlador.getTxtNombre().clear();
        abmListaInvolucradoControlador.getTxtDetalles().clear();
        abmListaInvolucradoControlador.getCmbCargo().setValue(null);
        abmListaInvolucradoControlador.getCmbEstado().setValue(null);

        // Filtramos miembros
        filtrarListaMiembros();

        // Restauramos los eventos asociados a los filtros
        abmListaInvolucradoControlador.getCmbCargo().setOnAction(handlerCargo);
        abmListaInvolucradoControlador.getCmbEstado().setOnAction(handlerEstado);
    }

    // Metodos de los CheckBox del formulario con lista

    public void seleccionarTodosLosMiembros() {
        // Guardamos el valor del CheckBox
        boolean valorActual = abmListaInvolucradoControlador.getCheckSeleccionarTodo().isSelected();
        // Creamos una copia de la lista de miembros (para evitar bugs)
        List<Involucrado> copiaInvolucrados = new ArrayList<>(abmListaInvolucradoControlador.getFiltroInvolucrados());

        // Iteramos sobre la copia de la lista
        for (Involucrado involucrado : copiaInvolucrados) {
            helpers.seleccionarItemCheckTableCell(involucrado.getMiembro(), abmListaInvolucradoControlador.getMiembrosSeleccionados(), valorActual);
        }

        // Actualizamos la tabla
        abmListaInvolucradoControlador.getTblInvolucrados().refresh();
    }
}