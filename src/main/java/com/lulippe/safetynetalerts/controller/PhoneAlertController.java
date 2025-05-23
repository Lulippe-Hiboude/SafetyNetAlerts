package com.lulippe.safetynetalerts.controller;

import com.lulippe.safetynetalerts.phoneAlert.api.PhoneAlertApi;
import com.lulippe.safetynetalerts.phoneAlert.model.PhoneAlertResponse;
import com.lulippe.safetynetalerts.service.PhoneAlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PhoneAlertController implements PhoneAlertApi {

    private final PhoneAlertService phoneAlertService;

    @Override
    public ResponseEntity<List<PhoneAlertResponse>> getPhoneAlertResponse(Integer firestation) {
        log.info("getting phone numbers of resident covered by station {}", firestation);
        final List<PhoneAlertResponse> phoneAlertResponses = phoneAlertService.getPhoneAlertResponse(firestation);
        log.info("phone numbers of resident covered by station {} retrieved successfully", firestation);
        return ResponseEntity.ok(phoneAlertResponses);
    }
}
