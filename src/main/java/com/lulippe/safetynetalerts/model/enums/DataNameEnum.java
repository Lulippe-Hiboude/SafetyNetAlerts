package com.lulippe.safetynetalerts.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum DataNameEnum {
    ADDRESS ("address"),
    CITY ("city"),
    MEDICATION("Medication"),
    ALLERGY(("Allergy")),
    STATION_NUMBER("station number"),
    FIRE_STATION_ADDRESS("fire station address"),
    FIRE_STATION_NUMBER("fire station number"),
    FIRST_NAME("first name"),
    LAST_NAME("last name"),
    BIRTHDATE("birthdate");

    private final String value;
}
