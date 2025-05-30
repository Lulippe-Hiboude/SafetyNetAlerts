package com.lulippe.safetynetalerts.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lulippe.safetynetalerts.communityEmail.model.CommunityEmailList;
import com.lulippe.safetynetalerts.communityEmail.model.EmailEntry;
import com.lulippe.safetynetalerts.repository.manager.DataManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class CommunityEmailControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DataManager dataManager;

    @AfterEach
    void resetDataContainer() {
        dataManager.init();
    }

    @Test
    @DisplayName("should return a CommunityEmailList")
    void shouldReturnACommunityEmailList() throws Exception {
        //given
        final String city = "Culver";
        final EmailEntry emailEntry = new EmailEntry();
        emailEntry.setEmail("jaboyd@email.com");
        final List<EmailEntry> emailEntries = Collections.singletonList(emailEntry);
        final CommunityEmailList communityEmailList = new CommunityEmailList();
        communityEmailList.setEmails(emailEntries);
        final String expectedJson = objectMapper.writeValueAsString(communityEmailList);

        //when & then
        mockMvc.perform(get("/communityEmail")
                        .param("city", city))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    @DisplayName("should return BAD_REQUEST if city is null or empty")
    void shouldReturnBadRequestIfCityIsNullOrEmpty() throws Exception {
        //given
        final String city = "";

        //when & then
        mockMvc.perform(get("/communityEmail")
                        .param("city", city))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("city cannot be null or empty"));
    }

    @Test
    @DisplayName("should return NOT_FOUND if no person found for given city")
    void shouldReturnNotFoundIfPersonNotFoundForGivenCity() throws Exception {
        //given
        final String city = "Paris";

        //when & then
        mockMvc.perform(get("/communityEmail")
                        .param("city", city))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("No Person found for this city :" + city));
    }

}
