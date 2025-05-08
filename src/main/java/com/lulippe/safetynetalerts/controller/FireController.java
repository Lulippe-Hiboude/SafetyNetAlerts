package com.lulippe.safetynetalerts.controller;

import com.lulippe.safetynetalerts.fire.api.FireApi;
import com.lulippe.safetynetalerts.fire.model.FireAddressResponse;
import com.lulippe.safetynetalerts.service.FireService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FireController implements FireApi {
    private final FireService fireService;

    @Override
    public ResponseEntity<FireAddressResponse> getFireResponse(String address) {
        log.info("getting FireAddressResponse for address {}", address);
        final FireAddressResponse fireAddressResponse = fireService.handleFireRequest(address);
        return ResponseEntity.ok(fireAddressResponse);
    }
}
