package com.lulippe.safetynetalerts.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lulippe.safetynetalerts.model.FireStation;
import com.lulippe.safetynetalerts.model.MedicalRecord;
import com.lulippe.safetynetalerts.model.Person;
import com.lulippe.safetynetalerts.repository.manager.DataManager;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Container for the application's data.
 * It groups all entities from the JSON file:
 * {@link Person}, {@link FireStation}, and {@link MedicalRecord}.
 * <p>
 * This class is used by {@link DataManager} to store data in memory.
 *
 * <p>When a modification (addition/deletion) is made to an entity,
 * it is directly applied to this object.</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class DataContainer {
    @JsonProperty("persons")
    private List<Person> persons;
    @JsonProperty("firestations")
    private List<FireStation> fireStations;
    @JsonProperty("medicalrecords")
    private List<MedicalRecord> medicalRecords;
}
