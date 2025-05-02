package com.lulippe.safetynetalerts.controller;


import com.lulippe.safetynetalerts.exception.entity_exception.InexistantObjectException;
import com.lulippe.safetynetalerts.exception.entity_exception.InvalidDataException;
import com.lulippe.safetynetalerts.exception.entity_exception.ObjectAlreadyExistsException;
import com.lulippe.safetynetalerts.fireStation.api.FireStationsApi;
import com.lulippe.safetynetalerts.fireStation.model.FireStationDto;
import com.lulippe.safetynetalerts.service.FireStationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FireStationController implements FireStationsApi {
    private final FireStationService fireStationService;

    @Override
    public ResponseEntity<String> createNewFireStation(FireStationDto fireStationDto) {
        try {
            log.info("Creating fireStation: {}", fireStationDto);
            fireStationService.handleFireStationCreationRequest(fireStationDto);
            return ResponseEntity.ok("FireStation create successfully : " + fireStationDto);
        } catch (ObjectAlreadyExistsException e) {
            log.error("FireStation already exists: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (InvalidDataException e) {
            log.error("FireStation creation failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<String> updateExistingFireStation(FireStationDto fireStationDto) {
        try {
            log.info("Updating fireStation: {}", fireStationDto);
            fireStationService.handleFireStationUpdateRequest(fireStationDto);
            return ResponseEntity.ok("FireStation updated successfully : " + fireStationDto);
        } catch (ObjectAlreadyExistsException e) {
            log.error("FireStation already exists with this station: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (InvalidDataException e) {
            log.error("FireStation update failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (InexistantObjectException e) {
            log.error("FireStation does not exist with this address: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<String> deleteExistingFireStation(FireStationDto fireStationDto) {
        try{
            log.info("Deleting fireStation: {}", fireStationDto);
            fireStationService.handleFireStationDeleteRequest(fireStationDto);
            return ResponseEntity.ok("FireStation deleted successfully : " + fireStationDto);
        }catch (InexistantObjectException e) {
            log.error("FireStation does not exist with this address and station: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (InvalidDataException e) {
            log.error("FireStation delete failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
