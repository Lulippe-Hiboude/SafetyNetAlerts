package com.lulippe.safetynetalerts.service;

import com.lulippe.safetynetalerts.exception.entity_exception.InexistantObjectException;
import com.lulippe.safetynetalerts.exception.entity_exception.InvalidDataException;
import com.lulippe.safetynetalerts.exception.entity_exception.ObjectAlreadyExistsException;
import com.lulippe.safetynetalerts.medicalRecord.model.*;
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

        given(medicalRecordRepository.findByFullName(newMedicalRecordDto.getFirstName(), newMedicalRecordDto.getLastName())).willReturn(Optional.empty());

        //when
        medicalRecordService.handleMedicalRecordCreationRequest(newMedicalRecordDto);

        //then
        verify(medicalRecordRepository, (times(1))).save(medicalRecordArgumentCaptor.capture());
        MedicalRecord medicalRecordSaved = medicalRecordArgumentCaptor.getValue();
        assertEquals("John", medicalRecordSaved.getFirstName());
        assertEquals("Doe", medicalRecordSaved.getLastName());
        assertEquals("03/05/1995", medicalRecordSaved.getBirthdate());
        assertEquals(medicines, medicalRecordSaved.getMedications());
        assertEquals(allergies, medicalRecordSaved.getAllergies());
    }

    @Test
    @DisplayName("should create a new medicalRecord with empty Lists if Allergies and Medications are null")
    void shouldCreateNewMedicalRecordWithEmptyListWhenAllergiesAndMedicationsAreNull() {
        //given
        MedicalRecordDto newMedicalRecordDto = new MedicalRecordDto();
        newMedicalRecordDto.setFirstName("John");
        newMedicalRecordDto.setLastName("Doe");
        newMedicalRecordDto.setBirthdate("03/05/1995");
        newMedicalRecordDto.setMedications(null);
        newMedicalRecordDto.setAllergies(null);

        given(medicalRecordRepository.findByFullName(newMedicalRecordDto.getFirstName(), newMedicalRecordDto.getLastName())).willReturn(Optional.empty());

        //when
        medicalRecordService.handleMedicalRecordCreationRequest(newMedicalRecordDto);

        //then
        verify(medicalRecordRepository, (times(1))).save(medicalRecordArgumentCaptor.capture());
        MedicalRecord medicalRecordSaved = medicalRecordArgumentCaptor.getValue();
        assertEquals("John", medicalRecordSaved.getFirstName());
        assertEquals("Doe", medicalRecordSaved.getLastName());
        assertEquals("03/05/1995", medicalRecordSaved.getBirthdate());
        assertEquals(0, medicalRecordSaved.getMedications().size());
        assertEquals(0, medicalRecordSaved.getAllergies().size());
    }

    @Test
    @DisplayName("should create a new medicalRecord with empty Lists if Allergies and Medications are empty")
    void shouldCreateNewMedicalRecordWithEmptyListWhenAllergiesAndMedicationsAreEmpty() {
        //given
        MedicalRecordDto newMedicalRecordDto = new MedicalRecordDto();
        newMedicalRecordDto.setFirstName("John");
        newMedicalRecordDto.setLastName("Doe");
        newMedicalRecordDto.setBirthdate("03/05/1995");
        newMedicalRecordDto.setMedications(new ArrayList<>());
        newMedicalRecordDto.setAllergies(new ArrayList<>());

        given(medicalRecordRepository.findByFullName(newMedicalRecordDto.getFirstName(), newMedicalRecordDto.getLastName())).willReturn(Optional.empty());

        //when
        medicalRecordService.handleMedicalRecordCreationRequest(newMedicalRecordDto);

        //then
        verify(medicalRecordRepository, (times(1))).save(medicalRecordArgumentCaptor.capture());
        MedicalRecord medicalRecordSaved = medicalRecordArgumentCaptor.getValue();
        assertEquals("John", medicalRecordSaved.getFirstName());
        assertEquals("Doe", medicalRecordSaved.getLastName());
        assertEquals("03/05/1995", medicalRecordSaved.getBirthdate());
        assertEquals(0, medicalRecordSaved.getMedications().size());
        assertEquals(0, medicalRecordSaved.getAllergies().size());
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

        given(medicalRecordRepository.findByFullName(newMedicalRecordDto.getFirstName(), newMedicalRecordDto.getLastName()))
                .willReturn(Optional.of(existingMedicalRecord));

        //when & then
        assertThrows(ObjectAlreadyExistsException.class, () -> medicalRecordService.handleMedicalRecordCreationRequest(newMedicalRecordDto));
        verify(medicalRecordRepository, (times(0))).save(any());
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
        verify(medicalRecordRepository, times(0)).save(any());
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
        verify(medicalRecordRepository, times(0)).save(any());
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
        verify(medicalRecordRepository, times(0)).save(any());
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
        verify(medicalRecordRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("should update an existing MedicalRecord with add operation")
    void shouldUpdateExistingMedicalRecordWithAddOperation() {
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

        final MedicalRecord existingMedicalRecord = MedicalRecord.builder()
                .firstName(firstName)
                .lastName(lastName)
                .birthdate("03/05/1994")
                .medications(new ArrayList<>())
                .allergies(new ArrayList<>())
                .build();

        given(medicalRecordRepository.findByFullName(medicalRecordUpdateDto.getFirstName(), medicalRecordUpdateDto.getLastName()))
                .willReturn(Optional.of(existingMedicalRecord));

        //when
        medicalRecordService.handleMedicalRecordUpdateRequest(medicalRecordUpdateDto);

        //then
        verify(medicalRecordRepository, times(1)).save(medicalRecordArgumentCaptor.capture());
        final MedicalRecord updatedMedicalRecord = medicalRecordArgumentCaptor.getValue();
        assertEquals(firstName, updatedMedicalRecord.getFirstName());
        assertEquals(lastName, updatedMedicalRecord.getLastName());
        assertEquals("03/05/1995", updatedMedicalRecord.getBirthdate());
        assertEquals("aznol:350mg", updatedMedicalRecord.getMedications().getFirst());
        assertEquals("dust",updatedMedicalRecord.getAllergies().getFirst());
    }

    @Test
    @DisplayName("Should update MedicalRecord with remove operation")
    void shouldUpdateMedicalRecordWithRemoveOperation() {
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

        List<String> existingMedications = new ArrayList<>();
        existingMedications.add("aznol:350mg");
        List<String> existingAllergies = new ArrayList<>();
        existingAllergies.add("dust");
        final MedicalRecord existingMedicalRecord = MedicalRecord.builder()
                .firstName(firstName)
                .lastName(lastName)
                .birthdate("03/05/1994")
                .medications(existingMedications)
                .allergies(existingAllergies)
                .build();

        given(medicalRecordRepository.findByFullName(medicalRecordUpdateDto.getFirstName(), medicalRecordUpdateDto.getLastName()))
                .willReturn(Optional.of(existingMedicalRecord));

        //when
        medicalRecordService.handleMedicalRecordUpdateRequest(medicalRecordUpdateDto);

        //then
        verify(medicalRecordRepository, times(1)).save(medicalRecordArgumentCaptor.capture());
        final MedicalRecord updatedMedicalRecord = medicalRecordArgumentCaptor.getValue();
        assertEquals(firstName, updatedMedicalRecord.getFirstName());
        assertEquals(lastName, updatedMedicalRecord.getLastName());
        assertEquals("03/05/1995", updatedMedicalRecord.getBirthdate());
        assertEquals(0, updatedMedicalRecord.getMedications().size());
        assertEquals(0, updatedMedicalRecord.getAllergies().size());
    }

    @Test
    @DisplayName("should update MedicalRecord with update operation")
    void shouldUpdateMedicalRecordWithUpdateOperation() {
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

        List<String> existingMedications = new ArrayList<>();
        existingMedications.add("aznol:350mg");
        List<String> existingAllergies = new ArrayList<>();
        existingAllergies.add("dust");
        final MedicalRecord existingMedicalRecord = MedicalRecord.builder()
                .firstName(firstName)
                .lastName(lastName)
                .birthdate("03/05/1994")
                .medications(existingMedications)
                .allergies(existingAllergies)
                .build();

        given(medicalRecordRepository.findByFullName(medicalRecordUpdateDto.getFirstName(), medicalRecordUpdateDto.getLastName()))
                .willReturn(Optional.of(existingMedicalRecord));

        //when
        medicalRecordService.handleMedicalRecordUpdateRequest(medicalRecordUpdateDto);

        //then
        verify(medicalRecordRepository, times(1)).save(medicalRecordArgumentCaptor.capture());
        final MedicalRecord updatedMedicalRecord = medicalRecordArgumentCaptor.getValue();
        assertEquals(firstName, updatedMedicalRecord.getFirstName());
        assertEquals(lastName, updatedMedicalRecord.getLastName());
        assertEquals("03/05/1995", updatedMedicalRecord.getBirthdate());
        assertEquals("aznol:300mg", updatedMedicalRecord.getMedications().getFirst());
        assertEquals("dust", updatedMedicalRecord.getAllergies().getFirst());
    }

    @Test
    @DisplayName("should throw InvalidMedicalRecordDataException if medication name is not valid")
    void shouldThrowInvalidMedicalRecordDataExceptionIfMedicationNameIsNotValid() {
        //given
        final String firstName = "John";
        final String lastName = "Doe";
        final MedicationUpdateEntry medicationUpdateEntry = new MedicationUpdateEntry();
        medicationUpdateEntry.setMedication("aznol=300mg");
        medicationUpdateEntry.setUpdateOperation(MedicationOperationType.UPDATE);
        List<MedicationUpdateEntry> medicines = new ArrayList<>();
        medicines.add(medicationUpdateEntry);
        final MedicalRecordUpdateDto medicalRecordUpdateDto = new MedicalRecordUpdateDto();
        medicalRecordUpdateDto.setFirstName(firstName);
        medicalRecordUpdateDto.setLastName(lastName);
        medicalRecordUpdateDto.setBirthdate("03/05/1995");
        medicalRecordUpdateDto.setMedications(medicines);

        List<String> existingMedications = new ArrayList<>();
        existingMedications.add("aznol:350mg");
        List<String> existingAllergies = new ArrayList<>();
        existingAllergies.add("dust");
        final MedicalRecord existingMedicalRecord = MedicalRecord.builder()
                .firstName(firstName)
                .lastName(lastName)
                .birthdate("03/05/1994")
                .medications(existingMedications)
                .allergies(existingAllergies)
                .build();

        given(medicalRecordRepository.findByFullName(medicalRecordUpdateDto.getFirstName(), medicalRecordUpdateDto.getLastName()))
                .willReturn(Optional.of(existingMedicalRecord));

        //when & then
        assertThrows(InvalidDataException.class, () -> medicalRecordService.handleMedicalRecordUpdateRequest(medicalRecordUpdateDto));
        verify(medicalRecordRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("Should throw medicationAlreadyExists Exception for add Operation")
    void shouldThrowMedicationAlreadyExistsExceptionForAddOperation() {
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

        List<String> existingMedications = new ArrayList<>();
        existingMedications.add("aznol:350mg");
        final MedicalRecord existingMedicalRecord = MedicalRecord.builder()
                .firstName(firstName)
                .lastName(lastName)
                .birthdate("03/05/1994")
                .medications(existingMedications)
                .allergies(new ArrayList<>())
                .build();

        given(medicalRecordRepository.findByFullName(medicalRecordUpdateDto.getFirstName(), medicalRecordUpdateDto.getLastName()))
                .willReturn(Optional.of(existingMedicalRecord));

        //when & then
        assertThrows(ObjectAlreadyExistsException.class, () -> medicalRecordService.handleMedicalRecordUpdateRequest(medicalRecordUpdateDto));
        verify(medicalRecordRepository, (times(0))).save(any());
    }

    @Test
    @DisplayName("Should throw Allergy already exists exception for add Operation")
    void shouldThrowAllergyAlreadyExistsExceptionForAddOperation() {
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

        List<String> existingAllergies = new ArrayList<>();
        existingAllergies.add("dust");
        final MedicalRecord existingMedicalRecord = MedicalRecord.builder()
                .firstName(firstName)
                .lastName(lastName)
                .birthdate("03/05/1994")
                .medications(new ArrayList<>())
                .allergies(existingAllergies)
                .build();

        given(medicalRecordRepository.findByFullName(medicalRecordUpdateDto.getFirstName(), medicalRecordUpdateDto.getLastName()))
                .willReturn(Optional.of(existingMedicalRecord));

        // when & then
        assertThrows(ObjectAlreadyExistsException.class, () -> medicalRecordService.handleMedicalRecordUpdateRequest(medicalRecordUpdateDto));
        verify(medicalRecordRepository, (times(0))).save(any());
    }

    @Test
    @DisplayName("Should return invalid Data Exception when medication is null")
    void shouldReturnInvalidDataExceptionWhenMedicationIsNull() {
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

        List<String> existingMedications = new ArrayList<>();
        existingMedications.add("aznol:350mg");
        final MedicalRecord existingMedicalRecord = MedicalRecord.builder()
                .firstName(firstName)
                .lastName(lastName)
                .birthdate("03/05/1994")
                .medications(existingMedications)
                .allergies(new ArrayList<>())
                .build();

        given(medicalRecordRepository.findByFullName(medicalRecordUpdateDto.getFirstName(), medicalRecordUpdateDto.getLastName()))
                .willReturn(Optional.of(existingMedicalRecord));

        //when & then
        assertThrows(InvalidDataException.class, () -> medicalRecordService.handleMedicalRecordUpdateRequest(medicalRecordUpdateDto));
        verify(medicalRecordRepository, (times(0))).save(any());
    }

    @Test
    @DisplayName("Should return invalid Data Exception when medication is empty")
    void shouldReturnInvalidDataExceptionWhenMedicationIsEmpty() {
        //given
        final String firstName = "John";
        final String lastName = "Doe";
        final MedicationUpdateEntry medicationUpdateEntry = new MedicationUpdateEntry();
        medicationUpdateEntry.setMedication("  ");
        medicationUpdateEntry.setUpdateOperation(MedicationOperationType.ADD);
        List<MedicationUpdateEntry> medicines = new ArrayList<>();
        medicines.add(medicationUpdateEntry);
        final MedicalRecordUpdateDto medicalRecordUpdateDto = new MedicalRecordUpdateDto();
        medicalRecordUpdateDto.setFirstName(firstName);
        medicalRecordUpdateDto.setLastName(lastName);
        medicalRecordUpdateDto.setBirthdate("03/05/1995");
        medicalRecordUpdateDto.setMedications(medicines);

        List<String> existingMedications = new ArrayList<>();
        existingMedications.add("aznol:350mg");
        final MedicalRecord existingMedicalRecord = MedicalRecord.builder()
                .firstName(firstName)
                .lastName(lastName)
                .birthdate("03/05/1994")
                .medications(existingMedications)
                .allergies(new ArrayList<>())
                .build();

        given(medicalRecordRepository.findByFullName(medicalRecordUpdateDto.getFirstName(), medicalRecordUpdateDto.getLastName()))
                .willReturn(Optional.of(existingMedicalRecord));

        //when & then
        assertThrows(InvalidDataException.class, () -> medicalRecordService.handleMedicalRecordUpdateRequest(medicalRecordUpdateDto));
        verify(medicalRecordRepository, (times(0))).save(any());
    }

    @Test
    @DisplayName("Should return invalid Data Exception when allergy is null")
    void shouldReturnInvalidDataExceptionWhenAllergyIsNull() {
        //given
        final String firstName = "John";
        final String lastName = "Doe";

        final AllergyUpdateEntry allergyUpdateEntry = new AllergyUpdateEntry();
        allergyUpdateEntry.setName(null);
        allergyUpdateEntry.setUpdateOperation(AllergyOperationType.ADD);
        List<AllergyUpdateEntry> allergies = new ArrayList<>();
        allergies.add(allergyUpdateEntry);
        final MedicalRecordUpdateDto medicalRecordUpdateDto = new MedicalRecordUpdateDto();
        medicalRecordUpdateDto.setFirstName(firstName);
        medicalRecordUpdateDto.setLastName(lastName);
        medicalRecordUpdateDto.setBirthdate("03/05/1995");
        medicalRecordUpdateDto.setAllergies(allergies);

        List<String> existingAllergies = new ArrayList<>();
        existingAllergies.add("dust");
        final MedicalRecord existingMedicalRecord = MedicalRecord.builder()
                .firstName(firstName)
                .lastName(lastName)
                .birthdate("03/05/1994")
                .medications(new ArrayList<>())
                .allergies(existingAllergies)
                .build();

        given(medicalRecordRepository.findByFullName(medicalRecordUpdateDto.getFirstName(), medicalRecordUpdateDto.getLastName()))
                .willReturn(Optional.of(existingMedicalRecord));

        //when & then
        assertThrows(InvalidDataException.class, () -> medicalRecordService.handleMedicalRecordUpdateRequest(medicalRecordUpdateDto));
        verify(medicalRecordRepository, (times(0))).save(any());
    }

    @Test
    @DisplayName("Should return invalid Data Exception when allergy is empty")
    void shouldReturnInvalidDataExceptionWhenAllergyIsEmpty() {
        //given
        final String firstName = "John";
        final String lastName = "Doe";

        final AllergyUpdateEntry allergyUpdateEntry = new AllergyUpdateEntry();
        allergyUpdateEntry.setName("  ");
        allergyUpdateEntry.setUpdateOperation(AllergyOperationType.ADD);
        List<AllergyUpdateEntry> allergies = new ArrayList<>();
        allergies.add(allergyUpdateEntry);
        final MedicalRecordUpdateDto medicalRecordUpdateDto = new MedicalRecordUpdateDto();
        medicalRecordUpdateDto.setFirstName(firstName);
        medicalRecordUpdateDto.setLastName(lastName);
        medicalRecordUpdateDto.setBirthdate("03/05/1995");
        medicalRecordUpdateDto.setAllergies(allergies);

        List<String> existingAllergies = new ArrayList<>();
        existingAllergies.add("dust");
        final MedicalRecord existingMedicalRecord = MedicalRecord.builder()
                .firstName(firstName)
                .lastName(lastName)
                .birthdate("03/05/1994")
                .medications(new ArrayList<>())
                .allergies(existingAllergies)
                .build();

        given(medicalRecordRepository.findByFullName(medicalRecordUpdateDto.getFirstName(), medicalRecordUpdateDto.getLastName()))
                .willReturn(Optional.of(existingMedicalRecord));

        //when & then
        assertThrows(InvalidDataException.class, () -> medicalRecordService.handleMedicalRecordUpdateRequest(medicalRecordUpdateDto));
        verify(medicalRecordRepository, (times(0))).save(any());
    }

    @Test
    @DisplayName("should not update existingMedications if updateMedications is null")
    void shouldNotUpdateExistingMedicationsIfMedicationsIsNull() {
        //given
        final String firstName = "John";
        final String lastName = "Doe";
        final MedicalRecordUpdateDto medicalRecordUpdateDto = new MedicalRecordUpdateDto();
        medicalRecordUpdateDto.setFirstName(firstName);
        medicalRecordUpdateDto.setLastName(lastName);
        medicalRecordUpdateDto.setBirthdate("03/05/1995");
        medicalRecordUpdateDto.setMedications(null);

        List<String> existingMedications = new ArrayList<>();
        existingMedications.add("aznol:350mg");
        List<String> existingAllergies = new ArrayList<>();
        existingAllergies.add("dust");
        final MedicalRecord existingMedicalRecord = MedicalRecord.builder()
                .firstName(firstName)
                .lastName(lastName)
                .birthdate("03/05/1994")
                .medications(existingMedications)
                .allergies(existingAllergies)
                .build();

        given(medicalRecordRepository.findByFullName(medicalRecordUpdateDto.getFirstName(), medicalRecordUpdateDto.getLastName()))
                .willReturn(Optional.of(existingMedicalRecord));

        //when
        medicalRecordService.handleMedicalRecordUpdateRequest(medicalRecordUpdateDto);

        //then
        verify(medicalRecordRepository, times(1)).save(medicalRecordArgumentCaptor.capture());
        final MedicalRecord updatedMedicalRecord = medicalRecordArgumentCaptor.getValue();
        assertEquals(firstName, updatedMedicalRecord.getFirstName());
        assertEquals(lastName, updatedMedicalRecord.getLastName());
        assertEquals("03/05/1995", updatedMedicalRecord.getBirthdate());
        assertEquals("aznol:350mg", updatedMedicalRecord.getMedications().getFirst());
        assertEquals("dust", updatedMedicalRecord.getAllergies().getFirst());
    }

    @Test
    @DisplayName("should not update existingMedications if updateMedications is empty")
    void shouldNotUpdateExistingMedicationsIfMedicationsIsEmpty() {
        //given
        final String firstName = "John";
        final String lastName = "Doe";
        final MedicalRecordUpdateDto medicalRecordUpdateDto = new MedicalRecordUpdateDto();
        medicalRecordUpdateDto.setFirstName(firstName);
        medicalRecordUpdateDto.setLastName(lastName);
        medicalRecordUpdateDto.setBirthdate("03/05/1995");
        medicalRecordUpdateDto.setMedications(new ArrayList<>());

        List<String> existingMedications = new ArrayList<>();
        existingMedications.add("aznol:350mg");
        List<String> existingAllergies = new ArrayList<>();
        existingAllergies.add("dust");
        final MedicalRecord existingMedicalRecord = MedicalRecord.builder()
                .firstName(firstName)
                .lastName(lastName)
                .birthdate("03/05/1994")
                .medications(existingMedications)
                .allergies(existingAllergies)
                .build();

        given(medicalRecordRepository.findByFullName(medicalRecordUpdateDto.getFirstName(), medicalRecordUpdateDto.getLastName()))
                .willReturn(Optional.of(existingMedicalRecord));

        //when
        medicalRecordService.handleMedicalRecordUpdateRequest(medicalRecordUpdateDto);

        //then
        verify(medicalRecordRepository, times(1)).save(medicalRecordArgumentCaptor.capture());
        final MedicalRecord updatedMedicalRecord = medicalRecordArgumentCaptor.getValue();
        assertEquals(firstName, updatedMedicalRecord.getFirstName());
        assertEquals(lastName, updatedMedicalRecord.getLastName());
        assertEquals("03/05/1995", updatedMedicalRecord.getBirthdate());
        assertEquals("aznol:350mg", updatedMedicalRecord.getMedications().getFirst());
        assertEquals("dust", updatedMedicalRecord.getAllergies().getFirst());
    }

    @Test
    @DisplayName("should not update existingAllergies if updateAllergies is null ")
    void shouldNotUpdateExistingAllergiesIfAllergiesIsNull() {
        //given
        final String firstName = "John";
        final String lastName = "Doe";

        final MedicalRecordUpdateDto medicalRecordUpdateDto = new MedicalRecordUpdateDto();
        medicalRecordUpdateDto.setFirstName(firstName);
        medicalRecordUpdateDto.setLastName(lastName);
        medicalRecordUpdateDto.setBirthdate("03/05/1995");
        medicalRecordUpdateDto.setAllergies(null);

        List<String> existingMedications = new ArrayList<>();
        existingMedications.add("aznol:350mg");
        List<String> existingAllergies = new ArrayList<>();
        existingAllergies.add("dust");
        final MedicalRecord existingMedicalRecord = MedicalRecord.builder()
                .firstName(firstName)
                .lastName(lastName)
                .birthdate("03/05/1994")
                .medications(existingMedications)
                .allergies(existingAllergies)
                .build();

        given(medicalRecordRepository.findByFullName(medicalRecordUpdateDto.getFirstName(), medicalRecordUpdateDto.getLastName()))
                .willReturn(Optional.of(existingMedicalRecord));

        //when
        medicalRecordService.handleMedicalRecordUpdateRequest(medicalRecordUpdateDto);

        //then
        verify(medicalRecordRepository, times(1)).save(medicalRecordArgumentCaptor.capture());
        final MedicalRecord updatedMedicalRecord = medicalRecordArgumentCaptor.getValue();
        assertEquals(firstName, updatedMedicalRecord.getFirstName());
        assertEquals(lastName, updatedMedicalRecord.getLastName());
        assertEquals("03/05/1995", updatedMedicalRecord.getBirthdate());
        assertEquals("aznol:350mg", updatedMedicalRecord.getMedications().getFirst());
        assertEquals("dust", updatedMedicalRecord.getAllergies().getFirst());
    }

    @Test
    @DisplayName("should not update existingAllergies if updateAllergies is empty ")
    void shouldNotUpdateExistingAllergiesIfAllergiesIsEmpty() {
        //given
        final String firstName = "John";
        final String lastName = "Doe";

        final MedicalRecordUpdateDto medicalRecordUpdateDto = new MedicalRecordUpdateDto();
        medicalRecordUpdateDto.setFirstName(firstName);
        medicalRecordUpdateDto.setLastName(lastName);
        medicalRecordUpdateDto.setBirthdate("03/05/1995");
        medicalRecordUpdateDto.setAllergies(new ArrayList<>());

        List<String> existingMedications = new ArrayList<>();
        existingMedications.add("aznol:350mg");
        List<String> existingAllergies = new ArrayList<>();
        existingAllergies.add("dust");
        final MedicalRecord existingMedicalRecord = MedicalRecord.builder()
                .firstName(firstName)
                .lastName(lastName)
                .birthdate("03/05/1994")
                .medications(existingMedications)
                .allergies(existingAllergies)
                .build();

        given(medicalRecordRepository.findByFullName(medicalRecordUpdateDto.getFirstName(), medicalRecordUpdateDto.getLastName()))
                .willReturn(Optional.of(existingMedicalRecord));

        //when
        medicalRecordService.handleMedicalRecordUpdateRequest(medicalRecordUpdateDto);

        //then
        verify(medicalRecordRepository, times(1)).save(medicalRecordArgumentCaptor.capture());
        final MedicalRecord updatedMedicalRecord = medicalRecordArgumentCaptor.getValue();
        assertEquals(firstName, updatedMedicalRecord.getFirstName());
        assertEquals(lastName, updatedMedicalRecord.getLastName());
        assertEquals("03/05/1995", updatedMedicalRecord.getBirthdate());
        assertEquals("aznol:350mg", updatedMedicalRecord.getMedications().getFirst());
        assertEquals("dust", updatedMedicalRecord.getAllergies().getFirst());
    }

    @Test
    @DisplayName("Should throw inexistant medication exception when medication does not exist for remove operation")
    void shouldThrowInexistantMedicationExceptionWhenMedicationDoesNotExistForRemoveOperation() {
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

        List<String> existingMedications = new ArrayList<>();
        existingMedications.add("paracetamol:1000mg");
        List<String> existingAllergies = new ArrayList<>();
        existingAllergies.add("dust");
        final MedicalRecord existingMedicalRecord = MedicalRecord.builder()
                .firstName(firstName)
                .lastName(lastName)
                .birthdate("03/05/1994")
                .medications(existingMedications)
                .allergies(existingAllergies)
                .build();

        given(medicalRecordRepository.findByFullName(medicalRecordUpdateDto.getFirstName(), medicalRecordUpdateDto.getLastName()))
                .willReturn(Optional.of(existingMedicalRecord));

        //when & then
        assertThrows(InexistantObjectException.class, () -> medicalRecordService.handleMedicalRecordUpdateRequest(medicalRecordUpdateDto));
        verify(medicalRecordRepository, (times(0))).save(any());
    }

    @Test
    @DisplayName("should throw inexistant allergy exception when allergy does not exist for Remove operation")
    void shouldThrowInexistantAllergyExceptionWhenAllergyDoesNotExistForRemoveOperation() {
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

        List<String> existingMedications = new ArrayList<>();
        existingMedications.add("aznol:350mg");
        List<String> existingAllergies = new ArrayList<>();
        existingAllergies.add("chestnuts");
        final MedicalRecord existingMedicalRecord = MedicalRecord.builder()
                .firstName(firstName)
                .lastName(lastName)
                .birthdate("03/05/1994")
                .medications(existingMedications)
                .allergies(existingAllergies)
                .build();

        given(medicalRecordRepository.findByFullName(medicalRecordUpdateDto.getFirstName(), medicalRecordUpdateDto.getLastName()))
                .willReturn(Optional.of(existingMedicalRecord));

        //when & then
        assertThrows(InexistantObjectException.class, () -> medicalRecordService.handleMedicalRecordUpdateRequest(medicalRecordUpdateDto));
        verify(medicalRecordRepository, (times(0))).save(any());
    }

    @Test
    @DisplayName("Should throw inexistant medication exception when medication does not exists for update operation")
    void shouldThrowInexistantMedicationExceptionWhenMedicationDoesNotExistForUpdateOperation() {
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

        List<String> existingMedications = new ArrayList<>();
        existingMedications.add("paracetamol:1000mg");
        List<String> existingAllergies = new ArrayList<>();
        existingAllergies.add("dust");
        final MedicalRecord existingMedicalRecord = MedicalRecord.builder()
                .firstName(firstName)
                .lastName(lastName)
                .birthdate("03/05/1994")
                .medications(existingMedications)
                .allergies(existingAllergies)
                .build();

        given(medicalRecordRepository.findByFullName(medicalRecordUpdateDto.getFirstName(), medicalRecordUpdateDto.getLastName()))
                .willReturn(Optional.of(existingMedicalRecord));

        //when & then
        assertThrows(InexistantObjectException.class, () -> medicalRecordService.handleMedicalRecordUpdateRequest(medicalRecordUpdateDto));
        verify(medicalRecordRepository, (times(0))).save(any());
    }

    @Test
    @DisplayName("Should throw inexistant medicalRecord exception if medicalRecords does not exist for update")
    void shouldThrowInexistantMedicalRecordExceptionWhenMedicalRecordDoesNotExistForUpdate() {
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

        given(medicalRecordRepository.findByFullName(medicalRecordUpdateDto.getFirstName(), medicalRecordUpdateDto.getLastName()))
                .willReturn(Optional.empty());

        //when & then
        assertThrows(InexistantObjectException.class, () -> medicalRecordService.handleMedicalRecordUpdateRequest(medicalRecordUpdateDto));
        verify(medicalRecordRepository, (times(0))).save(any());
    }

    @Test
    @DisplayName("Should delete an existing medicalRecord")
    void shouldDeleteAnExistingMedicalRecord() {
        final String firstName = "John";
        final String lastName = "Doe";
        List<String> existingMedications = new ArrayList<>();
        existingMedications.add("paracetamol:1000mg");
        List<String> existingAllergies = new ArrayList<>();
        existingAllergies.add("dust");
        final MedicalRecord existingMedicalRecord = MedicalRecord.builder()
                .firstName(firstName)
                .lastName(lastName)
                .birthdate("03/05/1994")
                .medications(existingMedications)
                .allergies(existingAllergies)
                .build();
        given(medicalRecordRepository.findByFullName(firstName,lastName)).willReturn(Optional.ofNullable(existingMedicalRecord));

        //when
        medicalRecordService.handleMedicalRecordDeleteRequest(firstName,lastName);

        //then
        verify(medicalRecordRepository,(times(1))).delete(medicalRecordArgumentCaptor.capture());
        MedicalRecord deletedMedicalRecord = medicalRecordArgumentCaptor.getValue();
        assertEquals(firstName, deletedMedicalRecord.getFirstName());
        assertEquals(lastName, deletedMedicalRecord.getLastName());
    }

    @Test
    @DisplayName("should throw InexistantMedicalRecordException if medicalRecord does not exist so cannot be deleted")
    void shouldThrowInexistantMedicalRecordExceptionWhenMedicalRecordDoesNotExistForDelete() {
        //given
        final String firstName = "John";
        final String lastName = "Doe";
        given(medicalRecordRepository.findByFullName(firstName,lastName)).willReturn(Optional.empty());

        //when & then
        assertThrows(InexistantObjectException.class, () -> medicalRecordService.handleMedicalRecordDeleteRequest(firstName,lastName));
        verify(medicalRecordRepository,(times(0))).delete(any());
    }

    @Test
    @DisplayName("should throw InvalidMedicalRecordDataException if the lastName or firstName is null for delete a medicalRecord")
    void shouldThrowInvalidMedicalRecordDataExceptionWhenLastNameIsNullForDelete() {
        //given
        final String firstName = "John";
        final String lastName = null;

        //when & then
        assertThrows(InvalidDataException.class, () -> medicalRecordService.handleMedicalRecordDeleteRequest(firstName,lastName));
        verify(medicalRecordRepository,(times(0))).delete(any());
    }

    @Test
    @DisplayName("should throw InvalidMedicalRecordDataException if the lastName or firstName is blank for delete a medicalRecord")
    void shouldThrowInvalidMedicalRecordDataExceptionWhenLastNameIsBlankForDelete() {
        //given
        final String firstName = "John";
        final String lastName = "  ";

        //when & then
        assertThrows(InvalidDataException.class, () -> medicalRecordService.handleMedicalRecordDeleteRequest(firstName,lastName));
        verify(medicalRecordRepository,(times(0))).delete(any());
    }

    @Test
    @DisplayName("should calculate the age of a person by his birthdate")
    void shouldCalculateTheAgeOfAPersonByHisBirthdate() {
        //given
        final String birthdate = "03/06/1984";
        //when
        final int expectedAge = medicalRecordService.calculatePersonAge(birthdate);
        //then
        assertEquals(41,expectedAge);
    }

}