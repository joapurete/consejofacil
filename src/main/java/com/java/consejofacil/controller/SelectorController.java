package com.java.consejofacil.controller;

import com.java.consejofacil.config.StageManager;
import com.java.consejofacil.controller.ABMExpediente.SelectorExpedienteController;
import com.java.consejofacil.controller.ABMMiembro.SelectorMiembroController;
import com.java.consejofacil.controller.ABMReunion.SelectorReunionController;
import com.java.consejofacil.model.Expediente;
import com.java.consejofacil.model.Miembro;
import com.java.consejofacil.model.Reunion;
import com.java.consejofacil.view.FXMLView;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

public class SelectorController {

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

    public SelectorController() {}

    // Metodos para interactuar con los selectores

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
