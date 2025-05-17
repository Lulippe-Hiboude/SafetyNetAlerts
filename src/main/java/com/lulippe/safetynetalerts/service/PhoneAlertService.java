package com.lulippe.safetynetalerts.service;

import com.lulippe.safetynetalerts.mapper.PhoneAlertResponseMapper;
import com.lulippe.safetynetalerts.model.FireStation;
import com.lulippe.safetynetalerts.model.Person;
import com.lulippe.safetynetalerts.phoneAlert.model.PhoneAlertResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.lulippe.safetynetalerts.utils.ValidationUtil.validateStationNumber;

@Slf4j
@RequiredArgsConstructor
@Service
public class PhoneAlertService {
    private final FireStationService fireStationService;
    private final PersonService personService;

    public List<PhoneAlertResponse> getPhoneAlertResponse(final Integer fireStation) {
        validateStationNumber(fireStation);
        return fireStationService.getFireStationByStationNumber(fireStation)
                .stream()
                .map(this::getPersonsPhone)
                .toList();
    }

    private PhoneAlertResponse getPersonsPhone(final FireStation fireStation) {
        final List<String> personsPhone = personService.getAllPersonsByAddress(fireStation.getAddress())
                .stream()
                .map(Person::getPhone)
                .toList();
        return  PhoneAlertResponseMapper.INSTANCE.toPhoneAlertResponse(Integer.parseInt(fireStation.getStation()),personsPhone);
    }
}
