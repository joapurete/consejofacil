package com.java.consejofacil.service.Miembro;

import com.java.consejofacil.model.Miembro;
import com.java.consejofacil.repository.MiembroRepository;
import com.java.consejofacil.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MiembroServiceImpl implements MiembroService, CrudService<Miembro> {

    @Autowired
    private MiembroRepository repository;

    @Override
    public Miembro save(Miembro entity) {
        return repository.save(entity);
    }

    @Override
    public Miembro update(Miembro entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(Miembro entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteById(Integer id) {

    }

    @Override
    public void deleteInBatch(List<Miembro> miembros) {
        repository.deleteAll(miembros);
    }

    @Override
    public Miembro findById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<Miembro> findAll()  { return repository.findAll(); }

    @Override
    public List<Miembro> encontrarMiembrosActivos() {
        return repository.findByestadoMiembro_estadoMiembro("Activo");
    }

    @Override
    @Transactional
    public int modificarDni(int dni_nuevo, int dni_actual) { return repository.modificarDni(dni_nuevo, dni_actual); }

    @Override
    @Transactional
    public int cambiarContrasena(String clave, int dni_miembro){ return repository.cambiarContrasena(clave, dni_miembro); }

    @Override
    public List<Object[]> contarCantidadMiembrosPorEstado() { return repository.contarCantidadMiembrosPorEstado(); }
}
