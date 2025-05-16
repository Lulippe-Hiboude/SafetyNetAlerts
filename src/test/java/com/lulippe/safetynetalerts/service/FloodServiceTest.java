package com.lulippe.safetynetalerts.service;

import com.lulippe.safetynetalerts.exception.entity_exception.InvalidDataException;
import com.lulippe.safetynetalerts.flood.model.PersonInformationByStationNumber;
import com.lulippe.safetynetalerts.model.FireStation;
import com.lulippe.safetynetalerts.model.MedicalRecord;
import com.lulippe.safetynetalerts.model.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class FloodServiceTest {
    @InjectMocks
    private FloodService floodService;

    @Mock
    private PersonService personService;

    @Mock
    private MedicalRecordService medicalRecordService;

    @Mock
    private FireStationService fireStationService;

    @Test
    @DisplayName("should return a list of PersonInformationByStationNumber with a given list of stationNumber")
    void shouldReturnAListOfPersonInformationByStationNumbers() {
        //given
        final Integer stationNumber = 1;
        final List<Integer> stationNumbers = List.of(stationNumber);
        final String address1 = "address";
        final String address2 = "city";
        final FireStation fireStation1 = new FireStation();
        fireStation1.setStation(String.valueOf(stationNumber));
        fireStation1.setAddress(address1);
        final FireStation fireStation2 = new FireStation();
        fireStation2.setStation(String.valueOf(stationNumber));
        fireStation2.setAddress(address2);
        final List<FireStation> fireStations = Arrays.asList(fireStation1, fireStation2);
        final String firstName1 = "firstName1";
        final String phone1 = "phone1";
        final String lastName1 = "lastName1";
        final String firstName2 = "firstName2";
        final String phone2 = "phone2";
        final String lastName2 = "lastName2";
        final Person person1 = new Person();
        person1.setFirstName(firstName1);
        person1.setPhone(phone1);
        person1.setLastName(lastName1);
        person1.setAddress(address1);
        final Person person2 = new Person();
        person2.setFirstName(firstName2);
        person2.setPhone(phone2);
        person2.setLastName(lastName2);
        person2.setAddress(address2);
        final List<Person> persons1 = List.of(person1);
        final List<Person> persons2 = List.of(person2);
        final MedicalRecord medicalRecord1 = new MedicalRecord();
        medicalRecord1.setBirthdate("12/05/2002");
        medicalRecord1.setFirstName(firstName1);
        medicalRecord1.setLastName(lastName1);
        medicalRecord1.setMedications(Collections.emptyList());
        medicalRecord1.setAllergies(Collections.emptyList());
        final MedicalRecord medicalRecord2 = new MedicalRecord();
        medicalRecord2.setBirthdate("12/05/2004");
        medicalRecord2.setFirstName(firstName2);
        medicalRecord2.setLastName(lastName2);
        medicalRecord2.setMedications(Collections.emptyList());
        medicalRecord2.setAllergies(Collections.emptyList());

        given(fireStationService.getFireStationByStationNumber(stationNumber)).willReturn(fireStations);
        given(personService.getAllPersonsByAddress(fireStation1.getAddress())).willReturn(persons1);
        given(personService.getAllPersonsByAddress(fireStation2.getAddress())).willReturn(persons2);
        given(medicalRecordService.getMedicalRecordByFirstNameAndLastName(person1.getFirstName(), person1.getLastName())).willReturn(medicalRecord1);
        given(medicalRecordService.getMedicalRecordByFirstNameAndLastName(person2.getFirstName(), person2.getLastName())).willReturn(medicalRecord2);
        given(medicalRecordService.calculatePersonAge(medicalRecord1.getBirthdate())).willReturn(20);
        given(medicalRecordService.calculatePersonAge(medicalRecord2.getBirthdate())).willReturn(30);
        //when
        final List<PersonInformationByStationNumber> personInformationByStationNumberList = floodService.getResidentsInformationsByStationNumbers(stationNumbers);

        //then
        assertNotNull(personInformationByStationNumberList);
        assertEquals(stationNumber, personInformationByStationNumberList.getFirst().getStationNumber());
        assertEquals(address1,personInformationByStationNumberList.getFirst().getHouseholds().getFirst().getAddress());
        assertEquals(address2,personInformationByStationNumberList.getFirst().getHouseholds().getLast().getAddress());
        assertEquals(firstName1,personInformationByStationNumberList.getFirst().getHouseholds().getFirst().getResidents().getFirst().getFirstName());
        assertEquals(20,personInformationByStationNumberList.getFirst().getHouseholds().getFirst().getResidents().getFirst().getAge());
        assertEquals(firstName2,personInformationByStationNumberList.getFirst().getHouseholds().getLast().getResidents().getFirst().getFirstName());
        assertEquals(30,personInformationByStationNumberList.getFirst().getHouseholds().getLast().getResidents().getFirst().getAge());
    }

    @Test
    @DisplayName("should throw invalidDataException if stationNumber list is empty")
    void shouldThrowInvalidDataExceptionIfStationNumberListIsEmpty() {
        //given
        final List<Integer> stationNumbers = Collections.emptyList();

        //when
        assertThrows(InvalidDataException.class, () -> floodService.getResidentsInformationsByStationNumbers(stationNumbers) );
    }

    @Test
    @DisplayName("should throw invalidDataException if one stationNumber is null")
    void shouldThrowInvalidDataExceptionIfOneStationNumberIsNull() {
        final Integer stationNumber1 = null;
        final Integer stationNumber2 = 1;
        final List<Integer> stationNumbers = new ArrayList<>();
        stationNumbers.add(stationNumber1);
        stationNumbers.add(stationNumber2);
        assertThrows(InvalidDataException.class, () -> floodService.getResidentsInformationsByStationNumbers(stationNumbers));
    }

    @Test
    @DisplayName("should throw invalidDataException if one stationNumber is < or = to 0")
    void shouldThrowInvalidDataExceptionIfOneStationNumberIsLessThanTo0() {
        final Integer stationNumber1 = -1;
        final Integer stationNumber2 = 1;
        final List<Integer> stationNumbers = new ArrayList<>();
        stationNumbers.add(stationNumber1);
        stationNumbers.add(stationNumber2);
        assertThrows(InvalidDataException.class, () -> floodService.getResidentsInformationsByStationNumbers(stationNumbers));
    }

}