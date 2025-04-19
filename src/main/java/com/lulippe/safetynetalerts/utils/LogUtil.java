package com.lulippe.safetynetalerts.utils;

import com.lulippe.safetynetalerts.medicalRecord.model.MedicationOperationType;
import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@UtilityClass
public class LogUtil {
    private static final Logger log = LoggerFactory.getLogger(LogUtil.class);

    public static void logPersonNotFound(String firstName, String lastName) {
        log.error("Person with firstName {} and lastName {} does not exist", firstName, lastName);
    }

    public static void logMedicalRecordNotFound(String firstName, String lastName) {
        log.error("MedicalRecord with firstName {} and lastName {} does not exist", firstName, lastName);
    }
}
