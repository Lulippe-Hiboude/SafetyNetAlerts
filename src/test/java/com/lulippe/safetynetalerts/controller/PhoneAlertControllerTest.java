package com.lulippe.safetynetalerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulippe.safetynetalerts.exception.entity_exception.InexistantObjectException;
import com.lulippe.safetynetalerts.exception.entity_exception.InvalidDataException;
import com.lulippe.safetynetalerts.phoneAlert.model.PhoneAlertResponse;
import com.lulippe.safetynetalerts.service.PhoneAlertService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PhoneAlertController.class)
@AutoConfigureMockMvc
class PhoneAlertControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PhoneAlertService phoneAlertService;

    @Test
    @DisplayName("should return a list of PhoneAlertResponse")
    void shouldReturnListOfPhoneAlertResponse() throws Exception {
        //given
        final String phone = "123-456-898";
        final List<String> phoneNumbers = List.of(phone);
        final Integer stationNumber = 1;
        final PhoneAlertResponse phoneAlertResponse = new PhoneAlertResponse();
        phoneAlertResponse.setPhoneNumbers(phoneNumbers);
        phoneAlertResponse.setStationNumber(stationNumber);
        final List<PhoneAlertResponse> phoneAlertResponses = List.of(phoneAlertResponse);
        final String expectedJson = objectMapper.writeValueAsString(phoneAlertResponses);
        given(phoneAlertService.getPhoneAlertResponse(stationNumber)).willReturn(phoneAlertResponses);

        //when & then
        mockMvc.perform(get("/phoneAlert")
        .param("firestation", String.valueOf(1)))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @DisplayName("should return Bad Request")
    void shouldReturnBadRequest() throws Exception {
        //given
        final int stationNumber = -1;
        given(phoneAlertService.getPhoneAlertResponse(stationNumber)).willThrow(new InvalidDataException("invalid station number"));

        //when & then
        mockMvc.perform(get("/phoneAlert")
                        .param("firestation", String.valueOf(stationNumber)))
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("should return Not Found")
    void shouldReturnNotFound() throws Exception {
        //given
        final int stationNumber = 1;
        given(phoneAlertService.getPhoneAlertResponse(stationNumber)).willThrow(new InexistantObjectException("no FireStation found"));

        //when & then
        mockMvc.perform(get("/phoneAlert")
                        .param("firestation", String.valueOf(stationNumber)))
                .andExpect(status().isNotFound());
    }
}