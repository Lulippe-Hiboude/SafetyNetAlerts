package com.lulippe.safetynetalerts.repository;

import com.lulippe.safetynetalerts.repository.manager.DataManager;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Repository;

@Repository
@Data
@AllArgsConstructor
public class FireStationRepository {
    private DataManager dataManager;
}
