package com.lulippe.safetynetalerts.service;

import com.lulippe.safetynetalerts.dto.PersonCreateDto;
import com.lulippe.safetynetalerts.exception.person_exception.InvalidPersonDataException;
import com.lulippe.safetynetalerts.exception.person_exception.PersonAlreadyExistsException;
import com.lulippe.safetynetalerts.mapper.PersonMapper;
import com.lulippe.safetynetalerts.model.Person;
import com.lulippe.safetynetalerts.repository.PersonRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Data
@Service
public class PersonService {
    private final PersonRepository personRepository;

    public void handlePersonCreationRequest(PersonCreateDto personCreateDto) {
        validatePersonCreateDto(personCreateDto);
        if (isPersonExist(personCreateDto.getFirstName(), personCreateDto.getLastName())) {
            log.info("Person already exists with first name {} and last name {}", personCreateDto.getFirstName(), personCreateDto.getLastName());
            throw new PersonAlreadyExistsException("Person already exists");
        }
        createPerson(personCreateDto);
    }

    private void createPerson(PersonCreateDto personCreateDto) {
        Person newPerson = PersonMapper.INSTANCE.ToPerson(personCreateDto);
        personRepository.savePerson(newPerson);
        log.info("New person created: {}", newPerson);
    }

    private Boolean isPersonExist(String firstName, String lastName) {
        return personRepository.getPersonByFirstNameAndLastName(firstName, lastName).isPresent();
    }

    private void validatePersonCreateDto(PersonCreateDto personCreateDto) {
        Optional.ofNullable(personCreateDto).orElseThrow(() -> new InvalidPersonDataException("personCreateDto is null"));

        if (Strings.isBlank(personCreateDto.getFirstName()) || Strings.isBlank(personCreateDto.getLastName())) {
            throw new InvalidPersonDataException("firstName or lastName is blank");
        }
    }
}
