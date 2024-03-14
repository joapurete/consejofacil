package com.java.consejofacil.controller.ABMMiembro;

import com.java.consejofacil.model.Miembro;
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
public class SelectorMiembroController extends BaseTablaMiembros implements Initializable {

    // Miembro
    private Miembro miembro;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Validamos el acceso del miembro
        getMiembroManager().validarAccesoMiembro();
        // Inicializamos la tabla de miembros
        getMiembroManager().inicializarTablaMiembros(this);
        // Inicializamos los filtros
        getMiembroManager().inicializarFiltros(this);

        // Establecemos en nulo al miembro
        miembro = null;
    }

    @FXML
    private void agregarMiembro() { getMiembroManager().agregarMiembroSelector(); }

    @FXML
    private void filtrarPorNombre() { getMiembroManager().filtrarMiembros(this); }

    @FXML
    private void filtrarPorCargo() { getMiembroManager().filtrarMiembros(this); }

    @FXML
    private void filtrarPorFechaNac() { getMiembroManager().filtrarMiembros(this); }

    @FXML
    private void filtrarPorEstado() { getMiembroManager().filtrarMiembros(this); }

    @FXML
    private void limpiarFiltros(){
        getMiembroManager().limpiarFiltros(this);
    }

    public void establecerMiembro(Miembro m) {
        // Establecemos el miembro
        this.miembro = m;

        if (miembro != null) {
            // Seleccionamos el miembro en cuestion en la tabla
            getMiembroManager().autoseleccionarMiembro();
        }
    }
}
