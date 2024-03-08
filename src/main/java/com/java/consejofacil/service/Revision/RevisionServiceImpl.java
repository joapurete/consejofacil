package com.java.consejofacil.service.Revision;

import com.java.consejofacil.model.Reunion;
import com.java.consejofacil.model.Revision;
import com.java.consejofacil.repository.RevisionRepository;
import com.java.consejofacil.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RevisionServiceImpl implements RevisionService, CrudService<Revision> {

    @Autowired
    private RevisionRepository repository;

    @Override
    public Revision save(Revision entity) {
        return repository.save(entity);
    }

    @Override
    public Revision update(Revision entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(Revision entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteById(Integer id) {

    }

    @Override
    public void deleteInBatch(List<Revision> revisiones) {
        repository.deleteAll(revisiones);
    }

    @Override
    public Revision findById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<Revision> findAll() {
        return repository.findAll();
    }

    @Override
    public List<Revision> encontrarRevisionesPorReunion(Reunion reunion) {
        return repository.findByReunion(reunion);
    }

    @Override
    public long existeDuplicado(int id_reunion, int id_expediente) {
        return repository.existeDuplicado(id_reunion, id_expediente);
    }
}
