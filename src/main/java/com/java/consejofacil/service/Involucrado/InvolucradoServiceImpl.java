package com.java.consejofacil.service.Involucrado;

import com.java.consejofacil.model.Expediente;
import com.java.consejofacil.model.Involucrado;
import com.java.consejofacil.repository.InvolucradoRepository;
import com.java.consejofacil.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvolucradoServiceImpl implements InvolucradoService, CrudService<Involucrado> {

    @Autowired
    private InvolucradoRepository repository;

    @Override
    public Involucrado save(Involucrado entity) {
        return repository.save(entity);
    }

    @Override
    public Involucrado update(Involucrado entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(Involucrado entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteById(Integer id) {

    }

    @Override
    public void deleteInBatch(List<Involucrado> involucrados) {
        repository.deleteAll(involucrados);
    }

    @Override
    public Involucrado findById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<Involucrado> findAll() {
        return repository.findAll();
    }

    @Override
    public List<Involucrado> encontrarInvolucradosPorExpediente(Expediente expediente) {
        return repository.findByExpediente(expediente);
    }

    @Override
    public long existeDuplicado(int id_expediente, int id_miembro) {
        return repository.existeDuplicado(id_expediente, id_miembro);
    }
}
