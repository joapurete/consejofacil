package com.java.consejofacil.service.TipoCambio;

import com.java.consejofacil.model.TipoCambio;
import com.java.consejofacil.repository.TipoCambioRepository;
import com.java.consejofacil.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TipoCambioServiceImpl implements TipoCambioService, CrudService<TipoCambio> {

    @Autowired
    private TipoCambioRepository repository;

    @Override
    public TipoCambio save(TipoCambio entity) {
        return repository.save(entity);
    }

    @Override
    public TipoCambio update(TipoCambio entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(TipoCambio entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteById(Integer id) {

    }

    @Override
    public void deleteInBatch(List<TipoCambio> tiposCambios) {
        repository.deleteAll(tiposCambios);
    }

    @Override
    public TipoCambio findById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<TipoCambio> findAll()  { return repository.findAll(); }
}