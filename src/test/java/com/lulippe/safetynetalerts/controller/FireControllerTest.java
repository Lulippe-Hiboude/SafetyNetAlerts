package com.lulippe.safetynetalerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulippe.safetynetalerts.exception.entity_exception.InexistantObjectException;
import com.lulippe.safetynetalerts.exception.entity_exception.InvalidDataException;
import com.lulippe.safetynetalerts.fire.model.FireAddressResponse;
import com.lulippe.safetynetalerts.service.FireService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = FireController.class)
@AutoConfigureMockMvc
class FireControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FireService fireService;

    @Test
    @DisplayName("should return FireAddressResponse when address is valid")
    void shouldReturnFireAddressResponse() throws Exception {
        final String address = "123 main street";
        final FireAddressResponse fireAddressResponse = new FireAddressResponse();
        fireAddressResponse.setStationNumber("1");
        fireAddressResponse.setResidents(List.of());
        given(fireService.handleFireRequest(address)).willReturn(fireAddressResponse);

        //when & then
        mockMvc.perform(get("/fire")
                        .param("address", address))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stationNumber").value("1"));
    }

    @Test
    @DisplayName("should return Not found if no fireStation found for given address")
    void shouldReturnNotFoundIfFireStationNotFound() throws Exception {
        //given
        String address = "123 main street";
        given(fireService.handleFireRequest(address)).willThrow(new InexistantObjectException("No fireStation for this address"));

        //when & then
        mockMvc.perform(get("/fire")
                        .param("address", address))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No fireStation for this address"));
    }

    @Test
    @DisplayName("should return bad request when Invalid address")
    void shouldReturnBadRequestIfInvalidAddress() throws Exception {
        //given
        final String address = "";
        given(fireService.handleFireRequest(address)).willThrow(new InvalidDataException("Address cannot be null or empty"));

        //when & then
        mockMvc.perform(get("/fire")
                        .param("address", address))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Address cannot be null or empty"));
    }
}