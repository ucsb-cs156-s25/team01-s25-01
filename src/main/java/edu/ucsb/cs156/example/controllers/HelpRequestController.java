package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.HelpRequest;
import edu.ucsb.cs156.example.entities.UCSBDate;
import edu.ucsb.cs156.example.entities.UCSBDiningCommons;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.HelpRequestRepository;

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
 * This is a REST controller for HelpRequest
 */

 @Tag(name = "HelpRequest")
@RequestMapping("/api/helprequest")
@RestController
@Slf4j
public class HelpRequestController extends ApiController {

    @Autowired
    HelpRequestRepository helprequestRepository;

     /**
     * List all HelpRequests
     * 
     * @return an iterable of HelpRequests
     */
    @Operation(summary= "List all helprequests")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<HelpRequest> allHelpRequests() {
        Iterable<HelpRequest> helprequests = helprequestRepository.findAll();
        return helprequests;
    }


    /**
     * Create a new helprequest
     * 
     * @param requesterEmail  the requester's email
     * @param teamId          the ID of the team of the requester
     * @param tableOrBreakoutRoom the table or breakout room of the requester
     * @param requestTime the timestamp on the help request, with time zome information
     * @param explanation the explanation for the problem
     * @param solved the boolean paramter indicating if the problem is sloved
     * @return the saved helprequest
     */
    @Operation(summary= "Create a new helprequest")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public HelpRequest postHelpRequest(
            @Parameter(name="requesterEmail") @RequestParam String requesterEmail,
            @Parameter(name="teamId") @RequestParam String teamId,
            @Parameter(name="tableOrBreakoutRoom") @RequestParam String tableOrBreakoutRoom,
            @Parameter(name="explanation") @RequestParam String explanation,
            @Parameter(name="solved") @RequestParam boolean solved,
            @Parameter(name="requestTime", description="date (in iso format, e.g. YYYY-mm-ddTHH:MM:SSZ; see https://en.wikipedia.org/wiki/ISO_8601)") @RequestParam("requestTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime requestTime)
            throws JsonProcessingException {

        // For an explanation of @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        // See: https://www.baeldung.com/spring-date-parameters

        log.info("requestTime={}", requestTime);

        HelpRequest helprequest = new HelpRequest();
        helprequest.setRequesterEmail(requesterEmail);
        helprequest.setTeamId(teamId);
        helprequest.setTableOrBreakoutRoom(tableOrBreakoutRoom);
        helprequest.setRequestTime(requestTime);
        helprequest.setExplanation(explanation);
        helprequest.setSolved(solved);

        HelpRequest savedHelpRequest = helprequestRepository.save(helprequest);

        return savedHelpRequest;
    }

    /**
     * Get a single helprequest by id
     * 
     * @param id the id of the helprequest
     * @return a HelpRequest
     */
    @Operation(summary= "Get a single helprequest")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public HelpRequest getById(
            @Parameter(name="id") @RequestParam Long id) {
        HelpRequest helprequest = helprequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(HelpRequest.class, id));

        return helprequest;
    }
    
         /**
     * Update a single helprequest
     * 
     * @param id       id of the date to update
     * @param incoming the new hleprequest
     * @return the updated date object
     */
    @Operation(summary= "Update a single helprequest")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public HelpRequest updateHelpRequest(
            @Parameter(name="id") @RequestParam Long id,
            @RequestBody @Valid HelpRequest incoming) {

        HelpRequest helprequest = helprequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(HelpRequest.class, id));

        helprequest.setRequesterEmail(incoming.getRequesterEmail());
        helprequest.setTeamId(incoming.getTeamId());
        helprequest.setTableOrBreakoutRoom(incoming.getTableOrBreakoutRoom());
        helprequest.setRequestTime(incoming.getRequestTime());
        helprequest.setExplanation(incoming.getExplanation());
        helprequest.setSolved(incoming.getSolved());

        helprequestRepository.save(helprequest);

        return helprequest;
    }

    /**
     * Delete a HelpRequest
     * 
     * @param id the id of the helprequest to delete
     * @return a message indicating the helprequest was deleted
     */
    @Operation(summary= "Delete a HelpRequest")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteHelpRequest(
            @Parameter(name="id") @RequestParam Long id) {
        HelpRequest helprequest = helprequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(HelpRequest.class, id));

        helprequestRepository.delete(helprequest);
        return genericMessage("HelpRequest with id %s deleted".formatted(id));
    }
}