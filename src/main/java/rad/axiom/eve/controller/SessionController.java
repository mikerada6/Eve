package rad.axiom.eve.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import rad.axiom.eve.exception.ConflictException;
import rad.axiom.eve.exception.NotAcceptableException;
import rad.axiom.eve.exception.ResourceNotFoundException;
import rad.axiom.eve.repository.SessionRepository;
import rad.axiom.eve.session.Session;
import rad.axiom.eve.session.SessionStatus;
import rad.axiom.eve.session.Walle;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ManagedResource(description = "Session Controller")
@Controller
@RequestMapping("/session")
public class SessionController {

    private static final Logger logger = LoggerFactory.getLogger(SessionController.class);
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private WalleController walleController;


    @GetMapping(path = "/")
    public @ResponseBody
    List<Session> findAllSessions() {
        logger.info("Finding all sessions");
        return sessionRepository.findAll();
    }

    @GetMapping(path = "/crete")
    public @ResponseBody
    Session startSession(@RequestParam(value = "walleId", required = true) String walleId, @RequestParam(value = "sort", required = false) ArrayList<String> sort, @RequestParam(value = "limit", required = false) ArrayList<String> limit) {
        logger.info("Starting a new Session");
        Walle walle = null;
        try {
            walle = walleController.findWalleById(walleId);
        } catch (ResourceNotFoundException ex) {
            logger.warn("We could not find a Wall-E with an id of {}.", walleId);
            throw new ResourceNotFoundException("Could not Wall-E with id: " + walleId + ".");
        }
        List<Session> sessions = sessionRepository.findAll();
        List<Session> openSessions = sessions.stream().filter(s -> (s.getWalle().equals(s.getWalle())) && (s.getStatus() != SessionStatus.CLOSED)).collect(Collectors.toList());

        if (openSessions.size() == 1) {
            Session session = openSessions.get(0);
            throw new ConflictException("A session is already open for this Wall-E with Id: " + session.getId()+".");
        }
        if (openSessions.size() > 0) {
            List<String> sessionIds = sessions.stream().map(s -> s.getId()).collect(Collectors.toList());
            throw new ConflictException("Multiple session are already open for this Wall-E with Ids: " + sessionIds);
        }

        Session s = new Session();
        s.setStartTime(System.currentTimeMillis());
        s.setWalle(walle);
        s.setStatus(SessionStatus.NORMAL);
        s = sessionRepository.save(s);

        //TODO Contact Wall-E to start the sort.

        return s;
    }

    @GetMapping(path = "/{id}")
    public @ResponseBody
    Session findSessionById(@PathVariable("id") String id) {
        logger.info("Finding session with Id:{}.", id);
        return sessionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Could not Session with id: " + id));
    }

    @GetMapping(path = "/{id}/close")
    public @ResponseBody
    Session closeSessionById(@PathVariable("id") String id) {
        logger.info("Closing session with Id:{}.", id);
        Session session = sessionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Could not Session with id: " + id));
        if (session.getStatus() == SessionStatus.CLOSED) {
            //the session is already closed;
            throw new NotAcceptableException("Session " + id + " was already closed at " + session.getEndTime() + ".");
        }
        long timestamp = System.currentTimeMillis();
        session.setEndTime(timestamp);
        session.setLastUpdated(timestamp);
        session.setStatus(SessionStatus.CLOSED);
        return sessionRepository.save(session);
    }

    @GetMapping(path = "/{id}/pause")
    public @ResponseBody
    Session pauseSessionById(@PathVariable("id") String id) {
        logger.info("Closing session with Id:{}.", id);
        Session session = sessionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Could not Session with id: " + id));
        if (session.getStatus() == SessionStatus.CLOSED) {
            //the session is already closed;
            throw new NotAcceptableException("Session " + id + " was already closed at " + session.getEndTime() + ".");
        }
        long timestamp = System.currentTimeMillis();
        session.setLastUpdated(timestamp);
        session.setStatus(SessionStatus.PAUSED);
        sessionRepository.save(session);
        return session;
    }

    public Session saveSession(Session s) {
        return sessionRepository.save(s);
    }


}
