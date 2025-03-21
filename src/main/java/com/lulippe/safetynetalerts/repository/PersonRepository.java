package com.lulippe.safetynetalerts.repository;

import com.lulippe.safetynetalerts.repository.manager.DataManager;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Repository;

@Data
@Repository
@AllArgsConstructor
public class PersonRepository {
    private DataManager dataManager;
}
