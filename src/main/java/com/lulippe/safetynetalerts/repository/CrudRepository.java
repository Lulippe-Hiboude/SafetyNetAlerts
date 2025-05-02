package com.lulippe.safetynetalerts.repository;

import java.util.Optional;

public interface CrudRepository <T>{
    void save(final T entity);
    void delete(final T entity);
}
