package edu.ucsb.cs156.example.controllers;


import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.UCSBDiningCommons;
import edu.ucsb.cs156.example.entities.UCSBOrganization;
import edu.ucsb.cs156.example.entities.UCSBOrganization.UCSBOrganizationBuilder;
import edu.ucsb.cs156.example.repositories.UCSBDiningCommonsRepository;
import edu.ucsb.cs156.example.repositories.UCSBOrganizationRepository;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import com.nimbusds.openid.connect.sdk.assurance.evidences.Organization;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;


import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@WebMvcTest(controllers = UCSBOrganizationsController.class)
@Import(TestConfig.class)




public class UCSBOrganizationsControllerTests extends ControllerTestCase {
   @MockBean
   UCSBOrganizationRepository ucsbOrganizationsRepository;


   @MockBean
   UserRepository userRepository;


   @Test
   public void logged_out_users_cannot_get_all() throws Exception {
           mockMvc.perform(get("/api/ucsborganizations/all"))
                           .andExpect(status().is(403)); // logged out users can't get all
   }


   @WithMockUser(roles = { "USER" })
   @Test
   public void logged_in_users_can_get_all() throws Exception {
           mockMvc.perform(get("/api/ucsborganizations/all"))
                           .andExpect(status().is(200)); // logged
   }


   @Test
   public void logged_out_users_cannot_get_by_id() throws Exception {
           mockMvc.perform(get("/api/ucsborganizations?orgCode=String"))
                           .andExpect(status().is(403)); // logged out users can't get by id
   }


   @Test
   public void logged_out_users_cannot_post() throws Exception {
           mockMvc.perform(post("/api/ucsborganizations/post"))
                           .andExpect(status().is(403));
   }


   @WithMockUser(roles = { "USER" })
   @Test
   public void logged_in_regular_users_cannot_post() throws Exception {
           mockMvc.perform(post("/api/ucsborganizations/post"))
                           .andExpect(status().is(403)); // only admins can post
   }


   @WithMockUser(roles = { "USER" })
   @Test
   public void logged_in_user_can_get_all_ucsborganizations() throws Exception {


           // arrange


           UCSBOrganization organization1 = UCSBOrganization.builder()
                           .orgCode("String")
                           .orgTranslationShort("str")
                           .orgTranslation("string")
                           .inactive(false).build();


           List<UCSBOrganization> expectedOrganization = Arrays.asList(organization1);


           when(ucsbOrganizationsRepository.findAll()).thenReturn(expectedOrganization);


           // act
           MvcResult response = mockMvc.perform(get("/api/ucsborganizations/all"))
                           .andExpect(status().isOk()).andReturn();


           // assert


           verify(ucsbOrganizationsRepository, times(1)).findAll();
           String expectedJson = mapper.writeValueAsString(expectedOrganization);
           String responseString = response.getResponse().getContentAsString();
           assertEquals(expectedJson, responseString);
   }


   @WithMockUser(roles = { "ADMIN", "USER" })
   @Test
   public void an_admin_user_can_post_a_new_organiations() throws Exception {
           // arrange




           UCSBOrganization organization1 = UCSBOrganization.builder()
           .orgCode("String")
           .orgTranslationShort("str")
           .orgTranslation("string")
           .inactive(true).build();




           when(ucsbOrganizationsRepository.save(eq(organization1))).thenReturn(organization1);


           // act
           MvcResult response = mockMvc.perform(
                           post("/api/ucsborganizations/post?orgCode=String&orgTranslationShort=str&orgTranslation=string&inactive=true")
                                           .with(csrf()))
                           .andExpect(status().isOk()).andReturn();


           // assert
           verify(ucsbOrganizationsRepository, times(1)).save(organization1);
           String expectedJson = mapper.writeValueAsString(organization1);
           String responseString = response.getResponse().getContentAsString();
           assertEquals(expectedJson, responseString);
   }




   @WithMockUser(roles = {"USER"})
   @Test
   public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {
       when(ucsbOrganizationsRepository.findByOrgCode(eq("String"))).thenReturn(Optional.empty());


       MvcResult response = mockMvc.perform(get("/api/ucsborganizations?orgCode=String"))
           .andExpect(status().isNotFound())
           .andReturn();


       verify(ucsbOrganizationsRepository, times(1)).findByOrgCode(eq("String"));
       Map<String, Object> json = responseToJson(response);
       assertEquals("EntityNotFoundException", json.get("type"));
       assertEquals("UCSBOrganization with id String not found", json.get("message"));
   }


   @WithMockUser(roles = {"USER"})
   @Test
   public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {
       UCSBOrganization organization1 = UCSBOrganization.builder()
           .orgCode("String")
           .orgTranslationShort("str")
           .orgTranslation("string")
           .inactive(true)
           .build();


       when(ucsbOrganizationsRepository.findByOrgCode(eq("String"))).thenReturn(Optional.of(organization1));


       MvcResult response = mockMvc.perform(get("/api/ucsborganizations?orgCode=String"))
           .andExpect(status().isOk())
           .andReturn();


       verify(ucsbOrganizationsRepository, times(1)).findByOrgCode(eq("String"));
       String expectedJson = mapper.writeValueAsString(organization1);
       assertEquals(expectedJson, response.getResponse().getContentAsString());
   }

   @WithMockUser(roles = { "ADMIN", "USER" })
   @Test
   public void admin_can_edit_an_existing_organizations() throws Exception {
           // arrange

           UCSBOrganization organization1 = UCSBOrganization.builder()
           .orgCode("String")
           .orgTranslationShort("str")
           .orgTranslation("string")
           .inactive(false)
           .build();

           UCSBOrganization editedorganization1 = UCSBOrganization.builder()
           .orgCode("String")
           .orgTranslationShort("int")
           .orgTranslation("integer")
           .inactive(true)
           .build();
      
           String requestBody = mapper.writeValueAsString(editedorganization1);

           when(ucsbOrganizationsRepository.findByOrgCode(eq("String"))).thenReturn(Optional.of(organization1));
           // act
           MvcResult response = mockMvc.perform(
                           put("/api/ucsborganizations?orgCode=String")
                                           .contentType(MediaType.APPLICATION_JSON)
                                           .characterEncoding("utf-8")
                                           .content(requestBody)
                                           .with(csrf()))
                           .andExpect(status().isOk()).andReturn();

           // assert
           verify(ucsbOrganizationsRepository, times(1)).findByOrgCode("String");
           verify(ucsbOrganizationsRepository, times(1)).save(editedorganization1); // should be saved with updated info
           String responseString = response.getResponse().getContentAsString();
           assertEquals(requestBody, responseString);
   }


   @WithMockUser(roles = { "ADMIN", "USER" })
   @Test
   public void admin_cannot_edit_organizations_that_does_not_exist() throws Exception {
           // arrange
           UCSBOrganization editedorganization1 = UCSBOrganization.builder()
           .orgCode("String")
           .orgTranslationShort("str")
           .orgTranslation("string")
           .inactive(true)
           .build();
           
           String requestBody = mapper.writeValueAsString(editedorganization1);

           when(ucsbOrganizationsRepository.findByOrgCode(eq("String"))).thenReturn(Optional.empty());

           // act
           MvcResult response = mockMvc.perform(
                           put("/api/ucsborganizations?orgCode=String")
                                           .contentType(MediaType.APPLICATION_JSON)
                                           .characterEncoding("utf-8")
                                           .content(requestBody)
                                           .with(csrf()))
                           .andExpect(status().isNotFound()).andReturn();

           // assert
           verify(ucsbOrganizationsRepository, times(1)).findByOrgCode("String");
           Map<String, Object> json = responseToJson(response);
           assertEquals("UCSBOrganization with id String not found", json.get("message"));

   }

   @WithMockUser(roles = { "ADMIN", "USER" })
   @Test
   public void admin_can_delete_a_ucsborganization() throws Exception {
           // arrange


           UCSBOrganization organization1 = UCSBOrganization.builder()
           .orgCode("String")
           .orgTranslationShort("str")
           .orgTranslation("string")
           .inactive(true)
           .build();


       when(ucsbOrganizationsRepository.findByOrgCode(eq("String"))).thenReturn(Optional.of(organization1));

           // act
           MvcResult response = mockMvc.perform(
                           delete("/api/ucsborganizations?orgCode=String")
                                           .with(csrf()))
                           .andExpect(status().isOk()).andReturn();

           // assert
           verify(ucsbOrganizationsRepository, times(1)).findByOrgCode("String");
           verify(ucsbOrganizationsRepository, times(1)).delete(organization1);

           Map<String, Object> json = responseToJson(response);
           assertEquals("UCSBOrganization with id String deleted", json.get("message"));
   }

   @WithMockUser(roles = { "ADMIN", "USER" })
   @Test
   public void admin_tries_to_delete_non_existant_organizations_and_gets_right_error_message()
                   throws Exception {
           // arrange

           when(ucsbOrganizationsRepository.findByOrgCode(eq("String"))).thenReturn(Optional.empty());

           // act
           MvcResult response = mockMvc.perform(
                           delete("/api/ucsborganizations?orgCode=String")
                                           .with(csrf()))
                           .andExpect(status().isNotFound()).andReturn();

           // assert
           verify(ucsbOrganizationsRepository, times(1)).findByOrgCode("String");
           Map<String, Object> json = responseToJson(response);
           assertEquals("UCSBOrganization with id String not found", json.get("message"));
   }
}


