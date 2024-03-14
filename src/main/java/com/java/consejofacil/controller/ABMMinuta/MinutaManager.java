package com.java.consejofacil.controller.ABMMinuta;

import com.java.consejofacil.config.StageManager;
import com.java.consejofacil.security.SessionManager;
import com.java.consejofacil.helper.Alertas.AlertHelper;
import com.java.consejofacil.helper.Componentes.ComponentHelper;
import com.java.consejofacil.controller.SelectorController;
import com.java.consejofacil.helper.Utilidades.DateFormatterHelper;
import com.java.consejofacil.model.Minuta;
import com.java.consejofacil.model.Reunion;
import com.java.consejofacil.service.Minuta.MinutaServiceImpl;
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

@Component
public class MinutaManager {

    // Servicios utilizados
    @Autowired
    @Lazy
    private ReunionServiceImpl reunionService;
    @Autowired
    @Lazy
    private MinutaServiceImpl minutaService;

    // Controladores de los fxml
    @Autowired
    @Lazy
    private ListaMinutasController listaMinutasControlador;
    @Autowired
    @Lazy
    private FormularioMinutaController abmMinutaControlador;
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

    public void inicializarTablaMinutas() {
        try {
            // Asociamos columnas de la tabla
            listaMinutasControlador.getColId().setCellValueFactory(new PropertyValueFactory<>("id"));
            listaMinutasControlador.getColTemaTratado().setCellValueFactory(new PropertyValueFactory<>("temaTratado"));
            listaMinutasControlador.getColReunion().setCellValueFactory(new PropertyValueFactory<>("reunion"));
            listaMinutasControlador.getColDetallesMinuta().setCellValueFactory(new PropertyValueFactory<>("detallesMinuta"));

            // Cargamos listas
            listaMinutasControlador.getMinutas().clear();
            listaMinutasControlador.getFiltroMinutas().clear();
            listaMinutasControlador.getMinutas().addAll(minutaService.findAll());
            listaMinutasControlador.getFiltroMinutas().addAll(listaMinutasControlador.getMinutas());

            // Cargamos la tabla
            listaMinutasControlador.getTblMinutas().setItems(listaMinutasControlador.getFiltroMinutas());
        } catch (Exception e) {
            listaMinutasControlador.getLog().error(e.getMessage());
        }
    }

    public void inicializarFiltros() {
        // Cargamos lista y combo de reuniones
        listaMinutasControlador.getReuniones().clear();
        listaMinutasControlador.getReuniones().addAll(reunionService.encontrarReunionesHastaHoy());
        listaMinutasControlador.getCmbReunion().setItems(listaMinutasControlador.getReuniones());
        configurarComboEditable(listaMinutasControlador.getCmbReunion());
    }

    public void inicializarCombosFormulario() {
        // Cargamos lista y combo de reuniones
        abmMinutaControlador.getReuniones().clear();
        abmMinutaControlador.getReuniones().addAll(reunionService.encontrarReunionesHastaHoy());
        abmMinutaControlador.getCmbReunion().setItems(abmMinutaControlador.getReuniones());
        configurarComboEditable(abmMinutaControlador.getCmbReunion());
    }

    // Metodo para cargar el ABM

    public void cargarFormulario(Minuta minuta) {
        try {
            boolean autocompletadoSeleccionado = listaMinutasControlador.getCheckAutocompletado().isSelected();
            String temaTratado = listaMinutasControlador.getTxtTemaTratado().getText().trim();
            String detallesMinuta = listaMinutasControlador.getTxtDetallesMinuta().getText().trim();
            Reunion reunionSeleccionada = listaMinutasControlador.getCmbReunion().getValue();

            // Cargamos FXML de ABMMinuta
            Parent rootNode = stageManager.loadView(FXMLView.FormularioMinuta.getFxmlFile());

            // Autocompletamos el formulario si el CheckBox está seleccionado
            if (minuta != null) {
                abmMinutaControlador.establecerMinuta(minuta);
            } else if (autocompletadoSeleccionado) {
                autocompletarFormulario(new Minuta(temaTratado, detallesMinuta, reunionSeleccionada));
            }

            // Abrimos modal
            stageManager.openModal(rootNode, FXMLView.FormularioMinuta);

        } catch (Exception e) {
            listaMinutasControlador.getLog().error(e.getMessage());
        }
    }

    public void procesarMinuta(Minuta minuta, boolean nuevoValor) {
        if (minuta != null) {
            // Si la minuta no está dentro de la lista, la agregamos
            if (nuevoValor) {
                // Lo agregamos a la lista
                listaMinutasControlador.getMinutas().add(minuta);

                // Aplicamos el filtro si es necesario
                if (aplicarFiltro(minuta)) {
                    listaMinutasControlador.getFiltroMinutas().add(minuta);
                }
            } else {
                // Actualizamos la minuta
                actualizarMinuta(minuta);

                // Quitamos el filtro si es necesario
                if (quitarFiltro(minuta)) {
                    listaMinutasControlador.getFiltroMinutas().remove(minuta);
                }
            }
        }
    }

    public void actualizarMinuta(Minuta minuta) {
        listaMinutasControlador.getMinutas().stream()
                // Filtramos los elementos que coinciden con el ID de la minuta
                .filter(min -> min.getId() == minuta.getId())
                // Actualizamos los atributos del elemento
                .findFirst().ifPresent(min -> {
                    min.setTemaTratado(minuta.getTemaTratado());
                    min.setDetallesMinuta(minuta.getDetallesMinuta());
                    min.setReunion(minuta.getReunion());
                });
    }

    // Metodos para agregar, modificar y eliminar minutas

    public Minuta obtenerDatosFormulario(){
        // Obtenemos la información en los campos
        String temaTratado = abmMinutaControlador.getTxtTemaTratado().getText().trim();
        String detallesMinuta = abmMinutaControlador.getTxtDetallesMinuta().getText().trim();
        Reunion reunionSeleccionada = abmMinutaControlador.getCmbReunion().getValue();

        // Creamos una nueva minuta auxiliar
        return new Minuta(temaTratado, detallesMinuta, reunionSeleccionada);
    }

    public void guardarMinuta() {
        if (validarCamposFormulario()) {
            // Creamos una nueva minuta auxiliar
            Minuta aux = obtenerDatosFormulario();

            // Si la minuta es diferente de nulo, queremos modificar
            if (abmMinutaControlador.getMinuta() != null) {

                // Establecemos el ID a la minuta auxiliar
                aux.setId(abmMinutaControlador.getMinuta().getId());

                // Modificamos la minuta
                modificarMinuta(aux);
            } else {
                // Si la minuta es nulo, queremos agregar
                agregarMinuta(aux);
            }

            // Actualizamos la tabla de minutas
            listaMinutasControlador.getTblMinutas().refresh();

            // Salimos del modal
            stageManager.closeModal(FXMLView.FormularioMinuta.getKey());
        }
    }

    public void agregarMinuta(Minuta minuta) {
        try {
            // Guardamos la minuta auxiliar
            minuta = minutaService.save(minuta);

            // Agregamos acción a la tabla
            procesarMinuta(minuta, true);

            // Mostramos un mensaje
            mostrarMensaje(false, "Info", "Se ha agregado la minuta correctamente!");

        } catch (Exception e) {
            abmMinutaControlador.getLog().error(e.getMessage());
            mostrarMensaje(true, "Error", "No se pudo agregar la minuta correctamente!");
        }
    }


    public void modificarMinuta(Minuta minuta) {
        try {
            // Modificamos la minuta
            minuta = minutaService.update(minuta);

            // Agregamos minuta modificada a la tabla
            procesarMinuta(minuta, false);

            // Mostramos un mensaje
            mostrarMensaje(false, "Info", "Se ha modificado la minuta correctamente!");

        } catch (Exception e) {
            abmMinutaControlador.getLog().error(e.getMessage());
            mostrarMensaje(true, "Error", "No se pudo modificar la minuta correctamente!");
        }
    }

    public void eliminarMinuta(Minuta minuta, boolean auto) {
        // Mostramos alerta de confirmacion
        if (auto || mostrarConfirmacion("Info", "¿Está seguro que desea eliminar la minuta?")) {
            try {
                // Eliminamos la minuta
                minutaService.delete(minuta);

                // Actualizamos las listas
                listaMinutasControlador.getMinutas().remove(minuta);
                listaMinutasControlador.getFiltroMinutas().remove(minuta);

                // Mostramos un mensaje
                mostrarMensaje(false, "Info", "Se ha eliminado la minuta correctamente!");

            } catch (Exception e) {
                listaMinutasControlador.getLog().error(e.getMessage());
                mostrarMensaje(true, "Info", "No se pudo eliminar la minuta correctamente!");
            }
        }
    }

    // Metodos para filtrar las minutas

    public void filtrarMinutas() {
        // Limpiamos la lista filtro de minutas
        listaMinutasControlador.getFiltroMinutas().clear();

        // Filtramos todas las minutas
        for (Minuta minuta : listaMinutasControlador.getMinutas()) {
            if (aplicarFiltro(minuta)) {
                listaMinutasControlador.getFiltroMinutas().add(minuta);
            }
        }

        // Actualizamos la tabla
        listaMinutasControlador.getTblMinutas().refresh();
    }

    public boolean aplicarFiltro(Minuta minuta) {
        // Obtenemos todos los filtros
        String temaTratado = listaMinutasControlador.getTxtTemaTratado().getText();
        Reunion reunion = listaMinutasControlador.getCmbReunion().getValue();
        String detalles = listaMinutasControlador.getTxtDetallesMinuta().getText();

        // Filtramos con base en los filtros obtenidos
        return (temaTratado == null || minuta.getTemaTratado().toLowerCase().contains(temaTratado.toLowerCase()))
                && (reunion == null || reunion.equals(minuta.getReunion()))
                && (detalles == null || minuta.getDetallesMinuta().toLowerCase().contains(detalles.toLowerCase()));
    }

    public boolean quitarFiltro(Minuta minuta) {
        // Obtenemos todos los filtros
        String temaTratado = listaMinutasControlador.getTxtTemaTratado().getText();
        Reunion reunion = listaMinutasControlador.getCmbReunion().getValue();
        String detalles = listaMinutasControlador.getTxtDetallesMinuta().getText();

        // Quitamos del filtro con base en los filtros obtenidos
        return (temaTratado != null && !minuta.getTemaTratado().toLowerCase().contains(temaTratado.toLowerCase()))
                || (reunion != null && !reunion.equals(minuta.getReunion()))
                || (detalles != null && !minuta.getDetallesMinuta().toLowerCase().startsWith(detalles.toLowerCase()));
    }

    // Metodos de autocarga del formulario

    public void autocompletarFormulario(Minuta minuta) {
        // Completamos el formulario a partir de la información proporcionada
        abmMinutaControlador.getTxtTemaTratado().setText(minuta.getTemaTratado());
        abmMinutaControlador.getTxtDetallesMinuta().setText(minuta.getDetallesMinuta());

        if (minuta.getReunion() != null) {
            abmMinutaControlador.getCmbReunion().setValue(minuta.getReunion());
        }
    }

    // Metdodos para validar los campos del formulario

    public boolean validarCamposFormulario() {
        ArrayList<String> errores = new ArrayList<>();
        Minuta minuta = obtenerDatosFormulario();

        // Verificamos que el tema tratado no este vacio y que no supere los 150 caracteres
        if (minuta.getTemaTratado().isEmpty()) {
            errores.add("Por favor, ingrese un tema a tratar.");
        } else if (minuta.getTemaTratado().length() > 150) {
            errores.add("El tema tratado no puede tener más de 150 caracteres.");

        }

        // Verificamos que los detalles no esten vacios y que no superen los 500 caracteres
        if (minuta.getDetallesMinuta().isEmpty()) {
            errores.add("Por favor, ingrese unos detalles de la minuta.");
        } else if (minuta.getDetallesMinuta().length() > 500) {
            errores.add("Los detalles de la minuta no pueden tener más de 500 caracteres.");
        }

        // Verificamos que haya seleccionado una reunion
        if (minuta.getReunion() == null) {
            errores.add("Por favor, seleccione una reunión.");
        } else {
            Reunion reunion = reunionService.findById(minuta.getReunion().getId());
            if (reunion == null) {
                errores.add("La reunión seleccionada no se encuentra en la base de datos.");
            } else if (reunion.getFechaReunion().isAfter(LocalDate.now())) {
                // Verificamos que la reunion no sea posterior al día de hoy
                errores.add("Debe seleccionar una reunión previa o programada para el día de hoy " + DateFormatterHelper.fechaHoy() + ".");
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
        EventHandler<ActionEvent> handlerReunion = listaMinutasControlador.getCmbReunion().getOnAction();

        // Desactivamos los eventos asociados a los filtros
        listaMinutasControlador.getCmbReunion().setOnAction(null);

        // Limpiamos los filtros
        listaMinutasControlador.getTxtTemaTratado().clear();
        listaMinutasControlador.getCmbReunion().setValue(null);
        listaMinutasControlador.getTxtDetallesMinuta().clear();

        // Filtramos las minutas
        filtrarMinutas();

        // Restauramos los eventos asociados a los filtros
        listaMinutasControlador.getCmbReunion().setOnAction(handlerReunion);
    }

    public void limpiarFormulario() {
        // Limpiamos el formulario
        abmMinutaControlador.getTxtTemaTratado().clear();
        abmMinutaControlador.getCmbReunion().setValue(null);
        abmMinutaControlador.getTxtDetallesMinuta().clear();
    }

    // Metodos para interactuar con los selectores

    public void seleccionarReunion(ComboBox<Reunion> combo) throws Exception {
        selectorControlador.seleccionarReunion(combo);
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
