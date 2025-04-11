package com.lulippe.safetynetalerts.service;

import com.lulippe.safetynetalerts.exception.person_exception.InvalidDataException;
import com.lulippe.safetynetalerts.exception.person_exception.ObjectAlreadyExistsException;
import com.lulippe.safetynetalerts.medicalRecord.model.MedicalRecordDto;
import com.lulippe.safetynetalerts.model.MedicalRecord;
import com.lulippe.safetynetalerts.repository.MedicalRecordRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MedicalRecordServiceTest {
    @InjectMocks
    private MedicalRecordService medicalRecordService;

    @Mock
    private MedicalRecordRepository medicalRecordRepository;

    @Captor
    private ArgumentCaptor<MedicalRecord> medicalRecordArgumentCaptor;

    @Test
    @DisplayName("should create a new MedicalRecord")
    void shouldCreateNewMedicalRecord() {
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

        given(medicalRecordRepository.findMedicalRecordByFirstNameAndLastName(newMedicalRecordDto.getFirstName(), newMedicalRecordDto.getLastName())).willReturn(Optional.empty());

        //when
        medicalRecordService.handleMedicalRecordCreationRequest(newMedicalRecordDto);

        //then
        verify(medicalRecordRepository, (times(1))).saveMedicalRecord(medicalRecordArgumentCaptor.capture());
        MedicalRecord medicalRecordSaved = medicalRecordArgumentCaptor.getValue();
        assertEquals("John", medicalRecordSaved.getFirstName());
        assertEquals("Doe", medicalRecordSaved.getLastName());
        assertEquals("03/05/1995", medicalRecordSaved.getBirthdate());
        assertEquals(medicines, medicalRecordSaved.getMedications());
        assertEquals(allergies, medicalRecordSaved.getAllergies());
    }

    @Test
    @DisplayName("Should throw MedicalRecord already exist exception If MedicalRecord already exists")
    void shouldThrowExceptionIfMedicalRecordAlreadyExists() {
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

        MedicalRecord existingMedicalRecord = new MedicalRecord();
        existingMedicalRecord.setFirstName("John");
        existingMedicalRecord.setLastName("Doe");
        existingMedicalRecord.setBirthdate("03/05/1995");
        existingMedicalRecord.setMedications(medicines);
        existingMedicalRecord.setAllergies(allergies);

        given(medicalRecordRepository.findMedicalRecordByFirstNameAndLastName(newMedicalRecordDto.getFirstName(), newMedicalRecordDto.getLastName()))
                .willReturn(Optional.of(existingMedicalRecord));

        //when & then
        assertThrows(ObjectAlreadyExistsException.class, () -> medicalRecordService.handleMedicalRecordCreationRequest(newMedicalRecordDto));
        verify(medicalRecordRepository, (times(0))).saveMedicalRecord(any());
    }

    @Test
    @DisplayName("should throw InvalidMedicalRecordDataException if lastName is null")
    void shouldThrowInvalidMedicalRecordDataExceptionIfLastNameIsNull() {
        //given
        List<String> medicines = new ArrayList<>();
        medicines.add("aznol:350mg");
        medicines.add("hydrapermazol:100mg");
        List<String> allergies = new ArrayList<>();
        allergies.add("peanut");
        MedicalRecordDto newMedicalRecordDto = new MedicalRecordDto();
        newMedicalRecordDto.setFirstName("John");
        newMedicalRecordDto.setLastName(null);
        newMedicalRecordDto.setBirthdate("03/05/1995");
        newMedicalRecordDto.setMedications(medicines);
        newMedicalRecordDto.setAllergies(allergies);

        //when & then
        assertThrows(InvalidDataException.class, () -> medicalRecordService.handleMedicalRecordCreationRequest(newMedicalRecordDto));
        verify(medicalRecordRepository, times(0)).saveMedicalRecord(any());
    }

    @Test
    @DisplayName("should throw InvalidMedicalRecordDataException if lastName is null")
    void shouldThrowInvalidMedicalRecordDataExceptionIfFirstNameIsNull() {
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

        //when & then
        assertThrows(InvalidDataException.class, () -> medicalRecordService.handleMedicalRecordCreationRequest(newMedicalRecordDto));
        verify(medicalRecordRepository, times(0)).saveMedicalRecord(any());
    }

    @Test
    @DisplayName("Should throw InvalidMedicalRecordDataException if firstName is empty")
    void shouldThrowInvalidMedicalRecordDataExceptionIfFirstNameIsEmpty() {
        //given
        List<String> medicines = new ArrayList<>();
        medicines.add("aznol:350mg");
        medicines.add("hydrapermazol:100mg");
        List<String> allergies = new ArrayList<>();
        allergies.add("peanut");
        MedicalRecordDto newMedicalRecordDto = new MedicalRecordDto();
        newMedicalRecordDto.setFirstName("  ");
        newMedicalRecordDto.setLastName("Doe");
        newMedicalRecordDto.setBirthdate("03/05/1995");
        newMedicalRecordDto.setMedications(medicines);
        newMedicalRecordDto.setAllergies(allergies);

        //when & then
        assertThrows(InvalidDataException.class, () -> medicalRecordService.handleMedicalRecordCreationRequest(newMedicalRecordDto));
        verify(medicalRecordRepository, times(0)).saveMedicalRecord(any());
    }

    @Test
    @DisplayName("Should throw InvalidMedicalRecordDataException if LastName is empty")
    void shouldThrowInvalidMedicalRecordDataExceptionIfLastNameIsEmpty() {
        //given
        List<String> medicines = new ArrayList<>();
        medicines.add("aznol:350mg");
        medicines.add("hydrapermazol:100mg");
        List<String> allergies = new ArrayList<>();
        allergies.add("peanut");
        MedicalRecordDto newMedicalRecordDto = new MedicalRecordDto();
        newMedicalRecordDto.setFirstName("John");
        newMedicalRecordDto.setLastName("  ");
        newMedicalRecordDto.setBirthdate("03/05/1995");
        newMedicalRecordDto.setMedications(medicines);
        newMedicalRecordDto.setAllergies(allergies);

        //when & then
        assertThrows(InvalidDataException.class, () -> medicalRecordService.handleMedicalRecordCreationRequest(newMedicalRecordDto));
        verify(medicalRecordRepository, times(0)).saveMedicalRecord(any());
    }
}