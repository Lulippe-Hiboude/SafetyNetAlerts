package com.lulippe.safetynetalerts.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulippe.safetynetalerts.dao.DataContainer;
import com.lulippe.safetynetalerts.medicalRecord.model.*;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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

    @Test
    @DisplayName("should update an existing MedicalRecord with add operation")
    void shouldUpdateMedicalRecordWithAddOperation() throws Exception {
        //given
        final String firstName = "Johnny";
        final String lastName = "Boyd";
        final MedicationUpdateEntry medicationUpdateEntry = new MedicationUpdateEntry();
        medicationUpdateEntry.setMedication("paracetamol:1000mg");
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

        DataContainer initialDataContainer = dataManager.getDataContainer();
        assertEquals(1, initialDataContainer.getMedicalRecords().size());
        assertTrue(initialDataContainer.getMedicalRecords().stream()
                .anyMatch(medicalRecord -> medicalRecord.getFirstName().equals("Johnny") && medicalRecord.getLastName().equals("Boyd")));

        //when & then
        mockMvc.perform(put("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicalRecordUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("MedicalRecord update successfully : " + medicalRecordUpdateDto));
        DataContainer updatedData = dataManager.getDataContainer();
        assertEquals(1, updatedData.getMedicalRecords().size());
        assertTrue(updatedData.getMedicalRecords().getFirst().getMedications().contains(medicationUpdateEntry.getMedication()));
        assertTrue(updatedData.getMedicalRecords().getFirst().getAllergies().contains(allergyUpdateEntry.getName()));
    }

    @Test
    @DisplayName("should update MedicalRecord with remove operation")
    void shouldUpdateMedicalRecordWithRemoveOperation() throws Exception {
        //given
        final String firstName = "Johnny";
        final String lastName = "Boyd";
        final MedicationUpdateEntry medicationUpdateEntry = new MedicationUpdateEntry();
        medicationUpdateEntry.setMedication("aznol:350mg");
        medicationUpdateEntry.setUpdateOperation(MedicationOperationType.REMOVE);
        List<MedicationUpdateEntry> medicines = new ArrayList<>();
        medicines.add(medicationUpdateEntry);
        final AllergyUpdateEntry allergyUpdateEntry = new AllergyUpdateEntry();
        allergyUpdateEntry.setName("nillacilan");
        allergyUpdateEntry.setUpdateOperation(AllergyOperationType.REMOVE);
        List<AllergyUpdateEntry> allergies = new ArrayList<>();
        allergies.add(allergyUpdateEntry);
        final MedicalRecordUpdateDto medicalRecordUpdateDto = new MedicalRecordUpdateDto();
        medicalRecordUpdateDto.setFirstName(firstName);
        medicalRecordUpdateDto.setLastName(lastName);
        medicalRecordUpdateDto.setBirthdate("03/05/1995");
        medicalRecordUpdateDto.setMedications(medicines);
        medicalRecordUpdateDto.setAllergies(allergies);

        DataContainer initialDataContainer = dataManager.getDataContainer();
        assertEquals(1, initialDataContainer.getMedicalRecords().size());
        assertTrue(initialDataContainer.getMedicalRecords().stream()
                .anyMatch(medicalRecord -> medicalRecord.getFirstName().equals("Johnny") && medicalRecord.getLastName().equals("Boyd")));

        //when & then
        mockMvc.perform(put("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicalRecordUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("MedicalRecord update successfully : " + medicalRecordUpdateDto));
        DataContainer updatedData = dataManager.getDataContainer();
        assertEquals(1, updatedData.getMedicalRecords().size());
        assertTrue(updatedData.getMedicalRecords().getFirst().getMedications().contains("hydrapermazol:100mg"));
        assertTrue(updatedData.getMedicalRecords().getFirst().getAllergies().isEmpty());
    }

    @Test
    @DisplayName("should update MedicalRecord with update operation")
    void shouldUpdateMedicalRecordWithUpdateOperation() throws Exception {
        //given
        final String firstName = "Johnny";
        final String lastName = "Boyd";
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

        DataContainer initialDataContainer = dataManager.getDataContainer();
        assertEquals(1, initialDataContainer.getMedicalRecords().size());
        assertTrue(initialDataContainer.getMedicalRecords().stream()
                .anyMatch(medicalRecord -> medicalRecord.getFirstName().equals("Johnny") && medicalRecord.getLastName().equals("Boyd")));

        //when & then
        mockMvc.perform(put("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicalRecordUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("MedicalRecord update successfully : " + medicalRecordUpdateDto));
        DataContainer updatedData = dataManager.getDataContainer();
        assertEquals(1, updatedData.getMedicalRecords().size());
        assertTrue(updatedData.getMedicalRecords().getFirst().getMedications().contains("aznol:300mg"));
        assertTrue(updatedData.getMedicalRecords().getFirst().getAllergies().getFirst().contains("nillacilan"));
    }

    @Test
    @DisplayName("should return conflict status if medication already exists for add operation")
    void shouldReturnConflictStatusIfMedicationAlreadyExistsForAddOperation() throws Exception {
        //given
        final String firstName = "Johnny";
        final String lastName = "Boyd";
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

        DataContainer initialDataContainer = dataManager.getDataContainer();
        assertEquals(1, initialDataContainer.getMedicalRecords().size());
        assertTrue(initialDataContainer.getMedicalRecords().stream()
                .anyMatch(medicalRecord -> medicalRecord.getFirstName().equals("Johnny") && medicalRecord.getLastName().equals("Boyd")));

        //when & then
        mockMvc.perform(put("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicalRecordUpdateDto)))
                .andExpect(status().isConflict())
                .andExpect(MockMvcResultMatchers.content().string("Medication " + medicationUpdateEntry.getMedication() + " already exists"));
        DataContainer updatedData = dataManager.getDataContainer();
        assertEquals(1, updatedData.getMedicalRecords().size());
        assertFalse(updatedData.getMedicalRecords().getFirst().getAllergies().contains("dust"));
    }

    @Test
    @DisplayName("should return conflict status if allergy already exists for add operation")
    void shouldReturnConflictStatusIfAllergyAlreadyExistsForAddOperation() throws Exception {
        //given
        final String firstName = "Johnny";
        final String lastName = "Boyd";
        final MedicationUpdateEntry medicationUpdateEntry = new MedicationUpdateEntry();
        medicationUpdateEntry.setMedication("paracetamol:1000mg");
        medicationUpdateEntry.setUpdateOperation(MedicationOperationType.ADD);
        List<MedicationUpdateEntry> medicines = new ArrayList<>();
        medicines.add(medicationUpdateEntry);
        final AllergyUpdateEntry allergyUpdateEntry = new AllergyUpdateEntry();
        allergyUpdateEntry.setName("nillacilan");
        allergyUpdateEntry.setUpdateOperation(AllergyOperationType.ADD);
        List<AllergyUpdateEntry> allergies = new ArrayList<>();
        allergies.add(allergyUpdateEntry);
        final MedicalRecordUpdateDto medicalRecordUpdateDto = new MedicalRecordUpdateDto();
        medicalRecordUpdateDto.setFirstName(firstName);
        medicalRecordUpdateDto.setLastName(lastName);
        medicalRecordUpdateDto.setBirthdate("03/05/1995");
        medicalRecordUpdateDto.setMedications(medicines);
        medicalRecordUpdateDto.setAllergies(allergies);

        DataContainer initialDataContainer = dataManager.getDataContainer();
        assertEquals(1, initialDataContainer.getMedicalRecords().size());
        assertTrue(initialDataContainer.getMedicalRecords().stream()
                .anyMatch(medicalRecord -> medicalRecord.getFirstName().equals("Johnny") && medicalRecord.getLastName().equals("Boyd")));

        //when & then
        mockMvc.perform(put("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicalRecordUpdateDto)))
                .andExpect(status().isConflict())
                .andExpect(MockMvcResultMatchers.content().string("Allergy " + allergyUpdateEntry.getName() + " already exists"));
        DataContainer updatedData = dataManager.getDataContainer();
        assertEquals(1, updatedData.getMedicalRecords().size());
        assertFalse(updatedData.getMedicalRecords().getFirst().getMedications().contains("paracetamol:1000mg"));
    }

    @Test
    @DisplayName("Should Return Bad Request when medication is null or empty")
    void shouldReturnBadRequestWhenMedicationIsNullOrEmpty() throws Exception {
        final String firstName = "Johnny";
        final String lastName = "Boyd";
        final MedicationUpdateEntry medicationUpdateEntry = new MedicationUpdateEntry();
        medicationUpdateEntry.setMedication(" ");
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

        DataContainer initialDataContainer = dataManager.getDataContainer();
        assertEquals(1, initialDataContainer.getMedicalRecords().size());
        assertTrue(initialDataContainer.getMedicalRecords().stream()
                .anyMatch(medicalRecord -> medicalRecord.getFirstName().equals("Johnny") && medicalRecord.getLastName().equals("Boyd")));

        //when & then
        mockMvc.perform(put("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicalRecordUpdateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Name cannot be null or empty"));
        DataContainer updatedData = dataManager.getDataContainer();
        assertEquals(1, updatedData.getMedicalRecords().size());
        assertFalse(updatedData.getMedicalRecords().getFirst().getAllergies().contains("dust"));
    }

    @Test
    @DisplayName("Should Return Bad Request when allergy is null or empty")
    void shouldReturnBadRequestWhenAllergyIsNullOrEmpty() throws Exception {
        final String firstName = "Johnny";
        final String lastName = "Boyd";
        final MedicationUpdateEntry medicationUpdateEntry = new MedicationUpdateEntry();
        medicationUpdateEntry.setMedication("paracetamol:1000mg");
        medicationUpdateEntry.setUpdateOperation(MedicationOperationType.ADD);
        List<MedicationUpdateEntry> medicines = new ArrayList<>();
        medicines.add(medicationUpdateEntry);
        final AllergyUpdateEntry allergyUpdateEntry = new AllergyUpdateEntry();
        allergyUpdateEntry.setName(null);
        allergyUpdateEntry.setUpdateOperation(AllergyOperationType.ADD);
        List<AllergyUpdateEntry> allergies = new ArrayList<>();
        allergies.add(allergyUpdateEntry);
        final MedicalRecordUpdateDto medicalRecordUpdateDto = new MedicalRecordUpdateDto();
        medicalRecordUpdateDto.setFirstName(firstName);
        medicalRecordUpdateDto.setLastName(lastName);
        medicalRecordUpdateDto.setBirthdate("03/05/1995");
        medicalRecordUpdateDto.setMedications(medicines);
        medicalRecordUpdateDto.setAllergies(allergies);

        DataContainer initialDataContainer = dataManager.getDataContainer();
        assertEquals(1, initialDataContainer.getMedicalRecords().size());
        assertTrue(initialDataContainer.getMedicalRecords().stream()
                .anyMatch(medicalRecord -> medicalRecord.getFirstName().equals("Johnny") && medicalRecord.getLastName().equals("Boyd")));

        //when & then
        mockMvc.perform(put("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicalRecordUpdateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Name cannot be null or empty"));
        DataContainer updatedData = dataManager.getDataContainer();
        assertEquals(1, updatedData.getMedicalRecords().size());
        assertFalse(updatedData.getMedicalRecords().getFirst().getMedications().contains("paracetamol:1000mg"));
    }

    @Test
    @DisplayName("should return Not Found when medication does not exist for remove operation")
    void shouldReturnNotFoundWhenMedicationDoesNotExistForRemoveOperation() throws Exception {
        //given
        final String firstName = "Johnny";
        final String lastName = "Boyd";
        final MedicationUpdateEntry medicationUpdateEntry = new MedicationUpdateEntry();
        medicationUpdateEntry.setMedication("aznol:300mg");
        medicationUpdateEntry.setUpdateOperation(MedicationOperationType.REMOVE);
        List<MedicationUpdateEntry> medicines = new ArrayList<>();
        medicines.add(medicationUpdateEntry);
        final AllergyUpdateEntry allergyUpdateEntry = new AllergyUpdateEntry();
        allergyUpdateEntry.setName("nillacilan");
        allergyUpdateEntry.setUpdateOperation(AllergyOperationType.REMOVE);
        List<AllergyUpdateEntry> allergies = new ArrayList<>();
        allergies.add(allergyUpdateEntry);
        final MedicalRecordUpdateDto medicalRecordUpdateDto = new MedicalRecordUpdateDto();
        medicalRecordUpdateDto.setFirstName(firstName);
        medicalRecordUpdateDto.setLastName(lastName);
        medicalRecordUpdateDto.setBirthdate("03/05/1995");
        medicalRecordUpdateDto.setMedications(medicines);
        medicalRecordUpdateDto.setAllergies(allergies);

        DataContainer initialDataContainer = dataManager.getDataContainer();
        assertEquals(1, initialDataContainer.getMedicalRecords().size());
        assertTrue(initialDataContainer.getMedicalRecords().stream()
                .anyMatch(medicalRecord -> medicalRecord.getFirstName().equals("Johnny") && medicalRecord.getLastName().equals("Boyd")));

        //when & then
        mockMvc.perform(put("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicalRecordUpdateDto)))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("Medication " + medicationUpdateEntry.getMedication() + " does not exist"));
        DataContainer updatedData = dataManager.getDataContainer();
        assertEquals(1, updatedData.getMedicalRecords().size());
        assertTrue(updatedData.getMedicalRecords().getFirst().getMedications().contains("aznol:350mg"));
        assertTrue(updatedData.getMedicalRecords().getFirst().getAllergies().contains("nillacilan"));
    }

    @Test
    @DisplayName("should return Not Found when allergy does not exist for remove operation")
    void shouldReturnNotFoundWhenAllergyDoesNotExistForRemoveOperation() throws Exception {
        //given
        final String firstName = "Johnny";
        final String lastName = "Boyd";
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

        DataContainer initialDataContainer = dataManager.getDataContainer();
        assertEquals(1, initialDataContainer.getMedicalRecords().size());
        assertTrue(initialDataContainer.getMedicalRecords().stream()
                .anyMatch(medicalRecord -> medicalRecord.getFirstName().equals("Johnny") && medicalRecord.getLastName().equals("Boyd")));

        //when & then
        mockMvc.perform(put("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicalRecordUpdateDto)))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("Allergy " + allergyUpdateEntry.getName() + " does not exist"));
        DataContainer updatedData = dataManager.getDataContainer();
        assertEquals(1, updatedData.getMedicalRecords().size());
        assertTrue(updatedData.getMedicalRecords().getFirst().getMedications().contains("aznol:350mg"));
        assertTrue(updatedData.getMedicalRecords().getFirst().getAllergies().contains("nillacilan"));
    }

    @Test
    @DisplayName("should return not found when medication does not exist for update operation")
    void shouldReturnNotFoundWhenMedicationDoesNotExistForUpdateOperation() throws Exception {
        //given
        final String firstName = "Johnny";
        final String lastName = "Boyd";
        final MedicationUpdateEntry medicationUpdateEntry = new MedicationUpdateEntry();
        medicationUpdateEntry.setMedication("paracetamol:300mg");
        medicationUpdateEntry.setUpdateOperation(MedicationOperationType.UPDATE);
        List<MedicationUpdateEntry> medicines = new ArrayList<>();
        medicines.add(medicationUpdateEntry);
        final MedicalRecordUpdateDto medicalRecordUpdateDto = new MedicalRecordUpdateDto();
        medicalRecordUpdateDto.setFirstName(firstName);
        medicalRecordUpdateDto.setLastName(lastName);
        medicalRecordUpdateDto.setBirthdate("03/05/1995");
        medicalRecordUpdateDto.setMedications(medicines);

        DataContainer initialDataContainer = dataManager.getDataContainer();
        assertEquals(1, initialDataContainer.getMedicalRecords().size());
        assertTrue(initialDataContainer.getMedicalRecords().stream()
                .anyMatch(medicalRecord -> medicalRecord.getFirstName().equals("Johnny") && medicalRecord.getLastName().equals("Boyd")));

        //when & then
        mockMvc.perform(put("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicalRecordUpdateDto)))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("Medication " + medicationUpdateEntry.getMedication() + " not found for update"));
        DataContainer updatedData = dataManager.getDataContainer();
        assertEquals(1, updatedData.getMedicalRecords().size());
        assertFalse(updatedData.getMedicalRecords().getFirst().getMedications().contains("paracetamol:300mg"));
        assertTrue(updatedData.getMedicalRecords().getFirst().getAllergies().getFirst().contains("nillacilan"));
    }

    @Test
    @DisplayName("should Return 200 status without modify medications list if updateMedications list is null or empty")
    void shouldReturn200StatusWithoutModifyMedicationsListIfUpdateMedicationsListIsNull() throws Exception {
        //given
        final String firstName = "Johnny";
        final String lastName = "Boyd";
        final List<MedicationUpdateEntry> medicationUpdateEntryList = new ArrayList<>();
        final AllergyUpdateEntry allergyUpdateEntry = new AllergyUpdateEntry();
        allergyUpdateEntry.setName("dust");
        allergyUpdateEntry.setUpdateOperation(AllergyOperationType.ADD);
        List<AllergyUpdateEntry> allergies = new ArrayList<>();
        allergies.add(allergyUpdateEntry);
        final MedicalRecordUpdateDto medicalRecordUpdateDto = new MedicalRecordUpdateDto();
        medicalRecordUpdateDto.setFirstName(firstName);
        medicalRecordUpdateDto.setLastName(lastName);
        medicalRecordUpdateDto.setBirthdate("03/05/1995");
        medicalRecordUpdateDto.setMedications(medicationUpdateEntryList);
        medicalRecordUpdateDto.setAllergies(allergies);

        DataContainer initialDataContainer = dataManager.getDataContainer();
        assertEquals(1, initialDataContainer.getMedicalRecords().size());
        assertTrue(initialDataContainer.getMedicalRecords().stream()
                .anyMatch(medicalRecord -> medicalRecord.getFirstName().equals("Johnny") && medicalRecord.getLastName().equals("Boyd")));

        //when & then
        mockMvc.perform(put("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(medicalRecordUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("MedicalRecord update successfully : " + medicalRecordUpdateDto));
        DataContainer updatedData = dataManager.getDataContainer();
        assertEquals(1, updatedData.getMedicalRecords().size());
        assertEquals(2,updatedData.getMedicalRecords().getFirst().getMedications().size());
        assertTrue(updatedData.getMedicalRecords().getFirst().getMedications().contains("aznol:350mg"));
        assertTrue(updatedData.getMedicalRecords().getFirst().getAllergies().contains(allergyUpdateEntry.getName()));
    }

    @Test
    @DisplayName("should delete a medicalRecord and update dataContainer")
    void shouldDeleteMedicalRecordAndUpdateDataContainer() throws Exception {
        //given
        final String firstName = "Johnny";
        final String lastName = "Boyd";
        DataContainer initialDataContainer = dataManager.getDataContainer();
        assertEquals(1, initialDataContainer.getMedicalRecords().size());
        assertTrue(initialDataContainer.getMedicalRecords().stream()
                .anyMatch(medicalRecord -> medicalRecord.getFirstName().equals(firstName) && medicalRecord.getLastName().equals(lastName)));

        //when & then
        mockMvc.perform(delete("/medicalRecord")
                        .param("firstName", firstName)
                        .param("lastName", lastName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("MedicalRecord with firstName " + firstName
                        + " and lastName " + lastName + ", delete successfully."));
        DataContainer updatedData = dataManager.getDataContainer();
        assertEquals(0, updatedData.getMedicalRecords().size());
    }

    @Test
    @DisplayName("should return 404 when medicalRecord does not exist for deletion")
    void shouldReturn404WhenMedicalRecordDoesNotExistForDeletion() throws Exception {
        //given
        final String firstName = "Jane";
        final String lastName = "Doe";
        DataContainer initialDataContainer = dataManager.getDataContainer();
        assertEquals(1, initialDataContainer.getMedicalRecords().size());
        assertFalse(initialDataContainer.getMedicalRecords().stream()
                .anyMatch(medicalRecord -> medicalRecord.getFirstName().equals(firstName) && medicalRecord.getLastName().equals(lastName)));

        //when & then
        mockMvc.perform(delete("/medicalRecord")
                        .param("firstName", firstName)
                        .param("lastName", lastName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("the medicalRecord with firstName: " + firstName
                        + " and lastName: " + lastName + " does not exist"));
        DataContainer updatedData = dataManager.getDataContainer();
        assertEquals(1, updatedData.getMedicalRecords().size());
    }

    @Test
    @DisplayName("should return 400 when lastName or firstName is blank for deletion")
    void shouldReturn400WhenLastNameIsBlankForDeletion() throws Exception {
        //given
        final String firstName = "Jane";
        final String lastName = " ";
        DataContainer initialDataContainer = dataManager.getDataContainer();
        assertEquals(1, initialDataContainer.getMedicalRecords().size());
        assertFalse(initialDataContainer.getMedicalRecords().stream()
                .anyMatch(medicalRecord -> medicalRecord.getFirstName().equals(firstName) && medicalRecord.getLastName().equals(lastName)));

        //when & then
        mockMvc.perform(delete("/medicalRecord")
                        .param("firstName", firstName)
                        .param("lastName", lastName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("firstName or LastName is blank: " + lastName + ", " + firstName));
    }
}
