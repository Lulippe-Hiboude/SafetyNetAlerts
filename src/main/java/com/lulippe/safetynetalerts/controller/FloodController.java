package com.lulippe.safetynetalerts.controller;

import com.lulippe.safetynetalerts.flood.api.FloodApi;
import com.lulippe.safetynetalerts.flood.model.PersonInformationByStationNumber;
import com.lulippe.safetynetalerts.service.FloodService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FloodController implements FloodApi {
    private final FloodService floodService;

    @Override
    public ResponseEntity<List<PersonInformationByStationNumber>> getResidentsInformationsByStationNumbers(final List<Integer> stations) {
        log.info("getting persons informations by stations");
        final List<PersonInformationByStationNumber> personInformationByStationNumbersList = floodService.getResidentsInformationsByStationNumbers(stations);
        log.info("persons informations by stations retrieved successfully");
        return ResponseEntity.ok(personInformationByStationNumbersList);
    }
}
