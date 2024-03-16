package com.java.consejofacil.service.HistorialCambio;

import com.java.consejofacil.model.HistorialCambio;
import com.java.consejofacil.repository.HistorialCambioRepository;
import com.java.consejofacil.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistorialCambioServiceImpl implements HistorialCambioService, CrudService<HistorialCambio> {
    @Autowired
    private HistorialCambioRepository repository;

    @Override
    public HistorialCambio save(HistorialCambio entity) {
        return repository.save(entity);
    }

    @Override
    public HistorialCambio update(HistorialCambio entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(HistorialCambio entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteById(Integer id) {

    }

    @Override
    public void deleteInBatch(List<HistorialCambio> cambios) {
        repository.deleteAll(cambios);
    }

    @Override
    public HistorialCambio findById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<HistorialCambio> findAll() {
        return repository.findAll();
    }
}
