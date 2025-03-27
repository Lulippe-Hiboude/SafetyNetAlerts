package com.lulippe.safetynetalerts.service;

import com.lulippe.safetynetalerts.dto.PersonCreateDto;
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
        PersonCreateDto personCreateDto = PersonCreateDto.builder()
                .firstName("John")
                .lastName("Doe")
                .address("45 rue de la Paix")
                .city("Paris")
                .zip("75001")
                .phone("1234567890")
                .email("john@doe.com")
                .build();

        given(personRepository.getPersonByFirstNameAndLastName(personCreateDto.getFirstName(), personCreateDto.getLastName())).willReturn(Optional.empty());

        //when
        personService.handlePersonCreationRequest(personCreateDto);

        //then
        verify(personRepository,(times(1))).savePerson(personArgumentCaptor.capture());
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
        verify(personRepository,times(0)).getPersonByFirstNameAndLastName(any(),any());
        verify(personRepository,(times(0))).savePerson(any());
    }

    @Test
    @DisplayName("should throw PersonAlreadyExistsException if person already exists")
    void shouldReturnNullIfPersonAlreadyExists() {
        //given
        PersonCreateDto personCreateDto = PersonCreateDto.builder()
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
        given(personRepository.getPersonByFirstNameAndLastName(personCreateDto.getFirstName(), personCreateDto.getLastName())).willReturn(Optional.ofNullable(existingPerson));

        //when & then
        assertThrows(PersonAlreadyExistsException.class, () -> personService.handlePersonCreationRequest(personCreateDto));
        verify(personRepository,times(0)).savePerson(any());
    }

    @Test
    @DisplayName("Should throw InvalidPersonDataException if firstName is null")
    void shouldThrowInvalidPersonDataExceptionIfFirstNameIsNull() {
        //given
        PersonCreateDto personCreateDto = PersonCreateDto.builder()
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
        verify(personRepository,times(0)).savePerson(any());
    }

    @Test
    @DisplayName("Should throw InvalidPersonDataException if lastName is null")
    void shouldThrowInvalidPersonDataExceptionIfLastNameIsNull() {
        //given
        PersonCreateDto personCreateDto = PersonCreateDto.builder()
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
        verify(personRepository,times(0)).savePerson(any());
    }

    @Test
    @DisplayName("Should throw InvalidPersonDataException if firstName is empty")
    void shouldThrowInvalidPersonDataExceptionIfFirstNameIsEmpty() {
        //given
        PersonCreateDto personCreateDto = PersonCreateDto.builder()
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
        verify(personRepository,times(0)).savePerson(any());
    }

    @Test
    @DisplayName("Should throw InvalidPersonDataException if lastName is empty")
    void shouldThrowInvalidPersonDataExceptionIfLastNameIsEmpty() {
        //given
        PersonCreateDto personCreateDto = PersonCreateDto.builder()
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
        verify(personRepository,times(0)).savePerson(any());
    }
}