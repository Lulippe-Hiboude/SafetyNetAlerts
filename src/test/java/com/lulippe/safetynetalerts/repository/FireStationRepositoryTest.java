package com.lulippe.safetynetalerts.repository;

import com.lulippe.safetynetalerts.dao.DataContainer;
import com.lulippe.safetynetalerts.model.FireStation;
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
class FireStationRepositoryTest {
    @InjectMocks
    private FireStationRepository fireStationRepository;

    @Mock
    private DataManager dataManager;

    @Test
    @DisplayName("Should return a fireStation")
    void shouldReturnAFireStation() {
        //given
        DataContainer dataContainer = initializeDataContainer();
        given(dataManager.getDataContainer()).willReturn(dataContainer);
        final String address = "10 downing street";

        //when
        final Optional<FireStation> expectedFireStation = fireStationRepository.findByAddress(address);

        //then
        assertTrue(expectedFireStation.isPresent());
        assertEquals(address, expectedFireStation.get().getAddress());
        assertEquals("1", expectedFireStation.get().getStation());
    }

    @Test
    @DisplayName("Should return null if no fireStation found")
    void shouldReturnNullFireStation() {
        //given
        final DataContainer dataContainer = initializeDataContainer();
        given(dataManager.getDataContainer()).willReturn(dataContainer);

        //when
        final Optional<FireStation> expectedFireStation = fireStationRepository.findByAddress("11 downing street");

        //then
        assertTrue(expectedFireStation.isEmpty());
    }

    @Test
    @DisplayName("should save a new fire Station")
    void shouldSaveNewFireStation() {
        //given
        DataContainer dataContainer = initializeDataContainer();
        given(dataManager.getDataContainer()).willReturn(dataContainer);
        FireStation newFireStation = FireStation.builder()
                .address("12 downing street")
                .station("2")
                .build();

        //when
        fireStationRepository.save(newFireStation);

        //then
        assertEquals(2,dataContainer.getFireStations().size());
        assertTrue(dataContainer.getFireStations().contains(newFireStation));
    }

    @Test
    @DisplayName("Should delete an existing fireStation")
    void shouldDeleteAnExistingFireStation() {
        //given
        DataContainer dataContainer = initializeDataContainer();
        given(dataManager.getDataContainer()).willReturn(dataContainer);
        FireStation fireStationToDelete = FireStation.builder()
                .address("10 downing street")
                .station("1")
                .build();

        //when
        fireStationRepository.delete(fireStationToDelete);

        //then
        assertEquals(0, dataContainer.getFireStations().size());
    }

    @Test
    @DisplayName("should return all fire station")
    void shouldReturnAllFireStations() {
        DataContainer dataContainer = initializeDataContainer();
        given(dataManager.getDataContainer()).willReturn(dataContainer);

        //when
        final List<FireStation> fireStationList = fireStationRepository.findAll();

        //then
        assertEquals(1, fireStationList.size());
    }

    private DataContainer initializeDataContainer() {
        FireStation fireStation = FireStation.builder()
                .address("10 downing street")
                .station("1")
                .build();
        List<FireStation> fireStationList = new ArrayList<>();
        fireStationList.add(fireStation);

        DataContainer dataContainer = new DataContainer();
        dataContainer.setFireStations(fireStationList);
        return dataContainer;
    }
}