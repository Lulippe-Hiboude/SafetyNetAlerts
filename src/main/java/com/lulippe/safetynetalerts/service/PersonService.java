package com.lulippe.safetynetalerts.service;

import com.lulippe.safetynetalerts.dto.PersonDto;
import com.lulippe.safetynetalerts.exception.person_exception.InexistantPersonException;
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

    public void handlePersonCreationRequest(PersonDto personCreateDto) {
        validatePersonDto(personCreateDto);
        if (isPersonExist(personCreateDto.getFirstName(), personCreateDto.getLastName())) {
            log.error("Person already exists with first name {} and last name {}", personCreateDto.getFirstName(), personCreateDto.getLastName());
            throw new PersonAlreadyExistsException("Person already exists");
        }
        createPerson(personCreateDto);
    }

    public void handlePersonUpdateRequest(PersonDto personUpdateDto) {
        validatePersonDto(personUpdateDto);
        updatePerson(personUpdateDto);
    }

    private void updatePerson(PersonDto personUpdateDto) {
        personRepository.findPersonByFirstNameAndLastName(personUpdateDto.getFirstName(), personUpdateDto.getLastName())
                .ifPresentOrElse(existingPerson -> {
                            PersonMapper.INSTANCE.ToPerson(personUpdateDto.getAddress(),personUpdateDto.getCity(),
                                    personUpdateDto.getZip(),personUpdateDto.getPhone(),
                                    personUpdateDto.getEmail(), existingPerson);
                            personRepository.savePerson(existingPerson);
                            log.info("Person with firstName {} and lastName {} has been successfully updated", existingPerson.getFirstName(), existingPerson.getLastName());
                        },
                        () -> {
                            log.error("Person with firstName {} and lastName {} does not exists", personUpdateDto.getFirstName(), personUpdateDto.getLastName());
                            throw new InexistantPersonException("the person with firstName: " + personUpdateDto.getFirstName() + " and lastName: " + personUpdateDto.getLastName() + " does not exist");
                        }
                );
    }

    private void createPerson(PersonDto personCreateDto) {
        Person newPerson = PersonMapper.INSTANCE.ToPerson(personCreateDto);
        personRepository.savePerson(newPerson);
        log.info("New person created: {}", newPerson);

    }

    private Boolean isPersonExist(String firstName, String lastName) {
        return personRepository.findPersonByFirstNameAndLastName(firstName, lastName).isPresent();
    }

    private void validatePersonDto(PersonDto personDto) {
        Optional.ofNullable(personDto).orElseThrow(() -> {
            log.error("personDto is null");
            return new InvalidPersonDataException("personDto is null");
        });

        if (Strings.isBlank(personDto.getFirstName()) || Strings.isBlank(personDto.getLastName())) {
            log.error("firstName or lastName is blank");
            throw new InvalidPersonDataException("firstName or lastName is blank");
        }
    }
}
