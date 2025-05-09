package com.lulippe.safetynetalerts.controller;

import com.lulippe.safetynetalerts.communityEmail.model.CommunityEmailList;
import com.lulippe.safetynetalerts.communityEmail.model.EmailEntry;
import com.lulippe.safetynetalerts.exception.entity_exception.InexistantObjectException;
import com.lulippe.safetynetalerts.exception.entity_exception.InvalidDataException;
import com.lulippe.safetynetalerts.service.CommunityEmailService;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CommunityEmailController.class)
@AutoConfigureMockMvc
class CommunityEmailControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommunityEmailService communityEmailService;

    private final EasyRandom easyRandom = new EasyRandom();

    @Test
    @DisplayName("should return a CommunityEmailList")
    void shouldReturnACommunityEmailList() throws Exception {
        //given
        final String city = "city";
        final EmailEntry emailEntry = easyRandom.nextObject(EmailEntry.class);
        final List<EmailEntry> emailEntries = Collections.singletonList(emailEntry);
        final CommunityEmailList communityEmailList = new CommunityEmailList();
        communityEmailList.setEmails(emailEntries);
        given(communityEmailService.getCommunityEmailList(city)).willReturn(communityEmailList);

        //when & then
        mockMvc.perform(get("/communityEmail")
                .param("city", city))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emails",hasSize(1)))
                .andExpect(jsonPath("$.emails[0].email").value(emailEntry.getEmail()));
    }

    @Test
    @DisplayName("should throw Not found if no person found with given city")
    void shouldThrowNotFoundIfNoPersonFound() throws Exception {
        //given
        final String city = "city";
        given(communityEmailService.getCommunityEmailList(city)).willThrow(new InexistantObjectException("No person found with given city"));

        //when & then
        mockMvc.perform(get("/communityEmail")
                .param("city", city))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should throw Bad request if city is empty")
    void shouldThrowBadRequestIfCityIsEmpty() throws Exception {
        //given
        final String city = "";
        given(communityEmailService.getCommunityEmailList(city)).willThrow(new InvalidDataException("city cannot be null or empty"));

        //when & then
        mockMvc.perform(get("/communityEmail")
                .param("city", city))
                .andExpect(status().isBadRequest());
    }
}