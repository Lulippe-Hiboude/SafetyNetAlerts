package com.lulippe.safetynetalerts.repository;

import com.lulippe.safetynetalerts.model.Person;
import com.lulippe.safetynetalerts.repository.manager.DataManager;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Data
@Repository
@AllArgsConstructor
public class PersonRepository {
    private DataManager dataManager;

    public Optional<Person> findPersonByFirstNameAndLastName(final String firstName, final String lastName) {
        return getPersons().stream()
                .filter(person -> person.getFirstName().equals(firstName) && person.getLastName().equals(lastName))
                .findFirst();
    }

    public void savePerson(final Person person) {
        Optional<Person> existingPersonOptional = findPersonByFirstNameAndLastName(person.getFirstName(), person.getLastName());
        existingPersonOptional.ifPresentOrElse(existingPerson -> {
                    int index = getPersons().indexOf(existingPerson);
                    getPersons().set(index, person);
                },
                () -> getPersons().add(person));
    }

    public void deletePerson(final Person person) {
        getPersons().remove(person);
    }

    private List<Person> getPersons() {
        return dataManager.getDataContainer().getPersons();
    }
}
