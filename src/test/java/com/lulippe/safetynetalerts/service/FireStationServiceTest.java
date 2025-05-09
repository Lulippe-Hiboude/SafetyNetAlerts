package com.lulippe.safetynetalerts.service;

import com.lulippe.safetynetalerts.exception.entity_exception.InexistantObjectException;
import com.lulippe.safetynetalerts.exception.entity_exception.InvalidDataException;
import com.lulippe.safetynetalerts.exception.entity_exception.ObjectAlreadyExistsException;
import com.lulippe.safetynetalerts.fireStation.model.FireStationCoverageResponse;
import com.lulippe.safetynetalerts.fireStation.model.FireStationDto;
import com.lulippe.safetynetalerts.model.FireStation;
import com.lulippe.safetynetalerts.model.MedicalRecord;
import com.lulippe.safetynetalerts.model.Person;
import com.lulippe.safetynetalerts.repository.FireStationRepository;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FireStationServiceTest {
    @InjectMocks
    private FireStationService fireStationService;

    @Mock
    private FireStationRepository fireStationRepository;

    @Mock
    private PersonService personService;

    @Mock
    private MedicalRecordService medicalRecordService;

    @Captor
    private ArgumentCaptor<FireStation> fireStationCaptor;

    private final EasyRandom easyRandom = new EasyRandom();

    @Test
    @DisplayName("should create a new fireStation")
    void shouldCreateNewFireStation() {
        //given
        final String address = "10 downing street";
        final String station = "10";
        final FireStationDto fireStationDto = new FireStationDto();
        fireStationDto.setAddress(address);
        fireStationDto.setStation(station);
        given(fireStationRepository.findByAddress(address)).willReturn(Optional.empty());

        //when
        fireStationService.handleFireStationCreationRequest(fireStationDto);

        //then
        verify(fireStationRepository, times(1)).save(fireStationCaptor.capture());
        final FireStation fireStationExpected = fireStationCaptor.getValue();
        assertEquals(address, fireStationExpected.getAddress());
        assertEquals(station, fireStationExpected.getStation());
    }

    @Test
    @DisplayName("should throw FireStationAlreadyExists Exception if fireStation already exists")
    void shouldThrowFireStationAlreadyExistsException() {
        //given
        final String address = "10 downing street";
        final String station = "10";
        final FireStationDto fireStationDto = new FireStationDto();
        fireStationDto.setAddress(address);
        fireStationDto.setStation(station);

        final FireStation fireStation = FireStation.builder()
                .address(address)
                .station(station)
                .build();
        given(fireStationRepository.findByAddress(address)).willReturn(Optional.of(fireStation));

        //when & then
        assertThrows(ObjectAlreadyExistsException.class, () -> fireStationService.handleFireStationCreationRequest(fireStationDto));
        verify(fireStationRepository, times(0)).save(fireStationCaptor.capture());
    }

    @Test
    @DisplayName("should throw invalidDataException if address is null for creation")
    void shouldThrowInvalidDataExceptionIfAddressIsNullForCreation() {
        //given
        final String station = "10";
        final FireStationDto fireStationDto = new FireStationDto();
        fireStationDto.setAddress(null);
        fireStationDto.setStation(station);

        //when & then
        assertThrows(InvalidDataException.class, () -> fireStationService.handleFireStationCreationRequest(fireStationDto));
        verify(fireStationRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("should throw invalidDataException if station is null for creation")
    void shouldThrowInvalidDataExceptionIfStationIsNullForCreation() {
        //given
        final String address = "10 downing street";
        final FireStationDto fireStationDto = new FireStationDto();
        fireStationDto.setAddress(address);
        fireStationDto.setStation(null);

        //when & then
        assertThrows(InvalidDataException.class, () -> fireStationService.handleFireStationCreationRequest(fireStationDto));
        verify(fireStationRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("should create and save clean fireStation data")
    void shouldCreateAndSaveCleanFireStationData() {
        //given
        final String address = "  10 downing street  ";
        final String station = "  10  ";
        final FireStationDto fireStationDto = new FireStationDto();
        fireStationDto.setAddress(address);
        fireStationDto.setStation(station);

        final String cleanAddress = "10 downing street";
        final String cleanStation = "10";
        given(fireStationRepository.findByAddress(cleanAddress)).willReturn(Optional.empty());

        //when
        fireStationService.handleFireStationCreationRequest(fireStationDto);

        //then
        verify(fireStationRepository, times(1)).save(fireStationCaptor.capture());
        final FireStation fireStationExpected = fireStationCaptor.getValue();
        assertEquals(cleanAddress, fireStationExpected.getAddress());
        assertEquals(cleanStation, fireStationExpected.getStation());
    }

    @Test
    @DisplayName("should throw invalidDataException if station is not a number")
    void shouldThrowInvalidDataExceptionIfStationIsNotANumber() {
        final String address = "10 downing street";
        final String station = "dix";
        final FireStationDto fireStationDto = new FireStationDto();
        fireStationDto.setAddress(address);
        fireStationDto.setStation(station);
        given(fireStationRepository.findByAddress(address)).willReturn(Optional.empty());
        assertThrows(InvalidDataException.class, () -> fireStationService.handleFireStationCreationRequest(fireStationDto));
        verify(fireStationRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("should update existingFireStation")
    void shouldUpdateExistingFireStation() {
        //given
        final String address = "10 downing street";
        final String station = "13";
        final FireStationDto fireStationDto = new FireStationDto();
        fireStationDto.setAddress(address);
        fireStationDto.setStation(station);
        final FireStation existingFireStation = FireStation.builder()
                .address(address)
                .station("10")
                .build();
        given(fireStationRepository.findByAddress(address)).willReturn(Optional.of(existingFireStation));

        //when
        fireStationService.handleFireStationUpdateRequest(fireStationDto);

        //then
        verify(fireStationRepository, times(1)).save(fireStationCaptor.capture());
        final FireStation fireStationExpected = fireStationCaptor.getValue();
        assertEquals(address, fireStationExpected.getAddress());
        assertEquals(station, fireStationExpected.getStation());
    }

    @Test
    @DisplayName("should throw invalidDataException if address is null for update")
    void shouldThrowInvalidDataExceptionIfAddressIsNullForUpdate() {
        //given
        final String station = "10";
        final FireStationDto fireStationDto = new FireStationDto();
        fireStationDto.setAddress(null);
        fireStationDto.setStation(station);

        //when & then
        assertThrows(InvalidDataException.class, () -> fireStationService.handleFireStationUpdateRequest(fireStationDto));
        verify(fireStationRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("should throw InvalidDataException if station is null for update")
    void shouldThrowInvalidDataExceptionIfStationIsNullForUpdate() {
        //given
        final String address = "10 downing street";
        final FireStationDto fireStationDto = new FireStationDto();
        fireStationDto.setAddress(address);
        fireStationDto.setStation(null);

        //when & then
        assertThrows(InvalidDataException.class, () -> fireStationService.handleFireStationUpdateRequest(fireStationDto));
        verify(fireStationRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("should throw InvalidDataException if station is not a valid number")
    void shouldThrowInvalidDataExceptionIfStationIsNotAValidNumber() {
        //given
        final String address = "10 downing street";
        final String station = "dix";
        final FireStationDto fireStationDto = new FireStationDto();
        fireStationDto.setAddress(address);
        fireStationDto.setStation(station);

        //when & then
        assertThrows(InvalidDataException.class, () -> fireStationService.handleFireStationUpdateRequest(fireStationDto));
        verify(fireStationRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("should throw InexistantObjectException if fireStation does not exist with the address")
    void shouldThrowInexistantObjectExceptionIfFireStationDoesNotExistWithTheAddress() {
        final String address = "10 downing street";
        final String station = "13";
        final FireStationDto fireStationDto = new FireStationDto();
        fireStationDto.setAddress(address);
        fireStationDto.setStation(station);
        given(fireStationRepository.findByAddress(address)).willReturn(Optional.empty());

        //when & then
        assertThrows(InexistantObjectException.class, () -> fireStationService.handleFireStationUpdateRequest(fireStationDto));
        verify(fireStationRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("should throw Object already exists exception if fireStation already exists with this station and address")
    void shouldThrowObjectAlreadyExistsExceptionIfFireStationAlreadyExistsWithThisStationAndAddress() {
        //given
        final String address = "10 downing street";
        final String station = "13";
        final FireStationDto fireStationDto = new FireStationDto();
        fireStationDto.setAddress(address);
        fireStationDto.setStation(station);

        final FireStation existingFireStation = FireStation.builder()
                .address(address)
                .station(station)
                .build();
        given(fireStationRepository.findByAddress(address)).willReturn(Optional.of(existingFireStation));

        //when & then
        assertThrows(ObjectAlreadyExistsException.class, () -> fireStationService.handleFireStationUpdateRequest(fireStationDto));
        verify(fireStationRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("should delete an existing FireStation")
    void shouldDeleteFireStation() {
        //given
        final String address = "  10 downing street  ";
        final String station = "  10  ";
        final FireStationDto fireStationDto = new FireStationDto();
        fireStationDto.setAddress(address);
        fireStationDto.setStation(station);

        final String cleanAddress = "10 downing street";
        final String cleanStation = "10";

        final FireStation fireStation = FireStation.builder()
                .address(cleanAddress)
                .station(station)
                .build();
        given(fireStationRepository.findByAddressAndStation(cleanAddress, cleanStation)).willReturn(Optional.of(fireStation));

        //when
        fireStationService.handleFireStationDeleteRequest(fireStationDto);
        verify(fireStationRepository, times(1)).delete(fireStation);
    }

    @Test
    @DisplayName("should throw InvalidDataException if station is not a valid number for delete")
    void shouldThrowInvalidDataExceptionIfStationIsNotAValidNumberForDelete() {
        //given
        final String address = "10 downing street";
        final String station = "dix";
        final FireStationDto fireStationDto = new FireStationDto();
        fireStationDto.setAddress(address);
        fireStationDto.setStation(station);

        //when & then
        assertThrows(InvalidDataException.class, () -> fireStationService.handleFireStationDeleteRequest(fireStationDto));
        verify(fireStationRepository, times(0)).delete(any());
    }

    @Test
    @DisplayName("should throw inexistant fireStation exception if fireStation does not exist with those address and station")
    void shouldThrowInexistantFireStationExceptionIfFireStationDoesNotExistWithAddressAndStation() {
        final String address = "10 downing street";
        final String station = "10";
        final FireStationDto fireStationDto = new FireStationDto();
        fireStationDto.setAddress(address);
        fireStationDto.setStation(station);
        given(fireStationRepository.findByAddressAndStation(address, station)).willReturn(Optional.empty());

        // when & then
        assertThrows(InexistantObjectException.class, () -> fireStationService.handleFireStationDeleteRequest(fireStationDto));
        verify(fireStationRepository, times(0)).delete(any());
    }

    @Test
    @DisplayName("should return a FireStationCoverageResponse")
    void shouldReturnFireStationCoverageResponse() {
        //given
        final String stationNumber = "3";
        final String address1 = "10 downing street";

        final FireStation fireStation1 = FireStation.builder()
                .address(address1)
                .station(stationNumber)
                .build();
        final String address2 = "12 downing street";
        final FireStation fireStation2 = FireStation.builder()
                .address(address2)
                .station(stationNumber)
                .build();
        final List<FireStation> fireStations = new ArrayList<>();
        fireStations.add(fireStation1);
        fireStations.add(fireStation2);
        given(fireStationRepository.findAll()).willReturn(fireStations);

        final Person person1 = easyRandom.nextObject(Person.class);
        person1.setAddress(address1);
        final Person person2 = easyRandom.nextObject(Person.class);
        person2.setAddress(address1);
        final List<Person> persons1 = new ArrayList<>();
        persons1.add(person1);
        persons1.add(person2);

        final Person person3 = easyRandom.nextObject(Person.class);
        person3.setAddress(address2);
        final Person person4 = easyRandom.nextObject(Person.class);
        person4.setAddress(address2);
        final List<Person> persons2 = new ArrayList<>();
        persons2.add(person3);
        persons2.add(person4);
        given(personService.getAllPersonsByAddress(address1)).willReturn(Optional.of(persons1));
        given(personService.getAllPersonsByAddress(address2)).willReturn(Optional.of(persons2));

        final MedicalRecord medicalRecord = easyRandom.nextObject(MedicalRecord.class);

        given(medicalRecordService.getMedicalRecordByFirstNameAndLastName(any(), any())).willReturn(medicalRecord);
        given(medicalRecordService.calculatePersonAge(medicalRecord.getBirthdate())).willReturn(24);
        //when
        final FireStationCoverageResponse expectedFireStationCoverageResponse = fireStationService.getResidentsByFireStation(stationNumber);

        //then
        assertNotNull(expectedFireStationCoverageResponse);
        assertEquals(4, expectedFireStationCoverageResponse.getAdultCount());
        assertEquals(0, expectedFireStationCoverageResponse.getChildCount());
        assertEquals(person1.getFirstName(), expectedFireStationCoverageResponse.getResidents().getFirst().getFirstName());
        assertEquals(person4.getFirstName(), expectedFireStationCoverageResponse.getResidents().getLast().getFirstName());
    }

    @Test
    @DisplayName("should throw InvalidDataException if station number is empty")
    void shouldThrowInvalidDataExceptionIfStationNumberIsEmpty() {
        //given
        final String stationNumber = "";

        //when & then
        assertThrows(InvalidDataException.class, () -> fireStationService.getResidentsByFireStation(stationNumber));
    }

    @Test
    @DisplayName("should throw InvalidDataException if station number is not a valid number")
    void shouldThrowInvalidDataExceptionIfStationNumberIsNotAValidNumber() {
        //given
        final String stationNumber = "quatre";

        //when & then
        assertThrows(InvalidDataException.class, () -> fireStationService.getResidentsByFireStation(stationNumber));
    }

    @Test
    @DisplayName("should throw InexistantObjectException if no FireStation found for this station number")
    void shouldThrowInexistantObjectExceptionIfNoFireStationFoundForThisStationNumber() {
        //given
        final String stationNumber = "2";
        final List<FireStation> fireStations = new ArrayList<>();
        given(fireStationRepository.findAll()).willReturn(fireStations);

        //when & then
        assertThrows(InexistantObjectException.class, () -> fireStationService.getResidentsByFireStation(stationNumber));
    }

    @Test
    @DisplayName("should return an empty list if no person found at the address")
    void shouldReturnAnEmptyListIfNoPersonFoundAtTheAddress() {
        //given
        final String stationNumber = "3";
        final String address1 = "10 downing street";

        final FireStation fireStation1 = FireStation.builder()
                .address(address1)
                .station(stationNumber)
                .build();
        final String address2 = "12 downing street";
        final FireStation fireStation2 = FireStation.builder()
                .address(address2)
                .station(stationNumber)
                .build();
        final List<FireStation> fireStations = new ArrayList<>();
        fireStations.add(fireStation1);
        fireStations.add(fireStation2);
        given(fireStationRepository.findAll()).willReturn(fireStations);
        given(personService.getAllPersonsByAddress(address1)).willReturn(Optional.empty());
        given(personService.getAllPersonsByAddress(address2)).willReturn(Optional.empty());

        //when
        final FireStationCoverageResponse expectedFireStationCoverageResponse = fireStationService.getResidentsByFireStation(stationNumber);

        //then
        assertNotNull(expectedFireStationCoverageResponse);
        assertEquals(Collections.emptyList(), expectedFireStationCoverageResponse.getResidents());
        assertEquals(0, expectedFireStationCoverageResponse.getChildCount());
        assertEquals(0, expectedFireStationCoverageResponse.getAdultCount());
    }
}