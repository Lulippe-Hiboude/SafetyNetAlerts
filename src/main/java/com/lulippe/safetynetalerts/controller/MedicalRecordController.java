package com.lulippe.safetynetalerts.controller;

import com.lulippe.safetynetalerts.exception.entity_exception.InexistantObjectException;
import com.lulippe.safetynetalerts.exception.entity_exception.InvalidDataException;
import com.lulippe.safetynetalerts.exception.entity_exception.ObjectAlreadyExistsException;
import com.lulippe.safetynetalerts.medicalRecord.api.MedicalRecordsApi;
import com.lulippe.safetynetalerts.medicalRecord.model.MedicalRecordDto;
import com.lulippe.safetynetalerts.medicalRecord.model.MedicalRecordUpdateDto;
import com.lulippe.safetynetalerts.service.MedicalRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.lulippe.safetynetalerts.utils.LogUtil.logMedicalRecordNotFound;

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

    @Override
    public ResponseEntity<String> updateMedicalRecord(@RequestBody @Valid MedicalRecordUpdateDto medicalRecordUpdateDto) {
        try {
            log.info("Updating person: {}", medicalRecordUpdateDto);
            medicalRecordService.handleMedicalRecordUpdateRequest(medicalRecordUpdateDto);
            return ResponseEntity.ok("MedicalRecord update successfully : " + medicalRecordUpdateDto);
        } catch (InexistantObjectException e) {
            logMedicalRecordNotFound(medicalRecordUpdateDto.getFirstName(), medicalRecordUpdateDto.getLastName());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (InvalidDataException e) {
            log.error("MedicalRecord update failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (ObjectAlreadyExistsException e) {
            log.error("the medication or the allergy already exists for add operation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<String> deleteMedicalRecord(String firstName, String lastName) {
        try{
            log.info("deleting MedicalRecord: {}", firstName + " " + lastName);
            medicalRecordService.handleMedicalRecordDeleteRequest(firstName, lastName);
            return ResponseEntity.ok("MedicalRecord with firstName " + firstName + " and lastName " + lastName + ", delete successfully.");
        } catch (InexistantObjectException e) {
            logMedicalRecordNotFound(firstName, lastName);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (InvalidDataException e) {
            log.error("MedicalRecord deletion failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
