package com.lulippe.safetynetalerts.service;

import com.lulippe.safetynetalerts.childAlert.model.ChildAlertResponse;
import com.lulippe.safetynetalerts.exception.entity_exception.InexistantObjectException;
import com.lulippe.safetynetalerts.model.MedicalRecord;
import com.lulippe.safetynetalerts.model.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ChildAlertServiceTest {

    @InjectMocks
    private ChildAlertService childAlertService;

    @Mock
    private PersonService personService;

    @Mock
    private MedicalRecordService medicalRecordService;

    @Test
    @DisplayName("should return a ChildAlertResponse")
    void shouldReturnAChildAlertResponse() {
        //given
        final String lastName = "Dupont";
        final String firstName = "Brigitte";
        final String address = "address";
        final Person person = Person.builder()
                .firstName(firstName)
                .lastName(lastName)
                .address(address)
                .build();
        final List<Person> persons = Collections.singletonList(person);
        String birthdate = "12/05/2018";
        final MedicalRecord medicalRecord = MedicalRecord.builder()
                .firstName(firstName)
                .lastName(lastName)
                .birthdate(birthdate)
                .build();

        given(personService.getAllPersonsByAddress(address)).willReturn(persons);
        given(medicalRecordService.getMedicalRecordByFirstNameAndLastName(firstName, lastName)).willReturn(medicalRecord);
        given(medicalRecordService.calculatePersonAge(birthdate)).willReturn(7);

        //when
        final ChildAlertResponse childAlertResponse = childAlertService.getChildAlertResponse(address);

        //then
        assertNotNull(childAlertResponse);
        assertNotNull(childAlertResponse.getChildren());
        assertEquals(0, childAlertResponse.getChildren().getFirst().getHouseholdMembers().size());
        assertEquals(7, childAlertResponse.getChildren().getFirst().getAge());
    }

    @Test
    @DisplayName("should Throw InexistantObjectException if no Person found by given address")
    void shouldThrowInexistantObjectExceptionIfNoPersonFoundByGivenAddress() {
        //given
        final String address = "address";
        given(personService.getAllPersonsByAddress(address)).willReturn(Collections.emptyList());

        //when & then
        assertThrows(InexistantObjectException.class , () -> childAlertService.getChildAlertResponse(address));
        verify(medicalRecordService,times(0)).getMedicalRecordByFirstNameAndLastName(any(),any());
    }

    @Test
    @DisplayName("should return an empty list of ChildInformation if no child is found")
    void shouldReturnAnEmptyListOfChildInformationIfNoChildFound() {
        //given
        final String lastName = "Dupont";
        final String firstName = "Brigitte";
        final String address = "address";
        final Person person = Person.builder()
                .firstName(firstName)
                .lastName(lastName)
                .address(address)
                .build();
        final List<Person> persons = Collections.singletonList(person);
        String birthdate = "12/05/2007";
        final MedicalRecord medicalRecord = MedicalRecord.builder()
                .firstName(firstName)
                .lastName(lastName)
                .birthdate(birthdate)
                .build();

        given(personService.getAllPersonsByAddress(address)).willReturn(persons);
        given(medicalRecordService.getMedicalRecordByFirstNameAndLastName(firstName, lastName)).willReturn(medicalRecord);
        given(medicalRecordService.calculatePersonAge(birthdate)).willReturn(18);

        //when
        final ChildAlertResponse childAlertResponse = childAlertService.getChildAlertResponse(address);
        assertNotNull(childAlertResponse);
        assertEquals(0, childAlertResponse.getChildren().size());
    }

}