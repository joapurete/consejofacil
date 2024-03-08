package com.java.consejofacil.service.Cargo;

import com.java.consejofacil.model.Cargo;
import com.java.consejofacil.repository.CargoRepository;
import com.java.consejofacil.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CargoServiceImpl implements CargoService, CrudService<Cargo> {

    @Autowired
    private CargoRepository repository;

    @Override
    public Cargo save(Cargo entity) {
        return repository.save(entity);
    }

    @Override
    public Cargo update(Cargo entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(Cargo entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteById(Integer id) {

    }

    @Override
    public void deleteInBatch(List<Cargo> cargos) {
        repository.deleteAll(cargos);
    }

    @Override
    public Cargo findById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<Cargo> findAll()  { return repository.findAll(); }

}
