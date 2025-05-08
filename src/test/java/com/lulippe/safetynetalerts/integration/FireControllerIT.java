package com.lulippe.safetynetalerts.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulippe.safetynetalerts.dao.DataContainer;
import com.lulippe.safetynetalerts.fire.model.FireAddressResponse;
import com.lulippe.safetynetalerts.fire.model.FireResident;
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

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class FireControllerIT {
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
    @DisplayName("should return fireAddressResponse")
    void shouldReturnFireAddressResponse() throws Exception {
        //given
        final String address = "1509 Culver St";

        final List<String> medications = new ArrayList<>();
        medications.add("aznol:350mg");
        medications.add("hydrapermazol:100mg");

        final List<String>allergies = new ArrayList<>();
        allergies.add("nillacilan");

        final FireResident fireResident = new FireResident();
        fireResident.setFirstName("Johnny");
        fireResident.setLastName("Boyd");
        fireResident.setPhone("841-874-6512");
        fireResident.setAge(41);
        fireResident.setMedications(medications);
        fireResident.setAllergies(allergies);
        final FireAddressResponse expectedResponse = new FireAddressResponse();
        expectedResponse.setStationNumber("3");
        expectedResponse.setResidents(List.of(fireResident));

        final String expectedJson = objectMapper.writeValueAsString(expectedResponse);
        DataContainer initialDataContainer = dataManager.getDataContainer();

        //when & then
        mockMvc.perform(get("/fire")
                .param("address", address))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @DisplayName("should return BAD_REQUEST if address is null or empty")
    void shouldReturnBadRequestIfAddressIsEmpty() throws Exception {
        final String address = "";
        DataContainer initialDataContainer = dataManager.getDataContainer();

        //when & then
        mockMvc.perform(get("/fire")
                        .param("address", address))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("address cannot be null or empty"));
    }

    @Test
    @DisplayName("should return Not_Found for given address")
    void shouldReturnNotFoundForGivenAddress() throws Exception {
        final String address = "1508 Culver St";
        DataContainer initialDataContainer = dataManager.getDataContainer();

        //when & then
        mockMvc.perform(get("/fire")
                        .param("address", address))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("No fireStation for this address"));
    }


}
