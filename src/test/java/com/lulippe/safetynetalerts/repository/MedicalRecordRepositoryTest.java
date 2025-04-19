package com.lulippe.safetynetalerts.repository;

import com.lulippe.safetynetalerts.dao.DataContainer;
import com.lulippe.safetynetalerts.model.MedicalRecord;
import com.lulippe.safetynetalerts.repository.manager.DataManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MedicalRecordRepositoryTest {
    @InjectMocks
    private MedicalRecordRepository medicalRecordRepository;
    @Mock
    private DataManager dataManager;

    @Test
    @DisplayName("should return a medicalRecord")
    void shouldReturnAMedicalRecord() {
        //given
        DataContainer dataContainer = initializeDataContainer();
        given(dataManager.getDataContainer()).willReturn(dataContainer);
        String firstName = "John";
        String lastName = "Doe";
        String birthDate = "12/05/2021";

        //when
        Optional<MedicalRecord> expectedMedicalRecord = medicalRecordRepository.findByFullName(firstName, lastName);

        //then
        assertTrue(expectedMedicalRecord.isPresent());
        assertEquals(firstName, expectedMedicalRecord.get().getFirstName());
        assertEquals(lastName, expectedMedicalRecord.get().getLastName());
        assertEquals(birthDate, expectedMedicalRecord.get().getBirthdate());
    }

    @Test
    @DisplayName("Should return null if no medicalRecord found")
    void shouldReturnNullIfNoMedicalRecordFound() {
        DataContainer dataContainer = initializeDataContainer();
        given(dataManager.getDataContainer()).willReturn(dataContainer);
        String firstName = "Jane";
        String lastName = "Doe";

        //when
        Optional<MedicalRecord> expectedMedicalRecord = medicalRecordRepository.findByFullName(firstName, lastName);

        //then
        assertTrue(expectedMedicalRecord.isEmpty());
    }

    @Test
    @DisplayName("should update an existing medicalRecord")
    void shouldUpdateAMedicalRecord() {
        DataContainer dataContainer = initializeDataContainer();
        given(dataManager.getDataContainer()).willReturn(dataContainer);
        String firstName = "John";
        String lastName = "Doe";
        String newBirthDate = "12/05/2025";
        MedicalRecord updatingMedicalRecord = MedicalRecord.builder()
                .firstName(firstName)
                .lastName(lastName)
                .birthdate(newBirthDate)
                .build();
        //when
        medicalRecordRepository.save(updatingMedicalRecord);

        //then
        assertEquals(1, dataContainer.getMedicalRecords().size());
        assertTrue(dataContainer.getMedicalRecords().contains(updatingMedicalRecord));
        assertEquals(newBirthDate, dataContainer.getMedicalRecords().getFirst().getBirthdate());
    }

    @Test
    @DisplayName("should delete an existing medicalRecord")
    void shouldDeleteAMedicalRecord() {
        //given
        DataContainer dataContainer = initializeDataContainer();
        given(dataManager.getDataContainer()).willReturn(dataContainer);
        List<String> medications = new ArrayList<>();
        medications.add("aznol :350mg");
        medications.add("paracetamol:1000mg");
        List<String> allergies = new ArrayList<>();
        allergies.add("chestnut");
        allergies.add("dust");
        MedicalRecord medicalRecordToDelete = MedicalRecord.builder()
                .firstName("John")
                .lastName("Doe")
                .birthdate("12/05/2021")
                .medications(medications)
                .allergies(allergies)
                .build();

        //when
        medicalRecordRepository.delete(medicalRecordToDelete);

        //then
        assertEquals(0, dataContainer.getMedicalRecords().size());
    }

    private DataContainer initializeDataContainer() {
        List<String> medications = new ArrayList<>();
        medications.add("aznol :350mg");
        medications.add("paracetamol:1000mg");
        List<String> allergies = new ArrayList<>();
        allergies.add("chestnut");
        allergies.add("dust");
        MedicalRecord medicalRecord = MedicalRecord.builder()
                .firstName("John")
                .lastName("Doe")
                .birthdate("12/05/2021")
                .medications(medications)
                .allergies(allergies)
                .build();
        List<MedicalRecord> medicalRecords = new ArrayList<>();
        medicalRecords.add(medicalRecord);
        DataContainer dataContainer = new DataContainer();
        dataContainer.setMedicalRecords(medicalRecords);
        return dataContainer;
    }

}