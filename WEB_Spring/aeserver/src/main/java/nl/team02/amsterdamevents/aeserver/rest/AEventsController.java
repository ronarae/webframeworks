package nl.team02.amsterdamevents.aeserver.rest;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import nl.team02.amsterdamevents.aeserver.models.AEvent;
import nl.team02.amsterdamevents.aeserver.models.Registration;
import nl.team02.amsterdamevents.aeserver.repositories.AEventsRepository;
import nl.team02.amsterdamevents.aeserver.repositories.AEventsRepositoryJpa;
import nl.team02.amsterdamevents.aeserver.repositories.RegistrationsRepositoryJpa;
import nl.team02.amsterdamevents.aeserver.views.ViewAEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


import javax.transaction.Transactional;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RestController
public class AEventsController {

    @Autowired
    private AEventsRepositoryJpa aEventsRepositoryJpa;

    @Autowired
    private RegistrationsRepositoryJpa registrationsRepositoryJpa;

    private URI getLocationURI(long id) {
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().
                path("/{id}").buildAndExpand(id).toUri();
        return location;
    }

    @GetMapping("/aevents")
    public List<AEvent> getAllAEvents(@RequestParam(name = "status", required = false) String status,
                                      @RequestParam(name = "title", required = false) String title,
                                      @RequestParam(name = "minRegistrations", required = false) Integer minRegistration) {
//
        if (status != null && title != null || status != null && minRegistration != null || minRegistration != null && title != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can only handle one request parameter title=, status= or minRegistration=");
        }

        if (status != null) {
            status = status.toUpperCase();
            for (AEvent.AEventStatus value : AEvent.AEventStatus.values()) {
                if (value.name().equals(status)) {
                    return aEventsRepositoryJpa.findByQuery("AEvent_find_by_status", value);
                }
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This AEvent status is not a valid. Please try again.");

        }

        if (title != null) {
            return aEventsRepositoryJpa.findByQuery("AEvent_find_by_title",'%' + title + '%');
        }
        if (minRegistration != null) {
            return aEventsRepositoryJpa.findByQuery("AEvent_find_by_minRegistrations", minRegistration);
        }

        return aEventsRepositoryJpa.findAll();
    }

    @JsonView(ViewAEvent.Public.class)
    @GetMapping("/aevents/summary")
    public List<AEvent> getAEventsSummary() {
        if (aEventsRepositoryJpa.findAll().size() != 0) {
            return aEventsRepositoryJpa.findAll();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There are no AEvents found");
    }

    @GetMapping("/aevents/{id}")
    public AEvent getAEventById(@PathVariable long id) throws ResponseStatusException {
        if (aEventsRepositoryJpa.findById(id) != null) {
            return aEventsRepositoryJpa.findById(id);
        }
        throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "Cannot find AEvents ID: " + id);
    }

    @PostMapping("/aevents")
    public ResponseEntity<AEvent> createAEvent(@RequestBody AEvent aEvent) {
        aEvent.setId(0);
        AEvent createdAEvent = aEventsRepositoryJpa.save(aEvent);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(createdAEvent.getId()).toUri();
        return ResponseEntity.created(location).body(createdAEvent);
    }

    @PostMapping("/aevents/random")
    public ResponseEntity<AEvent> createAEvent() {
        AEvent aEvent = AEvent.createRandomAEvent();
        AEvent createdAEvent = aEventsRepositoryJpa.save(aEvent);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(createdAEvent.getId()).toUri();
        return ResponseEntity.created(location).body(createdAEvent);
    }


    @PostMapping("/aevents/{id}/register")
    @Transactional
    public ResponseEntity<Registration> createNewRegistration(@PathVariable long id, @RequestBody LocalDateTime submissionDateTime) {
        AEvent aEvent = aEventsRepositoryJpa.findById(id);

        if (!aEvent.getStatus().equals(AEvent.AEventStatus.PUBLISHED)) {
            throw new RuntimeException("AEvent-id=" + aEvent.getId() + " is not published.");
        } else if (aEvent.getNumberOfRegistrations() >= aEvent.getMaxParticipants() && aEvent.getMaxParticipants() != 0) {
            throw new RuntimeException("AEvent-id=" + aEvent.getId() + " has exceeded the number of participants.");
        }
        if (submissionDateTime == null) {
            submissionDateTime = LocalDateTime.now();

        }
        Registration reg = aEvent.createNewRegistration(submissionDateTime);

        aEventsRepositoryJpa.save(aEvent);
        registrationsRepositoryJpa.save(reg);

        return ResponseEntity.created(
                ServletUriComponentsBuilder.fromCurrentRequest().path("").build().toUri()
        ).body(reg);
    }

    @PutMapping("/aevents/{id}")
    public ResponseEntity<AEvent> saveAEvent(@PathVariable long id, @RequestBody AEvent aEvent)
            throws ResponseStatusException {
        if (id == aEvent.getId()) {
            AEvent updatedAEvent = aEventsRepositoryJpa.save(aEvent);
            return ResponseEntity.created(getLocationURI(updatedAEvent.getId())).body(updatedAEvent);
        }
        throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "The requested ID does not meet the ID provided in the body");
    }

    @DeleteMapping("/aevents/{id}")
    public ResponseEntity<Boolean> deleteById(@PathVariable long id) throws ResponseStatusException {
        if (aEventsRepositoryJpa.deleteById(id)) {
            return ResponseEntity.ok(true);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot delete the AEvent because the provided ID: " + id + " does not exist");
    }


}
