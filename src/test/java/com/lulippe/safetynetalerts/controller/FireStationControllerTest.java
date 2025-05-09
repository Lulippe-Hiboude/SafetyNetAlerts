package com.lulippe.safetynetalerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulippe.safetynetalerts.exception.entity_exception.InexistantObjectException;
import com.lulippe.safetynetalerts.exception.entity_exception.InvalidDataException;
import com.lulippe.safetynetalerts.exception.entity_exception.ObjectAlreadyExistsException;
import com.lulippe.safetynetalerts.fireStation.model.FireStationCoverageResponse;
import com.lulippe.safetynetalerts.fireStation.model.FireStationDto;
import com.lulippe.safetynetalerts.service.FireStationService;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FireStationController.class)
@AutoConfigureMockMvc
class FireStationControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FireStationService fireStationService;

    private final EasyRandom easyRandom = new EasyRandom();

    @Test
    @DisplayName("Should create a new fireStation")
    void shouldCreateNewFireStation() throws Exception {
        //given
        final String address = "10 downing street";
        final String station = "1";
        FireStationDto fireStationDto = new FireStationDto();
        fireStationDto.setAddress(address);
        fireStationDto.setStation(station);
        doNothing().when(fireStationService).handleFireStationCreationRequest(fireStationDto);

        //when & then
        mockMvc.perform(post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fireStationDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("FireStation create successfully : " + fireStationDto));
    }

    @Test
    @DisplayName("should return 400 when address or station is null for creation")
    void shouldReturn400WhenAddressOrStationIsNullForCreation() throws Exception {
        //given
        final String address = null;
        final String station = "1";
        FireStationDto fireStationDto = new FireStationDto();
        fireStationDto.setAddress(address);
        fireStationDto.setStation(station);
        doThrow(new InvalidDataException("Address or Station is null")).when(fireStationService).handleFireStationCreationRequest(fireStationDto);

        //when & then
        mockMvc.perform(MockMvcRequestBuilders.post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fireStationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Address or Station is null"));
    }

    @Test
    @DisplayName("should return 409 when fireStation already exists for creation")
    void shouldReturn409WhenFireStationAlreadyExistsForCreation() throws Exception {
        //given
        final String address = "10 downing street";
        final String station = "1";
        FireStationDto fireStationDto = new FireStationDto();
        fireStationDto.setAddress(address);
        fireStationDto.setStation(station);
        doThrow(new ObjectAlreadyExistsException("FireStation already exists")).when(fireStationService).handleFireStationCreationRequest(fireStationDto);

        //when & then
        mockMvc.perform(MockMvcRequestBuilders.post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fireStationDto)))
                .andExpect(status().isConflict())
                .andExpect(content().string("FireStation already exists"));
    }

    @Test
    @DisplayName("should update an existing fireStation")
    void shouldUpdateExistingFireStation() throws Exception {
        //given
        final String address = "10 downing street";
        final String station = "1";
        FireStationDto fireStationDto = new FireStationDto();
        fireStationDto.setAddress(address);
        fireStationDto.setStation(station);
        doNothing().when(fireStationService).handleFireStationUpdateRequest(fireStationDto);

        //when & then
        mockMvc.perform(put("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fireStationDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("FireStation updated successfully : " + fireStationDto));
    }

    @Test
    @DisplayName("should throw Conflict when station number is already used for another fireStation")
    void shouldThrowConflictWhenStationNumberIsAlreadyUsedForAnotherFireStation() throws Exception {
        //given
        final String address = "10 downing street";
        final String station = "1";
        FireStationDto fireStationDto = new FireStationDto();
        fireStationDto.setAddress(address);
        fireStationDto.setStation(station);
        doThrow(new ObjectAlreadyExistsException("station number is already taken by another fireStation" + station)).when(fireStationService).handleFireStationUpdateRequest(fireStationDto);

        //when & then
        mockMvc.perform(MockMvcRequestBuilders.put("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fireStationDto)))
                .andExpect(status().isConflict())
                .andExpect(content().string("station number is already taken by another fireStation" + station));
    }

    @Test
    @DisplayName("should throw Bad_Request when data is blank")
    void shouldThrowBadRequestWhenDataIsBlank() throws Exception {
        //given
        final String address = "10 downing street";
        final String station = "";
        FireStationDto fireStationDto = new FireStationDto();
        fireStationDto.setAddress(address);
        fireStationDto.setStation(station);
        doThrow(new InvalidDataException(address + " or " + station + " is blank: " + fireStationDto.getAddress() + ", " + fireStationDto.getStation())).when(fireStationService).handleFireStationUpdateRequest(fireStationDto);

        //when & then
        mockMvc.perform(MockMvcRequestBuilders.put("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fireStationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(address + " or " + station + " is blank: " + fireStationDto.getAddress() + ", " + fireStationDto.getStation()));
    }

    @Test
    @DisplayName("should throw Not_Found when fireStation does not exist for update")
    void shouldThrowNotFoundWhenFireStationDoesNotExistForUpdate() throws Exception {
        //given
        final String address = "10 downing street";
        final String station = "1";
        FireStationDto fireStationDto = new FireStationDto();
        fireStationDto.setAddress(address);
        fireStationDto.setStation(station);
        doThrow(new InexistantObjectException("fireStation does not exist with this address: " + fireStationDto.getAddress())).when(fireStationService).handleFireStationUpdateRequest(fireStationDto);

        //when & then
        mockMvc.perform(MockMvcRequestBuilders.put("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fireStationDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("fireStation does not exist with this address: " + fireStationDto.getAddress()));
    }

    @Test
    @DisplayName("should delete an existing FireStation")
    void shouldDeleteExistingFireStation() throws Exception {
        //given
        final String address = "10 downing street";
        final String station = "1";
        final FireStationDto fireStationDto = new FireStationDto();
        fireStationDto.setAddress(address);
        fireStationDto.setStation(station);
        doNothing().when(fireStationService).handleFireStationDeleteRequest(fireStationDto);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.delete("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fireStationDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("FireStation deleted successfully : " + fireStationDto));
    }

    @Test
    @DisplayName("should Throw Not Found when FireStation does not exist for given address and station")
    void shouldThrowNotFoundWhenFireStationDoesNotExistForGivenAddressAndStation() throws Exception {
        final String address = "10 downing street";
        final String station = "1";
        FireStationDto fireStationDto = new FireStationDto();
        fireStationDto.setAddress(address);
        fireStationDto.setStation(station);
        doThrow(new InexistantObjectException("fireStation does not exist with this address and this station: address : "
                + fireStationDto.getAddress() + ", station : " + fireStationDto.getStation())).when(fireStationService).handleFireStationDeleteRequest(fireStationDto);
        // when & then
        mockMvc.perform(MockMvcRequestBuilders.delete("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fireStationDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("fireStation does not exist with this address and this station: address : "
                        + fireStationDto.getAddress() + ", station : " + fireStationDto.getStation()));
    }

    @Test
    @DisplayName("should throw bad request when data is blank for delete")
    void shouldThrowBadRequestWhenDataIsBlankForDelete() throws Exception {
        //given
        final String address = "10 downing street";
        final String station = "";
        FireStationDto fireStationDto = new FireStationDto();
        fireStationDto.setAddress(address);
        fireStationDto.setStation(station);
        doThrow(new InvalidDataException("fireStationAddress" + " or " + "Station number" + " is blank: " + address + ", " + station)).when(fireStationService).handleFireStationDeleteRequest(fireStationDto);

        //when & then
        mockMvc.perform(MockMvcRequestBuilders.delete("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fireStationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("fireStationAddress" + " or " + "Station number" + " is blank: " + address + ", " + station));
    }

    @Test
    @DisplayName("should get FireStationCoverageResponse")
    void shouldGetFireStationCoverageResponse() throws Exception {
        final String stationNumber = "1";
        final FireStationCoverageResponse fireStationCoverageResponse = easyRandom.nextObject(FireStationCoverageResponse.class);
        given(fireStationService.getResidentsByFireStation(stationNumber)).willReturn(fireStationCoverageResponse);

        //when & then
        mockMvc.perform(MockMvcRequestBuilders.get("/firestation")
                        .param("stationNumber", stationNumber))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(fireStationCoverageResponse)));
    }

    @Test
    @DisplayName("should return not found if service throw an InexistantObjectException")
    void shouldReturnNotFoundWhenServiceThrowAnInexistantObjectException() throws Exception {
        final String stationNumber = "1";
        given(fireStationService.getResidentsByFireStation(stationNumber)).willThrow(new InexistantObjectException("No fire station found"));

        //when & then
        mockMvc.perform(MockMvcRequestBuilders.get("/firestation")
                        .param("stationNumber", stationNumber))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No fire station found"));
    }

    @Test
    @DisplayName("should return Bad request if service throw an InvalidDataException")
    void shouldReturnBadRequestWhenServiceThrowAnInvalidDataException() throws Exception {
        final String stationNumber = "quatre";
        given(fireStationService.getResidentsByFireStation(stationNumber)).willThrow(new InvalidDataException("Invalid station, please enter a valid number"));

        //when & then
        mockMvc.perform(MockMvcRequestBuilders.get("/firestation")
                        .param("stationNumber", stationNumber))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid station, please enter a valid number"));
    }
}