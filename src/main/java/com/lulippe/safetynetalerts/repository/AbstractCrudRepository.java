package com.lulippe.safetynetalerts.repository;

import java.util.List;
import java.util.Optional;

public abstract class AbstractCrudRepository<T> implements CrudRepository<T> {
    protected abstract List<T> getDataList();
    protected abstract String getFirstName(T entity);
    protected abstract String getLastName(T entity);

    @Override
    public Optional<T> findByFullName(String firstName, String lastName) {
        return getDataList().stream()
                .filter(e -> getFirstName(e).equals(firstName) && getLastName(e).equals(lastName))
                .findFirst();
    }

    @Override
    public void save(T entity) {
        findByFullName(getFirstName(entity), getLastName(entity))
                .ifPresentOrElse(existing -> {
                    int index = getDataList().indexOf(existing);
                    getDataList().set(index, entity);
                }, () -> getDataList().add(entity));
    }
    @Override
    public void delete(T entity) {
        getDataList().remove(entity);
    }
}
