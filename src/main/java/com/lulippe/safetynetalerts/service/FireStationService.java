package com.lulippe.safetynetalerts.service;

import com.lulippe.safetynetalerts.exception.entity_exception.InexistantObjectException;
import com.lulippe.safetynetalerts.exception.entity_exception.InvalidDataException;
import com.lulippe.safetynetalerts.exception.entity_exception.ObjectAlreadyExistsException;
import com.lulippe.safetynetalerts.fireStation.model.FireStationDto;
import com.lulippe.safetynetalerts.mapper.FireStationMapper;
import com.lulippe.safetynetalerts.model.FireStation;
import com.lulippe.safetynetalerts.repository.FireStationRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Pattern;

import static com.lulippe.safetynetalerts.utils.LogUtil.logFireStationNumberInvalid;
import static com.lulippe.safetynetalerts.utils.ValidationUtil.validateStringsData;

@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Data
@Service
public class FireStationService {
    public static final String STATION_NUMBER = "station number";
    private final FireStationRepository fireStationRepository;
    final static String FIRE_STATION_ADDRESS = "fireStation address";
    final static String FIRE_STATION_NUMBER = "fireStation number";
    private final PersonService personService;

    public void handleFireStationCreationRequest(final FireStationDto fireStationCreateDto) {
        validateStringsData(fireStationCreateDto.getAddress(), fireStationCreateDto.getStation(),
                FIRE_STATION_ADDRESS, FIRE_STATION_NUMBER);
        cleanDto(fireStationCreateDto);
        validateCreationData(fireStationCreateDto);
        createFireStation(fireStationCreateDto);
    }

    public void handleFireStationUpdateRequest(final FireStationDto fireStationUpdateDto) {
        validateStringsData(fireStationUpdateDto.getAddress(), fireStationUpdateDto.getStation(),
                FIRE_STATION_ADDRESS, FIRE_STATION_NUMBER);
        cleanDto(fireStationUpdateDto);
        FireStation existingFireStation = getExistingFireStationForUpdate(fireStationUpdateDto);
        updateFireStation(fireStationUpdateDto, existingFireStation);
    }

    public void handleFireStationDeleteRequest(final FireStationDto fireStationDeleteDto) {
        validateStringsData(fireStationDeleteDto.getAddress(), fireStationDeleteDto.getStation(),
                FIRE_STATION_ADDRESS, FIRE_STATION_NUMBER);
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

        if(existingFireStation.getStation().equals(fireStationDto.getStation())) {
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
                .orElseThrow(() -> new InexistantObjectException("fireStation does not exist with this address and this station: address : "
                        + fireStationDto.getAddress() + ", station : " + fireStationDto.getStation()));
    }

    private Optional<FireStation> findFireStationByAddressAndStation(final String address, final String station) {
        return fireStationRepository.findByAddressAndStation(address, station);
    }
}
