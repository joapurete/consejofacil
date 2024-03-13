package com.java.consejofacil.helper.Utilidades;

import com.java.consejofacil.model.Asistencia;
import com.java.consejofacil.model.Involucrado;
import com.java.consejofacil.model.Revision;

import java.util.Map;

// Esta clase contiene estados y funciones utilizadas para trabajar con listas, unicamente en modulos de entidades debiles
public class ListHelper {

    // Flags utilizados para seleccionar elementos de una lista
    public static final int FLAG_NUEVO = 0; // Representa un nuevo elemento
    public static final int FLAG_MODIFICAR = 1; // Representa un elemento a modificar
    public static final int FLAG_ELIMINAR = 2; // Representa un elemento a eliminar

    // Esto es para obtener la key de la lista a partir del item seleccionado

    public static <T> Object getKey(T item) {
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

    // Esto es para seleccionar un item de la lista

    public static <T> void seleccionarItem(T item, Map<T, Integer> lista, Boolean newValue) {

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
}
