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
        Optional<Person> expectedPerson = personRepository.findPersonByFirstNameAndLastName(firstName, lastName);

        //then
        assertTrue(expectedPerson.isPresent());
        assertEquals(firstName, expectedPerson.get().getFirstName());
        assertEquals(lastName, expectedPerson.get().getLastName());
        assertEquals(email, expectedPerson.get().getEmail());
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
        Optional<Person> expectedPerson = personRepository.findPersonByFirstNameAndLastName("Jane", "Doe");
        assertTrue(expectedPerson.isEmpty());
    }

    @Test
    @DisplayName("should save a new person")
    void shouldSaveANewPerson() {
        //given
        DataContainer dataContainer = initializeDataContainer();
        given(dataManager.getDataContainer()).willReturn(dataContainer);

        Person newPerson = Person.builder()
                .firstName("Jane")
                .lastName("Doe").build();
        //when
        personRepository.savePerson(newPerson);

        //then
        assertEquals(2, dataContainer.getPersons().size());
        assertTrue(dataContainer.getPersons().contains(newPerson));
    }

    @Test
    @DisplayName("should update an existing person")
    void shouldUpdateAnExistingPerson() {
        //given
        DataContainer dataContainer = initializeDataContainer();
        given(dataManager.getDataContainer()).willReturn(dataContainer);

        Person updatingPerson = Person.builder()
                .firstName("John")
                .lastName("Doe")
                .email("j@doe.com")
                .build();

        //when
        personRepository.savePerson(updatingPerson);

        //then
        assertEquals(1, dataContainer.getPersons().size());
        assertTrue(dataContainer.getPersons().contains(updatingPerson));
        assertEquals(updatingPerson.getEmail(), dataContainer.getPersons().getFirst().getEmail());

    }

    @Test
    @DisplayName("should delete an existing person")
    void shouldDeleteAnExistingPerson() {
        //given
        DataContainer dataContainer = initializeDataContainer();
        given(dataManager.getDataContainer()).willReturn(dataContainer);
        Person personToDelete = Person.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@doe.com")
                .build();

        //when
        personRepository.deletePerson(personToDelete);

        //then
        assertEquals(0, dataContainer.getPersons().size());
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