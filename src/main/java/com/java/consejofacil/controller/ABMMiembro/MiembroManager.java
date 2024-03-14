package com.java.consejofacil.controller.ABMMiembro;

import com.java.consejofacil.config.StageManager;
import com.java.consejofacil.security.SessionManager;
import com.java.consejofacil.helper.Alertas.AlertHelper;
import com.java.consejofacil.helper.Componentes.TableCellFactoryHelper;
import com.java.consejofacil.helper.Utilidades.DateFormatterHelper;
import com.java.consejofacil.helper.Utilidades.ImageHelper;
import com.java.consejofacil.helper.Validaciones.DataValidatorHelper;
import com.java.consejofacil.model.Cargo;
import com.java.consejofacil.model.EstadoMiembro;
import com.java.consejofacil.model.Miembro;
import com.java.consejofacil.security.SecurityConfig;
import com.java.consejofacil.service.Cargo.CargoServiceImpl;
import com.java.consejofacil.service.EstadoMiembro.EstadoMiembroServiceImpl;
import com.java.consejofacil.service.Miembro.MiembroServiceImpl;
import com.java.consejofacil.view.FXMLView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import javafx.scene.image.Image;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class MiembroManager {

    // Servicios utilizados
    @Autowired
    @Lazy
    private MiembroServiceImpl miembroService;
    @Autowired
    @Lazy
    private CargoServiceImpl cargoService;
    @Autowired
    @Lazy
    private EstadoMiembroServiceImpl estadoMiembroService;

    // Controladores de los fxml
    @Autowired
    @Lazy
    private ListaMiembrosController listaMiembrosControlador;
    @Autowired
    @Lazy
    private FormularioMiembroController abmMiembroControlador;
    @Autowired
    @Lazy
    private CambiarContrasenaMiembroController cambiarContrasenaMiembroControlador;
    @Autowired
    @Lazy
    private SelectorMiembroController selectorMiembroControlador;

    // Stage Manager
    @Autowired
    @Lazy
    private StageManager stageManager;

    // Componente para codificar y validar contrasenas
    @Autowired
    @Lazy
    private SecurityConfig securityConfig;

    // Componente para obtener información de sesion
    @Autowired
    @Lazy
    private SessionManager sessionManager;

    // Metodo para validar el acceso del miembro

    public void validarAccesoMiembro() {
        sessionManager.validarAccesoMiembro();
    }

    // Metodos de inicialización de componentes

    public void inicializarTablaMiembros(BaseTablaMiembros controlador) {
        try {
            // Asociamos columnas de la tabla
            controlador.getColDni().setCellValueFactory(new PropertyValueFactory<>("dni"));
            controlador.getColNombre().setCellValueFactory(new PropertyValueFactory<>("nombre"));
            controlador.getColFechaNac().setCellValueFactory(new PropertyValueFactory<>("fechaNac"));
            controlador.getColCargo().setCellValueFactory(new PropertyValueFactory<>("cargo"));
            controlador.getColEstado().setCellValueFactory(new PropertyValueFactory<>("estadoMiembro"));

            // Configuramos la columna para mostrar el nombre completo del miembro
            TableCellFactoryHelper.configurarCeldaNombreCompleto(controlador.getColNombre());

            // Formateamos la fecha de nacimiento
            TableCellFactoryHelper.configurarCeldaFecha(controlador.getColFechaNac());

            // Limpiamos las listas listas
            controlador.getMiembros().clear();
            controlador.getFiltroMiembros().clear();

            // Obtenemos todos los miembros
            List<Miembro> miembros = miembroService.findAll();

            // Cargamos las listas basandonos en el controlador
            if (controlador instanceof SelectorMiembroController) {
                controlador.getMiembros().addAll(miembros);
            } else if (controlador instanceof ListaMiembrosController) {

                // Obtenemos la prioridad del miembro
                int prioridad = sessionManager.getUsuario() != null ?
                        sessionManager.getUsuario().getCargo().getPrioridad() : 0;

                // Iteramos los miembros y agregamos aquellos que tengan menor prioridad
                for (Miembro miembro : miembros) {
                    if (prioridad > miembro.getCargo().getPrioridad()) {
                        controlador.getMiembros().add(miembro);
                    }
                }
            }

            // Cargamos el filtro de miembros
            controlador.getFiltroMiembros().addAll(controlador.getMiembros());

            // Cargamos la tabla
            controlador.getTblMiembros().setItems(controlador.getFiltroMiembros());

        } catch (Exception e) {
            controlador.getLog().error(e.getMessage());
        }
    }

    public void inicializarFiltros(BaseTablaMiembros controlador) {
        // Cargamos lista y combo de cargos
        controlador.getCargos().clear();
        controlador.getCargos().addAll(cargoService.findAll());
        controlador.getCmbCargo().setItems(controlador.getCargos());

        // Cargamos lista y combo de estados
        controlador.getEstadosMiembros().clear();
        controlador.getEstadosMiembros().addAll(estadoMiembroService.findAll());
        controlador.getCmbEstado().setItems(controlador.getEstadosMiembros());
    }

    public void inicializarCombosFormulario() {
        // Cargamos lista y combo de estados
        abmMiembroControlador.getEstadosMiembros().clear();
        abmMiembroControlador.getEstadosMiembros().addAll(estadoMiembroService.findAll());
        abmMiembroControlador.getCmbEstado().setItems(abmMiembroControlador.getEstadosMiembros());
    }

    public void inicializarComboCargoFormulario(int prioridad) {
        // Cargamos lista y combo de cargos
        abmMiembroControlador.getCargos().clear();
        abmMiembroControlador.getCargos().addAll(cargoService.encontrarCargosPorPrioridad(prioridad));
        abmMiembroControlador.getCmbCargo().setItems(abmMiembroControlador.getCargos());
    }

    // Metodo para cargar el ABM

    public void cargarFormulario(Miembro miembro, Object controlador) {
        try {
            // Definimos si estamos dentro del módulo
            boolean inModule = controlador instanceof ListaMiembrosController;

            boolean autocompletadoSeleccionado = inModule && listaMiembrosControlador.getCheckAutocompletado().isSelected();
            Miembro aux = new Miembro();

            if (inModule) {
                aux.setNombre(listaMiembrosControlador.getTxtNombre().getText().trim());
                aux.setFechaNac(listaMiembrosControlador.getDtpFechaNac().getValue());
                aux.setCargo(listaMiembrosControlador.getCmbCargo().getValue());
                aux.setEstadoMiembro(listaMiembrosControlador.getCmbEstado().getValue());
            }

            // Cargamos FXML de ABMMiembro
            Parent rootNode = stageManager.loadView(FXMLView.FormularioMiembro.getFxmlFile());

            if (miembro != null) {
                abmMiembroControlador.establecerMiembro(miembro);
            } else if (autocompletadoSeleccionado) {
                // Autocompletamos el formulario si el CheckBox está seleccionado
                autocompletarFormulario(aux);
            }

            // Inicializamos el combo de cargos basandonos en la prioridad del usuario
            int prioridad = sessionManager.validarSesion() ? sessionManager.getUsuario().getCargo().getPrioridad() : 1;
            inicializarComboCargoFormulario(prioridad);

            // Antes de cargar la vista, hacemos las configuraciones correspondientes en el formulario
            configurarFormulario();

            // Abrimos modal
            stageManager.openModal(rootNode, FXMLView.FormularioMiembro);

        } catch (Exception e) {
            abmMiembroControlador.getLog().error(e.getMessage());
        }
    }

    public void procesarMiembro(Miembro miembro, boolean nuevoValor) {
        if (miembro != null && listaMiembrosControlador.getTblMiembros() != null) {

            // Si el miembro no está dentro de la lista, la agregamos
            if (nuevoValor) {
                // Lo agregamos a la lista
                listaMiembrosControlador.getMiembros().add(miembro);

                // Aplicamos el filtro si es necesario
                if (aplicarFiltro(miembro, listaMiembrosControlador)) {
                    listaMiembrosControlador.getFiltroMiembros().add(miembro);
                }
            } else {
                // Actualizamos el miembro
                actualizarMiembro(miembro);

                // Quitamos el filtro si es necesario
                if (quitarFiltro(miembro, listaMiembrosControlador)) {
                    listaMiembrosControlador.getFiltroMiembros().remove(miembro);
                }
            }
        }
    }

    public void actualizarMiembro(Miembro miembro) {
        listaMiembrosControlador.getMiembros().stream()
                // Filtramos los elementos que coinciden con el DNI del miembro
                .filter(m -> m.getDni() == abmMiembroControlador.getMiembro().getDni())
                // Actualizamos los atributos del elemento
                .findFirst().ifPresent(m -> {
                    m.setDni(miembro.getDni());
                    m.setNombre(miembro.getNombre());
                    m.setApellido(miembro.getApellido());
                    m.setClave(miembro.getClave());
                    m.setFechaNac(miembro.getFechaNac());
                    m.setCorreo(miembro.getCorreo());
                    m.setDireccion(miembro.getDireccion());
                    m.setTelefono(miembro.getTelefono());
                    m.setFoto(miembro.getFoto());
                    m.setCargo(miembro.getCargo());
                    m.setEstadoMiembro(miembro.getEstadoMiembro());
                });
    }

    // Metodos para agregar, modificar y eliminar miembros

    public byte[] obtenerBytesFotoPerfil() {
        if (abmMiembroControlador.getNuevaFotoPerfil() != null) {
            // Convertimos la imagen en bytes
            Image foto = abmMiembroControlador.getNuevaFotoPerfil();
            byte[] bytesFoto = ImageHelper.convertirImagenABytes(foto);

            // Verificamos si la foto de perfil esta vacia, y en caso de que ya tenga una foto de perfil antigua, se la guardamos
            if (bytesFoto == null && abmMiembroControlador.getMiembro() != null) {
                bytesFoto = abmMiembroControlador.getMiembro().getFoto();
            }

            return bytesFoto;
        } else if (abmMiembroControlador.getSeEliminoFotoPerfil()) {
            // En caso de que se haya eliminado la foto de perfil
            return null;
        }

        // Si no cambio la foto, ni la quiso eliminar, lo dejamos como esta
        return abmMiembroControlador.getMiembro().getFoto();
    }

    public Miembro obtenerDatosFormulario() {
        // Obtenemos la información proporcionada
        String nombre = abmMiembroControlador.getTxtNombre().getText().trim();
        String apellido = abmMiembroControlador.getTxtApellido().getText().trim();
        LocalDate fechaNacSeleccionada = abmMiembroControlador.getDtpFechaNac().getValue();
        String telefono = abmMiembroControlador.getTxtTelefono().getText().trim();
        String direccion = abmMiembroControlador.getTxtDireccion().getText().trim();
        String correo = abmMiembroControlador.getTxtCorreo().getText().trim();
        String contrasena = abmMiembroControlador.getTxtContrasena().getText().trim();

        // En cuanto al cargo y el estado, si no está en sesion establecer por defecto como miembro y en espera
        // Si es un miembro, no deberia tener acceso a estas propiedades, por lo que le establecemos el mismo cargo y estado
        Cargo cargoSeleccionado;
        EstadoMiembro estadoMiembroSeleccionado;
        if (!sessionManager.validarSesion()) {
            cargoSeleccionado = abmMiembroControlador.getCargos().getFirst();
            estadoMiembroSeleccionado = abmMiembroControlador.getEstadosMiembros().getLast();
        } else {
            if (tieneCargoMiembro()) {
                cargoSeleccionado = sessionManager.getUsuario().getCargo();
                estadoMiembroSeleccionado = sessionManager.getUsuario().getEstadoMiembro();
            } else {
                cargoSeleccionado = abmMiembroControlador.getCmbCargo().getValue();
                estadoMiembroSeleccionado = abmMiembroControlador.getCmbEstado().getValue();
            }
        }

        // Creamos un nuevo miembro auxiliar basándonos en el contexto
        Miembro miembro = new Miembro(nombre, apellido, fechaNacSeleccionada, telefono, direccion, correo,
                cargoSeleccionado, estadoMiembroSeleccionado);

        // Creamos un nuevo miembro auxiliar basándonos en el contexto
        if (abmMiembroControlador.esNuevoMiembro()) {
            miembro.setClave(contrasena);
        }

        // Devolvemos el miembro creado
        return miembro;
    }

    public void guardarMiembro() {
        if (validarCamposFormulario(abmMiembroControlador)) {
            // Creamos un nuevo miembro auxiliar
            Miembro aux = obtenerDatosFormulario();

            // Establecemos el DNI ingresado
            String dniNuevo = abmMiembroControlador.getTxtDni().getText().trim();
            aux.setDni(Integer.parseInt(dniNuevo));

            // Obtenemos los bytes de la foto de perfil (en caso de que haya)
            aux.setFoto(obtenerBytesFotoPerfil());

            // Si el miembro es diferente de nulo, queremos modificar
            if (abmMiembroControlador.getMiembro() != null) {

                // Establecemos la misma clave al miembro auxiliar
                aux.setClave(abmMiembroControlador.getMiembro().getClave());

                // Verificamos si el miembro que estamos moficando es el que se encuentra en sesion
                boolean esMismoMiembro = sessionManager.autenticarMiembro(abmMiembroControlador.getMiembro());

                // Primero, modificamos el DNI del miembro
                int filasActualizadas = miembroService.modificarDni(aux.getDni(), abmMiembroControlador.getMiembro().getDni());

                // Modificamos el miembro
                if (filasActualizadas > 0) {
                    modificarMiembro(aux);
                }

                // Modificamos la información del usuario en sesion, en caso de que sea el mismo
                if (esMismoMiembro) {
                    sessionManager.modificarSesion(aux);
                }

            } else {
                // Codificamos la contrasena, antes de guardar
                aux.setClave(securityConfig.codificarContrasena(aux.getClave()));

                // Si el miembro es nulo, queremos agregar
                agregarMiembro(aux);
            }

            if (listaMiembrosControlador.getTblMiembros() != null) {
                // Actuailizamos la tabla de miembros
                listaMiembrosControlador.getTblMiembros().refresh();
            }

            // Salimos del modal
            stageManager.closeModal(FXMLView.FormularioMiembro.getKey());
        }
    }

    public void agregarMiembro(Miembro miembro) {
        try {
            // Guardamos el miembro auxiliar
            miembro = miembroService.save(miembro);

            // Agregamos miembro a la tabla
            procesarMiembro(miembro, true);

            // Mostramos un mensaje
            mostrarMensaje(false, "Info", "Se ha agregado el miembro correctamente!");

        } catch (Exception e) {
            abmMiembroControlador.getLog().error(e.getMessage());
            mostrarMensaje(true, "Error", "No se pudo agregar el miembro correctamente!");
        }
    }

    public void modificarMiembro(Miembro miembro) {
        try {
            // Modificamos el miembro
            miembro = miembroService.update(miembro);

            // Agregamos miembro modificado a la tabla
            procesarMiembro(miembro, false);

            // Mostramos un mensaje
            mostrarMensaje(false, "Info", "Se ha modificado el miembro correctamente!");

        } catch (Exception e) {
            abmMiembroControlador.getLog().error(e.getMessage());
            mostrarMensaje(true, "Error", "No se pudo modificar el miembro correctamente!");
        }
    }

    public void eliminarMiembro(Miembro miembro, boolean auto) {
        // Mostramos alerta de confirmacion
        if (auto || mostrarConfirmacion("Info", "¿Está seguro que desea eliminar el miembro?")) {
            try {
                // Eliminamos el miembro
                miembroService.delete(miembro);

                // Actualizamos las listas
                listaMiembrosControlador.getMiembros().remove(miembro);
                listaMiembrosControlador.getFiltroMiembros().remove(miembro);

                // Mostramos un mensaje
                mostrarMensaje(false, "Info", "Se ha eliminado el miembro correctamente!");

                // Cerramos sesion, en caso de que sea el que haya iniciado sesion
                if (sessionManager.autenticarMiembro(miembro)) {
                    sessionManager.cerrarSesion();
                }

            } catch (Exception e) {
                listaMiembrosControlador.getLog().error(e.getMessage());
                mostrarMensaje(true, "Info", "No se pudo eliminar el miembro correctamente!");
            }
        }
    }

    // Metodos para cambiar la contrasena

    public void cambiarContrasena() {
        // Verificamos si el miembro no está vacio
        if (abmMiembroControlador.getMiembro() != null) {

            // Cargamos FXML de CambiarContrasena
            Parent rootNode = stageManager.loadView(FXMLView.CambiarContrasenaMiembro.getFxmlFile());

            // Establecemos el miembro
            cambiarContrasenaMiembroControlador.setMiembro(abmMiembroControlador.getMiembro());

            // Abrimos modal
            stageManager.openModal(rootNode, FXMLView.CambiarContrasenaMiembro);

        } else {
            mostrarMensaje(true, "Error", "Debes seleccionar un miembro válido!");
        }
    }

    public void guardarContrasenaNueva() {
        if (validarCamposFormulario(cambiarContrasenaMiembroControlador)) {
            try {
                // Obtenemos la nueva contrasena
                String contrasenaNueva = securityConfig.codificarContrasena(cambiarContrasenaMiembroControlador.
                        getTxtContrasenaNueva().getText().trim());
                int dni = cambiarContrasenaMiembroControlador.getMiembro().getDni();

                // Establecemos la nueva contrasena
                cambiarContrasenaMiembroControlador.getMiembro().setClave(contrasenaNueva);

                // Cambiamos la contrasena del miembro
                int filasActualizadas = miembroService.cambiarContrasena(contrasenaNueva, dni);
                if (filasActualizadas > 0) {
                    // Mostramos un mensaje
                    mostrarMensaje(false, "Info", "Se ha cambiado la contraseña del miembro correctamente!");
                }

                // Salimos del modal
                stageManager.closeModal(FXMLView.CambiarContrasenaMiembro.getKey());

            } catch (Exception e) {
                cambiarContrasenaMiembroControlador.getLog().error(e.getMessage());
                mostrarMensaje(true, "Info", "No se pudo cambiar la contraseña del miembro correctamente!");
            }
        }
    }


    // Metodos para filtrar los miembros

    public void filtrarMiembros(BaseTablaMiembros controlador) {
        // Limpiamos la lista filtro de miembros
        controlador.getFiltroMiembros().clear();

        // Filtramos todos los miembros
        for (Miembro m : controlador.getMiembros()) {
            if (aplicarFiltro(m, controlador)) {
                controlador.getFiltroMiembros().add(m);
            }
        }

        // Actualizamos la tabla
        controlador.getTblMiembros().refresh();
    }

    public boolean aplicarFiltro(Miembro m, BaseTablaMiembros controlador) {
        // Obtenemos todos los filtros
        String nombre = controlador.getTxtNombre().getText();
        LocalDate fechaNac = controlador.getDtpFechaNac().getValue();
        Cargo cargo = controlador.getCmbCargo().getValue();
        EstadoMiembro estado = controlador.getCmbEstado().getValue();

        // Filtramos con base en los filtros obtenidos
        return (nombre == null || m.toString().toLowerCase().contains(nombre.toLowerCase())
                || String.valueOf(m.getDni()).contains(nombre.toLowerCase()))
                && (fechaNac == null || fechaNac.equals(m.getFechaNac()))
                && (cargo == null || cargo.equals(m.getCargo()))
                && (estado == null || estado.equals(m.getEstadoMiembro()));
    }

    public boolean quitarFiltro(Miembro m, BaseTablaMiembros controlador) {
        // Obtenemos todos los filtros
        String nombre = controlador.getTxtNombre().getText();
        LocalDate fechaNac = controlador.getDtpFechaNac().getValue() != null ?
                controlador.getDtpFechaNac().getValue() : null;
        Cargo cargo = controlador.getCmbCargo().getValue();
        EstadoMiembro estado = controlador.getCmbEstado().getValue();

        // Quitamos del filtro con base en los filtros obtenidos
        return (nombre != null && !m.toString().toLowerCase().contains(nombre.toLowerCase())
                && !String.valueOf(m.getDni()).contains(nombre.toLowerCase()))
                || (fechaNac != null && !fechaNac.equals(m.getFechaNac()))
                || (cargo != null && !cargo.equals(m.getCargo()))
                || (estado != null && !estado.equals(m.getEstadoMiembro()));
    }

    // Metodos de autocarga del formulario

    public void autocompletarFormulario(Miembro miembro) {

        // Completamos el formulario a partir de la información proporcionada
        abmMiembroControlador.getTxtDni().setText(String.valueOf(miembro.getDni()));
        abmMiembroControlador.getTxtNombre().setText(miembro.getNombre());
        abmMiembroControlador.getTxtApellido().setText(miembro.getApellido());
        abmMiembroControlador.getCmbCargo().setValue(miembro.getCargo() != null ? miembro.getCargo() : null);
        abmMiembroControlador.getCmbEstado().setValue(miembro.getEstadoMiembro() != null ? miembro.getEstadoMiembro() : null);

        // Completamos con la información adicional
        abmMiembroControlador.getDtpFechaNac().setValue(miembro.getFechaNac() != null ? miembro.getFechaNac() : null);
        abmMiembroControlador.getTxtTelefono().setText(miembro.getTelefono());
        abmMiembroControlador.getTxtDireccion().setText(miembro.getDireccion());
        abmMiembroControlador.getTxtCorreo().setText(miembro.getCorreo());

        // Verificamos que tenga una foto de perfil
        if (miembro.getFoto() != null) {

            // Convertimos los bytes en imagen
            Image imagen = ImageHelper.convertirBytesAImage(miembro.getFoto());

            // Si la imagen se parseó correctamente, la establecemos como foto de perfil
            if (imagen != null) {
                abmMiembroControlador.getImgFotoPerfil().setImage(imagen);
            } else {
                // Mostramos un mensaje, en caso de error
                mostrarMensaje(true, "Error", "No se pudo cargar la foto de perfil.");
            }
        }
    }

    // Metodo para configurar y desactivar algunos componentes disponibles segun la accion deseada

    public void configurarFormulario() {

        if (!abmMiembroControlador.esNuevoMiembro()) {
            // Desactivamos el TextField para ingresar una contranse
            abmMiembroControlador.getTxtContrasena().setDisable(true);
            abmMiembroControlador.getTxtContrasena().setVisible(false);
            abmMiembroControlador.getLblContrasena().setVisible(false);
        } else {
            // Desactivamos el boton para cambiar la contrasena
            abmMiembroControlador.getBtnCambiarContrasena().setDisable(true);
            abmMiembroControlador.getBtnCambiarContrasena().setVisible(false);
        }

        // Si el usuario no está en sesion o es un miembro del consejo, desactivamos los cargos y estados
        if (!sessionManager.validarSesion() || tieneCargoMiembro()) {
            abmMiembroControlador.getCmbCargo().setDisable(true);
            abmMiembroControlador.getCmbCargo().setVisible(false);
            abmMiembroControlador.getLblCargo().setVisible(false);

            abmMiembroControlador.getCmbEstado().setDisable(true);
            abmMiembroControlador.getCmbEstado().setVisible(false);
            abmMiembroControlador.getLblEstado().setVisible(false);
        }
    }

    // Metdodos para validar los campos del formulario

    public boolean tieneCargoMiembro() {
        return sessionManager.validarSesion() &&
                sessionManager.getUsuario().getCargo().getCargo().equals("Miembro del Consejo");
    }

    public boolean validarCamposFormulario(Object controlador) {
        ArrayList<String> errores = new ArrayList<>();

        if (controlador instanceof FormularioMiembroController) {
            // Obtenemos los datos ingresados en el formulario
            Miembro miembro = obtenerDatosFormulario();
            String dni = abmMiembroControlador.getTxtDni().getText().trim();

            //Verificamos que el DNI no este vacio, que solo contenga digitos, y que no esté en uso
            if (dni.isEmpty()) {
                errores.add("Por favor, ingrese un DNI.");
            } else if (DataValidatorHelper.validarDni(dni)) {
                errores.add("El DNI debe contener entre 7 u 8 dígitos numéricos.");
            } else {
                boolean esDiferente = abmMiembroControlador.esNuevoMiembro() ||
                        abmMiembroControlador.getMiembro().getDni() != Integer.parseInt(dni);
                if (abmMiembroControlador.esNuevoMiembro() || esDiferente) {
                    if (miembroService.findById(Integer.parseInt(dni)) != null) {
                        errores.add("El DNI ingresado ya está en uso. Por favor, ingrese otro DNI.");
                    }
                }
            }

            // Verificamos que el nomnbre no este vacio y que no supere los 50 caracteres
            if (miembro.getNombre().isEmpty()) {
                errores.add("Por favor, ingrese un nombre.");
            } else if (miembro.getNombre().length() > 50) {
                errores.add("El nombre no puede tener más de 50 caracteres.");
            } else if (DataValidatorHelper.validarNombreApellido(miembro.getNombre())) {
                errores.add("El nombre debe contener solo letras y espacios.");
            }

            // Verificamos que el apellido no este vacio y que no supere los 50 caracteres
            if (miembro.getApellido().isEmpty()) {
                errores.add("Por favor, ingrese un apellido.");
            } else if (miembro.getApellido().length() > 50) {
                errores.add("El apellido no puede tener más de 50 caracteres.");
            } else if (DataValidatorHelper.validarNombreApellido(miembro.getApellido())) {
                errores.add("El apellido debe contener solo letras y espacios.");
            }

            // Verificamos que la fecha de nacimiento seleccionada no sea posterior al día de hoy
            if (miembro.getFechaNac() != null &&
                    abmMiembroControlador.getDtpFechaNac().getValue().isAfter(LocalDate.now())) {
                errores.add("Debe seleccionar una fecha de nacimiento que sea igual o anterior al dia de hoy " + DateFormatterHelper.fechaHoy() + ".");
            }

            // Verificamos que el telofono tenga el formato adecuado
            if (!miembro.getTelefono().isEmpty() &&
                    DataValidatorHelper.validarTelefono(miembro.getTelefono())) {
                errores.add("""
                        Por favor, ingrese un número de teléfono válido.
                        Tenga en cuenta que debe utilizar el siguiente formato: [+código del país] [código de la ciudad] [número del teléfono local].
                        El signo '+' y los paréntesis '()' son opcionales. Por ejemplo: +54 9 (3757) 54-7545 o 54 9 3757 54-7545.""");
            }

            // Verificamos que la direccion tenga el formato adecuado
            if (!miembro.getDireccion().isEmpty() &&
                    DataValidatorHelper.validarDireccion(miembro.getDireccion())) {
                errores.add("Por favor, ingrese una dirección válida.\n" +
                        "Tenga en cuenta que debe utilizar el siguiente formato: [número de la propiedad] [nombre de la calle] [nombre del barrio]. Por ejemplo: 456 Calle Rivadavia.");
            }

            // Verificamos que el correo tenga el formato adecuado
            if (!miembro.getCorreo().isEmpty() &&
                    DataValidatorHelper.validarCorreo(miembro.getCorreo())) {
                errores.add("Por favor, introduce una dirección de correo electrónico válida. Por ejemplo, usuario@dominio.com.");
            }

            // Verificamos que el contrasena no este vacia, que no supere los 50 caracteres y que cumpla los requisitos
            if (abmMiembroControlador.esNuevoMiembro()) {
                validarContrasena(miembro.getClave(), errores);
            }

            // Verificamos que haya seleccionado un cargo
            if (miembro.getCargo() == null) {
                errores.add("Por favor, seleccione un cargo.");
            } else {
                Cargo cargo = cargoService.findById(miembro.getCargo().getId());
                if (cargo == null) {
                    errores.add("El cargo seleccionado no se encuentra en la base de datos.");
                }
            }

            // Verificamos que haya seleccionado un estado de miembro
            if (miembro.getEstadoMiembro() == null) {
                errores.add("Por favor, seleccione un estado de miembro.");
            } else {
                EstadoMiembro estado = estadoMiembroService.findById(miembro.getEstadoMiembro().getId());
                if (estado == null) {
                    errores.add("El estado de miembro seleccionado no se encuentra en la base de datos.");
                }
            }
        } else if (controlador instanceof CambiarContrasenaMiembroController) {

            // Obtenemos las contrasenas ingresadas
            String contrasenaActual = cambiarContrasenaMiembroControlador.getTxtContrasenaActual().getText().trim();
            String contrasenaNueva = cambiarContrasenaMiembroControlador.getTxtContrasenaNueva().getText().trim();
            String repetirContrasenaNueva = cambiarContrasenaMiembroControlador.getTxtRepetirContrasenaNueva().getText().trim();

            // Verificamos que la contrasena actual no este vacia y que coincida con la antigua
            if (contrasenaActual.isEmpty()) {
                errores.add("Por favor, ingrese la contraseña actual.");
            } else if (!securityConfig.validarContrasena(contrasenaActual, cambiarContrasenaMiembroControlador.getMiembro().getClave())) {
                errores.add("La contraseña actual ingresada es incorrecta. Vuelva a intentarlo.");
            }

            // Verificamos que la nueva contrasena no este vacia, que no supere los 50 caracteres y que cumpla los requisitos
            validarContrasena(contrasenaNueva, errores);

            // Verificamos que haya ingresado nuevamente la nueva contrasena y que coincidan
            if (repetirContrasenaNueva.isEmpty()) {
                errores.add("Por favor, ingrese nuevamente la contraseña nueva.");
            } else if (!repetirContrasenaNueva.equals(contrasenaNueva)) {
                errores.add("La contraseña nueva no coincide. Vuelva a intentarlo.");
            }
        }

        // Verificamos si hay errores
        if (!errores.isEmpty()) {
            AlertHelper.mostrarCadenaMensajes(true, errores, "Se ha producido uno o varios errores:", "Error");
            return false;
        }

        return true;
    }

    public void validarContrasena(String contrasena, ArrayList<String> errores) {
        if (contrasena.isEmpty()) {
            errores.add("Por favor, ingrese un contraseña.");
        } else if (contrasena.length() > 50) {
            errores.add("La contraseña no puede tener más de 50 caracteres.");
        } else if (DataValidatorHelper.validarContrasena(contrasena)) {
            errores.add("""
                    Por favor, introduce una contraseña válida que cumpla con los siguientes requisitos:
                        • Al menos 6 caracteres de longitud.
                        • Al menos una letra mayúscula.
                        • Al menos una letra minúscula.
                        • Al menos un dígito numérico.
                        • Al menos un carácter especial [@#$%^&+=!].""");
        }
    }

    // Metodos para limpiar campos

    public void limpiarFiltros(BaseTablaMiembros controlador) {
        // Guardamos los eventos de los filtros
        EventHandler<ActionEvent> handlerCargo = controlador.getCmbCargo().getOnAction();
        EventHandler<ActionEvent> handlerEstado = controlador.getCmbEstado().getOnAction();
        EventHandler<ActionEvent> handlerFechaNac = controlador.getDtpFechaNac().getOnAction();

        // Desactivamos los eventos asociados a los filtros
        controlador.getCmbCargo().setOnAction(null);
        controlador.getCmbEstado().setOnAction(null);
        controlador.getDtpFechaNac().setOnAction(null);

        // Limpiamos los filtros
        controlador.getTxtNombre().clear();
        controlador.getCmbCargo().setValue(null);
        controlador.getDtpFechaNac().setValue(null);
        controlador.getCmbEstado().setValue(null);

        // Filtramos los expedientes
        filtrarMiembros(controlador);

        // Restauramos los eventos asociados a los filtros
        controlador.getCmbCargo().setOnAction(handlerCargo);
        controlador.getDtpFechaNac().setOnAction(handlerFechaNac);
        controlador.getCmbEstado().setOnAction(handlerEstado);
    }

    public void limpiarFormulario(Object controlador) {
        if (controlador instanceof FormularioMiembroController) {
            // Limpiamos el formulario
            abmMiembroControlador.getTxtDni().clear();
            abmMiembroControlador.getTxtNombre().clear();
            abmMiembroControlador.getTxtApellido().clear();
            abmMiembroControlador.getDtpFechaNac().setValue(null);
            abmMiembroControlador.getTxtTelefono().clear();
            abmMiembroControlador.getTxtDireccion().clear();
            abmMiembroControlador.getTxtCorreo().clear();
            abmMiembroControlador.getTxtContrasena().clear();
            abmMiembroControlador.getCmbCargo().setValue(null);
            abmMiembroControlador.getCmbEstado().setValue(null);
        } else if (controlador instanceof CambiarContrasenaMiembroController) {
            // Limpiamos el formulario para cambiar contrasena
            cambiarContrasenaMiembroControlador.getTxtContrasenaActual().clear();
            cambiarContrasenaMiembroControlador.getTxtContrasenaNueva().clear();
            cambiarContrasenaMiembroControlador.getTxtRepetirContrasenaNueva().clear();
        }
    }

    // Metodos para mostrar mensajes en pantalla

    public boolean mostrarConfirmacion(String titulo, String contenido) {
        return AlertHelper.mostrarConfirmacion(titulo, contenido);
    }

    public void mostrarMensaje(boolean error, String titulo, String contenido) {
        AlertHelper.mostrarMensaje(error, titulo, contenido);
    }

    // Metodos adicionales para el selector

    public void agregarMiembroSelector() {
        // Obtenemos el miembro seleccionado
        selectorMiembroControlador.setMiembro(selectorMiembroControlador.getTblMiembros().getSelectionModel().getSelectedItem());

        // Verificamos que sea diferente de nulo
        if (selectorMiembroControlador.getMiembro() == null) {
            mostrarMensaje(true, "Error", "Debes seleccionar un miembro!");
        } else {
            // Salimos del modal
            stageManager.closeModal(FXMLView.SelectorMiembro.getKey());
        }
    }

    public void autoseleccionarMiembro() {
        // Seleccionamos el miembro en cuestion en la tabla
        TableView.TableViewSelectionModel<Miembro> selectionModel = selectorMiembroControlador.getTblMiembros().getSelectionModel();
        selectionModel.select(selectorMiembroControlador.getMiembro());
    }

    // Metodos utilizados para la foto de perfil

    public void seleccionarFotoPerfil() {
        // Crear un cuadro de diálogo de selección de archivos
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Imagen");

        // Filtrar solo archivos de imagen
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Archivos de Imagen", "*.png", "*.jpg", "*.jpeg");
        fileChooser.getExtensionFilters().add(extFilter);

        // Mostrar el cuadro de diálogo y esperar a que el usuario seleccione un archivo
        File selectedFile = fileChooser.showOpenDialog(stageManager.getModalStages().get(FXMLView.FormularioMiembro.getKey()));
        if (selectedFile != null) {

            // Cargar la imagen seleccionada en un objeto Image
            Image image = new Image(selectedFile.toURI().toString());

            // Establecer la imagen en el ImageView
            abmMiembroControlador.getImgFotoPerfil().setImage(image);

            // Guardamos la nueva foto de perfil
            abmMiembroControlador.setNuevaFotoPerfil(image);
        }
    }

    public void eliminarFotoPerfil() {
        // Colocamos la foto de perfil por defecto
        ImageHelper.colocarImagenPorDefecto(abmMiembroControlador.getImgFotoPerfil());
        abmMiembroControlador.setNuevaFotoPerfil(null);
        abmMiembroControlador.setSeEliminoFotoPerfil(true);
    }
}