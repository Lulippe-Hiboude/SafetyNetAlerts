package com.lulippe.safetynetalerts.utils;

import com.lulippe.safetynetalerts.exception.entity_exception.InvalidDataException;
import lombok.experimental.UtilityClass;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@UtilityClass
public class ValidationUtil {
    private static final Logger log = LoggerFactory.getLogger(ValidationUtil.class);

    public static void validateLastNameAndFirstName(final String firstName, final String lastName) {
        if (Strings.isBlank(firstName) || Strings.isBlank(lastName)) {
            log.error("Invalid data: firstName or lastName is blank. firstName: '{}', lastName: '{}'", firstName, lastName);
            throw new InvalidDataException("firstName or LastName is blank: " + firstName + ", " + lastName);
        }
    }

    public static void validateStringsData(final String firstData, final String secondData,
                                           final String firstDataName, final String secondDataName) {
        if (Strings.isBlank(firstData) || Strings.isBlank(secondData)) {
            log.error("Invalid data: {} or {} is blank : {} : '{}', {}: '{}'", firstDataName, secondDataName,
                    firstDataName, firstData, secondDataName, secondData);
            throw new InvalidDataException(firstDataName + " or " + secondDataName + " is blank: " + firstData + ", " + secondData);
        }
    }

    public static void validateData(final String data, final String dataName) {
        if (Strings.isBlank(data)) {
            log.error("{} cannot be null or empty", dataName);
            throw new InvalidDataException(dataName + " cannot be null or empty");
        }
    }

    public static String normalizeAndValidate(final String data, final String dataName) {
        validateData(data, dataName);
        return data.trim();
    }

    public static void validateStationNumber(Integer number) {
        if (number == null || number <= 0) {
            log.error("Invalid stations number");
            throw new InvalidDataException("Invalid stations number");
        }
    }

}
