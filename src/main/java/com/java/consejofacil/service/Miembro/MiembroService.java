package com.java.consejofacil.service.Miembro;

import com.java.consejofacil.model.Miembro;
import java.util.List;

public interface MiembroService {

    List<Miembro> encontrarMiembrosActivos();

    int modificarDni(int dni_nuevo, int dni_actual);

    int cambiarContrasena(String clave, int dni_miembro);
}
