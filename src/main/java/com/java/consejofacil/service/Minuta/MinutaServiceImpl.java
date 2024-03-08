package com.java.consejofacil.service.Minuta;

import com.java.consejofacil.model.Minuta;
import com.java.consejofacil.repository.MinutaRepository;
import com.java.consejofacil.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MinutaServiceImpl implements MinutaService, CrudService<Minuta> {

    @Autowired
    private MinutaRepository repository;

    @Override
    public Minuta save(Minuta entity) {
        return repository.save(entity);
    }

    @Override
    public Minuta update(Minuta entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(Minuta entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteById(Integer id) {

    }

    @Override
    public void deleteInBatch(List<Minuta> minutas) {
        repository.deleteAll(minutas);
    }

    @Override
    public Minuta findById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<Minuta> findAll() {
        return repository.findAll();
    }
}
