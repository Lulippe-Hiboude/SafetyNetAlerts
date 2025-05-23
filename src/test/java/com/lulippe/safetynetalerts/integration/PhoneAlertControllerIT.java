package com.lulippe.safetynetalerts.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulippe.safetynetalerts.phoneAlert.model.PhoneAlertResponse;
import com.lulippe.safetynetalerts.repository.manager.DataManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class PhoneAlertControllerIT {
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
    @DisplayName("should return a list of PhoneAlertResponse")
    void shouldReturnListOfPhoneAlertResponse() throws Exception {
        //given
        final int stationNumber = 3;

        final String phoneNumber = "841-874-6512";
        List<String> phoneNumbers = List.of(phoneNumber);
        final PhoneAlertResponse phoneAlertResponse = new PhoneAlertResponse();
        phoneAlertResponse.setStationNumber(stationNumber);
        phoneAlertResponse.setPhoneNumbers(phoneNumbers);
        final List<PhoneAlertResponse> expectedPhoneAlertResponses = List.of(phoneAlertResponse);
        final String expectedJson = objectMapper.writeValueAsString(expectedPhoneAlertResponses);

        //when & then
        mockMvc.perform(get("/phoneAlert")
                .param("firestation", String.valueOf(stationNumber)))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @DisplayName("should return Bad Request")
    void shouldReturnBadRequest() throws Exception {
        //given
        final int stationNumber = -3;

        //when & then
        mockMvc.perform(get("/phoneAlert")
                        .param("firestation", String.valueOf(stationNumber)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid stations number"));
    }

    @Test
    @DisplayName("should return Not Found")
    void shouldReturnNotFound() throws Exception {
        final int stationNumber = 6;
        //when & then
        mockMvc.perform(get("/phoneAlert")
                        .param("firestation", String.valueOf(stationNumber)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No fireStations found for this station number: " + stationNumber));
    }
}