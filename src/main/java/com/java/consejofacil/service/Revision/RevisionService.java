package com.java.consejofacil.service.Revision;

import com.java.consejofacil.model.Reunion;
import com.java.consejofacil.model.Revision;

import java.util.List;

public interface RevisionService {

    List<Revision> encontrarRevisionesPorReunion(Reunion reunion);

    long existeDuplicado(int id_reunion, int id_expediente);
}
