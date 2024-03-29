package com.java.consejofacil.service.Expediente;

import com.java.consejofacil.model.Expediente;
import java.util.List;

public interface ExpedienteService {

    List<Expediente> encontrarExpedientesAbiertos();

    List<Object[]> contarCantidadExpedientesPorEstado();

    List<Expediente> encontrarUltimosExpedientes(int limite);

    List<Object[]> contarCantidadInvAccPorExpediente(int limite);
}
