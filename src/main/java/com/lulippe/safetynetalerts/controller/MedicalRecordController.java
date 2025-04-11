package com.lulippe.safetynetalerts.controller;

import com.lulippe.safetynetalerts.exception.person_exception.InvalidDataException;
import com.lulippe.safetynetalerts.exception.person_exception.ObjectAlreadyExistsException;
import com.lulippe.safetynetalerts.medicalRecord.api.MedicalRecordsApi;
import com.lulippe.safetynetalerts.medicalRecord.model.MedicalRecordDto;
import com.lulippe.safetynetalerts.service.MedicalRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MedicalRecordController implements MedicalRecordsApi {
    private final MedicalRecordService medicalRecordService;

    @Override
    public ResponseEntity<String> createNewMedicalRecord(@RequestBody @Valid MedicalRecordDto medicalRecordDto) {
        try {
            log.info("creating MedicalRecordDto: {}", medicalRecordDto);
            medicalRecordService.handleMedicalRecordCreationRequest(medicalRecordDto);
            return ResponseEntity.ok("MedicalRecord created successfully " + medicalRecordDto);
        } catch (ObjectAlreadyExistsException e) {
            log.error("MedicalRecord already exists: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (InvalidDataException e) {
            log.error("MedicalRecord creation failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
