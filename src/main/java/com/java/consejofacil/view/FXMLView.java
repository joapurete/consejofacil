package com.java.consejofacil.view;

import java.util.ResourceBundle;

// Declaramos un enumerado con todas las vistas de la aplicacion
@SuppressWarnings("SpellCheckingInspection")
public enum FXMLView {

    // Cada vista tiene asociada una clave de título y una ruta de archivo FXML
    Login("log.key", "log.title", "Login"),
    MainLayout("main.key", "inicio.title", "MainLayout"),
    Inicio("inicio.key", "inicio.title", "Inicio"),


    ListaExpedientes("lstExp.key", "lstExp.title", "ABMExpediente/ListaExpedientes"),
    FormularioExpediente("frmExp.key", "frmExp.title", "ABMExpediente/FormularioExpediente"),
    SelectorExpediente("slcExp.key", "slcExp.title", "ABMExpediente/SelectorExpediente"),


    ListaReuniones("lstReu.key", "lstReu.title", "ABMReunion/ListaReuniones"),
    FormularioReunion("frmReu.key", "frmReu.title", "ABMReunion/FormularioReunion"),
    SelectorReunion("slcReu.key", "slcReu.title", "ABMReunion/SelectorReunion"),


    ListaAcciones("lstAcc.key", "lstAcc.title", "ABMAccion/ListaAcciones"),
    FormularioAccion("frmAcc.key", "frmAcc.title", "ABMAccion/FormularioAccion"),


    ListaMinutas("lstMin.key", "lstMin.title", "ABMMinuta/ListaMinutas"),
    FormularioMinuta("frmMin.key", "frmMin.title", "ABMMinuta/FormularioMinuta"),


    ListaInvolucrados("lstInv.key", "lstInv.title", "ABMInvolucrado/ListaInvolucrados"),
    FormularioInvolucrado("frm1Inv.key", "frmInv.title", "ABMInvolucrado/FormularioInvolucrado"),
    FormularioListaInvolucrado("frm2Inv.key", "frmInv.title", "ABMInvolucrado/FormularioListaInvolucrado"),


    ListaAsistencias("lstAsis.key", "lstAsis.title", "ABMAsistencia/ListaAsistencias"),
    FormularioAsistencia("frm1Asis.key", "frmAsis.title", "ABMAsistencia/FormularioAsistencia"),
    FormularioListaAsistencia("frm2Asis.key", "frmAsis.title", "ABMAsistencia/FormularioListaAsistencia"),


    ListaRevisiones("lstRev.key", "lstRev.title", "ABMRevision/ListaRevisiones"),
    FormularioRevision("frm1Rev.key", "frmRev.title", "ABMRevision/FormularioRevision"),
    FormularioListaRevision("frm2Rev.key", "frmRev.title", "ABMRevision/FormularioListaRevision"),


    SelectorMiembro("slcMie.key", "slcMie.title", "ABMMiembro/SelectorMiembro");

    // Devolvemos la clave
    private final String key;

    private final String title;

    private final String fxmlFile;

    FXMLView(String key, String titleKey, String fxmlFile) {
        this.key = key;
        this.title = titleKey;
        this.fxmlFile = fxmlFile;
    }

    // Devolvemos la clave asociada
    public String getKey() {
        return ((!key.isEmpty()) ? getStringFromResourceBundle(key) : "");
    }

    // Devolvemos el titulo completo de la vista
    public String getTitle() {
        return "ConsejoFacil | " + ((!title.isEmpty()) ? getStringFromResourceBundle(title) : "");
    }

    // Devolvemos la ruta completa del archivo FXML
    public String getFxmlFile() {
        return String.format("/fxml/%s.fxml", fxmlFile);
    }

    // Obtenemos el título de la vista del archivo de recursos utilizando la clave dada
    String getStringFromResourceBundle(String key) {
        return ResourceBundle.getBundle("views").getString(key);
    }
}

