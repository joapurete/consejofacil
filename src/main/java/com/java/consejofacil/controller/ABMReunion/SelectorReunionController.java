package com.java.consejofacil.controller.ABMReunion;

import com.java.consejofacil.model.Reunion;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import java.net.URL;
import java.util.ResourceBundle;

@Setter
@Getter
@Controller
public class SelectorReunionController extends BaseTablaReuniones implements Initializable {

    // Miembro
    private Reunion reunion;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicializamos la tabla de reuniones
        getReunionManager().inicializarTablaReuniones(this);

        // Establecemos en nulo a la reunión
        reunion = null;
    }

    @FXML
    private void agregarReunion() {
        getReunionManager().agregarReunionSelector();
    }

    @FXML
    private void filtrarPorTexto() { getReunionManager().filtrarReuniones(this); }

    @FXML
    private void filtrarPorFechaReunion() { getReunionManager().filtrarReuniones(this); }

    @FXML
    private void limpiarFiltros() { getReunionManager().limpiarFiltros(this); }

    public void establecerReunion(Reunion r) {
        // Establecemos la reunión
        this.reunion = r;

        if (reunion != null) {
            // Seleccionamos la reunión en cuestion en la tabla
            getReunionManager().autoseleccionarReunion();
        }
    }
}