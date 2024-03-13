package com.java.consejofacil.service.Cargo;

import com.java.consejofacil.model.Cargo;

import java.util.List;

public interface CargoService {

    List<Cargo> encontrarCargosPorPrioridad(int prioridad);
}
