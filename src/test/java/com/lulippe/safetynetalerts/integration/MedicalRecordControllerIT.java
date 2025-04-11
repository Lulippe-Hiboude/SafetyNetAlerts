package com.lulippe.safetynetalerts.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulippe.safetynetalerts.dao.DataContainer;
import com.lulippe.safetynetalerts.medicalRecord.model.MedicalRecordDto;
import com.lulippe.safetynetalerts.repository.manager.DataManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class MedicalRecordControllerIT {
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
    @DisplayName("Should create a new MedicalRecord and update the dataContainer")
    void shouldCreateNewMedicalRecordAndUpdateDataContainer() throws Exception {
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

        DataContainer initialDataContainer = dataManager.getDataContainer();
        assertEquals(1, initialDataContainer.getMedicalRecords().size());
        assertFalse(initialDataContainer.getMedicalRecords().stream()
                .anyMatch(medicalRecord -> medicalRecord.getFirstName().equals("John") && medicalRecord.getLastName().equals("Doe")));
        //when & then
        mockMvc.perform(post("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMedicalRecordDto)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("MedicalRecord created successfully " + newMedicalRecordDto));
        DataContainer updatedData = dataManager.getDataContainer();
        assertEquals(2, updatedData.getMedicalRecords().size());
        assertTrue(updatedData.getMedicalRecords().stream()
                .anyMatch(medicalRecord -> medicalRecord.getFirstName().equals("John") && medicalRecord.getLastName().equals("Doe")));
    }

    @Test
    @DisplayName("Should return conflict (409) status when MedicalRecord already exists")
    void shouldReturnConflictStatusWhenMedicalRecordAlreadyExists() throws Exception {
        //given
        List<String> medicines = new ArrayList<>();
        medicines.add("aznol:350mg");
        medicines.add("hydrapermazol:100mg");
        List<String> allergies = new ArrayList<>();
        allergies.add("peanut");
        MedicalRecordDto newMedicalRecordDto = new MedicalRecordDto();
        newMedicalRecordDto.setFirstName("Johnny");
        newMedicalRecordDto.setLastName("Boyd");
        newMedicalRecordDto.setBirthdate("03/05/1995");
        newMedicalRecordDto.setMedications(medicines);
        newMedicalRecordDto.setAllergies(allergies);
        DataContainer initialDataContainer = dataManager.getDataContainer();
        assertEquals(1, initialDataContainer.getMedicalRecords().size());
        assertTrue(initialDataContainer.getMedicalRecords().stream()
                .anyMatch(medicalRecord -> medicalRecord.getFirstName().equals("Johnny") && medicalRecord.getLastName().equals("Boyd")));

        //when & then
        mockMvc.perform(post("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMedicalRecordDto)))
                .andExpect(status().isConflict())
                .andExpect(MockMvcResultMatchers.content().string("Medical record already exists with first name: " + newMedicalRecordDto.getFirstName()
                        + " and last name: " + newMedicalRecordDto.getLastName()));
        DataContainer updatedData = dataManager.getDataContainer();
        assertEquals(1, updatedData.getMedicalRecords().size());
        assertTrue(updatedData.getMedicalRecords().stream()
                .anyMatch(medicalRecord -> medicalRecord.getFirstName().equals("Johnny")
                        && medicalRecord.getLastName().equals("Boyd")
                        && medicalRecord.getBirthdate().equals("03/06/1984")));
    }

    @Test
    @DisplayName("should return 400 when firstName or lastName is null")
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

        DataContainer initialDataContainer = dataManager.getDataContainer();
        assertEquals(1, initialDataContainer.getMedicalRecords().size());
        assertFalse(initialDataContainer.getMedicalRecords().stream()
                .anyMatch(medicalRecord -> medicalRecord.getFirstName().equals("Johnny") && medicalRecord.getLastName().equals("Doe")));

        mockMvc.perform(post("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMedicalRecordDto)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("firstName or LastName is blank: " + newMedicalRecordDto.getFirstName() + ", " + newMedicalRecordDto.getLastName()));
        DataContainer updatedData = dataManager.getDataContainer();
        assertEquals(1, updatedData.getMedicalRecords().size());
        assertTrue(updatedData.getMedicalRecords().stream()
                .anyMatch(medicalRecord -> medicalRecord.getFirstName().equals("Johnny") && medicalRecord.getLastName().equals("Boyd")));
    }

    @Test
    @DisplayName("should return 400 when medicalRecordCreateDto is null")
    void shouldReturnBadRequestWhenMedicalRecordCreateDtoHasNullValues() throws Exception {
        //given
        DataContainer initialDataContainer = dataManager.getDataContainer();
        assertEquals(1, initialDataContainer.getMedicalRecords().size());
        assertTrue(initialDataContainer.getMedicalRecords().stream()
                .anyMatch(medicalRecord -> medicalRecord.getFirstName().equals("Johnny") && medicalRecord.getLastName().equals("Boyd")));

        //when & then
        mockMvc.perform(post("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(null)))
                .andExpect(status().isBadRequest());
    }
}
