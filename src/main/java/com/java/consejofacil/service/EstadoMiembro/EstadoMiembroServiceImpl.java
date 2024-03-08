package com.java.consejofacil.service.EstadoMiembro;

import com.java.consejofacil.model.EstadoMiembro;
import com.java.consejofacil.repository.EstadoMiembroRepository;
import com.java.consejofacil.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstadoMiembroServiceImpl implements EstadoMiembroService, CrudService<EstadoMiembro> {

    @Autowired
    private EstadoMiembroRepository repository;

    @Override
    public EstadoMiembro save(EstadoMiembro entity) {
        return repository.save(entity);
    }

    @Override
    public EstadoMiembro update(EstadoMiembro entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(EstadoMiembro entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteById(Integer id) {

    }

    @Override
    public void deleteInBatch(List<EstadoMiembro> estadosMiembros) {
        repository.deleteAll(estadosMiembros);
    }

    @Override
    public EstadoMiembro findById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<EstadoMiembro> findAll()  { return repository.findAll(); }
}
