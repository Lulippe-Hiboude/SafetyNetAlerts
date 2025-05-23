package com.lulippe.safetynetalerts.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulippe.safetynetalerts.childAlert.model.ChildAlertResponse;
import com.lulippe.safetynetalerts.repository.manager.DataManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class ChildAlertControllerIT {
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
    @DisplayName("should return ChildAlertResponse")
    void shouldReturnChildAlertResponse() throws Exception {
        //given
        final String address = "1509 Culver St";
        final ChildAlertResponse expectedChildAlertResponse = new ChildAlertResponse();
        expectedChildAlertResponse.setChildren(Collections.emptyList());
        final String expectedJson = objectMapper.writeValueAsString(expectedChildAlertResponse);

        //when & then
        mockMvc.perform(get("/childAlert")
                .param("address", address))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @DisplayName("should return BAD_REQUEST")
    void shouldReturnBadRequest() throws Exception {
        //given
        final String address = "";

        //when & then
        mockMvc.perform(get("/childAlert")
                        .param("address", address))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("address cannot be null or empty"));
    }

    @Test
    @DisplayName("should return NOT_FOUND")
    void shouldReturnNotFound() throws Exception {
        //given
        final String address = "1502 Culver St";

        //when & then
        mockMvc.perform(get("/childAlert")
                        .param("address", address))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("No person found for address: " + address));
    }
}