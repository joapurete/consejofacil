package com.java.consejofacil.helpers;

import com.java.consejofacil.config.StageManager;
import com.java.consejofacil.controller.ABMExpediente.SelectorExpedienteController;
import com.java.consejofacil.controller.ABMMiembro.SelectorMiembroController;
import com.java.consejofacil.controller.ABMReunion.SelectorReunionController;
import com.java.consejofacil.model.*;
import com.java.consejofacil.view.FXMLView;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Helpers {

    // Flags utilizados para seleccionar elementos de una lista
    public final int FLAG_NUEVO = 0; // Representa un nuevo elemento
    public final int FLAG_MODIFICAR = 1; // Representa un elemento a modificar
    public final int FLAG_ELIMINAR = 2; // Representa un elemento a eliminar

    // Controladores de los selectores
    @Autowired
    @Lazy
    private SelectorReunionController selectorReunionControlador;
    @Autowired
    @Lazy
    private SelectorExpedienteController selectorExpedienteControlador;
    @Autowired
    @Lazy
    private SelectorMiembroController selectorMiembroControlador;

    // Stage Manager
    @Autowired
    @Lazy
    private StageManager stageManager;

    public Helpers() {
    }

    // Metodos para mostrar mensajes en pantalla

    public boolean mostrarConfirmacion(String titulo, String contenido) {
        // Funcion para mostrar alerta de confirmacion
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setHeaderText(null);
        alerta.setTitle(titulo);
        alerta.setContentText(contenido);
        ButtonType resultado = alerta.showAndWait().orElse(ButtonType.CANCEL);
        return resultado == ButtonType.OK;
    }

    public void mostrarCadenaMensajes(ArrayList<String> mensajes, String titulo, Alert.AlertType tipoAlerta, String tituloAlerta) {
        if (!mensajes.isEmpty()) {
            // Concatenamos cada error en un sola cadena
            StringBuilder cadenaMensajes = new StringBuilder();
            cadenaMensajes.append(titulo).append("\n");
            for (String error : mensajes) {
                cadenaMensajes.append("- ").append(error).append("\n");
            }

            // Finalmente, mostramos los errores en pantalla
            mostrarMensaje(tipoAlerta, tituloAlerta, cadenaMensajes.toString());
        }
    }

    public void mostrarMensaje(Alert.AlertType tipo, String titulo, String contenido) {
        // Funcion para mostrar alerta
        Alert alert = new Alert(tipo);
        alert.setHeaderText(null);
        alert.setTitle(titulo);

        // Ajustamos el contenido
        Label contenidoAjustado = new Label(contenido);
        contenidoAjustado.setWrapText(true);
        alert.getDialogPane().setContent(contenidoAjustado);

        // Mostramos la alerta
        alert.showAndWait();
    }

    // Metodos de configuracion para algunos componentes

    // Esto para agregar items que no estén dentro de la lista predefinida de items del combo

    public <T> void configurarComboEditable(ComboBox<T> combo) {
        // Deshabilitamos la edicion del editor del combo
        combo.getEditor().setEditable(false);
        // Creamos un StringConverter personalizado para los objetos
        combo.setConverter(new StringConverter<>() {
            @Override
            public String toString(T objeto) {
                // Devolvemos el texto asociado al objeto
                return (objeto != null) ? objeto.toString() : "";
            }

            @Override
            public T fromString(String string) {
                // Devolvemos el objeto seleccionado
                return combo.getSelectionModel().getSelectedItem();
            }
        });
    }

    // Esto para configurar un CheckBox dentro de una tabla, y poder seleccionar y agregar el item asociado a una lista

    public <T, S> void configurarCheckTableCell(TableColumn<T, Boolean> columna, Map<S, Integer> lista) {
        columna.setCellValueFactory(features -> {
            T item = features.getValue();
            // Obtenemos la key a partir del item seleccionado
            S key = (S) getKey(item);

            // El CheckBox se seleccionará si key está contenido dentro de la lista
            return new SimpleBooleanProperty(lista.containsKey(key) && lista.get(key) != FLAG_ELIMINAR);
        });

        columna.setCellFactory(param -> new CheckBoxTableCell<>() {

            // Creamos un CheckBox
            private final CheckBox checkBox = new CheckBox();

            {
                // Le damos estilos personalizados
                checkBox.getStyleClass().add("check-box-personalizado");
                // Agregamos un listener para que guarde la información al presionar el CheckBox
                checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                    T item = getTableRow().getItem();

                    // Obtenemis la key a partir del elemento seleccionado
                    S key = (S) getKey(item);

                    // Si key es diferente de nulo, seleccionamos el item con la key asociada
                    if (key != null) {
                        seleccionarItemCheckTableCell(key, lista, newValue);
                    }
                });
            }

            @Override
            public void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    checkBox.setSelected(false);
                    setText(null);
                    setGraphic(null);
                } else {
                    // Establecemos el valor recibido en el CheckBox y lo establecemos como grafico
                    checkBox.setSelected(item);
                    setText(null);
                    setGraphic(checkBox);
                }
            }
        });
    }

    // Esto es para obtener la key de la lista a partir del item seleccionado

    private <T> Object getKey(T item) {
        Object key = null;

        if (item instanceof Involucrado involucrado) {
            key = involucrado.getMiembro();
        } else if (item instanceof Revision revision) {
            key = revision.getExpediente();
        } else if (item instanceof Asistencia asistencia) {
            key = asistencia.getMiembro();
        }
        return key;
    }

    public <T> void seleccionarItemCheckTableCell(T item, Map<T, Integer> lista, Boolean newValue) {

        // Si lista contiene al item, modificamos su flag si es un elemento existente en la base de datos
        // Si es un nuevo elemento, lo eliminamos, ya que no se realizó ninguna modificacion con el mismo
        if (lista.containsKey(item)) {
            // Obtenemos el flag correspondiente
            int flag = lista.get(item);
            // Establecemos
            boolean esElementoExistente = flag == FLAG_MODIFICAR || flag == FLAG_ELIMINAR;

            // Si el CheckBox está seleccionado
            if (newValue) {
                // Si es un elemento existente, modificamos el flag (para no perder el elemento)
                if (esElementoExistente) {
                    lista.put(item, FLAG_MODIFICAR);
                }
            } else {
                // Si el CheckBox no está seleccionado, y es un elemento existente, modificamos el flag
                if (esElementoExistente) {
                    lista.put(item, FLAG_ELIMINAR);
                } else {
                    // En caso de que sea un neuvo elemento, lo eliminamos de la lista
                    lista.remove(item);
                }
            }
        } else if (newValue) {
            // Si la lista no lo contiene, y el CheckBox está seleccionado, lo queremos agregar
            lista.put(item, FLAG_NUEVO);
        }
    }

    // Esto es para configurar fabricas de celdas

    // Esto es para configurar un TextField dentro de una tabla

    public <T> void configurarTxtFieldTabla(TableColumn<T, String> columna) {

        columna.setCellFactory(param -> new TextFieldTableCell<>(new DefaultStringConverter()) {

            // Creamos un TextField
            private final TextField textField = new TextField();

            {
                // Le damos estilos personalizados
                textField.getStyleClass().add("text-field-personalizado");
                // Establecemos el ancho preferido del TextField al ancho de la celda
                textField.prefWidthProperty().bind(columna.widthProperty().subtract(10));
                // Agregamos un listener para que guarde la información al finalizar la edicion
                textField.textProperty().addListener((obs, oldVal, newVal) -> commitEdit(newVal));
            }

            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    textField.setText("");
                    setText("");
                    setGraphic(null);
                } else {
                    // Estlecemos el texto a mostrar y el TextField como grafico
                    textField.setText(item);
                    setText(item);
                    setGraphic(textField);
                }
            }

            @Override
            public void commitEdit(String newValue) {
                super.commitEdit(newValue);

                // Obtenemos el elemento seleccionado
                T item = getTableRow().getItem();

                if (item != null) {
                    // Actualizamos los detalles del elemento seleccionado
                    if (item instanceof Involucrado involucrado) {
                        involucrado.setDetallesInvolucrado(newValue);
                    } else if (item instanceof Revision revision) {
                        revision.setDetallesRevision(newValue);
                    }
                }
            }
        });
    }

    // Esto es para configurar un ComboBox dentro de una tabla para seleccionar estados de asistencias

    public <T> void configurarCmbTablaEstadoAsistencia(TableColumn<T, EstadoAsistencia> columna, ObservableList<EstadoAsistencia> opciones) {

        columna.setCellFactory(param -> new ComboBoxTableCell<>(opciones) {

            // Creamos un ComboBox
            private final ComboBox<EstadoAsistencia> comboBox = new ComboBox<>();

            {
                // Configuramos el ComboBox con las opciones proporcionadas
                comboBox.getItems().addAll(opciones);
                // Le damos estilos personalizados
                comboBox.getStyleClass().add("combo-personalizado");
                // Establecemos el ancho preferido del ComboBox al ancho de la celda
                comboBox.prefWidthProperty().bind(columna.widthProperty().subtract(10));
                // Agregamos un listener para que guarde la información al finalizar la edición
                comboBox.setOnAction(event -> commitEdit(comboBox.getValue()));
            }

            @Override
            public void updateItem(EstadoAsistencia item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    comboBox.setValue(null);
                    setText(null);
                    setGraphic(null);
                } else {
                    // Establecemos el ComboBox como gráfico
                    comboBox.setValue(item);
                    setText(item.toString());
                    setGraphic(comboBox);
                }
            }

            @Override
            public void commitEdit(EstadoAsistencia newValue) {
                super.commitEdit(newValue);

                // Obtenemos el elemento seleccionado
                T item = getTableRow().getItem();

                if (item != null) {
                    // Actualizamos los detalles del elemento seleccionado
                    if (item instanceof Asistencia asistencia) {
                        asistencia.setEstadoAsistencia(newValue);
                    }
                }
            }
        });
    }


    // Esto es para mostrar el nombre completo de la persona en la tabla

    public void configurarNombreCompleto(TableColumn<Miembro, String> columna) {
        // Establecemos una fábrica de celdas para la columna
        // Devolvemos una nueva celda para la visualizacion de datos
        columna.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String nombre, boolean empty) {
                // Llamamos a la clase padre para cualquier inicializacion necesaria
                super.updateItem(nombre, empty);
                // Verificamos si el nombre es nulo
                if (nombre == null || empty) {
                    setText(null);
                } else {
                    // Si es diferente de nulo, concatenamos el nombre y apellido del miembro correspondiente
                    setText(nombre + " " + getTableView().getItems().get(getIndex()).getApellido());
                }
            }
        });
    }

    // Esto es para formatear las fechas en las tablas

    public <T> void formatearColumnaFecha(TableColumn<T, LocalDate> columna) {
        // Establecemos una fábrica de celdas para la columna
        columna.setCellFactory(column -> {
            // Devolvemos una nueva celda para la visualizacion de datos
            return new TableCell<>() {
                // Sobreescribimos el metodo para personalizar el comportamiento de actualizacion de celda
                @Override
                protected void updateItem(LocalDate fecha, boolean empty) {
                    // Llamamos a la clase padre para cualquier inicializacion necesaria
                    super.updateItem(fecha, empty);
                    // Verificamos si la fecha es nula
                    if (fecha == null || empty) {
                        setText(null);
                    } else {
                        // Si es diferente de nula, formateamos la fecha antes de mostrarla en la celda
                        setText(formatearFecha(fecha));
                    }
                }
            };
        });
    }

    public static String formatearFecha(LocalDate fecha) {
        // Definimos el formato de fecha y la fecha de hoy
        String formatoFecha = "dd 'de' MMMM 'de' yyyy";
        String dia;
        LocalDate hoy = LocalDate.now();

        // Verificamos si la fecha proporcionada es hoy, ayer o manaña
        if (fecha.equals(hoy)) {
            dia = "Hoy";
        } else if (fecha.equals(hoy.minusDays(1))) {
            dia = "Ayer";
        } else if (fecha.equals(hoy.plusDays(1))) {
            dia = "Mañana";
        } else {
            // En caso de que no sea ninguno, establecemos el día correspondiente
            dia = fecha.format(DateTimeFormatter.ofPattern("EEEE").withLocale(Locale.forLanguageTag("es")));
        }

        // Formateamos la fecha
        String fechaFormateada = dia + ", " + fecha.format(DateTimeFormatter.ofPattern(formatoFecha)
                .withLocale(Locale.forLanguageTag("es")));

        // Ponemos la primer letra en mayusculas
        fechaFormateada = fechaFormateada.substring(0, 1).toUpperCase() + fechaFormateada.substring(1);

        // Devolvemos la fecha formateada
        return fechaFormateada;
    }

    // Esto es para mostrar el DNI asociado al miembro en la tabla

    public <T> void configurarDniMiembro(TableColumn<T, Integer> columna) {
        columna.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer dni, boolean empty) {
                // Llamamos a la clase padre para cualquier inicializacion necesaria
                super.updateItem(dni, empty);
                // Verificamos si existe la celda
                if (empty) {
                    setText(null);
                } else {
                    T item = getTableView().getItems().get(getIndex());
                    setText(getString(item));
                }
            }

            private static <T> String getString(T item) {
                String dni = null;

                if (item instanceof Involucrado involucrado && involucrado.getMiembro() != null) {
                    // Si es diferente de nulo, mostramos el DNI correspodiente
                    dni = String.valueOf(involucrado.getMiembro().getDni());
                } else if (item instanceof Asistencia asistencia && asistencia.getMiembro() != null) {
                    // Si es diferente de nulo, mostramos el DNI correspodiente
                    dni = String.valueOf(asistencia.getMiembro().getDni());
                }
                return dni;
            }
        });
    }

    // Esto es para mostrar el cargo asociado al miembro en la tabla

    public <T> void configurarCargoMiembro(TableColumn<T, Cargo> columna) {
        columna.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Cargo cargo, boolean empty) {
                // Llamamos a la clase padre para cualquier inicializacion necesaria
                super.updateItem(cargo, empty);
                // Verificamos si existe la celda
                if (empty) {
                    setText(null);
                } else {
                    T item = getTableView().getItems().get(getIndex());
                    setText(getString(item));
                }
            }

            private static <T> String getString(T item) {
                String cargo = null;

                if (item instanceof Involucrado involucrado && involucrado.getMiembro() != null) {
                    // Si es diferente de nulo, mostramos el cargo correspodiente
                    cargo = involucrado.getMiembro().getCargo().toString();
                } else if (item instanceof Asistencia asistencia && asistencia.getMiembro() != null) {
                    // Si es diferente de nulo, mostramos el cargo correspodiente
                    cargo = asistencia.getMiembro().getCargo().toString();
                }
                return cargo;
            }
        });
    }

    // Esto es para mostrar el estado del miembro asociado en la tabla

    public <T> void configurarEstadoMiembro(TableColumn<T, EstadoMiembro> columna) {
        columna.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(EstadoMiembro estado, boolean empty) {
                // Llamamos a la clase padre para cualquier inicializacion necesaria
                super.updateItem(estado, empty);
                // Verificamos si existe la celda
                if (empty) {
                    setText(null);
                } else {
                    T item = getTableView().getItems().get(getIndex());
                    setText(getString(item));
                }
            }

            private static <T> String getString(T item) {
                String estado = null;

                if (item instanceof Involucrado involucrado && involucrado.getMiembro() != null) {
                    // Si es diferente de nulo, mostramos el estado correspodiente
                    estado = involucrado.getMiembro().getEstadoMiembro().toString();
                } else if (item instanceof Asistencia asistencia && asistencia.getMiembro() != null) {
                    estado = asistencia.getMiembro().getEstadoMiembro().toString();
                }
                return estado;
            }
        });
    }

    // Esto es para mostrar el ID del expdiente asociado en la tabla

    public <T> void configurarIdExpediente(TableColumn<T, Integer> columna) {
        columna.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer id, boolean empty) {
                // Llamamos a la clase padre para cualquier inicializacion necesaria
                super.updateItem(id, empty);
                // Verificamos si existe la celda
                if (empty) {
                    setText(null);
                } else {
                    T item = getTableView().getItems().get(getIndex());

                    if (item instanceof Revision revision) {
                        // Si es diferente de nulo, mostramos el ID correspodiente
                        assert revision.getExpediente() != null;
                        setText(String.valueOf(revision.getExpediente().getId()));
                    }
                }
            }
        });
    }

    // Esto es para mostrar la nota del expdiente asociado en la tabla

    public <T> void configurarNotaExpediente(TableColumn<T, String> columna) {
        columna.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String nota, boolean empty) {
                // Llamamos a la clase padre para cualquier inicializacion necesaria
                super.updateItem(nota, empty);
                // Verificamos si existe la celda
                if (empty) {
                    setText(null);
                } else {
                    T item = getTableView().getItems().get(getIndex());

                    if (item instanceof Revision revision) {
                        // Si es diferente de nulo, mostramos la nota correspodiente
                        assert revision.getExpediente() != null;
                        setText(revision.getExpediente().getTextoNota());
                    }
                }
            }
        });
    }

    // Esto es para mostrar la fecha de ingreso del expdiente asociado en la tabla

    public <T> void configurarFechaIngresoExpediente(TableColumn<T, LocalDate> columna) {
        columna.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate fechaIngreso, boolean empty) {
                // Llamamos a la clase padre para cualquier inicializacion necesaria
                super.updateItem(fechaIngreso, empty);
                // Verificamos si existe la celda
                if (empty) {
                    setText(null);
                } else {
                    T item = getTableView().getItems().get(getIndex());

                    if (item instanceof Revision revision) {
                        // Si es diferente de nulo, mostramos la fecha de ingreso correspodiente
                        assert revision.getExpediente() != null;
                        setText(formatearFecha(revision.getExpediente().getFechaIngreso()));
                    }
                }
            }
        });
    }

    // Esto es para mostrar el estado del expdiente asociado en la tabla

    public <T> void configurarEstadoExpediente(TableColumn<T, EstadoExpediente> columna) {
        columna.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(EstadoExpediente estado, boolean empty) {
                // Llamamos a la clase padre para cualquier inicializacion necesaria
                super.updateItem(estado, empty);
                // Verificamos si existe la celda
                if (empty) {
                    setText(null);
                } else {
                    T item = getTableView().getItems().get(getIndex());

                    if (item instanceof Revision revision) {
                        // Si es diferente de nulo, mostramos el estado correspodiente
                        assert revision.getExpediente() != null;
                        setText(revision.getExpediente().getEstadoExpediente().toString());
                    }
                }
            }
        });
    }

    // Metodo para redondear una imagen

    public void redondearImagen(ImageView imagen) {
        // Creamos un rectangulo con las dimensiones de la imagen para usarlo como clip y redondearla
        Rectangle clip = new Rectangle(
                imagen.getFitWidth(), imagen.getFitHeight()
        );

        // Redondeamos el rectangulo para darle la forma deseada
        clip.setArcWidth(50);
        clip.setArcHeight(50);

        // Aplicamos el rectangulo como clip para redondear la imagen
        imagen.setClip(clip);

        // Capturamos la imagen con el efecto de redondeo y le ponemos un color de fondo transparente
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        WritableImage imageRedondeada = imagen.snapshot(parameters, null);

        // Sacamos el clip para aplicar un efecto de sombra
        imagen.setClip(null);

        // Creamos un efecto de sombra para simular un borde
        imagen.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.GRAY, 1, 0.5, 0, 0));

        // Establecemos la imagen redondeada
        imagen.setImage(imageRedondeada);
    }

    // Metodos para interactuar con los selectores

    @FXML
    public void seleccionarReunion(ComboBox<Reunion> combo) throws Exception {
        // Cargamos FXML de SelectorReunion
        Parent rootNode = stageManager.loadView(FXMLView.SelectorReunion.getFxmlFile());

        // Verificamos si hay alguna reunión selecccionada
        if (combo.getValue() != null) {
            // Establecemos involucrado
            selectorReunionControlador.establecerReunion(combo.getValue());
        }

        // Abrimos modal
        stageManager.openModal(rootNode, FXMLView.SelectorReunion);

        // Obtenemos reunión seleccionada
        Reunion reunion = selectorReunionControlador.getReunion();

        // Verficamos que no sea nulo
        if (reunion != null) {
            // Seleccionamos la reunión en cuestion en el combo
            combo.setValue(reunion);
        }
    }

    @FXML
    public void seleccionarExpediente(ComboBox<Expediente> combo) throws Exception {
        // Cargamos FXML de SelectorExpediente
        Parent rootNode = stageManager.loadView(FXMLView.SelectorExpediente.getFxmlFile());

        // Verificamos si hay algún expediente selecccionado
        if (combo.getValue() != null) {
            // Establecemos reunion
            selectorExpedienteControlador.establecerExpediente(combo.getValue());
        }

        // Abrimos modal
        stageManager.openModal(rootNode, FXMLView.SelectorExpediente);

        // Obtenemos expediente seleccionado
        Expediente exp = selectorExpedienteControlador.getExpediente();

        // Verficamos que no sea nulo
        if (exp != null) {
            // Seleccionamos el expediente en cuestion en el combo
            combo.setValue(exp);
        }
    }

    public void seleccionarMiembro(ComboBox<Miembro> combo) {
        // Cargamos FXML de SelectorMiembro
        Parent rootNode = stageManager.loadView(FXMLView.SelectorMiembro.getFxmlFile());

        // Verificamos si hay algun iniciante selecccionado
        if (combo.getValue() != null) {
            // Establecemos miembro
            selectorMiembroControlador.establecerMiembro(combo.getValue());
        }

        // Abrimos modal
        stageManager.openModal(rootNode, FXMLView.SelectorMiembro);

        // Obtenemos miembro seleccionado
        Miembro m = selectorMiembroControlador.getMiembro();

        // Verficamos que no sea nulo
        if (m != null) {
            combo.setValue(m);
        }
    }

}
