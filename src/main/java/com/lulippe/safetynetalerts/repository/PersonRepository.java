package com.lulippe.safetynetalerts.repository;

import com.lulippe.safetynetalerts.model.Person;
import com.lulippe.safetynetalerts.repository.manager.DataManager;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Data
@Repository
@AllArgsConstructor
public class PersonRepository {
    private DataManager dataManager;

    public Optional<Person> getPersonByFirstNameAndLastName(final String firstName, final String lastName) {
        return dataManager.getDataContainer().getPersons().stream()
                .filter(person -> person.getFirstName().equals(firstName) && person.getLastName().equals(lastName))
                .findFirst();
    }

    public void savePerson(Person person) {
        dataManager.getDataContainer().getPersons().add(person);
    }
}
