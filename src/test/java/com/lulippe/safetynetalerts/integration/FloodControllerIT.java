package com.lulippe.safetynetalerts.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulippe.safetynetalerts.flood.model.PersonInformationByStationNumber;
import com.lulippe.safetynetalerts.flood.model.ResidentFloodInformation;
import com.lulippe.safetynetalerts.flood.model.ResidentsFloodInformations;
import com.lulippe.safetynetalerts.repository.manager.DataManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class FloodControllerIT {
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
    @DisplayName("should return a list of PersonInformationByStationNumber")
    void shouldReturnAListOfPersonInformationByStationNumber() throws Exception {
        //given
        final String stationNumber = "3";
        final String allergy = "nillacilan";
        final String medication1 = "aznol:350mg";
        final String medication2 = "hydrapermazol:100mg";
        final List<String> medications = Arrays.asList(medication1, medication2);
        final List<String> allergies = Collections.singletonList(allergy);
        final ResidentFloodInformation residentFloodInformation = new ResidentFloodInformation();
        residentFloodInformation.setFirstName("Johnny");
        residentFloodInformation.setLastName("Boyd");
        residentFloodInformation.setAge(41);
        residentFloodInformation.setPhone("841-874-6512");
        residentFloodInformation.setAllergies(allergies);
        residentFloodInformation.setMedications(medications);
        final List<ResidentFloodInformation> residentFloodInformations = Collections.singletonList(residentFloodInformation);
        final ResidentsFloodInformations residentsFloodInformations = new ResidentsFloodInformations();
        residentsFloodInformations.setAddress("1509 Culver St");
        residentsFloodInformations.setResidents(residentFloodInformations);
        final List<ResidentsFloodInformations> residentsFloodInformationsList = List.of(residentsFloodInformations);
        final PersonInformationByStationNumber personInformationByStationNumber = new PersonInformationByStationNumber();
        personInformationByStationNumber.setStationNumber(3);
        personInformationByStationNumber.setHouseholds(residentsFloodInformationsList);
        final List<PersonInformationByStationNumber> personInformationByStationNumberList = Collections.singletonList(personInformationByStationNumber);
        final String expectedJson = objectMapper.writeValueAsString(personInformationByStationNumberList);

        //when & then
        mockMvc.perform(get("/flood")
                        .param("stations", stationNumber))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @DisplayName("should return BAD REQUEST")
    void shouldReturnBADRequest() throws Exception {
        //given
        final String stationNumber = "-3";
        mockMvc.perform(get("/flood")
                        .param("stations", stationNumber))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should return NOT FOUND")
    void shouldReturnNotFound() throws Exception {
        //given
        final String stationNumber = "7";
        mockMvc.perform(get("/flood")
                        .param("stations", stationNumber))
                .andExpect(status().isNotFound());
    }
}
