package com.lulippe.safetynetalerts.service;

import com.lulippe.safetynetalerts.exception.entity_exception.InexistantObjectException;
import com.lulippe.safetynetalerts.exception.entity_exception.InvalidDataException;
import com.lulippe.safetynetalerts.exception.entity_exception.ObjectAlreadyExistsException;
import com.lulippe.safetynetalerts.fireStation.model.FireStationCoverageResponse;
import com.lulippe.safetynetalerts.fireStation.model.FireStationDto;
import com.lulippe.safetynetalerts.fireStation.model.ResidentInfo;
import com.lulippe.safetynetalerts.mapper.FireStationMapper;
import com.lulippe.safetynetalerts.mapper.PersonMapper;
import com.lulippe.safetynetalerts.model.FireStation;
import com.lulippe.safetynetalerts.model.MedicalRecord;
import com.lulippe.safetynetalerts.model.Person;
import com.lulippe.safetynetalerts.repository.FireStationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.lulippe.safetynetalerts.model.enums.DataNameEnum.*;
import static com.lulippe.safetynetalerts.utils.LogUtil.logFireStationNumberInvalid;
import static com.lulippe.safetynetalerts.utils.ValidationUtil.normalizeAndValidate;
import static com.lulippe.safetynetalerts.utils.ValidationUtil.validateStringsData;
import static java.util.stream.Collectors.toList;

@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Service
public class FireStationService {
    private final FireStationRepository fireStationRepository;
    private final PersonService personService;
    private final MedicalRecordService medicalRecordService;

    /**
     * Retrieves all residents covered by a given fire station number.
     * This includes a list of residents living at addresses served by the station,
     * along with statistics on the number of adults and children.
     *
     * @param stationNumber the fire station number (raw or unvalidated input).
     * @return a {@link FireStationCoverageResponse} object containing the list of residents
     *         and age group statistics (adults/children).
     * @throws IllegalArgumentException if the station number is invalid
     *         or no corresponding fire station is found.
     */
    public FireStationCoverageResponse getResidentsByFireStation(final String stationNumber) {
        final String cleanStationNumber = normalizeAndValidate(stationNumber, STATION_NUMBER.getValue());
        final List<FireStation> fireStations = getFireStation(stationNumber, cleanStationNumber);
        final List<Person> personsCoveredByFireStation = fireStations.stream()
                .flatMap(fireStation -> getAllPersonsByAddress(fireStation.getAddress())
                        .stream()
                )
                .toList();
        final Map<String, Integer> counts = countAdultsAndChildren(personsCoveredByFireStation);
        final List<ResidentInfo> residents = personsCoveredByFireStation.stream()
                .map(PersonMapper.INSTANCE::ToResidentInfo)
                .toList();

        return FireStationMapper.INSTANCE.toFireStationCoverageResponse(residents, counts);
    }

    /**
     * Handles the creation request of a new fire station from the provided DTO.
     * This includes validation, cleaning, duplicate checking, and persistence of the new station.
     *
     * @param fireStationCreateDto the DTO containing the address and station number to create.
     * @throws IllegalArgumentException if the provided data is invalid
     *         or if a fire station with the same data already exists.
     */
    public void handleFireStationCreationRequest(final FireStationDto fireStationCreateDto) {
        validateStringsData(fireStationCreateDto.getAddress(), fireStationCreateDto.getStation(),
                FIRE_STATION_ADDRESS.getValue(), FIRE_STATION_NUMBER.getValue());
        cleanDto(fireStationCreateDto);
        validateCreationData(fireStationCreateDto);
        createFireStation(fireStationCreateDto);
    }

    /**
     * Handles the update request for an existing fire station.
     * Validates and cleans the provided data, then updates the corresponding fire station.
     *
     * @param fireStationUpdateDto the DTO containing the address and the new station number.
     * @throws InexistantObjectException if the fire station to update does not exist.
     * @throws IllegalArgumentException if the provided data is invalid.
     */
    public void handleFireStationUpdateRequest(final FireStationDto fireStationUpdateDto) {
        validateStringsData(fireStationUpdateDto.getAddress(), fireStationUpdateDto.getStation(),
                FIRE_STATION_ADDRESS.getValue(), FIRE_STATION_NUMBER.getValue());
        cleanDto(fireStationUpdateDto);
        FireStation existingFireStation = getExistingFireStationForUpdate(fireStationUpdateDto);
        updateFireStation(fireStationUpdateDto, existingFireStation);
    }

    /**
     * Handles the deletion request for a fire station.
     * Validates and cleans the provided data, then deletes the corresponding fire station.
     *
     * @param fireStationDeleteDto the DTO containing the address and station number to delete.
     * @throws InexistantObjectException if the fire station to delete does not exist.
     * @throws IllegalArgumentException if the provided data is invalid.
     */
    public void handleFireStationDeleteRequest(final FireStationDto fireStationDeleteDto) {
        validateStringsData(fireStationDeleteDto.getAddress(), fireStationDeleteDto.getStation(),
                FIRE_STATION_ADDRESS.getValue(), FIRE_STATION_NUMBER.getValue());
        cleanDto(fireStationDeleteDto);
        FireStation existingFireStation = getExistingFireStationForDelete(fireStationDeleteDto);
        deleteFireStation(existingFireStation);
    }

    private void createFireStation(final FireStationDto fireStationDto) {
        FireStation newFireStation = FireStationMapper.INSTANCE.toFireStation(fireStationDto);
        fireStationRepository.save(newFireStation);
    }

    private void updateFireStation(final FireStationDto fireStationUpdateDto, final FireStation existingFireStation) {
        FireStationMapper.INSTANCE.toFireStation(fireStationUpdateDto, existingFireStation);
        fireStationRepository.save(existingFireStation);
        log.info("FireStation with address {} successfully updated", existingFireStation.getAddress());
    }

    private void deleteFireStation(final FireStation fireStationToDelete) {
        fireStationRepository.delete(fireStationToDelete);
        log.info("FireStation with address {} and station {} successfully deleted", fireStationToDelete.getAddress(), fireStationToDelete.getStation());
    }

    private void cleanDto(final FireStationDto fireStationDto) {
        fireStationDto.setAddress(fireStationDto.getAddress().trim());
        fireStationDto.setStation(fireStationDto.getStation().trim());
    }

    private Boolean isFireStationExist(final String address) {
        return findFireStationByAddress(address).isPresent();
    }

    public Optional<FireStation> findFireStationByAddress(final String address) {
        return fireStationRepository.findByAddress(address);
    }

    private Boolean isStationNumberValid(final String number) {
        return Pattern.matches("\\d+", number);
    }

    private void validateCreationData(final FireStationDto fireStationDto) {
        if (isFireStationExist(fireStationDto.getAddress())) {
            log.error("fireStation already exists with this address {}", fireStationDto.getAddress());
            throw new ObjectAlreadyExistsException("FireStation already exists");
        }
        if (!isStationNumberValid(fireStationDto.getStation())) {
            logFireStationNumberInvalid(fireStationDto.getStation());
            throw new InvalidDataException("Invalid station, please enter a valid number");
        }
    }

    private FireStation getExistingFireStationForUpdate(final FireStationDto fireStationDto) {

        if (!isStationNumberValid(fireStationDto.getStation())) {
            logFireStationNumberInvalid(fireStationDto.getStation());
            throw new InvalidDataException("Invalid station, please enter a valid number");
        }
        final FireStation existingFireStation = findFireStationByAddress(fireStationDto.getAddress())
                .orElseThrow(() -> new InexistantObjectException("fireStation does not exist with this address: " + fireStationDto.getAddress()));

        if (existingFireStation.getStation().equals(fireStationDto.getStation())) {
            log.error("fireStation already exists with this address {} and station number {}", fireStationDto.getAddress(), fireStationDto.getStation());
            throw new ObjectAlreadyExistsException("fireStation already exists with this address and station number");
        }
        return existingFireStation;
    }

    private FireStation getExistingFireStationForDelete(final FireStationDto fireStationDto) {
        if (!isStationNumberValid(fireStationDto.getStation())) {
            logFireStationNumberInvalid(fireStationDto.getStation());
            throw new InvalidDataException("Invalid station, please enter a valid number");
        }
        return findFireStationByAddressAndStation(fireStationDto.getAddress(), fireStationDto.getStation())
                .orElseThrow(() -> {
                    log.error("fireStation does not exist with this address {} and this station {}", fireStationDto.getAddress(), fireStationDto.getStation());
                    return new InexistantObjectException("fireStation does not exist with this address and this station: address : "
                            + fireStationDto.getAddress() + ", station : " + fireStationDto.getStation());
                });
    }

    private Optional<FireStation> findFireStationByAddressAndStation(final String address, final String station) {
        return fireStationRepository.findByAddressAndStation(address, station);
    }

    private List<Person> getAllPersonsByAddress(final String address) {
        return personService.getAllPersonsByAddress(address);
    }


    private Map<String, Integer> countAdultsAndChildren(final List<Person> personsCoveredByFireStation) {
        if (personsCoveredByFireStation.equals(Collections.emptyList())) {
            return Map.of(
                    "adults", 0,
                    "children", 0
            );
        }
        Map<Boolean, Long> counts = personsCoveredByFireStation.stream()
                .map(person -> {
                    final MedicalRecord medicalRecord = medicalRecordService.getMedicalRecordByFirstNameAndLastName(person.getFirstName(), person.getLastName());
                    int age = medicalRecordService.calculatePersonAge(medicalRecord.getBirthdate());
                    return age >= 18;
                })
                .collect(Collectors.partitioningBy(isAdult -> isAdult, Collectors.counting()));
        return Map.of(
                "adults", counts.getOrDefault(true, 0L).intValue(),
                "children", counts.getOrDefault(false, 0L).intValue()
        );
    }

    private List<FireStation> getFireStation(final String stationNumber, final String cleanStationNumber) {
        if (isStationNumberValid(cleanStationNumber)) {
            return Optional.of(fireStationRepository.findAll()
                            .stream()
                            .filter(fs -> fs.getStation().equals(cleanStationNumber))
                            .distinct()
                            .collect(toList()))
                    .filter(list -> !list.isEmpty())
                    .orElseThrow(() -> new InexistantObjectException("No fireStations found for this station number: " + stationNumber));
        }
        log.error("Invalid station number: {}", stationNumber);
        throw new InvalidDataException("Invalid station, please enter a valid number");
    }

    public List<FireStation> getFireStationByStationNumber(Integer stationNumber) {
        final List<FireStation> fireStationList = fireStationRepository.findByStation(String.valueOf(stationNumber));

        if (fireStationList.isEmpty()) {
            log.error("No fireStation found for {}", stationNumber);
            throw new InexistantObjectException("No fireStations found for this station number: " + stationNumber);
        }
        return fireStationList;
    }
}
