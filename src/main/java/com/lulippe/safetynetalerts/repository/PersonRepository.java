package com.lulippe.safetynetalerts.repository;

import com.lulippe.safetynetalerts.exception.entity_exception.InexistantObjectException;
import com.lulippe.safetynetalerts.model.Person;
import com.lulippe.safetynetalerts.repository.manager.DataManager;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class PersonRepository extends AbstractCrudRepository<Person> {
    private DataManager dataManager;

    @Override
    protected List<Person> getDataList() {
        return dataManager.getDataContainer().getPersons();
    }

    @Override
    protected String getFirstName(final Person entity) {
        return entity.getFirstName();
    }

    @Override
    protected String getLastName(final Person entity) {
        return entity.getLastName();
    }

    public List<Person> getAllPersonsByAddress(final String address) {
        return dataManager.getDataContainer()
                .getPersons()
                .stream()
                .filter(p -> p.getAddress().equals(address))
                .toList();
    }

    public Optional<List<Person>> getAllPersonsByCity(final String city) {
        return Optional.of(
                dataManager.getDataContainer()
                        .getPersons()
                        .stream()
                        .filter(p -> p.getCity().equals(city))
                        .toList()
        ).filter(list -> !list.isEmpty());
    }

    public Optional<List<Person>> getAllPersonsByLastName(String lastName) {
        return Optional.of(
                dataManager.getDataContainer()
                        .getPersons()
                        .stream()
                        .filter(p -> p.getLastName().equals(lastName))
                        .toList()
        ).filter(list -> !list.isEmpty());
    }
}
