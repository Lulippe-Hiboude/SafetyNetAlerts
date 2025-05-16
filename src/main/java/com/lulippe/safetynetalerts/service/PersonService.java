package com.lulippe.safetynetalerts.service;

import com.lulippe.safetynetalerts.dto.PersonDto;
import com.lulippe.safetynetalerts.exception.entity_exception.InexistantObjectException;
import com.lulippe.safetynetalerts.exception.entity_exception.ObjectAlreadyExistsException;
import com.lulippe.safetynetalerts.mapper.PersonMapper;
import com.lulippe.safetynetalerts.model.Person;
import com.lulippe.safetynetalerts.repository.PersonRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.lulippe.safetynetalerts.utils.LogUtil.logPersonNotFound;
import static com.lulippe.safetynetalerts.utils.ValidationUtil.validateLastNameAndFirstName;

@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Data
@Service
public class PersonService {
    private final PersonRepository personRepository;

    /**
     * Handles the creation of a new {@link Person} based on the provided {@link PersonDto}.
     * <p>
     * - Validates the first and last name for correctness.<br>
     * - Cleans and normalizes the data in the DTO (trimming whitespace, etc.).<br>
     * - Verifies if a person with the same name already exists in the system.<br>
     * - If not, proceeds to create the new person.
     *
     * @param personCreateDto The DTO containing the data of the person to be created.
     * @throws ObjectAlreadyExistsException if a person with the same name already exists.
     */
    public void handlePersonCreationRequest(final PersonDto personCreateDto) {
        validateLastNameAndFirstName(personCreateDto.getFirstName(), personCreateDto.getLastName());
        cleanPersonDto(personCreateDto);
        if (isPersonExist(personCreateDto.getFirstName(), personCreateDto.getLastName())) {
            log.error("Person already exists with first name {} and last name {}", personCreateDto.getFirstName(), personCreateDto.getLastName());
            throw new ObjectAlreadyExistsException("Person already exists");
        }
        createPerson(personCreateDto);
    }

    /**
     * Handles the update of an existing {@link Person} using the data provided in the {@link PersonDto}.
     * <p>
     * - Validates the first and last name.<br>
     * - Cleans the data inside the DTO.<br>
     * - Delegates to the update logic which applies the changes.
     *
     * @param personUpdateDto The DTO containing the updated data for the person.
     */
    public void handlePersonUpdateRequest(final PersonDto personUpdateDto) {
        validateLastNameAndFirstName(personUpdateDto.getFirstName(), personUpdateDto.getLastName());
        cleanPersonDto(personUpdateDto);
        updatePerson(personUpdateDto);
    }

    /**
     * Handles the deletion of a {@link Person} identified by their first and last name.
     * <p>
     * - Validates and trims the input names.<br>
     * - Calls the deletion logic for the person if they exist.
     *
     * @param firstName The first name of the person to be deleted.
     * @param lastName  The last name of the person to be deleted.
     * @throws InexistantObjectException if the person does not exist.
     */
    public void handlePersonDeleteRequest(final String firstName, final String lastName) {
        validateLastNameAndFirstName(lastName, firstName);
        String cleanFirstName = firstName.trim();
        String cleanLastName = lastName.trim();
        deletePersonByFirstNameAndLastName(cleanFirstName, cleanLastName);
    }

    public Optional<List<Person>> getAllPersonsByAddress(final String address) {
        return personRepository.getAllPersonsByAddress(address);
    }

    /**
     * Cleans and normalizes the content of a {@link PersonDto}.
     * <p>
     * - Trims whitespace from the first name, last name, and email if present.<br>
     * - Ensures consistent formatting before further processing (e.g., save or update).
     *
     * @param personDto The DTO to be cleaned.
     */
    private void cleanPersonDto(final PersonDto personDto) {
        personDto.setFirstName(personDto.getFirstName().trim());
        personDto.setLastName(personDto.getLastName().trim());
        if (!Strings.isBlank(personDto.getEmail())) {
            personDto.setEmail(personDto.getEmail().trim());
        }
    }

    private void deletePersonByFirstNameAndLastName(final String firstName, final String lastName) {
        findPersonByFirstNameAndLastName(firstName, lastName).ifPresentOrElse(
                existingPerson -> {
                    personRepository.delete(existingPerson);
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
                            personRepository.save(existingPerson);
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
        personRepository.save(newPerson);
        log.info("New person created: {}", newPerson);

    }

    private Boolean isPersonExist(final String firstName, final String lastName) {
        return findPersonByFirstNameAndLastName(firstName, lastName).isPresent();
    }

    private Optional<Person> findPersonByFirstNameAndLastName(final String firstName, final String lastName) {
        return personRepository.findByFullName(firstName, lastName);
    }

    public Set<String> getEmailListByCity(final String city) {
        final List<Person> personList = personRepository.getAllPersonsByCity(city)
                .orElseThrow(() -> new InexistantObjectException("No Person found for this city" + city));

        return personList.stream()
                .map(person -> {
                    if (Strings.isBlank(person.getEmail())) {
                        log.warn("{} {} does not have an email", person.getFirstName(), person.getLastName());
                        return "";
                    }
                    return person.getEmail();
                })
                .collect(Collectors.toSet());
    }

    public List<Person> getAllPersonsByLastName(final String lastName) {
        return personRepository.getAllPersonsByLastName(lastName)
                .orElseThrow(() -> new InexistantObjectException("No Person found for this last name: " + lastName));
    }
}
