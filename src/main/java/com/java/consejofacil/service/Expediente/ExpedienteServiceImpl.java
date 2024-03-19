package com.java.consejofacil.service.Expediente;

import com.java.consejofacil.model.Expediente;
import com.java.consejofacil.repository.ExpedienteRepository;
import com.java.consejofacil.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpedienteServiceImpl implements ExpedienteService, CrudService<Expediente> {
    @Autowired
    private ExpedienteRepository repository;

    @Override
    public Expediente save(Expediente entity) {
        return repository.save(entity);
    }

    @Override
    public Expediente update(Expediente entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(Expediente entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteById(Integer id) {

    }

    @Override
    public void deleteInBatch(List<Expediente> expedientes) {
        repository.deleteAll(expedientes);
    }

    @Override
    public Expediente findById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<Expediente> findAll() {
        return repository.findAll();
    }

    @Override
    public List<Expediente> encontrarExpedientesAbiertos() {
        return repository.findByestadoExpediente_estadoExpediente("Abierto");
    }

    @Override
    public List<Object[]> contarCantidadExpedientesPorEstado() { return repository.contarCantidadExpedientesPorEstado(); }

    @Override
    public List<Expediente> encontrarUltimosExpedientes(int limite) { return repository.encontrarUltimosExpedientes(limite); }

    @Override
    public List<Object[]> contarCantidadInvAccPorExpediente(int limite) { return repository.contarCantidadInvAccPorExpediente(limite); }
}
