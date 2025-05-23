package com.lulippe.safetynetalerts.controller;

import com.lulippe.safetynetalerts.childAlert.api.ChildAlertApi;
import com.lulippe.safetynetalerts.childAlert.model.ChildAlertResponse;
import com.lulippe.safetynetalerts.service.ChildAlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChildAlertController implements ChildAlertApi {
    private final ChildAlertService childAlertService;
    @Override
    public ResponseEntity<ChildAlertResponse> getChildAlertResponse(final String address) {
            log.info("Getting Child Alert Response");
            final ChildAlertResponse childAlertResponse = childAlertService.getChildAlertResponse(address);
            return ResponseEntity.ok(childAlertResponse);
    }
}
