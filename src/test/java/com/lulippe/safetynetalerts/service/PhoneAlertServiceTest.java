package com.lulippe.safetynetalerts.service;

import com.lulippe.safetynetalerts.exception.entity_exception.InvalidDataException;
import com.lulippe.safetynetalerts.model.FireStation;
import com.lulippe.safetynetalerts.model.Person;
import com.lulippe.safetynetalerts.phoneAlert.model.PhoneAlertResponse;
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

@ExtendWith(MockitoExtension.class)
class PhoneAlertServiceTest {

    @InjectMocks
    private PhoneAlertService phoneAlertService;

    @Mock
    private FireStationService fireStationService;

    @Mock
    private PersonService personService;

    @Test
    @DisplayName("should return a list of PhoneAlertResponse")
    void shouldReturnAListOfPhoneAlertResponse() {
        //given
        final int fireStationNumber = 3;
        final String address = "address";
        final FireStation fireStation = FireStation.builder()
                .station(String.valueOf(fireStationNumber))
                .address(address)
                .build();
        final List<FireStation> fireStations = Collections.singletonList(fireStation);
        final String phone = "123-456-898";
        final Person person = Person.builder()
                .address(address)
                .phone(phone)
                .build();
        final List<Person> persons = Collections.singletonList(person);

        given(fireStationService.getFireStationByStationNumber(fireStationNumber)).willReturn(fireStations);
        given(personService.getAllPersonsByAddress(address)).willReturn(persons);
        //when
        final List<PhoneAlertResponse> expectedPhoneAlertResponses = phoneAlertService.getPhoneAlertResponse(fireStationNumber);

        //then
        assertNotNull(expectedPhoneAlertResponses);
        assertEquals(1, expectedPhoneAlertResponses.size());
        assertEquals(phone,expectedPhoneAlertResponses.getFirst().getPhoneNumbers().getFirst());
        assertEquals(fireStationNumber,expectedPhoneAlertResponses.getFirst().getStationNumber());
    }

    @Test
    @DisplayName("should throw InvalidDataException when station number is invalid")
    void shouldThrowInvalidDataExceptionWhenStationNumberIsInvalid() {
        //given
        final int fireStationNumber = -3;

        //when & then
        assertThrows(InvalidDataException.class, () -> phoneAlertService.getPhoneAlertResponse(fireStationNumber) );
    }
}