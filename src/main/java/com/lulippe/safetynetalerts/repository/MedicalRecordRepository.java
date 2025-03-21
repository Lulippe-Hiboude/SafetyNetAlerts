package com.lulippe.safetynetalerts.repository;

import com.lulippe.safetynetalerts.repository.manager.DataManager;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class MedicalRecordRepository {
    private final DataManager dataManager;

}
