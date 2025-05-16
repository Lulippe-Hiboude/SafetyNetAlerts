package com.lulippe.safetynetalerts.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulippe.safetynetalerts.personInfoLastName.model.ResidentInformation;
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

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class PersonInfoLastNameControllerIT {
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
    @DisplayName("should return a list of ResidentInformation")
    void shouldReturnAListOfResidentInformation() throws Exception {
        //given
        final String lastName ="Boyd";
        final String medication1 = "aznol:350mg";
        final String medication2 = "hydrapermazol:100mg";
        final List<String> medications = Arrays.asList(medication1, medication2);
        final String allergy ="nillacilan";
        final List<String> allergies = List.of(allergy);
        final ResidentInformation residentInformation = new ResidentInformation();
        residentInformation.setFirstName("Johnny");
        residentInformation.setAddress("1509 Culver St");
        residentInformation.setEmail("jaboyd@email.com");
        residentInformation.setAge(41);
        residentInformation.setMedications(medications);
        residentInformation.setAllergies(allergies);
        final List<ResidentInformation> expectedResidentInformationList= List.of(residentInformation);
        final String expectedJson = objectMapper.writeValueAsString(expectedResidentInformationList);

        //when & then
        mockMvc.perform(get("/personInfoLastName")
                        .param("lastName", lastName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(content().json(expectedJson));
    }

    @Test
    @DisplayName("should return BAD REQUEST if lastName is null or empty")
    void shouldReturnBadRequestIfLastNameIsNullOrEmpty() throws Exception {
        //given
        final String lastName ="";

        //when & then
        mockMvc.perform(get("/personInfoLastName")
                        .param("lastName", lastName))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("lastName cannot be null or empty" +lastName));
    }

    @Test
    @DisplayName("should return NOT FOUND if no Person found by lastname")
    void shouldReturnNotFoundIfPersonNotFoundByLastName() throws Exception {
        //given
        final String lastName ="Boydd";
        //when & then
        mockMvc.perform(get("/personInfoLastName")
                        .param("lastName", lastName))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("No Person found for this last name: " + lastName));
    }

    @Test
    @DisplayName("should return NOT FOUND if no medicalRecord found for a person")
    void shouldReturnNotFoundIfMedicalRecordNotFoundForAPerson() throws Exception {
        //given
        final String lastName ="Bid";
        //when & then
        mockMvc.perform(get("/personInfoLastName")
                        .param("lastName", lastName))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("the medicalRecord with firstName: " + "Johnny"
                        + " and lastName: " + lastName + " does not exist"));
    }
}
