package com.lulippe.safetynetalerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulippe.safetynetalerts.exception.entity_exception.InexistantObjectException;
import com.lulippe.safetynetalerts.exception.entity_exception.InvalidDataException;
import com.lulippe.safetynetalerts.personInfoLastName.model.ResidentInformation;
import com.lulippe.safetynetalerts.service.PersonInfoLastNameService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PersonInfoLastNameController.class)
@AutoConfigureMockMvc
class PersonInfoLastNameControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PersonInfoLastNameService personInfoLastNameService;

    @Test
    @DisplayName("should return a list of ResidentInformation")
    void shouldReturnListOfResidentInformation() throws Exception {
        //given
        final String lastName = "Dupont";
        final String firstName = "Brigitte";
        final String address = "address";
        final String email = "email";
        final String medication = "aznol:350mg";
        final String allergy = "dust";
        final List<String> medications = Collections.singletonList(medication);
        final List<String> allergies = Collections.singletonList(allergy);
        final ResidentInformation residentInformation = new ResidentInformation();
        residentInformation.setFirstName(firstName);
        residentInformation.setAddress(address);
        residentInformation.setEmail(email);
        residentInformation.setMedications(medications);
        residentInformation.setAllergies(allergies);
        final List<ResidentInformation> expectedResidentInformationList = Collections.singletonList(residentInformation);
        given(personInfoLastNameService.getPersonInfoLastName(lastName)).willReturn(expectedResidentInformationList);

        //when & then
        mockMvc.perform(get("/personInfoLastName")
                .param("lastName", lastName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName").value(expectedResidentInformationList.getFirst().getFirstName()));
    }

    @Test
    @DisplayName("should return NOT FOUND if OjectNotFound Exception is thrown")
    void shouldReturnNotFoundIfObjectNotFound() throws Exception {
        //given
        final String lastName = "Dupont";
        given(personInfoLastNameService.getPersonInfoLastName(lastName)).willThrow(new InexistantObjectException(" object not found"));

        // when & then
        mockMvc.perform(get("/personInfoLastName")
                .param("lastName", lastName))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should return BAD REQUEST when lastName is invalid")
    void shouldReturnBadRequestWhenLastNameIsInvalid() throws Exception {
        //given
        final String lastName = "";
        given(personInfoLastNameService.getPersonInfoLastName(lastName)).willThrow(new InvalidDataException("lastName is invalid"));

        //when & then
        mockMvc.perform(get("/personInfoLastName")
                .param("lastName", lastName))
                .andExpect(status().isBadRequest());
    }
}