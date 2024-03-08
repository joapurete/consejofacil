package com.java.consejofacil.service.Involucrado;

import com.java.consejofacil.model.Expediente;
import com.java.consejofacil.model.Involucrado;

import java.util.List;

public interface InvolucradoService {

    List<Involucrado> encontrarInvolucradosPorExpediente(Expediente expediente);

    long existeDuplicado(int id_expediente, int id_miembro);
}
