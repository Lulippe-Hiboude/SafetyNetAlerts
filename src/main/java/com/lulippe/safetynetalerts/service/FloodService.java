package com.lulippe.safetynetalerts.service;

import com.lulippe.safetynetalerts.exception.entity_exception.InvalidDataException;
import com.lulippe.safetynetalerts.flood.model.PersonInformationByStationNumber;
import com.lulippe.safetynetalerts.flood.model.ResidentFloodInformation;
import com.lulippe.safetynetalerts.flood.model.ResidentsFloodInformations;
import com.lulippe.safetynetalerts.mapper.ResidentFloodInformationMapper;
import com.lulippe.safetynetalerts.model.FireStation;
import com.lulippe.safetynetalerts.model.MedicalRecord;
import com.lulippe.safetynetalerts.utils.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class FloodService {
    private final FireStationService fireStationService;
    private final PersonService personService;
    private final MedicalRecordService medicalRecordService;

    public List<PersonInformationByStationNumber> getResidentsInformationsByStationNumbers(final List<Integer> stationsNumber) {
        return Optional.ofNullable(stationsNumber)
                .filter(l -> !l.isEmpty())
                .orElseThrow(() -> {
                    log.error("invalid stationsNumber list");
                    return new InvalidDataException("Invalid stations number list");
                })
                .stream()
                .peek(ValidationUtil::validateStationNumber)
                .map(this::getPersonInformationByStationNumber)
                .toList();
    }

    private PersonInformationByStationNumber getPersonInformationByStationNumber(final Integer stationNumber) {
        final List<ResidentsFloodInformations> residentsFloodInformationsList = fireStationService.getFireStationByStationNumber(stationNumber)
                .stream()
                .map(this::getResidentsFloodInformations)
                .toList();
        return ResidentFloodInformationMapper.INSTANCE.toPersonInformationByStationNumber(stationNumber, residentsFloodInformationsList);
    }

    private ResidentsFloodInformations getResidentsFloodInformations(final FireStation fireStation) {
        final List<ResidentFloodInformation> residentFloodInformationList = personService.getAllPersonsByAddress(fireStation.getAddress())
                .stream()
                .map(person -> {
                    final MedicalRecord medicalRecord = medicalRecordService.getMedicalRecordByFirstNameAndLastName(person.getFirstName(), person.getLastName());
                    final int age = medicalRecordService.calculatePersonAge(medicalRecord.getBirthdate());
                    return ResidentFloodInformationMapper.INSTANCE.toResidentFloodInformation(person.getFirstName(), person.getLastName(), person.getPhone(), age, medicalRecord.getMedications(), medicalRecord.getAllergies());
                })
                .toList();
        return ResidentFloodInformationMapper.INSTANCE.toResidentsFloodInformations(fireStation.getAddress(), residentFloodInformationList);
    }

}
