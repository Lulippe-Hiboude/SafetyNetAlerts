package com.lulippe.safetynetalerts.repository;

import com.lulippe.safetynetalerts.model.MedicalRecord;
import com.lulippe.safetynetalerts.repository.manager.DataManager;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class MedicalRecordRepository extends AbstractCrudRepository<MedicalRecord> {
    private final DataManager dataManager;

    @Override
    protected List<MedicalRecord> getDataList() {
        return dataManager.getDataContainer().getMedicalRecords();
    }

    @Override
    protected String getFirstName(MedicalRecord entity) {
        return entity.getFirstName();
    }

    @Override
    protected String getLastName(MedicalRecord entity) {
        return entity.getLastName();
    }
}