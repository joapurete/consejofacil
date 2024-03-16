package com.java.consejofacil.controller.ABMInvolucrado;

import com.java.consejofacil.config.StageManager;
import com.java.consejofacil.controller.ABMHistorialCambio.HistorialCambioManager;
import com.java.consejofacil.security.SessionManager;
import com.java.consejofacil.helper.Alertas.AlertHelper;
import com.java.consejofacil.helper.Componentes.ComponentHelper;
import com.java.consejofacil.controller.SelectorController;
import com.java.consejofacil.helper.Componentes.TableCellFactoryHelper;
import com.java.consejofacil.helper.Utilidades.ListHelper;
import com.java.consejofacil.model.*;
import com.java.consejofacil.service.Cargo.CargoServiceImpl;
import com.java.consejofacil.service.EstadoMiembro.EstadoMiembroServiceImpl;
import com.java.consejofacil.service.Expediente.ExpedienteServiceImpl;
import com.java.consejofacil.service.Involucrado.InvolucradoServiceImpl;
import com.java.consejofacil.service.Miembro.MiembroServiceImpl;
import com.java.consejofacil.view.FXMLView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
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
public class InvolucradoManager {

    // Servicios necesarios
    @Autowired
    @Lazy
    private ExpedienteServiceImpl expedienteService;
    @Autowired
    @Lazy
    private MiembroServiceImpl miembroService;
    @Autowired
    @Lazy
    private InvolucradoServiceImpl involucradoService;
    @Autowired
    @Lazy
    private CargoServiceImpl cargoService;
    @Autowired
    @Lazy
    private EstadoMiembroServiceImpl estadoMiembroService;

    // Controladores de los fxml
    @Autowired
    @Lazy
    private ListaInvolucradosController listaInvolucradosControlador;
    @Autowired
    @Lazy
    private FormularioInvolucradoController abmInvolucradoControlador;
    @Autowired
    @Lazy
    private FormularioListaInvolucradoController abmListaInvolucradoControlador;
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

    // Componente para registrar los cambios realizados
    @Autowired
    @Lazy
    private HistorialCambioManager historialCambioManager;

    // Metodo para validar el acceso del miembro

    public void validarAccesoMiembro() {
        sessionManager.validarAccesoMiembro();
    }

    // Metodos de inicialización de componentes

    public void inicializarTablaInvolucrados() {
        try {
            // Asociamos columnas de la tabla
            listaInvolucradosControlador.getColExpediente().setCellValueFactory(new PropertyValueFactory<>("expediente"));
            listaInvolucradosControlador.getColInvolucrado().setCellValueFactory(new PropertyValueFactory<>("miembro"));
            listaInvolucradosControlador.getColDetallesInvolucrado().setCellValueFactory(new PropertyValueFactory<>("detallesInvolucrado"));

            // Cargamos listas
            listaInvolucradosControlador.getInvolucrados().clear();
            listaInvolucradosControlador.getFiltroInvolucrados().clear();
            listaInvolucradosControlador.getInvolucrados().addAll(involucradoService.findAll());
            listaInvolucradosControlador.getFiltroInvolucrados().addAll(listaInvolucradosControlador.getInvolucrados());

            // Cargamos la tabla
            listaInvolucradosControlador.getTblInvolucrados().setItems(listaInvolucradosControlador.getFiltroInvolucrados());
        } catch (Exception e) {
            listaInvolucradosControlador.getLog().error(e.getMessage());
        }
    }

    public void inicializarFiltros() {
        // Cargamos lista y combo de expedientes
        listaInvolucradosControlador.getExpedientes().clear();
        listaInvolucradosControlador.getExpedientes().addAll(expedienteService.findAll());
        listaInvolucradosControlador.getCmbExpediente().setItems(listaInvolucradosControlador.getExpedientes());
        ComponentHelper.configurarComboEditable(listaInvolucradosControlador.getCmbExpediente());

        // Cargamos lista y combo de miembros
        listaInvolucradosControlador.getMiembros().clear();
        listaInvolucradosControlador.getMiembros().addAll(miembroService.encontrarMiembrosActivos());
        listaInvolucradosControlador.getCmbInvolucrado().setItems(listaInvolucradosControlador.getMiembros());
        ComponentHelper.configurarComboEditable(listaInvolucradosControlador.getCmbInvolucrado());
    }

    public void inicializarCombosFormulario(BaseFormularioInvolucrado controlador) {
        // Cargamos lista y combo de expedientes
        controlador.getExpedientes().clear();
        controlador.getExpedientes().addAll(expedienteService.findAll());
        controlador.getCmbExpediente().setItems(controlador.getExpedientes());
        ComponentHelper.configurarComboEditable(controlador.getCmbExpediente());

        if (controlador instanceof FormularioInvolucradoController) {
            // Cargamos lista y combo de miembros
            controlador.getMiembros().clear();
            controlador.getMiembros().addAll(miembroService.encontrarMiembrosActivos());
            controlador.getCmbInvolucrado().setItems(controlador.getMiembros());
            ComponentHelper.configurarComboEditable(controlador.getCmbInvolucrado());
        }
    }

    // Metodo para cargar el ABM

    public void cargarFormulario(FXMLView fxml, Involucrado involucrado) throws Exception {
        try {
            // Obtenemos la selección de los controles
            boolean listaSeleccionada = listaInvolucradosControlador.getCheckLista().isSelected();
            boolean autocompletadoSeleccionado = listaInvolucradosControlador.getCheckAutocompletado().isSelected();
            Expediente expedienteSeleccionado = listaInvolucradosControlador.getCmbExpediente().getValue();
            Miembro involucradoSeleccionado = listaInvolucradosControlador.getCmbInvolucrado().getValue();
            String detallesInvolucrado = listaInvolucradosControlador.getTxtDetallesInvolucrado().getText().trim();

            // Cargamos FXML de ABMInvolucrado
            Parent rootNode = stageManager.loadView(fxml.getFxmlFile());

            // Obtenemos el controlador a utilizar
            BaseFormularioInvolucrado controlador = listaSeleccionada ? abmListaInvolucradoControlador : abmInvolucradoControlador;

            if (involucrado != null) {
                // Establecemos involucrado seleccionado
                controlador.establecerInvolucrado(involucrado, controlador);
            } else if (autocompletadoSeleccionado) {
                // Autocompletamos el formulario si el CheckBox está seleccionado
                autocompletarFormulario(new Involucrado(expedienteSeleccionado, involucradoSeleccionado, detallesInvolucrado), controlador);
            }

            // Abrimos modal
            stageManager.openModal(rootNode, fxml);

        } catch (Exception e) {
            listaInvolucradosControlador.getLog().error(e.getMessage());
        }
    }

    public void procesarInvolucrado(Involucrado involucrado, boolean nuevoValor) {
        if (involucrado != null) {
            // Si el involucrado no está dentro de la lista, lo agregamos
            if (nuevoValor) {
                // Lo agregamos a la lista
                listaInvolucradosControlador.getInvolucrados().add(involucrado);

                // Aplicamos el filtro si es necesario
                if (aplicarFiltro(involucrado)) {
                    listaInvolucradosControlador.getFiltroInvolucrados().add(involucrado);
                }
            } else {
                // Actualizamos el involucrado
                actualizarInvolucrado(involucrado);

                // Quitamos el filtro si es necesario
                if (quitarFiltro(involucrado)) {
                    listaInvolucradosControlador.getFiltroInvolucrados().remove(involucrado);
                }
            }
        }
    }

    public void actualizarInvolucrado(Involucrado involucrado) {
        listaInvolucradosControlador.getInvolucrados().stream()
                // Filtramos los elementos que coinciden con el ID del involucrado
                .filter(inv -> inv.getId() == involucrado.getId())
                // Actualizamos los atributos del elemento
                .findFirst().ifPresent(inv -> {
                    inv.setExpediente(involucrado.getExpediente());
                    inv.setMiembro(involucrado.getMiembro());
                    inv.setDetallesInvolucrado(involucrado.getDetallesInvolucrado());
                });
    }

    // Metodos para agregar, modificar y eliminar involucrados

    public Involucrado obtenerDatosFormulario(BaseFormularioInvolucrado controlador) {
        // Obtenemos la información de los campos
        Expediente expediente = controlador.getCmbExpediente().getValue();

        // Creamos un involucrado auxiliar
        Involucrado involucrado = new Involucrado(expediente, null, null);

        // Obtenemos los datos del formulario original
        if (controlador instanceof FormularioInvolucradoController) {
            involucrado.setMiembro(controlador.getCmbInvolucrado().getValue());
            involucrado.setDetallesInvolucrado(!controlador.getTxtDetallesInvolucrado().getText().trim().isEmpty() ?
                    controlador.getTxtDetallesInvolucrado().getText().trim() : "No hay detalles para mostrar.");
        }

        // Develmos el involucrado creado
        return involucrado;
    }

    public void guardarInvolucrado() {
        if (validarCamposFormulario(abmInvolucradoControlador)) {
            // Creamos un nuevo involucrado auxiliar
            Involucrado aux = obtenerDatosFormulario(abmInvolucradoControlador);

            // Si el involucrado es diferente de nulo, queremos modificar
            if (abmInvolucradoControlador.getInvolucrado() != null) {

                // Establecemos el ID al involucrado auxiliar
                aux.setId(abmInvolucradoControlador.getInvolucrado().getId());

                // Modificamos el involucrado
                if (modificarInvolucrado(aux)) {
                    mostrarMensaje(false, "Info", "Se ha modificado el involucrado correctamente!");
                } else {
                    mostrarMensaje(true, "Error", "No se pudo modificar el involucrado correctamente!");
                }

            } else {
                // Si el involucrado es nulo, queremos agregar
                if (agregarInvolucrado(aux)) {
                    mostrarMensaje(false, "Info", "Se ha agregado el involucrado correctamente!");
                } else {
                    mostrarMensaje(true, "Error", "No se pudo agregar el involucrado correctamente!");
                }
            }

            // Actuailizamos la tabla de revisiones
            listaInvolucradosControlador.getTblInvolucrados().refresh();

            // Salimos del modal
            stageManager.closeModal(FXMLView.FormularioInvolucrado.getKey());
        }
    }

    public boolean agregarInvolucrado(Involucrado involucrado) {
        try {
            // Guardamos el involucrado auxiliar
            involucrado = involucradoService.save(involucrado);

            // Agregamos involucrado a la tabla
            procesarInvolucrado(involucrado, true);

            // Agregamos cambio a la base de datos
            historialCambioManager.registrarCambio(historialCambioManager.getTipoIns().getKey(),
                    obtenerDetallesCambioInvolucrado(involucrado, historialCambioManager.getTipoIns().getValue()));

            // Indicamos que la operacion fue exitosa
            return true;

        } catch (Exception e) {
            abmInvolucradoControlador.getLog().error(e.getMessage());
        }
        // Indicamos que hubo un error
        return false;
    }


    public boolean modificarInvolucrado(Involucrado involucrado) {
        try {
            // Modificamos el involucrado
            involucrado = involucradoService.update(involucrado);

            // Agregamos involucrado modificado a la tabla
            procesarInvolucrado(involucrado, false);

            // Agregamos cambio a la base de datos
            historialCambioManager.registrarCambio(historialCambioManager.getTipoMod().getKey(),
                    obtenerDetallesCambioInvolucrado(involucrado, historialCambioManager.getTipoMod().getValue()));

            // Indicamos que la operacion fue exitosa
            return true;

        } catch (Exception e) {
            abmInvolucradoControlador.getLog().error(e.getMessage());
        }
        // Indicamos que hubo un error
        return false;
    }

    public boolean eliminarInvolucrado(Involucrado involucrado, boolean auto) {
        // Mostramos alerta de confirmacion
        if (auto || mostrarConfirmacion("Info", "¿Está seguro que desea eliminar el involucrado?")) {
            try {
                // Eliminamos el involucrado
                involucradoService.delete(involucrado);

                // Actualizamos las listas
                listaInvolucradosControlador.getInvolucrados().remove(involucrado);
                listaInvolucradosControlador.getFiltroInvolucrados().remove(involucrado);

                // Agregamos cambio a la base de datos
                historialCambioManager.registrarCambio(historialCambioManager.getTipoEli().getKey(),
                        obtenerDetallesCambioInvolucrado(involucrado, historialCambioManager.getTipoEli().getValue()));

                // Indicamos que la operacion fue exitosa
                return true;

            } catch (Exception e) {
                listaInvolucradosControlador.getLog().error(e.getMessage());
            }
        }
        // Indicamos que hubo un error
        return false;
    }

    // Metodo para construir texto para los detalles del cambio realizado

    public String obtenerDetallesCambioInvolucrado(Involucrado involucrado, String tipoCambio){
        return "Se ha registrado la " + tipoCambio.toLowerCase() + " del involucrado N° " + involucrado.getId() + ". " +
                "Detalles del involucrado: [" + involucrado.getDetallesInvolucrado() + "]. " +
                "Relacionada con el " + involucrado.getExpediente().toString() + ", " +
                "y perteneciente al miembro del consejo " + involucrado.getMiembro().toString() + ".";
    }

    // Metodos para filtrar los involucrados

    public void filtrarInvolucrados() {
        // Limpiamos la lista filtro de involucrados
        listaInvolucradosControlador.getFiltroInvolucrados().clear();

        // Filtramos todos los involucrados
        for (Involucrado inv : listaInvolucradosControlador.getInvolucrados()) {
            if (aplicarFiltro(inv)) {
                listaInvolucradosControlador.getFiltroInvolucrados().add(inv);
            }
        }

        // Actualizamos la tabla
        listaInvolucradosControlador.getTblInvolucrados().refresh();
    }

    public boolean aplicarFiltro(Involucrado inv) {
        // Obtenemos todos los filtros
        Expediente expediente = listaInvolucradosControlador.getCmbExpediente().getValue();
        Miembro miembro = listaInvolucradosControlador.getCmbInvolucrado().getValue();
        String detallesInvolucrado = listaInvolucradosControlador.getTxtDetallesInvolucrado().getText();

        // Filtramos con base en los filtros obtenidos
        return (expediente == null || expediente.equals(inv.getExpediente()))
                && (miembro == null || miembro.equals(inv.getMiembro()))
                && (detallesInvolucrado == null || inv.getDetallesInvolucrado().toLowerCase().contains(detallesInvolucrado.toLowerCase()));
    }

    public boolean quitarFiltro(Involucrado inv) {
        // Obtenemos todos los filtros
        Expediente expediente = listaInvolucradosControlador.getCmbExpediente().getValue();
        Miembro miembro = listaInvolucradosControlador.getCmbInvolucrado().getValue();
        String detallesInvolucrado = listaInvolucradosControlador.getTxtDetallesInvolucrado().getText();

        // Quitamos del filtro con base en los filtros obtenidos
        return (expediente != null && !expediente.equals(inv.getExpediente()))
                || (miembro != null && !miembro.equals(inv.getMiembro()))
                || (detallesInvolucrado != null && !inv.getDetallesInvolucrado().toLowerCase().contains(detallesInvolucrado.toLowerCase()));
    }

    // Metodos de autocarga de los formularios

    public void autocompletarFormulario(Involucrado inv, BaseFormularioInvolucrado controlador) {
        Expediente expediente = inv.getExpediente();
        Miembro involucrado = inv.getMiembro();
        String detallesInvolucrado = inv.getDetallesInvolucrado();

        // Establecemos el expediente seleccionado
        if (expediente != null) {
            controlador.getCmbExpediente().setValue(expediente);
        }

        if (controlador instanceof FormularioInvolucradoController) {
            // Completamos el formulario a partir de la información proporcionada
            controlador.getTxtDetallesInvolucrado().setText(detallesInvolucrado);

            // Establecemos el miembro seleccionado
            if (involucrado != null) {
                controlador.getCmbInvolucrado().setValue(involucrado);
            }
        }

        if (controlador instanceof FormularioListaInvolucradoController) {
            // Agregamos los involucrados asociados al expediente
            cargarListaInvolucrados(expediente);
        }
    }

    // Metdodos para validar los campos de los formularios

    private boolean validarCamposFormulario(BaseFormularioInvolucrado controlador) {
        ArrayList<String> errores = new ArrayList<>();

        // Creamos un involucrado auxiliar y otro para obtener los datos del formulario
        Involucrado aux = new Involucrado();
        Involucrado involucrado = obtenerDatosFormulario(controlador);

        // Verificamos que haya seleccionado un expediente
        if (involucrado.getExpediente() == null) {
            errores.add("Por favor, seleccione un expediente.");
        } else {
            Expediente expediente = expedienteService.findById(involucrado.getExpediente().getId());
            if (expediente == null) {
                errores.add("El expediente seleccionado no se encuentra en la base de datos.");
            } else if (controlador instanceof FormularioInvolucradoController) {
                // Establecemos expediente al auxiliar
                aux.setExpediente(expediente);
            }
        }

        // Validaciones específicas del formulario original

        if (controlador instanceof FormularioInvolucradoController) {

            // Verificamos que haya seleccionado un miembro y que no esté inactivo en el sistema
            if (involucrado.getMiembro() == null) {
                errores.add("Por favor, seleccione un involucrado.");
            } else {
                Miembro miembro = miembroService.findById(involucrado.getMiembro().getDni());
                if (miembro == null) {
                    errores.add("El involucrado seleccionado no se encuentra en la base de datos.");
                } else {
                    if (!miembro.getEstadoMiembro().getEstadoMiembro().equals("Activo")) {
                        errores.add("Debe seleccionar un involucrado activo en el sistema.");
                    }
                    // Establecemos miembro al auxiliar
                    aux.setMiembro(miembro);
                }
            }

            // Verificamos que los detalles no supere los 500 caracteres
            if (involucrado.getDetallesInvolucrado().length() > 500) {
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
            mostrarCadenaMensajes(true, errores, "Se ha producido uno o varios errores:", "Error");
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
                errores.add("El involucrado " + inv.getMiembro().toString() + " se encuentra inactivo o en espera.\n" +
                        "Debe seleccionar un involucrado activo en el sistema.");
            }
        }

        // Verificamos que los detalles no supere los 500 caracteres
        if (inv.getDetallesInvolucrado().length() > 500) {
            errores.add("Los detalles del involucrado " + inv.getMiembro().toString() + " no pueden tener más de 500 caracteres.");
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
            // Cargamos la tabla de involucrados sin un expediente seleccionado
            cargarListaInvolucrados(null);
        }
    }

    // Metodos para interactuar con los selectores

    public void seleccionarExpediente(ComboBox<Expediente> combo) throws Exception {
        selectorControlador.seleccionarExpediente(combo);
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

    public void inicializarListaInvolucrados() {
        // Asociamos las columnas de la tabla
        abmListaInvolucradoControlador.getColNombre().setCellValueFactory(new PropertyValueFactory<>("miembro"));
        abmListaInvolucradoControlador.getColDetallesInvolucrado().setCellValueFactory(new PropertyValueFactory<>("detallesInvolucrado"));

        // Configuramos las columnas en relacion con el miembro
        TableCellFactoryHelper.configurarCeldaDniMiembro(abmListaInvolucradoControlador.getColDni());
        TableCellFactoryHelper.configurarCeldaCargoMiembro(abmListaInvolucradoControlador.getColCargo());
        TableCellFactoryHelper.configurarCeldaEstadoMiembro(abmListaInvolucradoControlador.getColEstado());

        // Configuramos el TextField para que pueda modificar en tiempo de ejecucion los detalles del involucrado
        TableCellFactoryHelper.configurarCeldaTextField(abmListaInvolucradoControlador.getColDetallesInvolucrado());

        // Configuramos el CheckBox para que pueda seleccionar nuevos involucrados a la lista
        TableCellFactoryHelper.configurarCeldaCheck(abmListaInvolucradoControlador.getColSeleccionar(),
                abmListaInvolucradoControlador.getMiembrosSeleccionados());

        // Cargamos la lista de involucrados
        cargarListaInvolucrados(null);
    }

    public void inicializarFiltrosListaInvolucrados() {
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
                    if (flag != ListHelper.FLAG_ELIMINAR) {
                        // Validamos la información del involucrado
                        if (validarInvolucrado(involucrado)) {

                            // Verificamos que los detalles no esten vacios
                            if (involucrado.getDetallesInvolucrado().isEmpty()) {
                                involucrado.setDetallesInvolucrado("No hay detalles para mostrar.");
                            }

                            // Si el ID es diferente de 0, quiere decir que ya existe
                            if (involucrado.getId() != 0 || flag == ListHelper.FLAG_MODIFICAR) {
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
            mostrarCadenaMensajes(true, abmListaInvolucradoControlador.getCadenaErrores(), "Se ha producido uno o varios errores:", "Error");
            mostrarCadenaMensajes(false, abmListaInvolucradoControlador.getCadenaInfos(), "Se ha producido una o varias modificaciones:", "Info");

            // Actualizamos la tabla de involucrados
            listaInvolucradosControlador.getTblInvolucrados().refresh();

            // Salimos del modal
            stageManager.closeModal(FXMLView.FormularioListaInvolucrado.getKey());
        }
    }

    // Metodos para buscar y seleccionar nuevos expedientes

    public void buscarInvolucradosPorExpediente() {
        // Cargamos los involucrados asociados al expediente seleccionado
        cargarListaInvolucrados(abmListaInvolucradoControlador.getCmbExpediente().getValue());

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

        if (expediente != null) {
            // Cargamos la lista de involucrados seleccionados por expedientes
            abmListaInvolucradoControlador.getInvolucrados().addAll(involucradoService.encontrarInvolucradosPorExpediente(expediente));

            // Cargamos la lista de miembros seleccionados a partir de los involucrados
            cargarMiembrosSeleccionados();
        }

        // Obtenemos todos los miembros
        List<Miembro> listaMiembros = miembroService.findAll();

        // Iteramos todos los miembros para mostrar aquellos que no esten seleccionados en pantalla
        for (Miembro miembro : listaMiembros) {
            // Verificamos si el miembro seleccionado ya se encuentra en la lista
            if (buscarInvolucradoPorMiembro(miembro) == null) {
                // Si no se encuentra presente, lo agregamos
                abmListaInvolucradoControlador.getInvolucrados().add(new Involucrado(expediente, miembro, ""));
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
            abmListaInvolucradoControlador.getMiembrosSeleccionados().put(involucrado.getMiembro(), ListHelper.FLAG_MODIFICAR);
        }
    }

    // Metodos para filtrar los involucrados seleccionados

    public boolean mostrarMiembroSeleccionado(Involucrado inv) {
        // Verificamos si el CheckBox de mostrar seleccionados está presionado
        // En ese caso, mostrar solo aquellos que esten contenidos en la lista, y no los haya que eliminar
        return (abmListaInvolucradoControlador.getMiembrosSeleccionados().containsKey(inv.getMiembro())
                && abmListaInvolucradoControlador.getMiembrosSeleccionados().get(inv.getMiembro()) != ListHelper.FLAG_ELIMINAR)
                || !abmListaInvolucradoControlador.getCheckMostrarSeleccionados().isSelected();
    }

    public void filtrarListaInvolucrados() {
        // Limpiamos la lista filtro de miembros
        abmListaInvolucradoControlador.getFiltroInvolucrados().clear();

        // Filtramos todos los miembros según el criterio especificado
        for (Involucrado inv : abmListaInvolucradoControlador.getInvolucrados()) {
            if (mostrarMiembroSeleccionado(inv)) {
                if (aplicarFiltroListaInvolucrados(inv)) {
                    abmListaInvolucradoControlador.getFiltroInvolucrados().add(inv);
                }
            }
        }

        // Actualizamos la tabla
        abmListaInvolucradoControlador.getTblInvolucrados().refresh();
    }

    public boolean aplicarFiltroListaInvolucrados(Involucrado inv) {
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

    public void limpiarFiltrosListaInvolucrados() {
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
        filtrarListaInvolucrados();

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
            ListHelper.seleccionarItem(involucrado.getMiembro(), abmListaInvolucradoControlador.getMiembrosSeleccionados(), valorActual);
        }

        // Actualizamos la tabla
        abmListaInvolucradoControlador.getTblInvolucrados().refresh();
    }
}
