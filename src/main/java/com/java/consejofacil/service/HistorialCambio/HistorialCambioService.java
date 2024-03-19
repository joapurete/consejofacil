package com.java.consejofacil.service.HistorialCambio;
import com.java.consejofacil.model.HistorialCambio;
import java.util.List;

public interface HistorialCambioService {

    List<HistorialCambio> encontrarUltimosCambios(int limite);
}
