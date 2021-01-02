package rad.axiom.eve.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import rad.axiom.eve.exception.ResourceNotFoundException;
import rad.axiom.eve.repository.SessionRepository;
import rad.axiom.eve.repository.WalleRepository;
import rad.axiom.eve.session.Session;
import rad.axiom.eve.session.Walle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class WalleControllerTest {

    private static WalleController walleController;

    @BeforeAll
    public static void setUp() {
        walleController = new WalleController();
    }

    @Test
    void findAllWalles() {
        WalleRepository walleRepository = mock(WalleRepository.class);
        ReflectionTestUtils.setField(walleController,"walleRepository",walleRepository);

        Walle walleA = Walle.builder().id("walleA").address("localhost").sessions(new HashSet<Session>()).build();
        Walle walleB = Walle.builder().id("walleA").address("18.255.225.00").sessions(new HashSet<Session>()).build();
        ArrayList<Walle> list = new ArrayList<>();
        list.add(walleA);
        list.add(walleB);

        when(walleRepository.findAll()).thenReturn(list);
        List<Walle> response = walleController.findAllWalles();
        Assertions.assertEquals(2, response.size());
        verify(walleRepository).findAll();
    }

    @Test
    void findWalleById() {
        WalleRepository walleRepository = mock(WalleRepository.class);
        ReflectionTestUtils.setField(walleController,"walleRepository",walleRepository);

        Walle walleA = Walle.builder().id("walleA").address("localhost").sessions(new HashSet<Session>()).build();
        Walle walleB = Walle.builder().id("walleA").address("18.255.225.00").sessions(new HashSet<Session>()).build();
        ArrayList<Walle> list = new ArrayList<>();
        list.add(walleA);
        list.add(walleB);

        when(walleRepository.findById("walleA")).thenReturn(java.util.Optional.ofNullable(walleA));
        Walle response = walleController.findWalleById("walleA");
        Assertions.assertEquals(walleA, response);
        verify(walleRepository).findById("walleA");
    }

    @Test
    void findWalleByIdError() {
        WalleRepository walleRepository = mock(WalleRepository.class);
        ReflectionTestUtils.setField(walleController,"walleRepository",walleRepository);

        Walle walleA = Walle.builder().id("walleA").address("localhost").sessions(new HashSet<Session>()).build();
        Walle walleB = Walle.builder().id("walleA").address("18.255.225.00").sessions(new HashSet<Session>()).build();
        ArrayList<Walle> list = new ArrayList<>();
        list.add(walleA);
        list.add(walleB);

        when(walleRepository.findById("walleA")).thenReturn(java.util.Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            Walle response = walleController.findWalleById("walleA");
        });
        verify(walleRepository).findById("walleA");
    }
}