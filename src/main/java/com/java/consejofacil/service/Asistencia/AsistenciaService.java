package com.java.consejofacil.service.Asistencia;

import com.java.consejofacil.model.Asistencia;
import com.java.consejofacil.model.Reunion;

import java.util.List;

public interface AsistenciaService {

    List<Asistencia> encontrarAsistenciasPorReunion(Reunion reunion);

    long existeDuplicado(int id_reunion, int id_miembro);
}
