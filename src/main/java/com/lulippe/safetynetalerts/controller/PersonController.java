package com.lulippe.safetynetalerts.controller;

import com.lulippe.safetynetalerts.api.PersonApi;
import com.lulippe.safetynetalerts.dto.PersonDto;
import com.lulippe.safetynetalerts.exception.person_exception.InexistantPersonException;
import com.lulippe.safetynetalerts.exception.person_exception.InvalidPersonDataException;
import com.lulippe.safetynetalerts.exception.person_exception.PersonAlreadyExistsException;
import com.lulippe.safetynetalerts.service.PersonService;
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
public class PersonController implements PersonApi {
    private final PersonService personService;

    @Override
    public ResponseEntity<String> createPerson(@RequestBody @Valid final PersonDto personCreateDto) {
        try {
            log.info("Creating person: {}", personCreateDto);
            personService.handlePersonCreationRequest(personCreateDto);
            return ResponseEntity.ok("Person create successfully : " + personCreateDto);
        } catch (PersonAlreadyExistsException e) {
            log.error("Person already exists: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (InvalidPersonDataException e) {
            log.error("Person creation failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<String> updatePerson(@RequestBody @Valid PersonDto personUpdateDto) {
        try {
            log.info("Updating person: {}", personUpdateDto);
            personService.handlePersonUpdateRequest(personUpdateDto);
            return ResponseEntity.ok("Person update successfully : " + personUpdateDto);
        } catch (InexistantPersonException e) {
            log.error("Person does not exist: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (InvalidPersonDataException e) {
            log.error("Person update failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
