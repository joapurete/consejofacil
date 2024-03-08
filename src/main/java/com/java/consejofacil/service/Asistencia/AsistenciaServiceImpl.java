package com.java.consejofacil.service.Asistencia;

import com.java.consejofacil.model.Asistencia;
import com.java.consejofacil.model.Reunion;
import com.java.consejofacil.repository.AsistenciaRepository;
import com.java.consejofacil.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AsistenciaServiceImpl implements AsistenciaService, CrudService<Asistencia> {

    @Autowired
    private AsistenciaRepository repository;

    @Override
    public Asistencia save(Asistencia entity) {
        return repository.save(entity);
    }

    @Override
    public Asistencia update(Asistencia entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(Asistencia entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteById(Integer id) {

    }

    @Override
    public void deleteInBatch(List<Asistencia> asistencias) {
        repository.deleteAll(asistencias);
    }

    @Override
    public Asistencia findById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<Asistencia> findAll() {
        return repository.findAll();
    }

    @Override
    public List<Asistencia> encontrarAsistenciasPorReunion(Reunion reunion) {
        return repository.findByReunion(reunion);
    }

    @Override
    public long existeDuplicado(int id_reunion, int id_miembro) {
        return repository.existeDuplicado(id_reunion, id_miembro);
    }

}
