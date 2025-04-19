package com.lulippe.safetynetalerts.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulippe.safetynetalerts.exception.entity_exception.InexistantObjectException;
import com.lulippe.safetynetalerts.exception.entity_exception.InvalidDataException;
import com.lulippe.safetynetalerts.exception.entity_exception.ObjectAlreadyExistsException;
import com.lulippe.safetynetalerts.medicalRecord.model.*;
import com.lulippe.safetynetalerts.service.MedicalRecordService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

    @Test
    @DisplayName("should update medicalRecord with add operation")
    void shouldUpdateMedicalRecordWithAddOperation() throws Exception {
        //given
        final String firstName = "John";
        final String lastName = "Doe";
        final MedicationUpdateEntry medicationUpdateEntry = new MedicationUpdateEntry();
        medicationUpdateEntry.setMedication("aznol:350mg");
        medicationUpdateEntry.setUpdateOperation(MedicationOperationType.ADD);
        List<MedicationUpdateEntry> medicines = new ArrayList<>();
        medicines.add(medicationUpdateEntry);
        final AllergyUpdateEntry allergyUpdateEntry = new AllergyUpdateEntry();
        allergyUpdateEntry.setName("dust");
        allergyUpdateEntry.setUpdateOperation(AllergyOperationType.ADD);
        List<AllergyUpdateEntry> allergies = new ArrayList<>();
        allergies.add(allergyUpdateEntry);
        final MedicalRecordUpdateDto medicalRecordUpdateDto = new MedicalRecordUpdateDto();
        medicalRecordUpdateDto.setFirstName(firstName);
        medicalRecordUpdateDto.setLastName(lastName);
        medicalRecordUpdateDto.setBirthdate("03/05/1995");
        medicalRecordUpdateDto.setMedications(medicines);
        medicalRecordUpdateDto.setAllergies(allergies);

        doNothing().when(medicalRecordService).handleMedicalRecordUpdateRequest(medicalRecordUpdateDto);

        //when & then
        mockMvc.perform(put("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicalRecordUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("MedicalRecord update successfully : " + medicalRecordUpdateDto));
    }

    @Test
    @DisplayName("Should update MedicalRecord with remove operation")
    void shouldUpdateMedicalRecordWithRemoveOperation() throws Exception {
        //given
        final String firstName = "John";
        final String lastName = "Doe";
        final MedicationUpdateEntry medicationUpdateEntry = new MedicationUpdateEntry();
        medicationUpdateEntry.setMedication("aznol:350mg");
        medicationUpdateEntry.setUpdateOperation(MedicationOperationType.REMOVE);
        List<MedicationUpdateEntry> medicines = new ArrayList<>();
        medicines.add(medicationUpdateEntry);
        final AllergyUpdateEntry allergyUpdateEntry = new AllergyUpdateEntry();
        allergyUpdateEntry.setName("dust");
        allergyUpdateEntry.setUpdateOperation(AllergyOperationType.REMOVE);
        List<AllergyUpdateEntry> allergies = new ArrayList<>();
        allergies.add(allergyUpdateEntry);
        final MedicalRecordUpdateDto medicalRecordUpdateDto = new MedicalRecordUpdateDto();
        medicalRecordUpdateDto.setFirstName(firstName);
        medicalRecordUpdateDto.setLastName(lastName);
        medicalRecordUpdateDto.setBirthdate("03/05/1995");
        medicalRecordUpdateDto.setMedications(medicines);
        medicalRecordUpdateDto.setAllergies(allergies);

        doNothing().when(medicalRecordService).handleMedicalRecordUpdateRequest(medicalRecordUpdateDto);

        //when & then
        mockMvc.perform(put("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicalRecordUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("MedicalRecord update successfully : " + medicalRecordUpdateDto));
    }

    @Test
    @DisplayName("should update MedicalRecord with update operation")
    void shouldUpdateMedicalRecordWithUpdateOperation() throws Exception {
        //given
        final String firstName = "John";
        final String lastName = "Doe";
        final MedicationUpdateEntry medicationUpdateEntry = new MedicationUpdateEntry();
        medicationUpdateEntry.setMedication("aznol:300mg");
        medicationUpdateEntry.setUpdateOperation(MedicationOperationType.UPDATE);
        List<MedicationUpdateEntry> medicines = new ArrayList<>();
        medicines.add(medicationUpdateEntry);
        final MedicalRecordUpdateDto medicalRecordUpdateDto = new MedicalRecordUpdateDto();
        medicalRecordUpdateDto.setFirstName(firstName);
        medicalRecordUpdateDto.setLastName(lastName);
        medicalRecordUpdateDto.setBirthdate("03/05/1995");
        medicalRecordUpdateDto.setMedications(medicines);

        doNothing().when(medicalRecordService).handleMedicalRecordUpdateRequest(medicalRecordUpdateDto);

        //when & then
        mockMvc.perform(put("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicalRecordUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("MedicalRecord update successfully : " + medicalRecordUpdateDto));
    }

    @Test
    @DisplayName("Should throw 409 status when medication already exists for add Operation")
    void shouldThrow409WhenMedicationAlreadyExistsForAddOperation() throws Exception {
        //given
        final String firstName = "John";
        final String lastName = "Doe";
        final MedicationUpdateEntry medicationUpdateEntry = new MedicationUpdateEntry();
        medicationUpdateEntry.setMedication("aznol:350mg");
        medicationUpdateEntry.setUpdateOperation(MedicationOperationType.ADD);
        List<MedicationUpdateEntry> medicines = new ArrayList<>();
        medicines.add(medicationUpdateEntry);
        final MedicalRecordUpdateDto medicalRecordUpdateDto = new MedicalRecordUpdateDto();
        medicalRecordUpdateDto.setFirstName(firstName);
        medicalRecordUpdateDto.setLastName(lastName);
        medicalRecordUpdateDto.setBirthdate("03/05/1995");
        medicalRecordUpdateDto.setMedications(medicines);

        doThrow(new ObjectAlreadyExistsException("medication already exists")).when(medicalRecordService).handleMedicalRecordUpdateRequest(medicalRecordUpdateDto);

        //when & then
        mockMvc.perform(put("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicalRecordUpdateDto)))
                .andExpect(status().isConflict())
                .andExpect(content().string("medication already exists"));
    }

    @Test
    @DisplayName("Should throw 409 status when allergy already exists for add Operation")
    void shouldThrow409WhenAllergyAlreadyExistsForAddOperation() throws Exception {
        //given
        final String firstName = "John";
        final String lastName = "Doe";
        final AllergyUpdateEntry allergyUpdateEntry = new AllergyUpdateEntry();
        allergyUpdateEntry.setName("dust");
        allergyUpdateEntry.setUpdateOperation(AllergyOperationType.ADD);
        List<AllergyUpdateEntry> allergies = new ArrayList<>();
        allergies.add(allergyUpdateEntry);
        final MedicalRecordUpdateDto medicalRecordUpdateDto = new MedicalRecordUpdateDto();
        medicalRecordUpdateDto.setFirstName(firstName);
        medicalRecordUpdateDto.setLastName(lastName);
        medicalRecordUpdateDto.setBirthdate("03/05/1995");
        medicalRecordUpdateDto.setAllergies(allergies);

        doThrow(new ObjectAlreadyExistsException("allergy already exists")).when(medicalRecordService).handleMedicalRecordUpdateRequest(medicalRecordUpdateDto);

        //when & then
        mockMvc.perform(put("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicalRecordUpdateDto)))
                .andExpect(status().isConflict())
                .andExpect(content().string("allergy already exists"));
    }

    @Test
    @DisplayName("should throw 400 when firstName or lastName is null for updateMedicalRecord")
    void shouldThrow400WhenFirstNameOrLastNameIsNull() throws Exception {
        //given
        final String firstName = null;
        final String lastName = "Doe";
        final AllergyUpdateEntry allergyUpdateEntry = new AllergyUpdateEntry();
        allergyUpdateEntry.setName("dust");
        allergyUpdateEntry.setUpdateOperation(AllergyOperationType.ADD);
        List<AllergyUpdateEntry> allergies = new ArrayList<>();
        allergies.add(allergyUpdateEntry);
        final MedicalRecordUpdateDto medicalRecordUpdateDto = new MedicalRecordUpdateDto();
        medicalRecordUpdateDto.setFirstName(firstName);
        medicalRecordUpdateDto.setLastName(lastName);
        medicalRecordUpdateDto.setBirthdate("03/05/1995");
        medicalRecordUpdateDto.setAllergies(allergies);

        doThrow(new InvalidDataException("firstName or lastName is blank ")).when(medicalRecordService).handleMedicalRecordUpdateRequest(medicalRecordUpdateDto);

        //when & then
        mockMvc.perform(put("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicalRecordUpdateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("firstName or lastName is blank "));
    }

    @Test
    @DisplayName("should throw 404 status when medicalRecord does not exist")
    void shouldThrow404WhenMedicalRecordDoesNotExist() throws Exception {
        //given
        final String firstName = "john";
        final String lastName = "Doe";
        final AllergyUpdateEntry allergyUpdateEntry = new AllergyUpdateEntry();
        allergyUpdateEntry.setName("dust");
        allergyUpdateEntry.setUpdateOperation(AllergyOperationType.ADD);
        List<AllergyUpdateEntry> allergies = new ArrayList<>();
        allergies.add(allergyUpdateEntry);
        final MedicalRecordUpdateDto medicalRecordUpdateDto = new MedicalRecordUpdateDto();
        medicalRecordUpdateDto.setFirstName(firstName);
        medicalRecordUpdateDto.setLastName(lastName);
        medicalRecordUpdateDto.setBirthdate("03/05/1995");
        medicalRecordUpdateDto.setAllergies(allergies);
        doThrow(new InexistantObjectException("medicalRecord does not exist")).when(medicalRecordService).handleMedicalRecordUpdateRequest(medicalRecordUpdateDto);

        //when & then
        mockMvc.perform(put("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicalRecordUpdateDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("medicalRecord does not exist"));
    }

    @Test
    @DisplayName("should throw status 400 when medication is null")
    void shouldThrow400WhenMedicationIsNull() throws Exception {
        //given
        final String firstName = "John";
        final String lastName = "Doe";
        final MedicationUpdateEntry medicationUpdateEntry = new MedicationUpdateEntry();
        medicationUpdateEntry.setMedication(null);
        medicationUpdateEntry.setUpdateOperation(MedicationOperationType.ADD);
        List<MedicationUpdateEntry> medicines = new ArrayList<>();
        medicines.add(medicationUpdateEntry);
        final MedicalRecordUpdateDto medicalRecordUpdateDto = new MedicalRecordUpdateDto();
        medicalRecordUpdateDto.setFirstName(firstName);
        medicalRecordUpdateDto.setLastName(lastName);
        medicalRecordUpdateDto.setBirthdate("03/05/1995");
        medicalRecordUpdateDto.setMedications(medicines);

        doThrow(new InvalidDataException("medication is null")).when(medicalRecordService).handleMedicalRecordUpdateRequest(medicalRecordUpdateDto);

        //when & then
        mockMvc.perform(put("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicalRecordUpdateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("medication is null"));
    }

    @Test
    @DisplayName("should throw 404 status when medication does not exist for remove operation")
    void shouldThrow404WhenMedicationDoesNotExistForRemoveOperation() throws Exception {
        //given
        final String firstName = "John";
        final String lastName = "Doe";
        final MedicationUpdateEntry medicationUpdateEntry = new MedicationUpdateEntry();
        medicationUpdateEntry.setMedication("aznol:350mg");
        medicationUpdateEntry.setUpdateOperation(MedicationOperationType.REMOVE);
        List<MedicationUpdateEntry> medicines = new ArrayList<>();
        medicines.add(medicationUpdateEntry);
        final AllergyUpdateEntry allergyUpdateEntry = new AllergyUpdateEntry();
        allergyUpdateEntry.setName("dust");
        allergyUpdateEntry.setUpdateOperation(AllergyOperationType.REMOVE);
        List<AllergyUpdateEntry> allergies = new ArrayList<>();
        allergies.add(allergyUpdateEntry);
        final MedicalRecordUpdateDto medicalRecordUpdateDto = new MedicalRecordUpdateDto();
        medicalRecordUpdateDto.setFirstName(firstName);
        medicalRecordUpdateDto.setLastName(lastName);
        medicalRecordUpdateDto.setBirthdate("03/05/1995");
        medicalRecordUpdateDto.setMedications(medicines);
        medicalRecordUpdateDto.setAllergies(allergies);

        doThrow(new InexistantObjectException("medication does not exist")).when(medicalRecordService).handleMedicalRecordUpdateRequest(medicalRecordUpdateDto);

        //when & then
        mockMvc.perform(put("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicalRecordUpdateDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("medication does not exist"));
    }

    @Test
    @DisplayName("should delete a MedicalRecord and return 200 status")
    void shouldDeleteMedicalRecord() throws Exception {
        //given
        String firstName = "John";
        String lastName = "Doe";
        doNothing().when(medicalRecordService).handleMedicalRecordDeleteRequest(firstName, lastName);

        //when & then
        mockMvc.perform(MockMvcRequestBuilders.delete("/medicalRecord")
                        .param("firstName", firstName)
                        .param("lastName", lastName)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().string("MedicalRecord with firstName " + firstName + " and lastName " + lastName + ", delete successfully."));
    }

    @Test
    @DisplayName("should return 404 when medicalRecord does not exist for deletion")
    void shouldReturnNotFoundWhenMedicalRecordDoesNotExistForDeletion() throws Exception {
        //given
        String firstName = "John";
        String lastName = "Doe";
        doThrow(new InexistantObjectException("the medicalRecord with firstName: " + firstName
                + " and lastName: " + lastName + " does not exist"))
                .when(medicalRecordService).handleMedicalRecordDeleteRequest(firstName, lastName);

        //when & then
        mockMvc.perform(MockMvcRequestBuilders.delete("/medicalRecord")
                        .param("firstName", firstName)
                        .param("lastName", lastName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("the medicalRecord with firstName: " + firstName
                        + " and lastName: " + lastName + " does not exist"));
    }

    @Test
    @DisplayName("should return 400 status when firstName or lastName is null for delete medicalRecord")
    void shouldReturnBadRequestWhenFirstNameOrLastNameIsBlankForDeletePerson() throws Exception {
        //given
        String firstName = "John";
        String lastName = "  ";
        doThrow(new InvalidDataException("firstName or lastName is blank "))
                .when(medicalRecordService).handleMedicalRecordDeleteRequest(firstName, lastName);

        //when & then
        mockMvc.perform(MockMvcRequestBuilders.delete("/medicalRecord")
                        .param("firstName", firstName)
                        .param("lastName", lastName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("firstName or lastName is blank "));
    }
}