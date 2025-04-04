package com.lulippe.safetynetalerts.service;

import com.lulippe.safetynetalerts.dto.PersonDto;
import com.lulippe.safetynetalerts.exception.person_exception.InexistantPersonException;
import com.lulippe.safetynetalerts.exception.person_exception.InvalidPersonDataException;
import com.lulippe.safetynetalerts.exception.person_exception.PersonAlreadyExistsException;
import com.lulippe.safetynetalerts.model.Person;
import com.lulippe.safetynetalerts.repository.PersonRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {
    @InjectMocks
    private PersonService personService;

    @Mock
    private PersonRepository personRepository;

    @Captor
    private ArgumentCaptor<Person> personArgumentCaptor;

    @Test
    @DisplayName("should create a person")
    void shouldCreatePerson() {
        //given
        PersonDto personCreateDto = PersonDto.builder()
                .firstName("John")
                .lastName("Doe")
                .address("45 rue de la Paix")
                .city("Paris")
                .zip("75001")
                .phone("1234567890")
                .email("john@doe.com")
                .build();

        given(personRepository.findPersonByFirstNameAndLastName(personCreateDto.getFirstName(), personCreateDto.getLastName())).willReturn(Optional.empty());

        //when
        personService.handlePersonCreationRequest(personCreateDto);

        //then
        verify(personRepository, (times(1))).savePerson(personArgumentCaptor.capture());
        Person personSaved = personArgumentCaptor.getValue();
        assertEquals("John", personSaved.getFirstName());
        assertEquals("Doe", personSaved.getLastName());
        assertEquals("45 rue de la Paix", personSaved.getAddress());
        assertEquals("75001", personSaved.getZip());
        assertEquals("Paris", personSaved.getCity());
        assertEquals("1234567890", personSaved.getPhone());
        assertEquals("john@doe.com", personSaved.getEmail());
    }

    @Test
    @DisplayName("should throw InvalidPersonDataException if PersonCreateDTO is null")
    void shouldThrowInvalidPersonDataExceptionIfPersonCreateDTOIsNull() {
        //when & then
        assertThrows(InvalidPersonDataException.class, () -> personService.handlePersonCreationRequest(null));
        verify(personRepository, times(0)).findPersonByFirstNameAndLastName(any(), any());
        verify(personRepository, (times(0))).savePerson(any());
    }

    @Test
    @DisplayName("should throw PersonAlreadyExistsException if person already exists")
    void shouldThrowPersonAlreadyExistsExceptionIfPersonAlreadyExists() {
        //given
        PersonDto personCreateDto = PersonDto.builder()
                .firstName("John")
                .lastName("Doe")
                .address("45 rue de la Paix")
                .city("Paris")
                .zip("75001")
                .phone("1234567890")
                .email("john@doe.com")
                .build();

        Person existingPerson = Person.builder()
                .firstName("John")
                .lastName("Doe")
                .address("45 rue de la Paix")
                .city("Paris")
                .zip("75001")
                .phone("1234567890")
                .email("john@doe.com")
                .build();
        given(personRepository.findPersonByFirstNameAndLastName(personCreateDto.getFirstName(), personCreateDto.getLastName())).willReturn(Optional.ofNullable(existingPerson));

        //when & then
        assertThrows(PersonAlreadyExistsException.class, () -> personService.handlePersonCreationRequest(personCreateDto));
        verify(personRepository, times(0)).savePerson(any());
    }

    @Test
    @DisplayName("Should throw InvalidPersonDataException if firstName is null")
    void shouldThrowInvalidPersonDataExceptionIfFirstNameIsNull() {
        //given
        PersonDto personCreateDto = PersonDto.builder()
                .firstName(null)
                .lastName("Doe")
                .address("45 rue de la Paix")
                .city("Paris")
                .zip("75001")
                .phone("1234567890")
                .email("john@doe.com")
                .build();

        //when & then
        assertThrows(InvalidPersonDataException.class, () -> personService.handlePersonCreationRequest(personCreateDto));
        verify(personRepository, times(0)).savePerson(any());
    }

    @Test
    @DisplayName("Should throw InvalidPersonDataException if lastName is null")
    void shouldThrowInvalidPersonDataExceptionIfLastNameIsNull() {
        //given
        PersonDto personCreateDto = PersonDto.builder()
                .firstName("John")
                .lastName(null)
                .address("45 rue de la Paix")
                .city("Paris")
                .zip("75001")
                .phone("1234567890")
                .email("john@doe.com")
                .build();

        //when & then
        assertThrows(InvalidPersonDataException.class, () -> personService.handlePersonCreationRequest(personCreateDto));
        verify(personRepository, times(0)).savePerson(any());
    }

    @Test
    @DisplayName("Should throw InvalidPersonDataException if firstName is empty")
    void shouldThrowInvalidPersonDataExceptionIfFirstNameIsEmpty() {
        //given
        PersonDto personCreateDto = PersonDto.builder()
                .firstName("  ")
                .lastName("Doe")
                .address("45 rue de la Paix")
                .city("Paris")
                .zip("75001")
                .phone("1234567890")
                .email("john@doe.com")
                .build();

        //when & then
        assertThrows(InvalidPersonDataException.class, () -> personService.handlePersonCreationRequest(personCreateDto));
        verify(personRepository, times(0)).savePerson(any());
    }

    @Test
    @DisplayName("Should throw InvalidPersonDataException if lastName is empty")
    void shouldThrowInvalidPersonDataExceptionIfLastNameIsEmpty() {
        //given
        PersonDto personCreateDto = PersonDto.builder()
                .firstName("John")
                .lastName("  ")
                .address("45 rue de la Paix")
                .city("Paris")
                .zip("75001")
                .phone("1234567890")
                .email("john@doe.com")
                .build();

        //when & then
        assertThrows(InvalidPersonDataException.class, () -> personService.handlePersonCreationRequest(personCreateDto));
        verify(personRepository, times(0)).savePerson(any());
    }

    @Test
    @DisplayName("should update a person if it exists")
    void shouldUpdatePersonIfItExists() {
        //given
        PersonDto personUpdateDto = PersonDto.builder()
                .firstName("John")
                .lastName("Doe")
                .address("75 rue de la Gare")
                .city("Lyon")
                .zip("69001")
                .phone(null)
                .email(null)
                .build();

        Person existingPerson = Person.builder()
                .firstName("John")
                .lastName("Doe")
                .address("45 rue de la Paix")
                .city("Paris")
                .zip("75001")
                .phone("1234567890")
                .email("john@doe.com")
                .build();
        given(personRepository.findPersonByFirstNameAndLastName(personUpdateDto.getFirstName(), personUpdateDto.getLastName())).willReturn(Optional.ofNullable(existingPerson));

        //when
        personService.handlePersonUpdateRequest(personUpdateDto);

        //then
        assert existingPerson != null;
        verify(personRepository, times(1)).savePerson(personArgumentCaptor.capture());
        Person updatedPerson = personArgumentCaptor.getValue();
        assertEquals(personUpdateDto.getFirstName(), updatedPerson.getFirstName());
        assertEquals(personUpdateDto.getLastName(), updatedPerson.getLastName());
        assertEquals(personUpdateDto.getAddress(), updatedPerson.getAddress());
        assertEquals(personUpdateDto.getCity(), updatedPerson.getCity());
        assertEquals(personUpdateDto.getZip(), updatedPerson.getZip());
        assertEquals(existingPerson.getPhone(), updatedPerson.getPhone());
        assertEquals(existingPerson.getEmail(), updatedPerson.getEmail());
    }

    @Test
    @DisplayName("should throw InexistantPersonException if the person does not exist")
    void shouldThrowInexistantPersonExceptionIfThePersonDoesNotExist() {
        //given
        PersonDto personUpdateDto = PersonDto.builder()
                .firstName("John")
                .lastName("Doe")
                .address("75 rue de la Gare")
                .city("Lyon")
                .zip("69001")
                .phone(null)
                .email(null)
                .build();
        given(personRepository.findPersonByFirstNameAndLastName(personUpdateDto.getFirstName(), personUpdateDto.getLastName())).willReturn(Optional.empty());

        //when & then
        assertThrows(InexistantPersonException.class, () -> personService.handlePersonUpdateRequest(personUpdateDto));
        verify(personRepository, times(0)).savePerson(any());
    }

    @Test
    @DisplayName("should throw InvalidPersonException if firstName is null ")
    void shouldThrowInvalidPersonExceptionIfFirstNameIsNull() {
        PersonDto personUpdateDto = PersonDto.builder()
                .firstName(null)
                .lastName("Doe")
                .address("75 rue de la Gare")
                .city("Lyon")
                .zip("69001")
                .phone(null)
                .email(null)
                .build();

        //when & then
        assertThrows(InvalidPersonDataException.class, () -> personService.handlePersonUpdateRequest(personUpdateDto));
        verify(personRepository, times(0)).savePerson(any());
    }

    @Test
    @DisplayName("should throw InvalidPersonException if firstName is null ")
    void shouldThrowInvalidPersonExceptionIfLastNameIsNull() {
        PersonDto personUpdateDto = PersonDto.builder()
                .firstName("John")
                .lastName(null)
                .address("75 rue de la Gare")
                .city("Lyon")
                .zip("69001")
                .phone(null)
                .email(null)
                .build();

        //when & then
        assertThrows(InvalidPersonDataException.class, () -> personService.handlePersonUpdateRequest(personUpdateDto));
        verify(personRepository, times(0)).savePerson(any());
    }

    @Test
    @DisplayName("should throw InvalidPersonException if firstName is blank ")
    void shouldThrowInvalidPersonExceptionIfFirstNameIsBlank() {
        PersonDto personUpdateDto = PersonDto.builder()
                .firstName("  ")
                .lastName("Doe")
                .address("75 rue de la Gare")
                .city("Lyon")
                .zip("69001")
                .phone(null)
                .email(null)
                .build();

        //when & then
        assertThrows(InvalidPersonDataException.class, () -> personService.handlePersonUpdateRequest(personUpdateDto));
        verify(personRepository, times(0)).savePerson(any());
    }

    @Test
    @DisplayName("should throw InvalidPersonException if lastName is blank ")
    void shouldThrowInvalidPersonExceptionIfLastNameIsBlank() {
        PersonDto personUpdateDto = PersonDto.builder()
                .firstName("John")
                .lastName("  ")
                .address("75 rue de la Gare")
                .city("Lyon")
                .zip("69001")
                .phone(null)
                .email(null)
                .build();

        //when & then
        assertThrows(InvalidPersonDataException.class, () -> personService.handlePersonUpdateRequest(personUpdateDto));
        verify(personRepository, times(0)).savePerson(any());
    }

    @Test
    @DisplayName("should delete a person")
    void shouldDeletePerson() {
        //given
        final String firstName = "John";
        final String lastName = "Doe";
        Person existingPerson = Person.builder()
                .firstName(firstName)
                .lastName(lastName)
                .address("45 rue de la Paix")
                .city("Paris")
                .zip("75001")
                .phone("1234567890")
                .email("john@doe.com")
                .build();
        given(personRepository.findPersonByFirstNameAndLastName(firstName, lastName)).willReturn(Optional.ofNullable(existingPerson));


        //when
        personService.handlePersonDeleteRequest(firstName, lastName);

        //then
        verify(personRepository, times(1)).deletePerson(personArgumentCaptor.capture());
        Person deletedPerson = personArgumentCaptor.getValue();
        assertEquals(firstName, deletedPerson.getFirstName());
        assertEquals(lastName, deletedPerson.getLastName());
    }

    @Test
    @DisplayName("should throw InexistantPersonException if person does not exist so cannot be deleted")
    void shouldThrowInexistantPersonExceptionIfPersonDoesNotExist() {
        //given
        final String firstName = "John";
        final String lastName = "Doe";
        given(personRepository.findPersonByFirstNameAndLastName(firstName, lastName)).willReturn(Optional.empty());

        //when & then
        assertThrows(InexistantPersonException.class, () -> personService.handlePersonDeleteRequest(firstName, lastName));
        verify(personRepository, times(0)).deletePerson(any());
    }

    @Test
    @DisplayName("should throw InvalidPersonDataException if the lastName or firstName is null for delete a person")
    void shouldThrowInvalidPersonDataExceptionIfLastNameOrFirstNameIsNull() {
        //given
        final String firstName = "John";
        final String lastName = null;

        //when & then
        assertThrows(InvalidPersonDataException.class, () -> personService.handlePersonDeleteRequest(firstName, lastName));
        verify(personRepository, times(0)).deletePerson(any());
    }

    @Test
    @DisplayName("should throw InvalidPersonDataException if the lastName or firstName is blank for delete a person")
    void shouldThrowInvalidPersonDataExceptionIfLastNameOrFirstNameIsBlank() {
        //given
        final String firstName = "John";
        final String lastName = " ";

        //when & then
        assertThrows(InvalidPersonDataException.class, () -> personService.handlePersonDeleteRequest(firstName, lastName));
        verify(personRepository, times(0)).deletePerson(any());
    }
}