package com.lulippe.safetynetalerts.repository;

import com.lulippe.safetynetalerts.dao.DataContainer;
import com.lulippe.safetynetalerts.model.Person;
import com.lulippe.safetynetalerts.repository.manager.DataManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PersonRepositoryTest {
    @InjectMocks
    private PersonRepository personRepository;

    @Mock
    private DataManager dataManager;

    @Test
    @DisplayName("should return a person")
    void shouldReturnAPerson() {
        DataContainer dataContainer = initializeDataContainer();
        given(dataManager.getDataContainer()).willReturn(dataContainer);
        String firstName = "John";
        String lastName = "Doe";
        String email = "john@doe.com";
        //when
        Optional<Person> expectedPerson = personRepository.getPersonByFirstNameAndLastName(firstName, lastName);

        //then
        assertTrue(expectedPerson.isPresent());
        assertEquals(expectedPerson.get().getFirstName(), firstName);
        assertEquals(expectedPerson.get().getLastName(), lastName);
        assertEquals(expectedPerson.get().getEmail(), email);
    }

    @Test
    @DisplayName("should return null if no person found")
    void shouldReturnNullIfNoPersonFound() {
        Person person = new Person();
        person.setFirstName("John");
        person.setLastName("Doe");
        person.setEmail("john@doe.com");
        List<Person> personList = new ArrayList<>();
        personList.add(person);
        DataContainer dataContainer = new DataContainer();
        dataContainer.setPersons(personList);
        given(dataManager.getDataContainer()).willReturn(dataContainer);

        //when
        Optional<Person> expectedPerson = personRepository.getPersonByFirstNameAndLastName("Jane", "Doe");
        assertTrue(expectedPerson.isEmpty());
    }

    private DataContainer initializeDataContainer() {
        Person person = new Person();
        person.setFirstName("John");
        person.setLastName("Doe");
        person.setEmail("john@doe.com");
        List<Person> personList = new ArrayList<>();
        personList.add(person);
        DataContainer dataContainer = new DataContainer();
        dataContainer.setPersons(personList);
        return dataContainer;
    }

}