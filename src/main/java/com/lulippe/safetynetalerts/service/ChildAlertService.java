package com.lulippe.safetynetalerts.service;

import com.lulippe.safetynetalerts.childAlert.model.ChildAlertResponse;
import com.lulippe.safetynetalerts.childAlert.model.ChildInformation;
import com.lulippe.safetynetalerts.childAlert.model.HouseholdMember;
import com.lulippe.safetynetalerts.exception.entity_exception.InexistantObjectException;
import com.lulippe.safetynetalerts.mapper.ChildAlertResponseMapper;
import com.lulippe.safetynetalerts.model.MedicalRecord;
import com.lulippe.safetynetalerts.model.Person;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.lulippe.safetynetalerts.utils.ValidationUtil.normalizeAndValidate;

@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Service
public class ChildAlertService {
    public static final String ADDRESS = "address";
    private final MedicalRecordService medicalRecordService;
    private final PersonService personService;

    public ChildAlertResponse getChildAlertResponse(final String address) {
        normalizeAndValidate(address, ADDRESS);
        final List<Person> persons = personService.getAllPersonsByAddress(address);
        if (persons.isEmpty()) {
            log.error("No persons found for address {}", address);
            throw new InexistantObjectException("No person found for address: " + address);
        }
        final List<ChildInformation> childInformationList = getChildrenInformationList(persons);
        return ChildAlertResponseMapper.INSTANCE.toChildAlertResponse(childInformationList);
    }

    private List<ChildInformation> getChildrenInformationList(List<Person> persons) {
        return persons.stream()
                .filter(this::isAChild)
                .map(child -> {
                    final MedicalRecord medicalRecord = medicalRecordService.getMedicalRecordByFirstNameAndLastName(child.getFirstName(), child.getLastName());
                    final int childAge = medicalRecordService.calculatePersonAge(medicalRecord.getBirthdate());
                    List<HouseholdMember> householdMembers = persons.stream()
                            .filter(p -> (!p.getFirstName().equals(child.getFirstName())) || (!p.getLastName().equals(child.getLastName())))
                            .map(p -> ChildAlertResponseMapper.INSTANCE.toHouseholdMember(p.getFirstName(), p.getLastName()))
                            .toList();
                    return ChildAlertResponseMapper.INSTANCE.toChildInformation(child.getFirstName(), child.getLastName(), childAge, householdMembers);
                })
                .toList();
    }

    private boolean isAChild(Person person) {
        MedicalRecord medicalRecord = medicalRecordService.getMedicalRecordByFirstNameAndLastName(person.getFirstName(), person.getLastName());
        int age = medicalRecordService.calculatePersonAge(medicalRecord.getBirthdate());
        return age < 18;
    }
}
