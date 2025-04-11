package com.lulippe.safetynetalerts.service;

import com.lulippe.safetynetalerts.dto.PersonDto;
import com.lulippe.safetynetalerts.exception.person_exception.InexistantObjectException;
import com.lulippe.safetynetalerts.exception.person_exception.ObjectAlreadyExistsException;
import com.lulippe.safetynetalerts.mapper.PersonMapper;
import com.lulippe.safetynetalerts.model.Person;
import com.lulippe.safetynetalerts.repository.PersonRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.lulippe.safetynetalerts.utils.LogUtil.logPersonNotFound;
import static com.lulippe.safetynetalerts.utils.ValidationUtil.validateLastNameAndFirstName;

@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Data
@Service
public class PersonService {
    private final PersonRepository personRepository;

    public void handlePersonCreationRequest(final PersonDto personCreateDto) {
        validateLastNameAndFirstName(personCreateDto.getFirstName(), personCreateDto.getLastName());
        if (isPersonExist(personCreateDto.getFirstName(), personCreateDto.getLastName())) {
            log.error("Person already exists with first name {} and last name {}", personCreateDto.getFirstName(), personCreateDto.getLastName());
            throw new ObjectAlreadyExistsException("Person already exists");
        }
        createPerson(personCreateDto);
    }

    public void handlePersonUpdateRequest(final PersonDto personUpdateDto) {
        validateLastNameAndFirstName(personUpdateDto.getFirstName(), personUpdateDto.getLastName());
        updatePerson(personUpdateDto);
    }

    public void handlePersonDeleteRequest(final String firstName, final String lastName) {
        validateLastNameAndFirstName(lastName, firstName);
        deletePersonByFirstNameAndLastName(firstName, lastName);
    }

    private void deletePersonByFirstNameAndLastName(final String firstName, final String lastName) {
        findPersonByFirstNameAndLastName(firstName, lastName).ifPresentOrElse(
                existingPerson -> {
                    personRepository.deletePerson(existingPerson);
                    log.info("Person with first name {} and last name {} successfully deleted", firstName, lastName);
                },
                () -> {
                    logPersonNotFound(firstName, lastName);
                    throw new InexistantObjectException("Person with firstName " + firstName + " and lastName " + lastName + " does not exist");
                });
    }

    private void updatePerson(final PersonDto personUpdateDto) {
        findPersonByFirstNameAndLastName(personUpdateDto.getFirstName(), personUpdateDto.getLastName())
                .ifPresentOrElse(existingPerson -> {
                            PersonMapper.INSTANCE.ToPerson(personUpdateDto.getAddress(), personUpdateDto.getCity(),
                                    personUpdateDto.getZip(), personUpdateDto.getPhone(),
                                    personUpdateDto.getEmail(), existingPerson);
                            personRepository.savePerson(existingPerson);
                            log.info("Person with firstName {} and lastName {} has been successfully updated", existingPerson.getFirstName(), existingPerson.getLastName());
                        },
                        () -> {
                            logPersonNotFound(personUpdateDto.getFirstName(), personUpdateDto.getLastName());
                            throw new InexistantObjectException("the person with firstName: " + personUpdateDto.getFirstName()
                                    + " and lastName: " + personUpdateDto.getLastName() + " does not exist");
                        }
                );
    }

    private void createPerson(final PersonDto personCreateDto) {
        Person newPerson = PersonMapper.INSTANCE.ToPerson(personCreateDto);
        personRepository.savePerson(newPerson);
        log.info("New person created: {}", newPerson);

    }

    private Boolean isPersonExist(final String firstName, final String lastName) {
        return findPersonByFirstNameAndLastName(firstName, lastName).isPresent();
    }

    private Optional<Person> findPersonByFirstNameAndLastName(final String firstName, final String lastName) {
        return personRepository.findPersonByFirstNameAndLastName(firstName, lastName);
    }
}
