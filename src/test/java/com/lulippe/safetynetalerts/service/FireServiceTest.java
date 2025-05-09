package com.lulippe.safetynetalerts.service;

import com.lulippe.safetynetalerts.exception.entity_exception.InexistantObjectException;
import com.lulippe.safetynetalerts.exception.entity_exception.InvalidDataException;
import com.lulippe.safetynetalerts.fire.model.FireAddressResponse;
import com.lulippe.safetynetalerts.fire.model.FireResident;
import com.lulippe.safetynetalerts.model.FireStation;
import com.lulippe.safetynetalerts.model.MedicalRecord;
import com.lulippe.safetynetalerts.model.Person;
import org.jeasy.random.EasyRandom;
import org.joda.time.DateTime;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FireServiceTest {
    @InjectMocks
    private FireService fireService;

    @Mock
    private FireStationService fireStationService;

    @Mock
    private PersonService personService;

    @Mock
    private MedicalRecordService medicalRecordService;

    private final EasyRandom easyRandom= new EasyRandom();

    @Test
    @DisplayName("should return a FireAddressResponse for a given address")
    void shouldReturnFireAddressResponseForGivenAddress() {
        //given
        final String address = "1509 Culver St";
        final String stationNumber = "3";
        final FireStation fireStation = FireStation.builder()
                .address(address)
                .station(stationNumber)
                .build();

        final String firstName = "John";
        final String lastName = "Doe";
        final Person person = Person.builder()
                .firstName(firstName)
                .lastName(lastName)
                .address(address)
                .zip("97451")
                .email("j.doe@email.com")
                .city("Culver")
                .phone("123456789")
                .build();

        final List<String> medications = new ArrayList<>();
        medications.add("paracetamol : 1000mg");
        medications.add("aznol:350mg");
        final List<String> allergies = new ArrayList<>();
        allergies.add("dust");
        final MedicalRecord medicalRecord = MedicalRecord.builder()
                .firstName(firstName)
                .lastName(lastName)
                .birthdate("03/06/1984")
                .medications(medications)
                .allergies(allergies)
                .build();

        final List<Person> persons = List.of(person);
        given(fireStationService.findFireStationByAddress(address)).willReturn(Optional.of(fireStation));
        given(personService.getAllPersonsByAddress(address)).willReturn(Optional.of(persons));
        given(medicalRecordService.getMedicalRecordByFirstNameAndLastName(firstName, lastName)).willReturn(medicalRecord);

        //when
        final FireAddressResponse fireAddressResponse = fireService.handleFireRequest(address);

        //then
        assertEquals(fireStation.getStation(),fireAddressResponse.getStationNumber());
        assertNotNull(fireAddressResponse.getResidents());
        assertEquals(1, fireAddressResponse.getResidents().size());

        FireResident resident = fireAddressResponse.getResidents().getFirst();
        assertEquals(firstName, resident.getFirstName());
        assertEquals(lastName, resident.getLastName());
        assertEquals(person.getPhone(), resident.getPhone());
        assertEquals(Years.yearsBetween(
                DateTimeFormat.forPattern("MM/dd/yyyy").parseDateTime(medicalRecord.getBirthdate()),
                DateTime.now()
        ).getYears(), resident.getAge());

        assertEquals(medications, resident.getMedications());
        assertEquals(allergies, resident.getAllergies());
    }

    @Test
    @DisplayName("should throw InexistantObjectException if no fireStation found for given address")
    void shouldThrowInexistantObjectExceptionIfNoFireStationFoundForGivenAddress() {
        //given
        final String address = "1509 Culver St";
        given(fireStationService.findFireStationByAddress(address)).willReturn(Optional.empty());

        //when & then
        assertThrows(InexistantObjectException.class , () -> fireService.handleFireRequest(address));
        verify(personService,times(0)).getAllPersonsByAddress(address);
        verify(medicalRecordService,times(0)).getMedicalRecordByFirstNameAndLastName(any(),any());
    }

    @Test
    @DisplayName("should throw InexistantObjectException if noMedicalRecord found for given fullName")
    void shouldThrowInexistantObjectExceptionIfNoMedicalRecordFoundForGivenFullName() {
        //given
        final String address = "1509 Culver St";
        final String stationNumber = "3";
        final FireStation fireStation = FireStation.builder()
                .address(address)
                .station(stationNumber)
                .build();

        final String firstName = "John";
        final String lastName = "Doe";
        final Person person = Person.builder()
                .firstName(firstName)
                .lastName(lastName)
                .address(address)
                .zip("97451")
                .email("j.doe@email.com")
                .city("Culver")
                .phone("123456789")
                .build();
        final List<Person> persons = List.of(person);
        given(fireStationService.findFireStationByAddress(address)).willReturn(Optional.of(fireStation));
        given(personService.getAllPersonsByAddress(address)).willReturn(Optional.of(persons));
        given(medicalRecordService.getMedicalRecordByFirstNameAndLastName("John", "Doe"))
                .willThrow(new InexistantObjectException("No medical record found"));

        //when
        assertThrows(InexistantObjectException.class , () -> fireService.handleFireRequest(address));
    }

    @Test
    @DisplayName("should throw InvalidDataException if address is null or blank")
    void shouldThrowInvalidDataExceptionIfAddressIsNullOrBlank() {
        //given
        final String address = null;

        //when & then
        assertThrows(InvalidDataException.class , () -> fireService.handleFireRequest(address));
        verify(fireStationService,times(0)).findFireStationByAddress(address);
        verify(personService,times(0)).getAllPersonsByAddress(address);
        verify(medicalRecordService,times(0)).getMedicalRecordByFirstNameAndLastName(any(),any());
    }

    @Test
    @DisplayName("should throw InvalidDataException if birthdate is null or blank")
    void shouldThrowInvalidDataExceptionIfBirthdateIsNullOrBlank() {
        //given
        final String address = "1509 Culver St";
        final String stationNumber = "3";
        final FireStation fireStation = FireStation.builder()
                .address(address)
                .station(stationNumber)
                .build();

        final String firstName = "John";
        final String lastName = "Doe";
        final Person person = Person.builder()
                .firstName(firstName)
                .lastName(lastName)
                .address(address)
                .zip("97451")
                .email("j.doe@email.com")
                .city("Culver")
                .phone("123456789")
                .build();

        final List<String> medications = new ArrayList<>();
        medications.add("paracetamol : 1000mg");
        medications.add("aznol:350mg");
        final List<String> allergies = new ArrayList<>();
        allergies.add("dust");
        final MedicalRecord medicalRecord = MedicalRecord.builder()
                .firstName(firstName)
                .lastName(lastName)
                .birthdate(null)
                .medications(medications)
                .allergies(allergies)
                .build();

        final List<Person> persons = List.of(person);
        given(fireStationService.findFireStationByAddress(address)).willReturn(Optional.of(fireStation));
        given(personService.getAllPersonsByAddress(address)).willReturn(Optional.of(persons));
        given(medicalRecordService.getMedicalRecordByFirstNameAndLastName(firstName, lastName)).willReturn(medicalRecord);

        //when
        assertThrows(InvalidDataException.class , () -> fireService.handleFireRequest(address));
    }


}