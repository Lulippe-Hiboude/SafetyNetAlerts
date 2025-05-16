package com.lulippe.safetynetalerts.service;

import com.lulippe.safetynetalerts.mapper.ResidentInformationMapper;
import com.lulippe.safetynetalerts.model.MedicalRecord;
import com.lulippe.safetynetalerts.model.Person;
import com.lulippe.safetynetalerts.personInfoLastName.model.ResidentInformation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.lulippe.safetynetalerts.utils.ValidationUtil.validateData;

@Slf4j
@RequiredArgsConstructor
@Service
public class PersonInfoLastNameService {
    private static final String LAST_NAME = "lastName";
    private final PersonService personService;
    private final MedicalRecordService medicalRecordService;

    public List<ResidentInformation> getPersonInfoLastName(final String lastName) {
        validateData(lastName, LAST_NAME);
        final List<Person> personsByLastName = personService.getAllPersonsByLastName(lastName);
        return personsByLastName.stream()
                .map(person -> {
                    final MedicalRecord medicalRecord = medicalRecordService.getMedicalRecordByFirstNameAndLastName(person.getFirstName(), person.getLastName());
                    final int age = medicalRecordService.calculatePersonAge(medicalRecord.getBirthdate());
                    return ResidentInformationMapper.INSTANCE.toResidentInformation(person.getFirstName(), person.getAddress(), age, person.getEmail(), medicalRecord.getMedications(), medicalRecord.getAllergies());
                })
                .toList();
    }
}
