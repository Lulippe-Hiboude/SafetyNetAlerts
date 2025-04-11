package com.lulippe.safetynetalerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulippe.safetynetalerts.exception.person_exception.InvalidDataException;
import com.lulippe.safetynetalerts.exception.person_exception.ObjectAlreadyExistsException;
import com.lulippe.safetynetalerts.medicalRecord.model.MedicalRecordDto;
import com.lulippe.safetynetalerts.service.MedicalRecordService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MedicalRecordController.class)
@AutoConfigureMockMvc
class MedicalRecordControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private MedicalRecordService medicalRecordService;

    @Test
    @DisplayName("Should create a new MedicalRecord")
    void shouldCreateNewMedicalRecord() throws Exception {
        //given
        List<String> medicines = new ArrayList<>();
        medicines.add("aznol:350mg");
        medicines.add("hydrapermazol:100mg");
        List<String> allergies = new ArrayList<>();
        allergies.add("peanut");
        MedicalRecordDto newMedicalRecordDto = new MedicalRecordDto();
        newMedicalRecordDto.setFirstName("John");
        newMedicalRecordDto.setLastName("Doe");
        newMedicalRecordDto.setBirthdate("03/05/1995");
        newMedicalRecordDto.setMedications(medicines);
        newMedicalRecordDto.setAllergies(allergies);

        doNothing().when(medicalRecordService).handleMedicalRecordCreationRequest(newMedicalRecordDto);

        //when & then
        mockMvc.perform(post("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMedicalRecordDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("MedicalRecord created successfully " + newMedicalRecordDto));
    }

    @Test
    @DisplayName("should return 400 when firstName or lastName is null for CreateMedicalRecord")
    void shouldReturn400WhenFirstNameOrLastNameIsNull() throws Exception {
        //given
        List<String> medicines = new ArrayList<>();
        medicines.add("aznol:350mg");
        medicines.add("hydrapermazol:100mg");
        List<String> allergies = new ArrayList<>();
        allergies.add("peanut");
        MedicalRecordDto newMedicalRecordDto = new MedicalRecordDto();
        newMedicalRecordDto.setFirstName(null);
        newMedicalRecordDto.setLastName("Doe");
        newMedicalRecordDto.setBirthdate("03/05/1995");
        newMedicalRecordDto.setMedications(medicines);
        newMedicalRecordDto.setAllergies(allergies);

        doThrow(new InvalidDataException("firstName or lastName is blank ")).when(medicalRecordService).handleMedicalRecordCreationRequest(newMedicalRecordDto);
        //when & then
        mockMvc.perform(post("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMedicalRecordDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("firstName or lastName is blank "));
    }

    @Test
    @DisplayName("should return 409 when medicalRecord already exists")
    void shouldReturn409WhenMedicalRecordAlreadyExists() throws Exception {
        //given
        List<String> medicines = new ArrayList<>();
        medicines.add("aznol:350mg");
        medicines.add("hydrapermazol:100mg");
        List<String> allergies = new ArrayList<>();
        allergies.add("peanut");
        MedicalRecordDto newMedicalRecordDto = new MedicalRecordDto();
        newMedicalRecordDto.setFirstName("John");
        newMedicalRecordDto.setLastName("Doe");
        newMedicalRecordDto.setBirthdate("03/05/1995");
        newMedicalRecordDto.setMedications(medicines);
        newMedicalRecordDto.setAllergies(allergies);

        doThrow(new ObjectAlreadyExistsException("MedicalRecord already exists")).when(medicalRecordService).handleMedicalRecordCreationRequest(newMedicalRecordDto);

        //when & then
        mockMvc.perform(post("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMedicalRecordDto)))
                .andExpect(status().isConflict())
                .andExpect(content().string("MedicalRecord already exists"));
    }

}