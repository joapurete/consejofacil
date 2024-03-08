package com.java.consejofacil.service.EstadoExpediente;

import com.java.consejofacil.model.EstadoExpediente;
import com.java.consejofacil.repository.EstadoExpedienteRepository;
import com.java.consejofacil.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstadoExpedienteServiceImpl implements EstadoExpedienteService, CrudService<EstadoExpediente> {

    @Autowired
    private EstadoExpedienteRepository repository;

    @Override
    public EstadoExpediente save(EstadoExpediente entity) {
        return repository.save(entity);
    }

    @Override
    public EstadoExpediente update(EstadoExpediente entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(EstadoExpediente entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteById(Integer id) {

    }

    @Override
    public void deleteInBatch(List<EstadoExpediente> estadosExpedientes) {
        repository.deleteAll(estadosExpedientes);
    }

    @Override
    public EstadoExpediente findById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<EstadoExpediente> findAll()  { return repository.findAll(); }

}
