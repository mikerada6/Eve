package rad.axiom.eve.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.test.util.ReflectionTestUtils;
import rad.axiom.eve.exception.ConflictException;
import rad.axiom.eve.exception.NotAcceptableException;
import rad.axiom.eve.exception.ResourceNotFoundException;
import rad.axiom.eve.repository.SessionRepository;
import rad.axiom.eve.session.Session;
import rad.axiom.eve.session.SessionStatus;
import rad.axiom.eve.session.Walle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(JUnitPlatform.class)
class SessionControllerTest {

    private static SessionController sessionController;


    @BeforeAll
    public static void setUp() {
        sessionController = new SessionController();
    }


    @Test
    void findAllSessions() {
        SessionRepository sessionRepositoryA = mock(SessionRepository.class);
        ReflectionTestUtils.setField(sessionController,"sessionRepository",sessionRepositoryA);

        Walle walleA = Walle.builder().id("walleA").address("localhost").sessions(new HashSet<Session>()).build();
        Session sessionA = Session.builder().id("a").walle(walleA).build();
        ArrayList<Session> list = new ArrayList<>();
        list.add(sessionA);


        when(sessionRepositoryA.findAll()).thenReturn(list);
        List<Session> sessions = sessionController.findAllSessions();
        Assertions.assertEquals(1, sessions.size());
        verify(sessionRepositoryA).findAll();
    }

    @Test
    void saveSession() {
        SessionRepository sessionRepository = mock(SessionRepository.class);
        ReflectionTestUtils.setField(sessionController,"sessionRepository",sessionRepository);

        Walle walleA = Walle.builder().id("walleA").address("localhost").sessions(new HashSet<Session>()).build();
        Session sessionA = Session.builder().id("a").walle(walleA).build();
        ArrayList<Session> list = new ArrayList<>();
        when(sessionRepository.save(sessionA)).thenReturn(sessionA);
        Session result = sessionController.saveSession(sessionA);
        Assertions.assertEquals(sessionA, result);
        verify(sessionRepository).save(sessionA);
    }

    @Test
    void pauseSessionThatIsClosed() {
        SessionRepository sessionRepository = mock(SessionRepository.class);
        ReflectionTestUtils.setField(sessionController,"sessionRepository",sessionRepository);

        Walle walleA = Walle.builder().id("walleA").address("localhost").sessions(new HashSet<Session>()).build();
        Session sessionA = Session.builder().id("a").walle(walleA).status(SessionStatus.CLOSED).build();
        when(sessionRepository.findById("a")).thenReturn(java.util.Optional.ofNullable(sessionA));

        Exception exception = assertThrows(NotAcceptableException.class, () -> {
            Session result = sessionController.pauseSessionById("a");
        });
        Assertions.assertTrue(exception.getMessage().contains("Session a was already closed at "));
        verify(sessionRepository).findById("a");
    }

    @Test
    void pauseSession() {
        SessionRepository sessionRepository = mock(SessionRepository.class);
        ReflectionTestUtils.setField(sessionController,"sessionRepository",sessionRepository);

        Walle walleA = Walle.builder().id("walleA").address("localhost").sessions(new HashSet<Session>()).build();
        Session sessionA = Session.builder().id("a").walle(walleA).status(SessionStatus.NORMAL).build();
        when(sessionRepository.findById("a")).thenReturn(java.util.Optional.ofNullable(sessionA));
        when(sessionRepository.save(any(Session.class))).thenReturn(sessionA);
        Session result = sessionController.pauseSessionById("a");
        Assertions.assertEquals(SessionStatus.PAUSED, result.getStatus());
        verify(sessionRepository).findById("a");
        verify(sessionRepository).save(any(Session.class));
    }

    @Test
    void closeSessionThatIsClosed() {
        SessionRepository sessionRepository = mock(SessionRepository.class);
        ReflectionTestUtils.setField(sessionController,"sessionRepository",sessionRepository);

        Walle walleA = Walle.builder().id("walleA").address("localhost").sessions(new HashSet<Session>()).build();
        Session sessionA = Session.builder().id("a").walle(walleA).status(SessionStatus.CLOSED).build();
        when(sessionRepository.findById("a")).thenReturn(java.util.Optional.ofNullable(sessionA));

        Exception exception = assertThrows(NotAcceptableException.class, () -> {
            Session result = sessionController.closeSessionById("a");
        });
        verify(sessionRepository).findById("a");
    }


    @Test
    void closeSession() {
        SessionRepository sessionRepository = mock(SessionRepository.class);
        ReflectionTestUtils.setField(sessionController,"sessionRepository",sessionRepository);

        Walle walleA = Walle.builder().id("walleA").address("localhost").sessions(new HashSet<Session>()).build();
        Session sessionA = Session.builder().id("a").walle(walleA).status(SessionStatus.NORMAL).build();
        when(sessionRepository.findById("a")).thenReturn(java.util.Optional.ofNullable(sessionA));
        when(sessionRepository.save(any(Session.class))).thenReturn(sessionA);
        Session result = sessionController.closeSessionById("a");
        Assertions.assertEquals(SessionStatus.CLOSED, result.getStatus());
        verify(sessionRepository).findById("a");
        verify(sessionRepository).save(any(Session.class));
    }

    @Test
    void findSessionById() {
        SessionRepository sessionRepository = mock(SessionRepository.class);
        ReflectionTestUtils.setField(sessionController,"sessionRepository",sessionRepository);

        Walle walleA = Walle.builder().id("walleA").address("localhost").sessions(new HashSet<Session>()).build();
        Session sessionA = Session.builder().id("a").walle(walleA).status(SessionStatus.NORMAL).build();
        when(sessionRepository.findById("a")).thenReturn(java.util.Optional.ofNullable(sessionA));
        Session result = sessionController.findSessionById("a");
        Assertions.assertNotNull(result);
        Assertions.assertEquals("a", result.getId());
        verify(sessionRepository).findById("a");
    }

    @Test
    void findSessionByIdError() {
        SessionRepository sessionRepository = mock(SessionRepository.class);
        ReflectionTestUtils.setField(sessionController,"sessionRepository",sessionRepository);

        when(sessionRepository.findById("a")).thenReturn(java.util.Optional.empty());
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            Session result = sessionController.findSessionById("a");
        });
        verify(sessionRepository).findById("a");
    }

    @Test
    void createSessionWithBadWalle() {
        SessionRepository sessionRepository = mock(SessionRepository.class);
        WalleController walleController = mock(WalleController.class);
        ReflectionTestUtils.setField(sessionController,"sessionRepository",sessionRepository);
        ReflectionTestUtils.setField(sessionController,"walleController",walleController);

        Walle walleA = Walle.builder().id("walleA").address("localhost").sessions(new HashSet<Session>()).build();
        Session sessionA = Session.builder().id("a").walle(walleA).status(SessionStatus.CLOSED).build();
        Session sessionB = Session.builder().id("b").walle(walleA).status(SessionStatus.NORMAL).build();
        ArrayList<Session> list = new ArrayList<>();
        list.add(sessionA);
        list.add(sessionB);
        when(walleController.findWalleById(any(String.class))).thenThrow(ResourceNotFoundException.class);

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            sessionController.startSession("a", null, null);
        });

        Assertions.assertEquals("Could not Wall-E with id: a.", exception.getMessage());
        verify(walleController).findWalleById(any(String.class));

    }

    @Test
    void createSessionWithMultipleSessionOpenForWalle() {
        SessionRepository sessionRepository = mock(SessionRepository.class);
        WalleController walleController = mock(WalleController.class);
        ReflectionTestUtils.setField(sessionController,"sessionRepository",sessionRepository);
        ReflectionTestUtils.setField(sessionController,"walleController",walleController);

        Walle walleA = Walle.builder().id("walleA").address("localhost").sessions(new HashSet<Session>()).build();
        Session sessionA = Session.builder().id("a").walle(walleA).status(SessionStatus.CLOSED).build();
        Session sessionB = Session.builder().id("b").walle(walleA).status(SessionStatus.NORMAL).build();
        Session sessionC = Session.builder().id("b").walle(walleA).status(SessionStatus.NORMAL).build();
        ArrayList<Session> list = new ArrayList<>();
        list.add(sessionA);
        list.add(sessionB);
        list.add(sessionC);
        when(sessionRepository.findAll()).thenReturn(list);
        when(walleController.findWalleById(any(String.class))).thenReturn(walleA);

        Exception exception = assertThrows(ConflictException.class, () -> {
            sessionController.startSession("a", null, null);
        });

        Assertions.assertEquals("Multiple session are already open for this Wall-E with Ids: [a, b, b]", exception.getMessage());
        verify(sessionRepository).findAll();
        verify(walleController).findWalleById(any(String.class));

    }

    @Test
    void createSessionWithSessionOpenForWalle() {
        SessionRepository sessionRepository = mock(SessionRepository.class);
        WalleController walleController = mock(WalleController.class);
        ReflectionTestUtils.setField(sessionController,"sessionRepository",sessionRepository);
        ReflectionTestUtils.setField(sessionController,"walleController",walleController);

        Walle walleA = Walle.builder().id("walleA").address("localhost").sessions(new HashSet<Session>()).build();
        Session sessionA = Session.builder().id("a").walle(walleA).status(SessionStatus.CLOSED).build();
        Session sessionB = Session.builder().id("b").walle(walleA).status(SessionStatus.NORMAL).build();
        ArrayList<Session> list = new ArrayList<>();
        list.add(sessionA);
        list.add(sessionB);
        when(sessionRepository.findAll()).thenReturn(list);
        when(walleController.findWalleById(any(String.class))).thenReturn(walleA);

        Exception exception = assertThrows(ConflictException.class, () -> {
            sessionController.startSession("a", null, null);
        });

        Assertions.assertEquals("A session is already open for this Wall-E with Id: b.", exception.getMessage());
        verify(sessionRepository).findAll();
        verify(walleController).findWalleById(any(String.class));

    }

    @Test
    void createSession() {
        SessionRepository sessionRepository = mock(SessionRepository.class);
        WalleController walleController = mock(WalleController.class);
        ReflectionTestUtils.setField(sessionController,"sessionRepository",sessionRepository);
        ReflectionTestUtils.setField(sessionController,"walleController",walleController);

        Walle walleA = Walle.builder().id("walleA").address("localhost").sessions(new HashSet<Session>()).build();
        Session sessionA = Session.builder().id("a").walle(walleA).status(SessionStatus.CLOSED).build();
        Session sessionB = Session.builder().id("b").walle(walleA).status(SessionStatus.CLOSED).build();
        ArrayList<Session> list = new ArrayList<>();
        list.add(sessionA);
        list.add(sessionB);
        when(sessionRepository.findAll()).thenReturn(list);
        when(walleController.findWalleById(any(String.class))).thenReturn(walleA);
        when(sessionRepository.save(any(Session.class))).thenReturn(sessionA);

        sessionController.startSession("a", null, null);

        verify(sessionRepository).findAll();
        verify(walleController).findWalleById(any(String.class));
        verify(sessionRepository).save(any(Session.class));
    }
}
