package com.lulippe.safetynetalerts.service;

import com.lulippe.safetynetalerts.exception.entity_exception.InexistantObjectException;
import com.lulippe.safetynetalerts.fire.model.FireAddressResponse;
import com.lulippe.safetynetalerts.fire.model.FireResident;
import com.lulippe.safetynetalerts.mapper.FireMapper;
import com.lulippe.safetynetalerts.model.FireStation;
import com.lulippe.safetynetalerts.model.MedicalRecord;
import com.lulippe.safetynetalerts.model.Person;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.lulippe.safetynetalerts.utils.ValidationUtil.normalizeAndValidate;
import static com.lulippe.safetynetalerts.utils.ValidationUtil.validateData;

@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Data
@Service
public class FireService {
    private final FireStationService fireStationService;
    private final PersonService personService;
    private final MedicalRecordService medicalRecordService;
    private static final String ADDRESS = "address";
    static final String BIRTHDATE = "birthdate";

    public FireAddressResponse handleFireRequest(final String address) {
        final String cleanAddress = normalizeAndValidate(address, ADDRESS);

        final FireStation existingfireStation = getFireStation(cleanAddress);
        final List<Person> personList = getPersonListByAddress(cleanAddress);

        return getFireAddressResponse(personList, existingfireStation);
    }

    private FireAddressResponse getFireAddressResponse(List<Person> personList, FireStation existingfireStation) {
        final List<FireResident> residents = new ArrayList<>();

        createFireResidentList(personList, residents);

        return FireMapper.INSTANCE.toFireAddressResponse(existingfireStation.getStation(), residents);
    }

    private void createFireResidentList(List<Person> personList, List<FireResident> residents) {
        personList.forEach(person -> {
            final MedicalRecord medicalRecord = medicalRecordService.getMedicalRecordByFirstNameAndLastName(person.getFirstName(), person.getLastName());
            final int personAge = calculatePersonAge(medicalRecord.getBirthdate());
            final FireResident fireResident = FireMapper.INSTANCE.toFireResident(person.getFirstName(), person.getLastName(), person.getPhone(), personAge,
                    medicalRecord.getMedications(), medicalRecord.getAllergies());
            residents.add(fireResident);
        });
    }

    private int calculatePersonAge(final String birthdate) {
        validateData(birthdate, BIRTHDATE);
        DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy");
        DateTime birth = formatter.parseDateTime(birthdate);
        DateTime now = DateTime.now();
        Years age = Years.yearsBetween(birth, now);
        return age.getYears();
    }

    private List<Person> getPersonListByAddress(final String cleanAddress) {
        return personService.getAllPersonsByAddress(cleanAddress);
    }

    private FireStation getFireStation(final String address) {
        return fireStationService.findFireStationByAddress(address)
                .orElseThrow(() -> new InexistantObjectException("No fireStation for this address"));
    }
}
