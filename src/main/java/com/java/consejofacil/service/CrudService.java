package com.java.consejofacil.service;

import java.util.List;

public interface CrudService<T>{

    T save (T entity);

    T update(T entity);

    void delete(T entity);

    void deleteById(Integer id);

    void deleteInBatch(List<T> entity);

    T findById(Integer id);

    List<T> findAll();

}
