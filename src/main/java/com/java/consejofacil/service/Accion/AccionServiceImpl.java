package com.java.consejofacil.service.Accion;

import com.java.consejofacil.model.Accion;
import com.java.consejofacil.repository.AccionRepository;
import com.java.consejofacil.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccionServiceImpl implements AccionService, CrudService<Accion> {

    @Autowired
    private AccionRepository repository;

    @Override
    public Accion save(Accion entity) {
        return repository.save(entity);
    }

    @Override
    public Accion update(Accion entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(Accion entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteById(Integer id) {

    }

    @Override
    public void deleteInBatch(List<Accion> acciones) {
        repository.deleteAll(acciones);
    }

    @Override
    public Accion findById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<Accion> findAll() {
        return repository.findAll();
    }
}
