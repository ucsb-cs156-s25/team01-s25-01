package edu.ucsb.cs156.example.controllers;


import edu.ucsb.cs156.example.entities.UCSBDate;
import edu.ucsb.cs156.example.entities.UCSBDiningCommons;
import edu.ucsb.cs156.example.entities.UCSBOrganization;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.UCSBDateRepository;
import edu.ucsb.cs156.example.repositories.UCSBDiningCommonsRepository;
import edu.ucsb.cs156.example.repositories.UCSBOrganizationRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;


import com.fasterxml.jackson.core.JsonProcessingException;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import jakarta.validation.Valid;


import java.time.LocalDateTime;


/**
* This is a REST controller for UCSBOrganizations
*/


@Tag(name = "UCSBOrganizations")
@RequestMapping("/api/ucsborganizations")
@RestController
@Slf4j


public class UCSBOrganizationsController extends ApiController{


   @Autowired
   UCSBOrganizationRepository ucsborganizationRepository;


   /**
    * THis method returns a list of all ucsborganizations.
    * @return a list of all ucsborganizations
    */
   @Operation(summary= "List all ucsb organizations")
   @PreAuthorize("hasRole('ROLE_USER')")
   @GetMapping("/all")
   public Iterable<UCSBOrganization> allOrganizations() {
       Iterable<UCSBOrganization> organizations = ucsborganizationRepository.findAll();
       return organizations;
   }


    /**
    * This method creates a new ucsborganizations. Accessible only to users with the role "ROLE_ADMIN".
    * @param orgCode the ucsborganizations code
    * @param orgTranslationShort the ucsborganizations translation short
    * @param orgTranslation the ucsb organizations translation
    * @param inactive whether or not the ucsb organizations is active
    * @return the save ucsborganizations
    */
   @Operation(summary= "Create a new organizations")
   @PreAuthorize("hasRole('ROLE_ADMIN')")
   @PostMapping("/post")
   public UCSBOrganization postOrganizationss(
       @Parameter(name="orgCode") @RequestParam String orgCode,
       @Parameter(name="orgTranslationShort") @RequestParam String orgTranslationShort,
       @Parameter(name="orgTranslation") @RequestParam String orgTranslation,
       @Parameter(name="inactive") @RequestParam boolean inactive
       )
       {


       UCSBOrganization organizations = new UCSBOrganization();
       organizations.setOrgCode(orgCode);
       organizations.setOrgTranslationShort(orgTranslationShort);
       organizations.setOrgTranslation(orgTranslation);
       organizations.setInactive(inactive);


       UCSBOrganization savedOrganizations = ucsborganizationRepository.save(organizations);


       return savedOrganizations;
   }
    @Operation(summary = "Get a single UCSBOrganization by orgCode")
   @PreAuthorize("hasRole('ROLE_USER')")
   @GetMapping("")
   public UCSBOrganization getById(
       @Parameter(name="orgCode") @RequestParam String orgCode) {
      
       UCSBOrganization organization = ucsborganizationRepository.findByOrgCode(orgCode)
               .orElseThrow(() -> new EntityNotFoundException(UCSBOrganization.class, orgCode));
  
       return organization;
   }

   /**
    * Update a single UCSB Organization
    *
    * @param orgCode       orgCode of the Organization to update
    * @param incoming the new Organization Item
    * @return the updated Organization object
    */
    @Operation(summary = "Update a Organization")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public UCSBOrganization updateUCSBOrganization(
            @Parameter(name = "orgCode") @RequestParam String orgCode,
            @RequestBody @Valid UCSBOrganization incoming) {
 
 
                UCSBOrganization ucsbOrganization = ucsborganizationRepository.findByOrgCode(orgCode)
                .orElseThrow(() -> new EntityNotFoundException(UCSBOrganization.class, orgCode));
 
 
        ucsbOrganization.setOrgTranslationShort(incoming.getOrgTranslationShort());
        ucsbOrganization.setOrgTranslation(incoming.getOrgTranslation());
        ucsbOrganization.setInactive(incoming.getInactive());
 
 
        ucsborganizationRepository.save(ucsbOrganization);
 
 
        return ucsbOrganization;
    }
    
    /**
     * Delete a ucsborganizations. Accessible only to users with the role "ROLE_ADMIN".
     * @param orgCode orgCode of the organizations
     * @return a message indiciating the organizations was deleted
     */
    @Operation(summary= "Delete a UCSBOrganizations")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteCommons(
            @Parameter(name="orgCode") @RequestParam String orgCode) {
        UCSBOrganization organizations = ucsborganizationRepository.findByOrgCode(orgCode)
                .orElseThrow(() -> new EntityNotFoundException(UCSBOrganization.class, orgCode));

                ucsborganizationRepository.delete(organizations);
        return genericMessage("UCSBOrganization with id %s deleted".formatted(orgCode));
    }
}
