package com.lulippe.safetynetalerts.controller;

import com.lulippe.safetynetalerts.api.PersonApi;
import com.lulippe.safetynetalerts.dto.PersonCreateDto;
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
    public ResponseEntity<String> createPerson(@RequestBody @Valid final PersonCreateDto personCreateDto) {
        try {
            log.info("Creating person: {}", personCreateDto);
            personService.handlePersonCreationRequest(personCreateDto);
            return ResponseEntity.ok("Person create successfully : " + personCreateDto);
        } catch (PersonAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (InvalidPersonDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
