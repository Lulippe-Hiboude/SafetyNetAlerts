package com.lulippe.safetynetalerts.repository;

import com.lulippe.safetynetalerts.model.MedicalRecord;
import com.lulippe.safetynetalerts.repository.manager.DataManager;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Data
@Repository
@AllArgsConstructor
public class MedicalRecordRepository {
    private final DataManager dataManager;

    private List<MedicalRecord> getMedicalRecords() {
        return dataManager.getDataContainer().getMedicalRecords();
    }

    ;

    public Optional<MedicalRecord> findMedicalRecordByFirstNameAndLastName(final String firstName, final String lastName) {
        return getMedicalRecords().stream()
                .filter(medicalRecord -> medicalRecord.getFirstName().equals(firstName) && medicalRecord.getLastName().equals(lastName))
                .findFirst();
    }

    public void saveMedicalRecord(final MedicalRecord medicalRecord) {
        Optional<MedicalRecord> optionalMedicalRecord = findMedicalRecordByFirstNameAndLastName(medicalRecord.getFirstName(), medicalRecord.getLastName());
        optionalMedicalRecord.ifPresentOrElse(existingMedicalRecord -> {
                    int index = getMedicalRecords().indexOf(existingMedicalRecord);
                    getMedicalRecords().set(index, medicalRecord);
                },
                () -> getMedicalRecords().add(medicalRecord));
    }
}