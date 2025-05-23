package com.lulippe.safetynetalerts.controller;

import com.lulippe.safetynetalerts.personInfoLastName.api.PersonInfoLastNameApi;
import com.lulippe.safetynetalerts.personInfoLastName.model.ResidentInformation;
import com.lulippe.safetynetalerts.service.PersonInfoLastNameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PersonInfoLastNameController implements PersonInfoLastNameApi {
    private final PersonInfoLastNameService personInfoLastNameService;
    @Override
    public ResponseEntity<List<ResidentInformation>> getPersonInfoLastName(final String lastName) {
        log.info("getting Resident Information by last name");
        final List<ResidentInformation> residentInformationList = personInfoLastNameService.getPersonInfoLastName(lastName);
        return ResponseEntity.ok(residentInformationList);
    }
}
