package com.lulippe.safetynetalerts.service;

import com.lulippe.safetynetalerts.exception.entity_exception.InexistantObjectException;
import com.lulippe.safetynetalerts.exception.entity_exception.InvalidDataException;
import com.lulippe.safetynetalerts.exception.entity_exception.ObjectAlreadyExistsException;

import com.lulippe.safetynetalerts.fireStation.model.FireStationDto;
import com.lulippe.safetynetalerts.model.FireStation;
import com.lulippe.safetynetalerts.repository.FireStationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.OptionalInt;

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

    @Captor
    private ArgumentCaptor<FireStation> fireStationCaptor;

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
        given(fireStationRepository.findByStation(station)).willReturn(Optional.empty());

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
    @DisplayName("should throw ObjectAlreadyExists if station is already use")
    void shouldThrowObjectAlreadyExistsIfStationIsAlreadyUse() {
        //given
        final String address = "10 downing street";
        final String station = "13";
        final FireStationDto fireStationDto = new FireStationDto();
        fireStationDto.setAddress(address);
        fireStationDto.setStation(station);

        final FireStation existingFireStation = FireStation.builder()
                .address("12 downing street")
                .station(station)
                .build();
        given(fireStationRepository.findByStation(station)).willReturn(Optional.of(existingFireStation));

        //when & then
        assertThrows(ObjectAlreadyExistsException.class, () -> fireStationService.handleFireStationUpdateRequest(fireStationDto));
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
        given(fireStationRepository.findByStation(station)).willReturn(Optional.empty());
        given(fireStationRepository.findByAddress(address)).willReturn(Optional.empty());

        //when & then
        assertThrows(InexistantObjectException.class, () -> fireStationService.handleFireStationUpdateRequest(fireStationDto));
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
        given(fireStationRepository.findByAddressAndStation(address,station)).willReturn(Optional.empty());

        // when & then
        assertThrows(InexistantObjectException.class, () -> fireStationService.handleFireStationDeleteRequest(fireStationDto));
        verify(fireStationRepository, times(0)).delete(any());
    }
}