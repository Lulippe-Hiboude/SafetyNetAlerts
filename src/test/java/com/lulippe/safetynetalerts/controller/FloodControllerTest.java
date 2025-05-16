package com.lulippe.safetynetalerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulippe.safetynetalerts.exception.entity_exception.InexistantObjectException;
import com.lulippe.safetynetalerts.exception.entity_exception.InvalidDataException;
import com.lulippe.safetynetalerts.flood.model.PersonInformationByStationNumber;
import com.lulippe.safetynetalerts.service.FloodService;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FloodController.class)
@AutoConfigureMockMvc
class FloodControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FloodService floodService;
    private final EasyRandom easyRandom = new EasyRandom();

    @Test
    @DisplayName("should return a list of PersonInformationByStationNumber")
    void shouldReturnListOfPersonInformationByStationNumber() throws Exception {
        //given
        final Integer stationNumber = 1;
        final List<Integer> stationNumbers = List.of(stationNumber);
        final PersonInformationByStationNumber personInformationByStationNumber1= easyRandom.nextObject(PersonInformationByStationNumber.class);
        final PersonInformationByStationNumber personInformationByStationNumber2= easyRandom.nextObject(PersonInformationByStationNumber.class);
        final List<PersonInformationByStationNumber> expectedPersonInformationByStationNumber = List.of(personInformationByStationNumber1,personInformationByStationNumber2);
        given(floodService.getResidentsInformationsByStationNumbers(stationNumbers)).willReturn(expectedPersonInformationByStationNumber);

        //when & then
        mockMvc.perform(get("/flood")
                .param("stations", "1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("should return 400 BAD REQUEST")
    void shouldReturn400BadRequest() throws Exception {

        final List<Integer> stationNumbers = Collections.emptyList();
        given(floodService.getResidentsInformationsByStationNumbers(stationNumbers)).willThrow(new InvalidDataException("Bad Request"));

        //when & then
        mockMvc.perform(get("/flood")
                        .param("stations", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should return 404 NOT FOUND")
    void shouldReturn404NotFound() throws Exception {
        //given
        final Integer stationNumber = 1;
        final List<Integer> stationNumbers = List.of(stationNumber);
        given(floodService.getResidentsInformationsByStationNumbers(stationNumbers)).willThrow(new InexistantObjectException("Not FOUND"));

        //when & then
        mockMvc.perform(get("/flood")
                        .param("stations", "1"))
                .andExpect(status().isNotFound());
    }
}