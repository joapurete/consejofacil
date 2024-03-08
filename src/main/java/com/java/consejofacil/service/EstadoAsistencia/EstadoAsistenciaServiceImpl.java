package com.java.consejofacil.service.EstadoAsistencia;

import com.java.consejofacil.model.EstadoAsistencia;
import com.java.consejofacil.repository.EstadoAsistenciaRepository;
import com.java.consejofacil.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstadoAsistenciaServiceImpl implements EstadoAsistenciaService, CrudService<EstadoAsistencia> {

    @Autowired
    private EstadoAsistenciaRepository repository;

    @Override
    public EstadoAsistencia save(EstadoAsistencia entity) {
        return repository.save(entity);
    }

    @Override
    public EstadoAsistencia update(EstadoAsistencia entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(EstadoAsistencia entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteById(Integer id) {

    }

    @Override
    public void deleteInBatch(List<EstadoAsistencia> estadosAsistencias) {
        repository.deleteAll(estadosAsistencias);
    }

    @Override
    public EstadoAsistencia findById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<EstadoAsistencia> findAll()  { return repository.findAll(); }

}
