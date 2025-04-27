package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.HelpRequest;
import edu.ucsb.cs156.example.entities.UCSBDate;
import edu.ucsb.cs156.example.repositories.HelpRequestRepository;
import edu.ucsb.cs156.example.repositories.UCSBDateRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.time.LocalDateTime;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = HelpRequestController.class)
@Import(TestConfig.class)
public class HelpRequestControllerTests extends ControllerTestCase {
    
    @MockBean
    HelpRequestRepository helprequestRepository;

    @MockBean
    UserRepository userRepository;


    @Test
    public void logged_out_users_cannot_get_all() throws Exception {
            mockMvc.perform(get("/api/helprequest/all"))
                            .andExpect(status().is(403)); // logged out users can't get all
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_users_can_get_all() throws Exception {
            mockMvc.perform(get("/api/helprequest/all"))
                            .andExpect(status().is(200)); // logged
    }

    @Test
        public void logged_out_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/helprequest/post"))
                                .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_regular_users_cannot_post() throws Exception {
            mockMvc.perform(post("/api/helprequest/post"))
                            .andExpect(status().is(403)); // only admins can post
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_user_can_get_all_ucsbdates() throws Exception {

            // arrange
            LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");
            
            HelpRequest helprequest1 = HelpRequest.builder()
                .requesterEmail("hjin133@ucsb.edu")
                .teamId("01")
                .tableOrBreakoutRoom("table1")
                .requestTime(ldt1)
                .explanation("Can-i-get-a-help?")
                .solved(true)
                .build();

            ArrayList<HelpRequest> expectedHelpRequest = new ArrayList<>();
            expectedHelpRequest.add(helprequest1);

            when(helprequestRepository.findAll()).thenReturn(expectedHelpRequest);

            // act
            MvcResult response = mockMvc.perform(get("/api/helprequest/all"))
                            .andExpect(status().isOk()).andReturn();

            // assert

            verify(helprequestRepository, times(1)).findAll();
            String expectedJson = mapper.writeValueAsString(expectedHelpRequest);

            String responseString = response.getResponse().getContentAsString();
            assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void an_admin_user_can_post_a_new_helprequest() throws Exception {
                // arrange

                LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");

                HelpRequest helprequest1 = HelpRequest.builder()
                    .requesterEmail("hjin133@ucsb.edu")
                    .teamId("01")
                    .tableOrBreakoutRoom("table1")
                    .requestTime(ldt1)
                    .explanation("Can-i-get-a-help?")
                    .solved(true)
                    .build();

                when(helprequestRepository.save(eq(helprequest1))).thenReturn(helprequest1);

                // act
                MvcResult response = mockMvc.perform(
                                post("/api/helprequest/post?requesterEmail=hjin133@ucsb.edu&teamId=01&tableOrBreakoutRoom=table1&requestTime=2022-01-03T00:00:00&explanation=Can-i-get-a-help?&solved=true")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(helprequestRepository, times(1)).save(helprequest1);
                String expectedJson = mapper.writeValueAsString(helprequest1);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

}
