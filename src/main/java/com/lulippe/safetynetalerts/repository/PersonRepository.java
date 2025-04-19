package com.lulippe.safetynetalerts.repository;

import com.lulippe.safetynetalerts.model.Person;
import com.lulippe.safetynetalerts.repository.manager.DataManager;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class PersonRepository extends AbstractCrudRepository<Person> {
    private DataManager dataManager;

    @Override
    protected List<Person> getDataList() {
        return dataManager.getDataContainer().getPersons();
    }

    @Override
    protected String getFirstName(Person entity) {
        return entity.getFirstName();
    }

    @Override
    protected String getLastName(Person entity) {
        return entity.getLastName();
    }
}
