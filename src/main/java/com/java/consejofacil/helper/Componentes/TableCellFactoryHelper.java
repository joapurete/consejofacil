package com.java.consejofacil.helper.Componentes;

import com.java.consejofacil.helper.Utilidades.DateFormatterHelper;
import com.java.consejofacil.helper.Utilidades.ListHelper;
import com.java.consejofacil.model.*;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DefaultStringConverter;
import java.time.LocalDate;
import java.util.Map;

public class TableCellFactoryHelper {

    // Metodos de configuracion para algunos componentes

    // Esto es para configurar fabricas de celdas

    // Esto para configurar un CheckBox dentro de una tabla, y poder seleccionar y agregar el item asociado a una lista

    public static <T, S> void configurarCeldaCheck(TableColumn<T, Boolean> columna, Map<S, Integer> lista) {
        columna.setCellValueFactory(features -> {
            T item = features.getValue();
            // Obtenemos la key a partir del item seleccionado
            S key = (S) ListHelper.getKey(item);

            // El CheckBox se seleccionará si key está contenido dentro de la lista
            return new SimpleBooleanProperty(lista.containsKey(key) &&
                    lista.get(key) != ListHelper.FLAG_ELIMINAR);
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
                    S key = (S) ListHelper.getKey(item);

                    // Si key es diferente de nulo, seleccionamos el item con la key asociada
                    if (key != null) {
                        ListHelper.seleccionarItem(key, lista, newValue);
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

    // Esto es para configurar un TextField dentro de una tabla

    public static <T> void configurarCeldaTextField(TableColumn<T, String> columna) {

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
                    if (item.isEmpty()) { textField.setPromptText("Opcional"); }
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

    public static <T> void configurarCeldaComboEstadoAsistencia(TableColumn<T, EstadoAsistencia> columna, ObservableList<EstadoAsistencia> opciones) {

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

    public static void configurarCeldaNombreCompleto(TableColumn<Miembro, String> columna) {
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

    public static <T> void configurarCeldaFecha(TableColumn<T, LocalDate> columna) {
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
                        setText(!empty ? "--" : null);
                    } else {
                        // Si es diferente de nula, formateamos la fecha antes de mostrarla en la celda
                        setText(DateFormatterHelper.formatearFecha(fecha));
                    }
                }
            };
        });
    }

    // Esto es para mostrar el DNI asociado al miembro en la tabla

    public static <T> void configurarCeldaDniMiembro(TableColumn<T, Integer> columna) {
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

    public static <T> void configurarCeldaCargoMiembro(TableColumn<T, Cargo> columna) {
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

    public static <T> void configurarCeldaEstadoMiembro(TableColumn<T, EstadoMiembro> columna) {
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

    public static <T> void configurarCeldaIdExpediente(TableColumn<T, Integer> columna) {
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

    public static <T> void configurarCeldaNotaExpediente(TableColumn<T, String> columna) {
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

    public static <T> void configurarCeldaFechaIngresoExpediente(TableColumn<T, LocalDate> columna) {
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
                        setText(DateFormatterHelper.formatearFecha(revision.getExpediente().getFechaIngreso()));
                    }
                }
            }
        });
    }

    // Esto es para mostrar el estado del expdiente asociado en la tabla

    public static <T> void configurarCeldaEstadoExpediente(TableColumn<T, EstadoExpediente> columna) {
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

}
