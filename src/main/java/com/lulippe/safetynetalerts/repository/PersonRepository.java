package com.lulippe.safetynetalerts.repository;

import com.lulippe.safetynetalerts.model.Person;
import com.lulippe.safetynetalerts.repository.manager.DataManager;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public Optional<List<Person>> getAllPersonsByAddress(final String address) {
        return Optional.of(dataManager.getDataContainer()
                .getPersons()
                .stream()
                .filter(p -> p.getAddress().equals(address))
                .toList());
    }
}
