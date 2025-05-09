package com.lulippe.safetynetalerts.controller;

import com.lulippe.safetynetalerts.communityEmail.api.CommunityEmailApi;
import com.lulippe.safetynetalerts.communityEmail.model.CommunityEmailList;
import com.lulippe.safetynetalerts.service.CommunityEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CommunityEmailController implements CommunityEmailApi {
    private final CommunityEmailService communityEmailService;

    @Override
    public ResponseEntity<CommunityEmailList> getCommunityEmail(String city) {
        log.info("getting CommunityEmailList for city {}", city);
        return ResponseEntity.ok(communityEmailService.getCommunityEmailList(city));
    }
}
