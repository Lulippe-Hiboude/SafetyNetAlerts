package com.lulippe.safetynetalerts.service;

import com.lulippe.safetynetalerts.exception.entity_exception.InexistantObjectException;
import com.lulippe.safetynetalerts.exception.entity_exception.InvalidDataException;
import com.lulippe.safetynetalerts.exception.entity_exception.ObjectAlreadyExistsException;
import com.lulippe.safetynetalerts.mapper.MedicalRecordMapper;
import com.lulippe.safetynetalerts.medicalRecord.model.AllergyUpdateEntry;
import com.lulippe.safetynetalerts.medicalRecord.model.MedicalRecordDto;
import com.lulippe.safetynetalerts.medicalRecord.model.MedicalRecordUpdateDto;
import com.lulippe.safetynetalerts.medicalRecord.model.MedicationUpdateEntry;
import com.lulippe.safetynetalerts.model.MedicalRecord;
import com.lulippe.safetynetalerts.repository.MedicalRecordRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.lulippe.safetynetalerts.utils.LogUtil.logMedicalRecordNotFound;
import static com.lulippe.safetynetalerts.utils.ValidationUtil.validateLastNameAndFirstName;

@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Data
@Service
public class MedicalRecordService {
    private final MedicalRecordRepository medicalRecordRepository;

    /**
     * Handles the creation process of a new {@link MedicalRecord}.
     * <p>
     * - Validates the presence and format of first and last name.<br>
     * - Cleans the input {@link MedicalRecordDto} to ensure data consistency.<br>
     * - Checks for duplicate medical records based on first and last name.<br>
     * - If the record does not exist, proceeds to creation; otherwise, throws an exception.
     *
     * @param medicalRecordCreateDto The DTO containing the data for the new medical record.
     * @throws ObjectAlreadyExistsException if a medical record with the same first and last name already exists.
     */
    public void handleMedicalRecordCreationRequest(MedicalRecordDto medicalRecordCreateDto) {
        validateLastNameAndFirstName(medicalRecordCreateDto.getFirstName(), medicalRecordCreateDto.getLastName());
        cleanMedicalRecordDto(medicalRecordCreateDto);

        if (isMedicalRecordExists(medicalRecordCreateDto.getLastName(), medicalRecordCreateDto.getFirstName())) {
            log.error("Medical record already exists with first name {} and last name {}", medicalRecordCreateDto.getFirstName(), medicalRecordCreateDto.getLastName());
            throw new ObjectAlreadyExistsException("Medical record already exists with first name: " + medicalRecordCreateDto.getFirstName() + " and last name: " + medicalRecordCreateDto.getLastName());
        }
        createMedicalRecord(medicalRecordCreateDto);
    }

    /**
     * Handles the update process for an existing {@link MedicalRecord}.
     * <p>
     * - Validates the presence and format of first and last name.<br>
     * - Cleans the update DTO fields.<br>
     * - Retrieves the existing medical record by first and last name.<br>
     * - Validates the update operations on medications and allergies (add/remove/update).<br>
     * - Applies the updates to the existing record.
     *
     * @param medicalRecordUpdateDto The DTO containing the updates to apply to the existing medical record.
     * @throws InexistantObjectException    if the record is not found for the given name.
     * @throws ObjectAlreadyExistsException or {@link InexistantObjectException} for invalid medication or allergy updates.
     */
    public void handleMedicalRecordUpdateRequest(MedicalRecordUpdateDto medicalRecordUpdateDto) {
        validateLastNameAndFirstName(medicalRecordUpdateDto.getFirstName(), medicalRecordUpdateDto.getLastName());
        cleanMedicalRecordUpdateDto(medicalRecordUpdateDto);
        final MedicalRecord existingMedicalRecord = getMedicalRecordByFirstNameAndLastName(medicalRecordUpdateDto.getFirstName(), medicalRecordUpdateDto.getLastName());
        validateMedicationUpdate(existingMedicalRecord.getMedications(), medicalRecordUpdateDto.getMedications());
        validateAllergyUpdate(existingMedicalRecord.getAllergies(), medicalRecordUpdateDto.getAllergies());
        updateMedicalRecord(medicalRecordUpdateDto, existingMedicalRecord);
    }

    /**
     * Handles the deletion of a {@link MedicalRecord} based on the provided first and last name.
     * <p>
     * - Validates the input names for null/empty values and format.<br>
     * - Trims whitespace from both names to ensure accurate matching.<br>
     * - Delegates the deletion logic to the method responsible for removing the record.
     *
     * @param firstName The first name of the person whose medical record should be deleted.
     * @param lastName  The last name of the person whose medical record should be deleted.
     * @throws InexistantObjectException if no medical record is found for the given name.
     */
    public void handleMedicalRecordDeleteRequest(final String firstName, final String lastName) {
        validateLastNameAndFirstName(lastName, firstName);
        String cleanFirstName = firstName.trim();
        String cleanLastName = lastName.trim();
        deleteMedicalRecordByFullName(cleanFirstName, cleanLastName);
    }

    /**
     * Cleans and normalizes the fields of a {@link MedicalRecordDto} object.
     * <p>
     * - Trims leading/trailing spaces from basic fields (firstName, lastName, birthdate).<br>
     * - Initializes allergies and medications lists if they are null.<br>
     * - Normalizes allergy and medication names (e.g., lowercase, trim).<br>
     * - Formats medications by removing extra spaces around colons (e.g., "med : 100mg" → "med:100mg").
     *
     * @param medicalRecordDto The {@link MedicalRecordDto} to clean.
     */
    private void cleanMedicalRecordDto(MedicalRecordDto medicalRecordDto) {
        medicalRecordDto.setFirstName(medicalRecordDto.getFirstName().trim());
        medicalRecordDto.setLastName(medicalRecordDto.getLastName().trim());
        medicalRecordDto.setBirthdate(medicalRecordDto.getBirthdate().trim());
        if (medicalRecordDto.getAllergies() == null) {
            medicalRecordDto.setAllergies(new ArrayList<>());
        }
        if (medicalRecordDto.getMedications() == null) {
            medicalRecordDto.setMedications(new ArrayList<>());
        }
        if (!medicalRecordDto.getAllergies().isEmpty()) {
            medicalRecordDto.setAllergies(medicalRecordDto.getAllergies().stream()
                    .map(this::normalizeAndValidateName)
                    .collect(Collectors.toList()));
        }

        if (!medicalRecordDto.getMedications().isEmpty()) {
            medicalRecordDto.setMedications(medicalRecordDto.getMedications().stream()
                    .map(this::normalizeAndValidateName)
                    .map(s -> s.replaceAll("\\s*:\\s*", ":"))
                    .collect(Collectors.toList()));
        }
    }

    /**
     * Trims unnecessary spaces from the basic fields of a {@link MedicalRecordUpdateDto}.
     *
     * @param medicalRecordUpdateDto The DTO containing the updated medical record data.
     */
    private void cleanMedicalRecordUpdateDto(MedicalRecordUpdateDto medicalRecordUpdateDto) {
        medicalRecordUpdateDto.setFirstName(medicalRecordUpdateDto.getFirstName().trim());
        medicalRecordUpdateDto.setLastName(medicalRecordUpdateDto.getLastName().trim());
        medicalRecordUpdateDto.setBirthdate(medicalRecordUpdateDto.getBirthdate().trim());
    }

    private void deleteMedicalRecordByFullName(final String firstName, final String lastName) {
        medicalRecordRepository.delete(getMedicalRecordByFirstNameAndLastName(firstName, lastName));
        log.info("Medical record with first name {} and last name {} successfully deleted", firstName, lastName);
    }

    private void updateMedicalRecord(MedicalRecordUpdateDto medicalRecordUpdateDto, MedicalRecord existingMedicalRecord) {
        MedicalRecordMapper.INSTANCE.toMedicalRecord(medicalRecordUpdateDto.getBirthdate(), medicalRecordUpdateDto.getMedications(), medicalRecordUpdateDto.getAllergies()
                , existingMedicalRecord);
        medicalRecordRepository.save(existingMedicalRecord);
        log.info("MedicalRecord with firstName {} and lastName {} has been successfully updated", existingMedicalRecord.getFirstName(), existingMedicalRecord.getLastName());
    }

    private MedicalRecord getMedicalRecordByFirstNameAndLastName(String firstName, String lastName) {
        return medicalRecordRepository.findByFullName(firstName, lastName)
                .orElseThrow(() -> {
                    logMedicalRecordNotFound(firstName, lastName);
                    return new InexistantObjectException("the medicalRecord with firstName: " + firstName
                            + " and lastName: " + lastName + " does not exist");
                });
    }

    private void createMedicalRecord(MedicalRecordDto medicalRecordCreateDto) {
        MedicalRecord newMedicalRecord = MedicalRecordMapper.INSTANCE.toMedicalRecord(medicalRecordCreateDto);
        medicalRecordRepository.save(newMedicalRecord);
        log.info("new medical record created {}", newMedicalRecord);
    }

    private Boolean isMedicalRecordExists(String lastName, String firstName) {
        return medicalRecordRepository.findByFullName(firstName, lastName).isPresent();
    }

    /**
     * Normalizes allergy names within a list of {@link AllergyUpdateEntry} updates.
     * <p>
     * Typically includes operations such as trimming and validating the name format.
     *
     * @param updateAllergies The list of allergy updates to clean.
     */
    private void cleanAllergyUpdate(List<AllergyUpdateEntry> updateAllergies) {
        updateAllergies.forEach(allergy -> {
            allergy.setName(normalizeAndValidateName(allergy.getName()));
        });
    }

    /**
     * Validates a list of allergy updates against existing allergy data.
     * <p>
     * - Ensures that a new allergy (ADD operation) doesn't already exist.<br>
     * - Ensures that an allergy to be removed (REMOVE operation) actually exists.<br>
     * Throws business exceptions if any rule is violated:
     * {@link ObjectAlreadyExistsException} or {@link InexistantObjectException}.
     *
     * @param existingAllergies The current list of allergies.
     * @param updateAllergies   The list of allergy update operations to validate.
     */
    private void validateAllergyUpdate(List<String> existingAllergies, List<AllergyUpdateEntry> updateAllergies) {
        if (updateAllergies == null || updateAllergies.isEmpty()) {
            return;
        }
        cleanAllergyUpdate(updateAllergies);

        for (AllergyUpdateEntry allergyUpdateEntry : updateAllergies) {
            final String allergyName = allergyUpdateEntry.getName();

            switch (allergyUpdateEntry.getUpdateOperation()) {
                case ADD:
                    if (existingAllergies.contains(allergyName)) {
                        log.error("Allergy {} already exists", allergyName);
                        throw new ObjectAlreadyExistsException("Allergy already exists with name " + allergyName);
                    }
                    break;
                case REMOVE:
                    if (!existingAllergies.contains(allergyName)) {
                        log.error("Allergy {} does not exist", allergyName);
                        throw new InexistantObjectException("Allergy " + allergyName + " does not exist");
                    }
                    break;
            }
        }
    }

    /**
     * Cleans and normalizes medication names in a list of {@link MedicationUpdateEntry} updates.
     * <p>
     * - Trims and validates the medication name.<br>
     * - Removes unnecessary spaces around colons in the dosage format (e.g., "doliprane : 500mg" → "doliprane:500mg").
     *
     * @param updateMedications The list of medication updates to clean.
     */
    private void cleanMedicationUpdate(List<MedicationUpdateEntry> updateMedications) {
        updateMedications.forEach(medication -> {
            medication.setMedication(normalizeAndValidateName(medication.getMedication()));
            medication.setMedication(medication.getMedication().replaceAll("\\s*:\\s*", ":"));
        });
    }

    /**
     * Validates a list of medication updates against existing medication data.
     * <p>
     * - For ADD operations: ensures the medication doesn't already exist.<br>
     * - For REMOVE operations: ensures the medication exists.<br>
     * - For UPDATE operations: searches for the medication by name (ignoring dosage)
     * and ensures it exists before updating.<br>
     * <p>
     * Throws business exceptions in case of invalid operations:
     * {@link ObjectAlreadyExistsException}, {@link InexistantObjectException}, or {@link InvalidDataException}.
     *
     * @param existingMedications The current list of medications.
     * @param updatedMedications  The list of medication update operations to validate.
     */
    private void validateMedicationUpdate(List<String> existingMedications, List<MedicationUpdateEntry> updatedMedications) {
        if (updatedMedications == null || updatedMedications.isEmpty()) {
            return;
        }

        cleanMedicationUpdate(updatedMedications);

        for (MedicationUpdateEntry medicationUpdateEntry : updatedMedications) {
            final String med = medicationUpdateEntry.getMedication();
            switch (medicationUpdateEntry.getUpdateOperation()) {
                case ADD:
                    if (existingMedications.contains(med)) {
                        log.error("Medication {} already exists.", med);
                        throw new ObjectAlreadyExistsException("Medication: " + med + " already exists.");
                    }
                    break;
                case REMOVE:
                    if (!existingMedications.contains(med)) {
                        log.error("Medication {} does not exist", med);
                        throw new InexistantObjectException("Medication " + med + " does not exist");
                    }
                    break;
                case UPDATE:
                    int indexToUpdate = IntStream.range(0, existingMedications.size())
                            .filter(i -> getMedicationName(existingMedications.get(i)).equals(getMedicationName(med)))
                            .findFirst()
                            .orElse(-1);

                    if (indexToUpdate == -1) {
                        log.error("Medication {} not found for update", med);
                        throw new InexistantObjectException("Medication " + med + " not found for update");
                    }
            }
        }
    }

    private static void validateName(String name) {
        if (Strings.isBlank(name)) {
            log.error("Name {} cannot be null or empty", name);
            throw new InvalidDataException("Name cannot be null or empty");
        }
    }

    private String normalizeAndValidateName(String name) {
        validateName(name);
        return name.trim().toLowerCase();
    }

    private static String getMedicationName(String medication) {
        if (!medication.contains(":")) {
            log.error("Medication {} is not a valid medication name", medication);
            throw new InvalidDataException("Medication: " + medication + " is not a valid medication name.");
        }
        return medication.split(":")[0];
    }
}
