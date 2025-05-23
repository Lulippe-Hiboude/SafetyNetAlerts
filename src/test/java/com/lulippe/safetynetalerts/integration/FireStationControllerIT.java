package com.lulippe.safetynetalerts.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulippe.safetynetalerts.dao.DataContainer;
import com.lulippe.safetynetalerts.fireStation.model.FireStationDto;
import com.lulippe.safetynetalerts.repository.manager.DataManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class FireStationControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DataManager dataManager;

    @AfterEach
    void resetDataContainer() {
        dataManager.init();
    }

    @Test
    @DisplayName("Should create a new fireStation and update dataContainer")
    void shouldCreateNewFireStationAndUpdateDataContainer() throws Exception {
        //given
        final String address = "10 downing street";
        final String station = "4";
        FireStationDto fireStationDto = new FireStationDto();
        fireStationDto.setAddress(address);
        fireStationDto.setStation(station);
        DataContainer initialDataContainer = dataManager.getDataContainer();
        assertEquals(1, initialDataContainer.getFireStations().size());
        assertFalse(initialDataContainer.getFireStations().stream()
                .anyMatch(fireStation -> fireStation.getAddress().equals(address) && fireStation.getStation().equals(station)));

        //when & then
        mockMvc.perform(post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fireStationDto)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("FireStation create successfully : " + fireStationDto));
        DataContainer updatedData = dataManager.getDataContainer();
        assertEquals(2, updatedData.getFireStations().size());
        assertTrue(updatedData.getFireStations().stream()
                .anyMatch(fireStation -> fireStation.getAddress().equals(address) && fireStation.getStation().equals(station)));
    }

    @Test
    @DisplayName("Should return 409 if fireStation already exists")
    void shouldReturn409IfFireStationAlreadyExists() throws Exception {
        //given
        final String address = "1509 Culver St";
        final String station = "3";
        FireStationDto fireStationDto = new FireStationDto();
        fireStationDto.setAddress(address);
        fireStationDto.setStation(station);
        DataContainer initialDataContainer = dataManager.getDataContainer();
        assertEquals(1, initialDataContainer.getFireStations().size());
        assertTrue(initialDataContainer.getFireStations().stream()
                .anyMatch(fireStation -> fireStation.getAddress().equals(address) && fireStation.getStation().equals(station)));

        //when & then
        mockMvc.perform(post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fireStationDto)))
                .andExpect(status().isConflict())
                .andExpect(MockMvcResultMatchers.content().string("FireStation already exists"));
        DataContainer updatedData = dataManager.getDataContainer();
        assertEquals(1, updatedData.getFireStations().size());
        assertTrue(updatedData.getFireStations().stream()
                .anyMatch(fireStation -> fireStation.getAddress().equals(address) && fireStation.getStation().equals(station)));
    }

    @Test
    @DisplayName("Should return 400 when address or station is null for creation")
    void shouldReturn400WhenAddressOrStationIsNullForCreation() throws Exception {
        //given
        final String address = null;
        final String station = "4";
        FireStationDto fireStationDto = new FireStationDto();
        fireStationDto.setAddress(address);
        fireStationDto.setStation(station);
        DataContainer initialDataContainer = dataManager.getDataContainer();
        assertEquals(1, initialDataContainer.getFireStations().size());
        assertTrue(initialDataContainer.getFireStations().stream()
                .anyMatch(fireStation -> fireStation.getAddress().equals("1509 Culver St") && fireStation.getStation().equals("3")));

        //when & then
        mockMvc.perform(post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fireStationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("fire station address or fire station number is blank: " + address + ", " + station));
        DataContainer updatedData = dataManager.getDataContainer();
        assertEquals(1, updatedData.getFireStations().size());
        assertTrue(updatedData.getFireStations().stream()
                .anyMatch(fireStation -> fireStation.getAddress().equals("1509 Culver St") && fireStation.getStation().equals("3")));
    }

    @Test
    @DisplayName("should return 400 when fireStationDto is null for Creation")
    void shouldReturn400WhenFireStationDtoIsNullForCreation() throws Exception {
        DataContainer initialDataContainer = dataManager.getDataContainer();
        assertEquals(1, initialDataContainer.getFireStations().size());
        assertTrue(initialDataContainer.getFireStations().stream()
                .anyMatch(fireStation -> fireStation.getAddress().equals("1509 Culver St") && fireStation.getStation().equals("3")));
        mockMvc.perform(post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(null)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should return 400 when fireStationDto Station is not a valid number")
    void shouldReturn400WhenFireStationDtoStationIsNotAValidNumber() throws Exception {
        //given
        final String address = "10 downing street";
        final String station = "quatre";
        FireStationDto fireStationDto = new FireStationDto();
        fireStationDto.setAddress(address);
        fireStationDto.setStation(station);
        DataContainer initialDataContainer = dataManager.getDataContainer();
        assertEquals(1, initialDataContainer.getFireStations().size());
        assertFalse(initialDataContainer.getFireStations().stream()
                .anyMatch(fireStation -> fireStation.getAddress().equals(address) && fireStation.getStation().equals(station)));
        mockMvc.perform(post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fireStationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Invalid station, please enter a valid number"));
    }

    @Test
    @DisplayName("should update an existing fireStation")
    void shouldUpdateAnExistingFireStation() throws Exception {
        //given
        final String address = "1509 Culver St";
        final String station = "4";
        FireStationDto fireStationDto = new FireStationDto();
        fireStationDto.setAddress(address);
        fireStationDto.setStation(station);
        DataContainer initialDataContainer = dataManager.getDataContainer();
        assertEquals(1, initialDataContainer.getFireStations().size());
        assertTrue(initialDataContainer.getFireStations().stream()
                .anyMatch(fireStation -> fireStation.getAddress().equals(address) && fireStation.getStation().equals("3")));

        //when & then
        mockMvc.perform(put("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fireStationDto)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("FireStation updated successfully : " + fireStationDto));
        DataContainer updatedData = dataManager.getDataContainer();
        assertEquals(1, updatedData.getFireStations().size());
        assertTrue(updatedData.getFireStations().stream()
                .anyMatch(fireStation -> fireStation.getAddress().equals(address) && fireStation.getStation().equals(station)));
    }

    @Test
    @DisplayName("should return 400 if fireStation already exists with the same address and station number")
    void shouldReturn400IfFireStationAlreadyExistsWithTheSameAddressAndNumber() throws Exception {
        //given
        final String address = "1509 Culver St";
        final String updateStation = "3";
        FireStationDto fireStationDto = new FireStationDto();
        fireStationDto.setAddress(address);
        fireStationDto.setStation(updateStation);
        DataContainer initialDataContainer = dataManager.getDataContainer();
        assertEquals(1, initialDataContainer.getFireStations().size());
        assertTrue(initialDataContainer.getFireStations().stream()
                .anyMatch(fireStation -> fireStation.getAddress().equals(address) && fireStation.getStation().equals(updateStation)));

        //when & then
        mockMvc.perform(put("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fireStationDto)))
                .andExpect(status().isConflict())
                .andExpect(MockMvcResultMatchers.content().string("fireStation already exists with this address and station number"));
        DataContainer updatedData = dataManager.getDataContainer();
        assertEquals(1, updatedData.getFireStations().size());
        assertTrue(updatedData.getFireStations().stream()
                .anyMatch(fireStation -> fireStation.getAddress().equals(address) && fireStation.getStation().equals(updateStation)));
    }

    @Test
    @DisplayName("should return 404 if fireStation does not exist for update")
    void shouldReturn404IfFireStationDoesNotExistForUpdate() throws Exception {
        //given
        final String address = "10 downing street";
        final String station = "4";
        FireStationDto fireStationDto = new FireStationDto();
        fireStationDto.setAddress(address);
        fireStationDto.setStation(station);
        DataContainer initialDataContainer = dataManager.getDataContainer();
        assertEquals(1, initialDataContainer.getFireStations().size());
        assertTrue(initialDataContainer.getFireStations().stream()
                .anyMatch(fireStation -> fireStation.getAddress().equals("1509 Culver St") && fireStation.getStation().equals("3")));

        //when & then
        mockMvc.perform(put("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fireStationDto)))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("fireStation does not exist with this address: " + fireStationDto.getAddress()));
        DataContainer updatedData = dataManager.getDataContainer();
        assertEquals(1, updatedData.getFireStations().size());
        assertFalse(updatedData.getFireStations().stream()
                .anyMatch(fireStation -> fireStation.getAddress().equals(address) && fireStation.getStation().equals(station)));
    }

    @Test
    @DisplayName("should return 400 when fireStationDto Station is not a valid number for update")
    void shouldReturn400WhenFireStationDtoStationIsNotAValidNumberForUpdate() throws Exception {
        //given
        final String address = "10 downing street";
        final String station = "quatre";
        FireStationDto fireStationDto = new FireStationDto();
        fireStationDto.setAddress(address);
        fireStationDto.setStation(station);
        DataContainer initialDataContainer = dataManager.getDataContainer();
        assertEquals(1, initialDataContainer.getFireStations().size());
        assertTrue(initialDataContainer.getFireStations().stream()
                .anyMatch(fireStation -> fireStation.getAddress().equals("1509 Culver St") && fireStation.getStation().equals("3")));

        //when & then
        mockMvc.perform(put("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fireStationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Invalid station, please enter a valid number"));
        DataContainer updatedData = dataManager.getDataContainer();
        assertEquals(1, updatedData.getFireStations().size());
        assertTrue(updatedData.getFireStations().stream()
                .anyMatch(fireStation -> fireStation.getAddress().equals("1509 Culver St") && fireStation.getStation().equals("3")));
    }

    @Test
    @DisplayName("Should return 400 when address or station is null for update")
    void shouldReturn400WhenAddressOrStationIsNullForUpdate() throws Exception {
        //given
        final String address = "1509 Culver St";
        final String station = null;
        FireStationDto fireStationDto = new FireStationDto();
        fireStationDto.setAddress(address);
        fireStationDto.setStation(station);
        DataContainer initialDataContainer = dataManager.getDataContainer();
        assertEquals(1, initialDataContainer.getFireStations().size());
        assertTrue(initialDataContainer.getFireStations().stream()
                .anyMatch(fireStation -> fireStation.getAddress().equals("1509 Culver St") && fireStation.getStation().equals("3")));

        //when & then
        mockMvc.perform(put("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fireStationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("fire station address or fire station number is blank: " + address + ", " + station));
        DataContainer updatedData = dataManager.getDataContainer();
        assertEquals(1, updatedData.getFireStations().size());
        assertTrue(updatedData.getFireStations().stream()
                .anyMatch(fireStation -> fireStation.getAddress().equals("1509 Culver St") && fireStation.getStation().equals("3")));
    }

    @Test
    @DisplayName("should delete existing FireStation")
    void shouldDeleteExistingFireStation() throws Exception {
        //given
        final String address = "1509 Culver St";
        final String station = "3";
        FireStationDto fireStationDto = new FireStationDto();
        fireStationDto.setAddress(address);
        fireStationDto.setStation(station);
        DataContainer initialDataContainer = dataManager.getDataContainer();
        assertEquals(1, initialDataContainer.getFireStations().size());
        assertTrue(initialDataContainer.getFireStations().stream()
                .anyMatch(fireStation -> fireStation.getAddress().equals(address) && fireStation.getStation().equals(station)));

        // when & then
        mockMvc.perform(delete("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fireStationDto)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("FireStation deleted successfully : " + fireStationDto));
    }

    @Test
    @DisplayName("should return 404 when fireStation does not exist for given station")
    void shouldReturn404WhenFireStationDoesNotExistForGivenStation() throws Exception {
        //given
        final String address = "1509 Culver St";
        final String station = "4";
        FireStationDto fireStationDto = new FireStationDto();
        fireStationDto.setAddress(address);
        fireStationDto.setStation(station);
        DataContainer initialDataContainer = dataManager.getDataContainer();
        assertEquals(1, initialDataContainer.getFireStations().size());
        assertFalse(initialDataContainer.getFireStations().stream()
                .anyMatch(fireStation -> fireStation.getAddress().equals(address) && fireStation.getStation().equals(station)));

        // when & then
        mockMvc.perform(delete("/firestation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(fireStationDto)))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("fireStation does not exist with this address and this station: address : "
                        + fireStationDto.getAddress() + ", station : " + fireStationDto.getStation()));
    }

    @Test
    @DisplayName("should return 404 when fireStation does not exist for given address")
    void shouldReturn404WhenFireStationDoesNotExistForGivenAddress() throws Exception {
        //given
        final String address = "1508 Culver St";
        final String station = "3";
        FireStationDto fireStationDto = new FireStationDto();
        fireStationDto.setAddress(address);
        fireStationDto.setStation(station);
        DataContainer initialDataContainer = dataManager.getDataContainer();
        assertEquals(1, initialDataContainer.getFireStations().size());
        assertFalse(initialDataContainer.getFireStations().stream()
                .anyMatch(fireStation -> fireStation.getAddress().equals(address) && fireStation.getStation().equals(station)));

        // when & then
        mockMvc.perform(delete("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fireStationDto)))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("fireStation does not exist with this address and this station: address : "
                        + fireStationDto.getAddress() + ", station : " + fireStationDto.getStation()));
    }

    @Test
    @DisplayName("should return 400 when fireStationDto Station is not a valid number for delete")
    void shouldReturn400WhenFireStationDtoStationIsNotAValidNumberForDelete() throws Exception {
        //given
        final String address = "10 downing street";
        final String station = "quatre";
        FireStationDto fireStationDto = new FireStationDto();
        fireStationDto.setAddress(address);
        fireStationDto.setStation(station);
        DataContainer initialDataContainer = dataManager.getDataContainer();
        assertEquals(1, initialDataContainer.getFireStations().size());
        assertFalse(initialDataContainer.getFireStations().stream()
                .anyMatch(fireStation -> fireStation.getAddress().equals(address) && fireStation.getStation().equals(station)));
        mockMvc.perform(delete("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fireStationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Invalid station, please enter a valid number"));
    }

    @Test
    @DisplayName("Should return 400 when address or station is null for deletion")
    void shouldReturn400WhenAddressOrStationIsNullForDeletion() throws Exception {
        //given
        final String address = "1509 Culver St";
        final String station = "";
        FireStationDto fireStationDto = new FireStationDto();
        fireStationDto.setAddress(address);
        fireStationDto.setStation(station);
        DataContainer initialDataContainer = dataManager.getDataContainer();
        assertEquals(1, initialDataContainer.getFireStations().size());
        assertTrue(initialDataContainer.getFireStations().stream()
                .anyMatch(fireStation -> fireStation.getAddress().equals("1509 Culver St") && fireStation.getStation().equals("3")));

        //when & then
        mockMvc.perform(delete("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fireStationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("fire station address or fire station number is blank: " + address + ", " + station));
        DataContainer updatedData = dataManager.getDataContainer();
        assertEquals(1, updatedData.getFireStations().size());
        assertTrue(updatedData.getFireStations().stream()
                .anyMatch(fireStation -> fireStation.getAddress().equals("1509 Culver St") && fireStation.getStation().equals("3")));
    }

    @Test
    @DisplayName("should get a FireStationCoverageResponse")
    void shouldGetFireStationCoverageResponse() throws Exception {
        //given
        final String station = "3";
        DataContainer initialDataContainer = dataManager.getDataContainer();

        //when & then
        mockMvc.perform(get("/firestation")
                .param("stationNumber", station))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.residents[0].firstName").value("Johnny"))
                .andExpect(jsonPath("$.residents[0].lastName").value("Boyd"))
                .andExpect(jsonPath("$.residents[0].address").value("1509 Culver St"))
                .andExpect(jsonPath("$.adultCount").value(1))
                .andExpect(jsonPath("$.childCount").value(0));
    }

    @Test
    @DisplayName("should return bad request for GetFireStationCoverageResponse")
    void shouldReturnBadRequestForGetFireStationCoverageResponse() throws Exception {
        final String station = "three";
        DataContainer initialDataContainer = dataManager.getDataContainer();

        //when & then
        mockMvc.perform(get("/firestation")
                .param("stationNumber", station))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid station, please enter a valid number"));
    }
}