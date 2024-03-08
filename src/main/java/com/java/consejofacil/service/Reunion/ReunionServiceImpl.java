package com.java.consejofacil.service.Reunion;

import com.java.consejofacil.model.Reunion;
import com.java.consejofacil.repository.ReunionRepository;
import com.java.consejofacil.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReunionServiceImpl implements ReunionService, CrudService<Reunion> {
    @Autowired
    private ReunionRepository repository;

    @Override
    public Reunion save(Reunion entity) {
        return repository.save(entity);
    }

    @Override
    public Reunion update(Reunion entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(Reunion entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteById(Integer id) {
    }

    @Override
    public void deleteInBatch(List<Reunion> reuniones) {
        repository.deleteAll(reuniones);
    }

    @Override
    public Reunion findById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<Reunion> findAll() {
        return repository.findAll();
    }

    @Override
    public List<Reunion> encontrarReunionesHastaHoy() {
        return repository.encontrarReunionesHastaHoy();
    }
}
