package com.lulippe.safetynetalerts.mapper;

import com.lulippe.safetynetalerts.medicalRecord.model.AllergyUpdateEntry;
import com.lulippe.safetynetalerts.medicalRecord.model.MedicalRecordDto;
import com.lulippe.safetynetalerts.medicalRecord.model.MedicationUpdateEntry;
import com.lulippe.safetynetalerts.model.MedicalRecord;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.IntStream;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MedicalRecordMapper {
    Logger log = LoggerFactory.getLogger(MedicalRecordMapper.class);
    MedicalRecordMapper INSTANCE = Mappers.getMapper(MedicalRecordMapper.class);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "birthdate", source = "birthdate")
    @Mapping(target = "medications", source = "medications")
    @Mapping(target = "allergies", source = "allergies")
    MedicalRecord toMedicalRecord(final MedicalRecordDto medicalRecordDto);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "birthdate", source = "birthdate")
    void partialUpdate(final String birthdate, final List<String> medications, final List<String> allergies, @MappingTarget final MedicalRecord existingMedicalRecord);

    default void toMedicalRecord(final String birthdate, final List<MedicationUpdateEntry> medications, final List<AllergyUpdateEntry> allergies, @MappingTarget final MedicalRecord existingMedicalRecord) {
        partialUpdate(birthdate,
                medications != null ? mapMedications(medications, existingMedicalRecord.getMedications()) : existingMedicalRecord.getMedications(),
                allergies != null ? mapAllergies(allergies, existingMedicalRecord.getAllergies()) : existingMedicalRecord.getAllergies(),
                existingMedicalRecord);
    }

    default List<String> mapAllergies(final List<AllergyUpdateEntry> updateAllergies, @NotNull final List<String> existingAllergies) {
        if (updateAllergies == null || updateAllergies.isEmpty()) return existingAllergies;
        for (AllergyUpdateEntry updateAllergy : updateAllergies) {
            switch (updateAllergy.getUpdateOperation()) {
                case ADD:
                    existingAllergies.add(updateAllergy.getName());
                    log.info("Allergy {} added", updateAllergy.getName());
                    break;
                case REMOVE:
                    existingAllergies.remove(updateAllergy.getName());
                    log.info("Allergy {} removed", updateAllergy.getName());
                    break;
            }
        }
        return existingAllergies;
    }

    default List<String> mapMedications(final List<MedicationUpdateEntry> updateMedications, @NotNull final List<String> existingMedications) {
        if (updateMedications == null || updateMedications.isEmpty()) return existingMedications;

        for (MedicationUpdateEntry updateMedication : updateMedications) {
            switch (updateMedication.getUpdateOperation()) {
                case ADD:
                    existingMedications.add(updateMedication.getMedication());
                    log.info("Medication {} added", updateMedication.getMedication());
                    break;
                case REMOVE:
                    existingMedications.remove(updateMedication.getMedication());
                    log.info("Medication {} removed", updateMedication.getMedication());
                    break;
                case UPDATE:
                    updateMedication(existingMedications, updateMedication);
                    break;
            }
        }
        return existingMedications;
    }

    private static void updateMedication(List<String> existingMedications, MedicationUpdateEntry updateMedication) {
        String medicationNameToUpdate = getMedicationName(updateMedication.getMedication());
        int indexToUpdate = IntStream.range(0, existingMedications.size())
                .filter(i -> getMedicationName(existingMedications.get(i)).equals(medicationNameToUpdate))
                .findFirst()
                .orElse(-1);

        existingMedications.set(indexToUpdate, updateMedication.getMedication());
        log.info("Medication {} updated", updateMedication.getMedication());
    }

    private static String getMedicationName( String medication) {
        return medication.split(":")[0];
    }
}
