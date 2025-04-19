package com.lulippe.safetynetalerts.repository;

import java.util.Optional;

public interface CrudRepository <T>{
    Optional<T> findByFullName(final String firstName,final String lastName);
    void save(final T entity);
    void delete(final T entity);
}
