package com.lulippe.safetynetalerts.service;

import com.lulippe.safetynetalerts.exception.entity_exception.InexistantObjectException;
import com.lulippe.safetynetalerts.exception.entity_exception.InvalidDataException;
import com.lulippe.safetynetalerts.model.MedicalRecord;
import com.lulippe.safetynetalerts.model.Person;
import com.lulippe.safetynetalerts.personInfoLastName.model.ResidentInformation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonInfoLastNameServiceTest {

    @InjectMocks
    private PersonInfoLastNameService personInfoLastNameService;

    @Mock
    private PersonService personService;

    @Mock
    private MedicalRecordService medicalRecordService;

    @Test
    @DisplayName("should return a list of ResidentInformation")
    void shouldReturnAListOfResidentInformation() {
        //given
        final String lastName = "Dupont";
        final String firstName = "Brigitte";
        final String address = "address";
        final String email = "email";
        final Person person = Person.builder()
                .firstName(firstName)
                .address(address)
                .email(email)
                .lastName(lastName).build();
        final List<Person> persons = Collections.singletonList(person);
        String birthdate = "12/05/2005";
        final String medication = "aznol:350mg";
        final String allergy = "dust";
        final List<String> medications = Collections.singletonList(medication);
        final List<String> allergies = Collections.singletonList(allergy);
        final MedicalRecord medicalRecord = MedicalRecord.builder()
                .firstName(firstName)
                .lastName(lastName)
                .medications(medications)
                .allergies(allergies)
                .birthdate(birthdate)
                .build();

        given(personService.getAllPersonsByLastName(lastName)).willReturn(persons);
        given(medicalRecordService.getMedicalRecordByFirstNameAndLastName(firstName, lastName)).willReturn(medicalRecord);
        given(medicalRecordService.calculatePersonAge(birthdate)).willReturn(20);

        //when
        final List<ResidentInformation> residentInformationList = personInfoLastNameService.getPersonInfoLastName(lastName);

        //then
        assertNotNull(residentInformationList);
        assertEquals(1, residentInformationList.size());
        assertEquals(firstName, residentInformationList.getFirst().getFirstName());
        assertEquals(address, residentInformationList.getFirst().getAddress());
        assertEquals(email, residentInformationList.getFirst().getEmail());
        assertEquals(medications, residentInformationList.getFirst().getMedications());
        assertEquals(allergies, residentInformationList.getFirst().getAllergies());
    }

    @Test
    @DisplayName("should throw InvalidDataException if lastName is empty")
    void shouldThrowInvalidDataExceptionIfLastNameIsEmpty() {
        //given
        final String lastName = " ";


        //when & then
        assertThrows(InvalidDataException.class, () -> personInfoLastNameService.getPersonInfoLastName(lastName));
        verify(personService, times(0)).getAllPersonsByLastName(lastName);
    }

    @Test
    @DisplayName("Should throw InexistantObjectException if no Person found by given lastName")
    void shouldThrowInexistantObjectExceptionIfNoPersonFoundByGivenLastName() {
        //given
        final String lastName = "Dupont";
        doThrow(InexistantObjectException.class).when(personService).getAllPersonsByLastName(lastName);

        //when & then
        assertThrows(InexistantObjectException.class, () -> personInfoLastNameService.getPersonInfoLastName(lastName));
        verify(medicalRecordService, times(0)).getMedicalRecordByFirstNameAndLastName(lastName, eq(any()));
    }

    @Test
    @DisplayName("Should throw InexistantObjectException if no medical record found ")
    void shouldThrowInexistantObjectExceptionIfNoMedicalRecordFound() {
        //given
        final String lastName = "Dupont";
        final String firstName = "Brigitte";
        final String address = "address";
        final String email = "email";
        final Person person = Person.builder()
                .firstName(firstName)
                .address(address)
                .email(email)
                .lastName(lastName).build();
        final List<Person> persons = Collections.singletonList(person);

        given(personService.getAllPersonsByLastName(lastName)).willReturn(persons);
        doThrow(InexistantObjectException.class).when(medicalRecordService).getMedicalRecordByFirstNameAndLastName(firstName, lastName);

        //when & then
        assertThrows(InexistantObjectException.class, () -> personInfoLastNameService.getPersonInfoLastName(lastName));
        verify(medicalRecordService, times(0)).calculatePersonAge(any());
    }
}