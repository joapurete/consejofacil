package com.java.consejofacil.helper.Validaciones;

import java.util.regex.Pattern;

public class DataValidatorHelper {

    // Regex utilizados para validar diferentes campos
    private static final String regexDni = "^\\d{7,8}$";
    private static final String regexTelefono = "^\\+?\\d{0,3}[-.\\s]?\\d?[-.\\s]?\\(?\\d{2,4}\\)?[-.\\s]?\\d{2,4}[-\\s]?\\d{4}$";
    private static final String regexCorreo = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}(\\.[A-Za-z]{2,})?$";
    private static final String regexContrasena = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])\\S{6,}$";
    private static final String regexDireccion = "^\\d+\\s+[a-zA-ZÁáÉéÍíÓóÚúÜü]+(?:\\s+[a-zA-ZÁáÉéÍíÓóÚúÜü]+)*$";
    private static final String regexNombreApellido = "^[A-Za-zÁáÉéÍíÓóÚúÜü]+(?:\\s+[A-Za-zÁáÉéÍíÓóÚúÜü]+)*$";

    // Metodo para validar un DNI
    public static boolean validarDni(String dni){
        return !Pattern.matches(regexDni, dni);
    }

    // Metodo para validar un telefono
    public  static boolean validarTelefono(String telefono) {
        return !Pattern.matches(regexTelefono, telefono);
    }

    // Metodo para validar un correo electronico
    public static boolean validarCorreo(String correo) {
        return !Pattern.matches(regexCorreo, correo);
    }

    // Metodo para validar una contrasena
    public static boolean validarContrasena(String contrasena) {
        return !Pattern.matches(regexContrasena, contrasena);
    }

    // Metodo para validar una direccion
    public static boolean validarDireccion(String direccion) {
        return !Pattern.matches(regexDireccion, direccion);
    }

    // Metodo para validar un nombre o apellido
    public static boolean validarNombreApellido(String nombre) {
        return !Pattern.matches(regexNombreApellido, nombre);
    }
}
