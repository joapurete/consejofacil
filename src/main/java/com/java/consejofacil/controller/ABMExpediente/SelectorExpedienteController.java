package com.java.consejofacil.controller.ABMExpediente;

import com.java.consejofacil.model.Expediente;
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
public class SelectorExpedienteController extends BaseTablaExpedientes implements Initializable {

    // Expediente
    private Expediente expediente;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicializamos la tabla de expedientes
        getExpedienteManager().inicializarTablaExpedientes(this);
        // Inicializamos los filtros
        getExpedienteManager().inicializarFiltros(this);

        // Establecemos en nulo al expediente
        expediente = null;
    }

    @FXML
    private void agregarExpediente() { getExpedienteManager().agregarExpedienteSelector(); }

    @FXML
    private void filtrarPorNota() { getExpedienteManager().filtrarExpedientes(this); }

    @FXML
    private void filtrarPorIniciante() { getExpedienteManager().filtrarExpedientes(this); }

    @FXML
    private void filtrarPorFechaIngreso() { getExpedienteManager().filtrarExpedientes(this); }

    @FXML
    private void filtrarPorEstado() { getExpedienteManager().filtrarExpedientes(this); }

    @FXML
    private void limpiarFiltros(){
        getExpedienteManager().limpiarFiltros(this);
    }

    @FXML
    private void seleccionarIniciante() throws Exception { getExpedienteManager().seleccionarMiembro(getCmbIniciante()); }

    public void establecerExpediente(Expediente exp) {
        // Establecemos el expediente
        this.expediente = exp;

        if (expediente != null) {
            // Seleccionamos el expediente en cuestion en la tabla
            getExpedienteManager().autoseleccionarExpediente();
        }
    }
}
