package com.java.consejofacil.helper.Componentes;

import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;

public class ComponentHelper {

    // Metodos de configuracion para algunos componentes

    // Esto para agregar items que no estén dentro de la lista predefinida de items del combo

    public static <T> void configurarComboEditable(ComboBox<T> combo) {
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
}
