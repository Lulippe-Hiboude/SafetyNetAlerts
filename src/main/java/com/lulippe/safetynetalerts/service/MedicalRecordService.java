package com.lulippe.safetynetalerts.service;

import com.lulippe.safetynetalerts.exception.person_exception.ObjectAlreadyExistsException;
import com.lulippe.safetynetalerts.mapper.MedicalRecordMapper;
import com.lulippe.safetynetalerts.medicalRecord.model.MedicalRecordDto;
import com.lulippe.safetynetalerts.model.MedicalRecord;
import com.lulippe.safetynetalerts.repository.MedicalRecordRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.lulippe.safetynetalerts.utils.ValidationUtil.validateLastNameAndFirstName;

@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Data
@Service
public class MedicalRecordService {
    private final MedicalRecordRepository medicalRecordRepository;

    public void handleMedicalRecordCreationRequest(MedicalRecordDto medicalRecordCreateDto) {
        validateLastNameAndFirstName(medicalRecordCreateDto.getFirstName(), medicalRecordCreateDto.getLastName());
        if (isMedicalRecordExists(medicalRecordCreateDto.getLastName(), medicalRecordCreateDto.getFirstName())) {
            log.error("Medical record already exists with first name {} and last name {}", medicalRecordCreateDto.getFirstName(), medicalRecordCreateDto.getLastName());
            throw new ObjectAlreadyExistsException("Medical record already exists with first name: " + medicalRecordCreateDto.getFirstName() + " and last name: " + medicalRecordCreateDto.getLastName());
        }
        createMedicalRecord(medicalRecordCreateDto);
    }

    private void createMedicalRecord(MedicalRecordDto medicalRecordCreateDto) {
        MedicalRecord newMedicalRecord = MedicalRecordMapper.INSTANCE.toMedicalRecord(medicalRecordCreateDto);
        medicalRecordRepository.saveMedicalRecord(newMedicalRecord);
        log.info("new medical record created {}", newMedicalRecord);
    }

    private Boolean isMedicalRecordExists(String lastName, String firstName) {
        return medicalRecordRepository.findMedicalRecordByFirstNameAndLastName(firstName, lastName).isPresent();
    }
}
