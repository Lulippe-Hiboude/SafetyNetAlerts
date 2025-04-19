package com.lulippe.safetynetalerts.controller;

import com.lulippe.safetynetalerts.api.PersonApi;
import com.lulippe.safetynetalerts.dto.PersonDto;
import com.lulippe.safetynetalerts.exception.entity_exception.InexistantObjectException;
import com.lulippe.safetynetalerts.exception.entity_exception.InvalidDataException;
import com.lulippe.safetynetalerts.exception.entity_exception.ObjectAlreadyExistsException;
import com.lulippe.safetynetalerts.service.PersonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.lulippe.safetynetalerts.utils.LogUtil.logPersonNotFound;

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
        } catch (ObjectAlreadyExistsException e) {
            log.error("Person already exists: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (InvalidDataException e) {
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
        } catch (InexistantObjectException e) {
            logPersonNotFound(personUpdateDto.getFirstName(), personUpdateDto.getLastName());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (InvalidDataException e) {
            log.error("Person update failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<String> deletePerson(@RequestParam @Valid String firstName, @RequestParam @Valid String lastName) {
        try {
            log.info("Deleting person by firstName {} and  lastName {}", firstName, lastName);
            personService.handlePersonDeleteRequest(firstName, lastName);
            return ResponseEntity.ok("Person with firstName " + firstName + " and lastName " + lastName + ", delete successfully.");
        } catch (InexistantObjectException e) {
            logPersonNotFound(firstName, lastName);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (InvalidDataException e) {
            log.error("Person delete failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
