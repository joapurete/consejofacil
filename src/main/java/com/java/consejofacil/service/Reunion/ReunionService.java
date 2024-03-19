package com.java.consejofacil.service.Reunion;

import com.java.consejofacil.model.Reunion;
import java.util.List;

public interface ReunionService {

    List<Reunion> encontrarReunionesHastaHoy();

    List<Reunion> encontrarProximasReuniones(int limite);

    List<Reunion> encontrarUltimasReuniones(int limite);

    List<Object[]> contarCantidadRevAsisMinPorExpediente(int limite);
}
